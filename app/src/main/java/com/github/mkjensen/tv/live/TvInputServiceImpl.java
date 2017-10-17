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

import com.google.android.media.tv.companionlibrary.BaseTvInputService;
import com.google.android.media.tv.companionlibrary.TvPlayer;
import com.google.android.media.tv.companionlibrary.model.InternalProviderData;
import com.google.android.media.tv.companionlibrary.model.Program;
import com.google.android.media.tv.companionlibrary.model.RecordedProgram;

import android.content.Context;
import android.media.tv.TvInputManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.mkjensen.tv.util.Log;

import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.android.AndroidInjection;

public class TvInputServiceImpl extends BaseTvInputService {

  private static final String TAG = "TvInputServiceImpl";

  @SuppressWarnings("WeakerAccess")
  @Inject
  Provider<TvPlayerImpl> playerProvider;

  @Override
  public void onCreate() {

    AndroidInjection.inject(this);

    super.onCreate();
  }

  @Nullable
  @Override
  public Session onCreateSession(@NonNull String inputId) {

    return sessionCreated(new SessionImpl(this, inputId));
  }

  private class SessionImpl extends BaseTvInputService.Session {

    private final TvPlayerImpl player;

    private String currentUrl;

    SessionImpl(@NonNull Context context, @NonNull String inputId) {

      super(context, inputId);

      player = playerProvider.get();
      player.setSession(this);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        notifyTimeShiftStatusChanged(TvInputManager.TIME_SHIFT_STATUS_UNSUPPORTED);
      }
    }

    @NonNull
    @Override
    public TvPlayer getTvPlayer() {

      return player;
    }

    @Override
    public boolean onPlayProgram(@Nullable Program program, long startPosMs) {

      Log.d(TAG, "onPlayProgram " + program + ", " + startPosMs);

      if (program == null) {
        return false;
      }

      InternalProviderData internalProviderData = program.getInternalProviderData();

      if (internalProviderData == null) {
        return false;
      }

      String url = internalProviderData.getVideoUrl();

      if (Objects.equals(currentUrl, url)) {
        return true;
      }

      player.play(url);
      currentUrl = url;

      return true;
    }

    @Override
    public boolean onPlayRecordedProgram(@NonNull RecordedProgram recordedProgram) {

      // Unsupported.
      return false;
    }

    @Override
    public void onSetCaptionEnabled(boolean enabled) {

      // Unsupported.
    }

    @Override
    public void onRelease() {

      player.release();

      super.onRelease();
    }
  }
}
