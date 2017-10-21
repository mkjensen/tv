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

package com.github.mkjensen.tv.backend;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.github.mkjensen.tv.model.BroadcastDetails;
import com.github.mkjensen.tv.model.Broadcasts;
import com.github.mkjensen.tv.model.Video;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class OnDemandRepository {

  private final CallLiveData<Broadcasts> broadcasts;

  private final CallLiveDataLruCache<BroadcastDetails> broadcastDetailsCache;

  private final CallLiveDataLruCache<Video> videoCache;

  @Inject
  OnDemandRepository(@NonNull DrService drService) {

    this.broadcasts = CallLiveData.wrap(drService.getBroadcasts());

    this.broadcastDetailsCache = new CallLiveDataLruCache<>(100, drService::getBroadcastDetails);

    this.videoCache = new CallLiveDataLruCache<>(10, drService::getVideo);
  }

  public LiveData<Broadcasts> getBroadcasts() {

    return broadcasts;
  }

  public LiveData<BroadcastDetails> getBroadcastDetails(String broadcastId) {

    return broadcastDetailsCache.get(broadcastId);
  }

  public LiveData<Video> getVideo(String videoUrl) {

    return videoCache.get(videoUrl);
  }

  public void refresh() {

    broadcasts.refresh();
    broadcastDetailsCache.refresh();
    videoCache.refresh();
  }
}
