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

import com.github.mkjensen.tv.backend.DrService;
import com.github.mkjensen.tv.model.DrChannel;
import com.github.mkjensen.tv.model.DrSchedule;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Response;
import timber.log.Timber;

public class EpgSyncJobServiceImpl extends EpgSyncJobService {

  private static final String DR_CHANNEL_ID = "drChannelId";

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

  @SuppressWarnings("WeakerAccess")
  @Inject
  DrService drService;

  @Override
  public void onCreate() {

    AndroidInjection.inject(this);

    super.onCreate();
  }

  @NonNull
  @Override
  public List<Channel> getChannels() {

    Timber.d("Getting channels");

    Response<List<DrChannel>> response;

    try {
      response = drService.getChannels().execute();

    } catch (IOException ex) {
      Timber.e(ex, "Error while fetching channels");
      return Collections.emptyList();
    }

    if (!response.isSuccessful()) {
      Timber.e("Error while fetching channels: " + response.message());
      return Collections.emptyList();
    }

    List<DrChannel> drChannels = response.body();

    if (drChannels != null) {
      Collections.sort(drChannels, (o1, o2) -> o1.getTitle().compareTo(o2.getTitle()));
    } else {
      drChannels = Collections.emptyList();
    }

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

    Timber.d("Finished getting channels");

    return channels;
  }

  @Nullable
  private static InternalProviderData createInternalProviderData(@NonNull DrChannel drChannel) {

    InternalProviderData internalProviderData = new InternalProviderData();

    String streamUrl = drChannel.getStreamUrl();

    if (streamUrl == null) {
      Timber.w("Did not find stream URL for channel: " + drChannel.getId());
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

    Timber.d("Getting programs for channel: " + channel);

    String drChannelId = getDrChannelId(channel);

    if (drChannelId == null) {
      return Collections.emptyList();
    }

    Date startDate = new Date(startMs);
    String formattedStartDate = DATE_FORMAT.format(startDate);
    Timber.d("getProgramsForChannel for channel [%s] and start date [%s]",
        channelUri, formattedStartDate);
    Response<DrSchedule> response;

    try {
      response = drService.getScheduleForChannel(drChannelId, formattedStartDate).execute();

    } catch (IOException ex) {
      Timber.e(ex, "Error while fetching schedule for channel: " + channel);
      return Collections.emptyList();
    }

    if (!response.isSuccessful()) {
      Timber.e("Error while fetching schedule for channel [%s]: %s",
          channel, response.message());
      return Collections.emptyList();
    }

    List<Program> programs = new ArrayList<>();

    DrSchedule drSchedule = response.body();

    if (drSchedule != null) {
      for (DrSchedule.DrProgram drProgram : drSchedule.getProgrammes()) {

        Program.Builder builder = new Program.Builder()
            .setChannelId(channel.getId())
            .setDescription(drProgram.getDescription())
            .setEndTimeUtcMillis(drProgram.getEndTime().getTime())
            .setInternalProviderData(channel.getInternalProviderData())
            .setStartTimeUtcMillis(drProgram.getStartTime().getTime())
            .setTitle(drProgram.getTitle());

        DrSchedule.DrProgram.DrProgramDetails details = drProgram.getDetails();

        if (details != null) {
          builder
              .setPosterArtUri(drProgram.getDetails().getImageUrl());
        }

        programs.add(builder.build());
      }
    }

    Timber.d("Finished getting programs for channel: " + channel);

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
