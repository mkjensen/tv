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

package com.github.mkjensen.tv.ondemand.browse;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.github.mkjensen.tv.backend.OnDemandRepository;
import com.github.mkjensen.tv.model.Broadcast;
import com.github.mkjensen.tv.model.Broadcasts;

import java.util.List;

import javax.inject.Inject;

public class BrowseViewModel extends ViewModel {

  private final LiveData<List<Broadcast>> lastChanceBroadcasts;

  private final LiveData<List<Broadcast>> latestNewsBroadcasts;

  private final LiveData<List<Broadcast>> topBroadcasts;

  @Inject
  BrowseViewModel(@NonNull OnDemandRepository onDemandRepository) {

    LiveData<Broadcasts> broadcasts = onDemandRepository.getBroadcasts();

    this.lastChanceBroadcasts = Transformations.map(broadcasts,
        b -> b.getLastChanceBroadcasts().getBroadcasts());

    this.latestNewsBroadcasts = Transformations.map(broadcasts,
        b -> b.getLatestNewsBroadcasts().getBroadcasts());

    this.topBroadcasts = Transformations.map(broadcasts,
        b -> b.getTopBroadcasts().getBroadcasts());
  }

  LiveData<List<Broadcast>> getLastChanceBroadcasts() {

    return lastChanceBroadcasts;
  }

  LiveData<List<Broadcast>> getLatestNewsBroadcasts() {

    return latestNewsBroadcasts;
  }

  LiveData<List<Broadcast>> getTopBroadcasts() {

    return topBroadcasts;
  }
}
