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

package com.github.mkjensen.tv;

import android.app.Application;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.mkjensen.tv.inject.ApplicationModule;
import com.github.mkjensen.tv.inject.BackendComponent;
import com.github.mkjensen.tv.inject.BackendModule;
import com.github.mkjensen.tv.inject.DaggerBackendComponent;
import com.github.mkjensen.tv.inject.DaggerPlaybackComponent;
import com.github.mkjensen.tv.inject.PlaybackComponent;
import com.github.mkjensen.tv.inject.PlaybackModule;

public class TvApplication extends Application {

  private BackendComponent backendComponent;

  private PlaybackComponent playbackComponent;

  @Override
  public void onCreate() {

    super.onCreate();

    initDagger();
  }

  private void initDagger() {

    ApplicationModule applicationModule = new ApplicationModule(this);
    BackendModule backendModule = new BackendModule();

    backendComponent = DaggerBackendComponent.builder()
        .applicationModule(applicationModule)
        .backendModule(backendModule)
        .build();

    playbackComponent = DaggerPlaybackComponent.builder()
        .applicationModule(applicationModule)
        .backendModule(backendModule)
        .playbackModule(new PlaybackModule())
        .build();
  }

  @CheckResult
  @NonNull
  public BackendComponent getBackendComponent() {

    return backendComponent;
  }

  @CheckResult
  @NonNull
  public PlaybackComponent getPlaybackComponent() {

    return playbackComponent;
  }
}
