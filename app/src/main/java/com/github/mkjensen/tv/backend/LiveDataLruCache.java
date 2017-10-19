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

import retrofit2.Call;

/**
 * Caches {@link LiveData} values using {@link String} keys. Additionally, the keys are used to create missing values
 * using {@link CallCreator}.
 */
class LiveDataLruCache<T> extends LruCache<String, LiveData<T>> {

  private final CallCreator<T> callCreator;

  /**
   * @see LruCache#LruCache(int)
   */
  LiveDataLruCache(int maxSize, @NonNull CallCreator<T> callCreator) {

    super(maxSize);

    this.callCreator = callCreator;
  }

  @Override
  protected LiveData<T> create(String key) {

    Call<T> call = callCreator.create(key);
    return CallLiveData.wrap(call);
  }

  interface CallCreator<T> {

    /**
     * @see LruCache#create(Object)
     */
    Call<T> create(String key);
  }
}
