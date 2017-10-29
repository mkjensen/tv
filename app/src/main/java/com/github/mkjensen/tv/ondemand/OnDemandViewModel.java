/*
 * Copyright 2017 Martin Kamp Jensen
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

package com.github.mkjensen.tv.ondemand;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.github.mkjensen.tv.backend.OnDemandRepository;
import com.github.mkjensen.tv.model.Broadcast;
import com.github.mkjensen.tv.model.BroadcastDetails;
import com.github.mkjensen.tv.model.BroadcastList;
import com.github.mkjensen.tv.model.MainBroadcasts;
import com.github.mkjensen.tv.model.Video;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

@SuppressWarnings("WeakerAccess")
public class OnDemandViewModel extends ViewModel {

  private final LiveData<Broadcast> broadcast;

  private final LiveData<List<Broadcast>> lastChanceBroadcasts;

  private final LiveData<List<Broadcast>> latestNewsBroadcasts;

  private final LiveData<List<Broadcast>> mostViewedBroadcasts;

  private final OnDemandRepository onDemandRepository;

  private final MutableLiveData<Broadcast> selectedBroadcast;

  private final LiveData<List<Broadcast>> topBroadcasts;

  private final LiveData<Video> video;

  @Inject
  OnDemandViewModel(@NonNull OnDemandRepository onDemandRepository) {

    this.onDemandRepository = onDemandRepository;

    LiveData<MainBroadcasts> mainBroadcasts = onDemandRepository.getMainBroadcasts();
    this.lastChanceBroadcasts = Transformations.map(mainBroadcasts, b -> b.getLastChanceBroadcasts().getBroadcasts());
    this.latestNewsBroadcasts = Transformations.map(mainBroadcasts, b -> b.getLatestNewsBroadcasts().getBroadcasts());
    this.topBroadcasts = Transformations.map(mainBroadcasts, b -> b.getTopBroadcasts().getBroadcasts());

    this.mostViewedBroadcasts = Transformations.map(onDemandRepository.getMostViewedBroadcasts(),
        BroadcastList::getBroadcasts);

    this.selectedBroadcast = new MutableLiveData<>();
    LiveData<BroadcastDetails> broadcastDetails = Transformations.switchMap(this.selectedBroadcast,
        broadcast -> onDemandRepository.getBroadcastDetails(broadcast.getId()));
    this.broadcast = createBroadcast(selectedBroadcast, broadcastDetails);
    this.video = Transformations.switchMap(broadcastDetails,
        details -> onDemandRepository.getVideo(details.getBroadcast().getVideoDetailsUrl()));
  }

  LiveData<List<Broadcast>> getLastChanceBroadcasts() {

    return lastChanceBroadcasts;
  }

  LiveData<List<Broadcast>> getLatestNewsBroadcasts() {

    return latestNewsBroadcasts;
  }

  LiveData<List<Broadcast>> getMostViewedBroadcasts() {

    return mostViewedBroadcasts;
  }

  LiveData<List<Broadcast>> getTopBroadcasts() {

    return topBroadcasts;
  }

  LiveData<Broadcast> getSelectedBroadcast() {

    return broadcast;
  }

  void setSelectedBroadcast(Broadcast broadcast) {

    if (!Objects.equals(broadcast, selectedBroadcast.getValue())) {
      selectedBroadcast.setValue(broadcast);
    }
  }

  public LiveData<Video> getVideo() {

    return video;
  }

  public void refresh() {

    onDemandRepository.refresh();
  }

  private static LiveData<Broadcast> createBroadcast(LiveData<Broadcast> broadcastTrigger,
                                                     LiveData<BroadcastDetails> broadcastDetailsTrigger) {

    MediatorLiveData<Broadcast> broadcast = new MediatorLiveData<>();

    broadcast.addSource(broadcastTrigger, broadcast::setValue);

    broadcast.addSource(broadcastDetailsTrigger, broadcastDetails -> {
      if (broadcastDetails != null) {
        broadcast.setValue(broadcastDetails.getBroadcast());
      }
    });

    return broadcast;
  }
}
