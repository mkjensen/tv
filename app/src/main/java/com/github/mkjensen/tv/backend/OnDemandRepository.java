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
import android.support.v4.util.LruCache;

import com.github.mkjensen.tv.model.BroadcastDetails;
import com.github.mkjensen.tv.model.Broadcasts;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class OnDemandRepository {

  private final LiveData<Broadcasts> broadcasts;

  private final BroadcastDetailsCache broadcastDetailsCache;

  private final DrService drService;

  @Inject
  OnDemandRepository(@NonNull DrService drService) {

    this.broadcasts = CallLiveData.wrap(drService.getBroadcasts());
    this.broadcastDetailsCache = new BroadcastDetailsCache();
    this.drService = drService;
  }

  public LiveData<Broadcasts> getBroadcasts() {

    return broadcasts;
  }

  public LiveData<BroadcastDetails> getBroadcastDetails(String broadcastId) {

    return broadcastDetailsCache.get(broadcastId);
  }

  private final class BroadcastDetailsCache extends LruCache<String, LiveData<BroadcastDetails>> {

    BroadcastDetailsCache() {

      super(10);
    }

    @Override
    protected LiveData<BroadcastDetails> create(String key) {

      return CallLiveData.wrap(drService.getBroadcastDetails(key));
    }
  }
}
