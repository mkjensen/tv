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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.VideoSupportFragment;
import android.support.v17.leanback.app.VideoSupportFragmentGlueHost;
import android.support.v17.leanback.media.PlaybackGlue;
import android.support.v17.leanback.media.PlaybackGlue.PlayerCallback;

import com.github.mkjensen.tv.inject.viewmodel.ViewModelProvider;
import com.github.mkjensen.tv.playback.ExoPlayerAdapter;
import com.github.mkjensen.tv.playback.ExoPlayerAdapterPlaybackTransportControlGlue;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class PlaybackFragment extends VideoSupportFragment {

  @SuppressWarnings("WeakerAccess")
  @Inject
  ViewModelProvider viewModelProvider;

  @SuppressWarnings("WeakerAccess")
  @Inject
  ExoPlayerAdapter exoPlayerAdapter;

  private ExoPlayerAdapterPlaybackTransportControlGlue glue;

  @Override
  public void onAttach(Context context) {

    AndroidSupportInjection.inject(this);

    super.onAttach(context);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    initializeGlue();

    setControlsOverlayAutoHideEnabled(true);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {

    super.onActivityCreated(savedInstanceState);

    OnDemandViewModel viewModel = viewModelProvider.get(this, OnDemandViewModel.class);

    viewModel.getSelectedBroadcast().observe(this, broadcast -> {

      if (broadcast == null) {
        return;
      }

      glue.setSubtitle(broadcast.getSubtitle());
      glue.setTitle(broadcast.getTitle());
    });

    viewModel.getVideo().observe(this, video -> {

      if (video == null) {
        return;
      }

      glue.getPlayerAdapter().prepare(video.getUrl());
    });
  }

  @Override
  public void onPause() {

    super.onPause();

    glue.pause();
  }

  private void initializeGlue() {

    // TODO: Handle via dependency injection
    glue = new ExoPlayerAdapterPlaybackTransportControlGlue(getActivity(), exoPlayerAdapter);
    glue.setHost(new VideoSupportFragmentGlueHost(this));
    glue.addPlayerCallback(new PlayerCallback() {

      @Override
      public void onPreparedStateChanged(PlaybackGlue glue) {

        if (glue.isPrepared()) {
          glue.removePlayerCallback(this);
          glue.play();
        }
      }
    });
  }
}
