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

import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;

import retrofit2.Call;

/**
 * Caches {@link CallLiveData} instances representing {@link Call}s using {@link String} keys. Additionally, the keys
 * are used to create missing values using {@link CallCreator}.
 */
class CallLiveDataLruCache<T> extends LruCache<String, CallLiveData<T>> {

  private final CallCreator<T> callCreator;

  /**
   * @see LruCache#LruCache(int)
   */
  CallLiveDataLruCache(int maxSize, @NonNull CallCreator<T> callCreator) {

    super(maxSize);

    this.callCreator = callCreator;
  }

  @Override
  protected CallLiveData<T> create(String key) {

    Call<T> call = callCreator.create(key);
    return CallLiveData.wrap(call);
  }

  void refresh() {

    for (CallLiveData<T> callLiveData : snapshot().values()) {
      callLiveData.refresh();
    }
  }

  interface CallCreator<T> {

    /**
     * @see LruCache#create(Object)
     */
    Call<T> create(String key);
  }
}
