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
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.github.mkjensen.tv.model.DrChannel;
import com.github.mkjensen.tv.util.Log;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class DrRepository {

  private static final String TAG = "DrRepository";

  private final DrService drService;

  @Inject
  DrRepository(@NonNull DrService drService) {

    this.drService = drService;
  }

  public LiveData<Collection<DrChannel>> getChannels() {

    Log.d(TAG, "Getting channels");

    MutableLiveData<Collection<DrChannel>> data = new MutableLiveData<>();

    drService.getChannels().enqueue(new Callback<List<DrChannel>>() {

      @Override
      public void onResponse(@NonNull Call<List<DrChannel>> call,
                             @NonNull Response<List<DrChannel>> response) {

        if (!response.isSuccessful()) {
          Log.e(TAG, "Error while fetching channels: " + response.message());
          return;
        }

        Log.d(TAG, "Got channels");
        data.setValue(response.body());
      }

      @Override
      public void onFailure(@NonNull Call<List<DrChannel>> call, @NonNull Throwable throwable) {

        Log.e(TAG, "Error while fetching channels", throwable);
      }
    });

    return data;
  }
}
