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

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.VideoSupportFragment;
import android.support.v17.leanback.app.VideoSupportFragmentGlueHost;
import android.support.v17.leanback.media.PlaybackGlue;

import com.github.mkjensen.tv.inject.viewmodel.ViewModelProvider;
import com.github.mkjensen.tv.playback.ExoPlayerAdapterPlaybackTransportControlGlue;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import timber.log.Timber;

public class PlaybackFragment extends VideoSupportFragment {

  @SuppressWarnings("WeakerAccess")
  @Inject
  ViewModelProvider viewModelProvider;

  @SuppressWarnings("WeakerAccess")
  @Inject
  SimpleExoPlayer simpleExoPlayer;

  @SuppressWarnings("WeakerAccess")
  @Inject
  DataSource.Factory dataSourceFactory;

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

      String videoUrl = video.getUrl();

      Timber.d("Preparing video: " + videoUrl);
      HlsMediaSource mediaSource = new HlsMediaSource(Uri.parse(videoUrl), dataSourceFactory, null, null);
      simpleExoPlayer.prepare(mediaSource);
    });
  }

  @Override
  public void onResume() {

    Timber.d("onResume");

    super.onResume();

    simpleExoPlayer.setPlayWhenReady(true);
  }

  @Override
  public void onPause() {

    Timber.d("onPause");

    if (glue.isPlaying()) {
      glue.pause();
    }

    super.onPause();
  }

  @Override
  public void onDestroy() {

    Timber.d("onDestroy");

    simpleExoPlayer.release();

    super.onDestroy();
  }

  private void initializeGlue() {

    // TODO: Handle via dependency injection
    LeanbackPlayerAdapter leanbackPlayerAdapter = new LeanbackPlayerAdapter(getActivity(), simpleExoPlayer, 16);
    glue = new ExoPlayerAdapterPlaybackTransportControlGlue(getActivity(), leanbackPlayerAdapter);
    glue.setHost(new VideoSupportFragmentGlueHost(this));

    glue.addPlayerCallback(new PlaybackGlue.PlayerCallback() {

      @Override
      public void onPlayStateChanged(PlaybackGlue glue) {

        setControlsOverlayAutoHideEnabled(glue.isPlaying());
      }
    });
  }
}
