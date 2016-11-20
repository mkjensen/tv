/*
 * Copyright 2016 Martin Kamp Jensen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mkjensen.tv.live;

import com.google.android.media.tv.companionlibrary.EpgSyncJobService;
import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.InternalProviderData;
import com.google.android.media.tv.companionlibrary.model.Program;

import android.media.tv.TvContract;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.mkjensen.tv.TvApplication;
import com.github.mkjensen.tv.backend.DrService;
import com.github.mkjensen.tv.model.DrChannel;
import com.github.mkjensen.tv.model.DrSchedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;

public class EpgSyncJobServiceImpl extends EpgSyncJobService {

  private static final String TAG = "EpgSyncJobServiceImpl";

  private static final String DR_CHANNEL_ID = "drChannelId";

  @Inject
  DrService drService;

  @Override
  public void onCreate() {

    super.onCreate();

    ((TvApplication) getApplicationContext()).getBackendComponent().inject(this);
  }

  @NonNull
  @Override
  public List<Channel> getChannels() {

    Response<List<DrChannel>> response;

    try {
      response = drService.getChannels().execute();

    } catch (IOException ex) {
      Log.e(TAG, "Error while fetching channels", ex);
      return Collections.emptyList();
    }

    if (!response.isSuccessful()) {
      Log.e(TAG, "Error while fetching channels: " + response.message());
      return Collections.emptyList();
    }

    List<DrChannel> drChannels = response.body();

    Collections.sort(drChannels, new Comparator<DrChannel>() {

      @Override
      public int compare(DrChannel o1, DrChannel o2) {
        return o1.getTitle().compareTo(o2.getTitle());
      }
    });

    List<Channel> channels = new ArrayList<>(drChannels.size());

    int displayNumber = 1;

    for (DrChannel drChannel : drChannels) {

      if (drChannel.isWebChannel()) {
        continue;
      }

      InternalProviderData internalProviderData = createInternalProviderData(drChannel);

      if (internalProviderData == null) {
        continue;
      }

      channels.add(new Channel.Builder()
          .setChannelLogo(drChannel.getImageUrl())
          .setDisplayName(drChannel.getTitle())
          .setDisplayNumber(Integer.toString(displayNumber++))
          .setInternalProviderData(internalProviderData)
          .setOriginalNetworkId(drChannel.getId().hashCode())
          .setServiceType(TvContract.Channels.SERVICE_TYPE_AUDIO_VIDEO)
          .setType(TvContract.Channels.TYPE_OTHER)
          .build());
    }

    return channels;
  }

  @Nullable
  private static InternalProviderData createInternalProviderData(@NonNull DrChannel drChannel) {

    InternalProviderData internalProviderData = new InternalProviderData();

    String streamUrl = drChannel.getStreamUrl();

    if (streamUrl == null) {
      Log.w(TAG, String.format("Did not find stream URL for channel [%s]", drChannel.getId()));
      return null;
    }

    internalProviderData.setVideoUrl(streamUrl);

    try {
      internalProviderData.put(DR_CHANNEL_ID, drChannel.getId());

    } catch (InternalProviderData.ParseException ex) {
      throw new RuntimeException(ex);
    }

    return internalProviderData;
  }

  @NonNull
  @Override
  public List<Program> getProgramsForChannel(@NonNull Uri channelUri, @NonNull Channel channel,
                                             long startMs, long endMs) {

    String drChannelId = getDrChannelId(channel);

    if (drChannelId == null) {
      return Collections.emptyList();
    }

    Response<DrSchedule> response;

    try {
      response = drService.getScheduleForChannel(drChannelId).execute();

    } catch (IOException ex) {
      Log.e(TAG, String.format("Error while fetching schedule for channel [%s]: %s", channel, ex));
      return Collections.emptyList();
    }

    if (!response.isSuccessful()) {
      Log.e(TAG, String.format("Error while fetching schedule for channel [%s]: %s",
          channel, response.message()));
      return Collections.emptyList();
    }

    List<Program> programs = new ArrayList<>();

    for (DrSchedule.DrProgram drProgram : response.body().getProgrammes()) {

      programs.add(new Program.Builder()
          .setChannelId(channel.getId())
          .setDescription(drProgram.getDescription())
          .setEndTimeUtcMillis(drProgram.getEndTime().getTime())
          .setInternalProviderData(channel.getInternalProviderData())
          .setStartTimeUtcMillis(drProgram.getStartTime().getTime())
          .setTitle(drProgram.getTitle())
          .build());
    }

    return programs;
  }

  @Nullable
  private static String getDrChannelId(@NonNull Channel channel) {

    try {
      InternalProviderData internalProviderData = channel.getInternalProviderData();

      if (internalProviderData == null) {
        return null;
      }

      return (String) internalProviderData.get(DR_CHANNEL_ID);

    } catch (InternalProviderData.ParseException ex) {
      return null;
    }
  }
}
