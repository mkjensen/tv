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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Wraps Retrofit's {@link Call} as {@link LiveData}.
 */
class CallLiveData<T> extends LiveData<T> implements Callback<T> {

  private final Call<T> call;

  private CallLiveData(@NonNull Call<T> call) {

    this.call = call;
  }

  /**
   * Creates a {@link LiveData} wrapper for the specified {@link Call}.
   *
   * @param call the non-{@code null} {@link Call} to wrap
   */
  static <T> CallLiveData<T> wrap(@NonNull Call<T> call) {

    return new CallLiveData<>(call);
  }

  @Override
  public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {

    if (!response.isSuccessful()) {
      Timber.e("onResponse: Call [%s] unsuccessful: %s, %s", call.request().url(), response.code(),
          response.message());
      // TODO: Forward error
      return;
    }

    Timber.d("onResponse: Call [%s] successful", call.request().url());
    setValue(response.body());
  }

  @Override
  public void onFailure(@NonNull Call<T> call, @NonNull Throwable throwable) {

    Timber.e(throwable, "Call [%s] failed", call.request().url());
    // TODO: Forward error
  }


  @Override
  protected void onActive() {

    if (call.isCanceled() || call.isExecuted()) {
      Timber.d("onActive: Call [%s] is cancelled or has already been executed", call.request().url());
      return;
    }

    Timber.d("onActive: Enqueuing call [%s]", call.request().url());
    call.enqueue(this);
  }
}
