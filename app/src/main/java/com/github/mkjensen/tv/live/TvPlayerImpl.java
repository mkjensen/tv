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

package com.github.mkjensen.tv.live;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.media.tv.companionlibrary.TvPlayer;

import android.media.PlaybackParams;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputService;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Surface;

import com.github.mkjensen.tv.util.Log;

import javax.inject.Inject;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_READY;

class TvPlayerImpl implements TvPlayer, EventListener {

  private static final String TAG = "TvPlayerImpl";

  private final DataSource.Factory dataSourceFactory;

  private final SimpleExoPlayer simpleExoPlayer;

  private TvInputService.Session session;

  @Inject
  TvPlayerImpl(@NonNull DataSource.Factory dataSourceFactory, @NonNull SimpleExoPlayer simpleExoPlayer) {

    this.dataSourceFactory = dataSourceFactory;
    this.simpleExoPlayer = simpleExoPlayer;
    this.simpleExoPlayer.addListener(this);
  }

  void setSession(@NonNull TvInputService.Session session) {

    this.session = session;
  }

  void play(@NonNull String url) {

    HlsMediaSource hlsMediaSource = new HlsMediaSource(
        Uri.parse(url),
        dataSourceFactory,
        null,
        null);

    simpleExoPlayer.prepare(hlsMediaSource);
    simpleExoPlayer.setPlayWhenReady(true);
  }

  void release() {

    simpleExoPlayer.release();
  }

  @Override
  public void seekTo(long position) {

    simpleExoPlayer.seekTo(position);
  }

  @Override
  public void setPlaybackParams(@Nullable PlaybackParams params) {

    //noinspection deprecation
    simpleExoPlayer.setPlaybackParams(params);
  }

  @Override
  public long getCurrentPosition() {

    return simpleExoPlayer.getCurrentPosition();
  }

  @Override
  public long getDuration() {

    return simpleExoPlayer.getDuration();
  }

  @Override
  public void setSurface(@Nullable Surface surface) {

    simpleExoPlayer.setVideoSurface(surface);
  }

  @Override
  public void setVolume(float volume) {

    simpleExoPlayer.setVolume(volume);
  }

  @Override
  public void pause() {

    simpleExoPlayer.setPlayWhenReady(false);
  }

  @Override
  public void play() {

    simpleExoPlayer.setPlayWhenReady(true);
  }

  @Override
  public void registerCallback(@NonNull Callback callback) {

    Log.d(TAG, "registerCallback " + callback);
    // Do nothing.
  }

  @Override
  public void unregisterCallback(@NonNull Callback callback) {

    Log.d(TAG, "unregisterCallback " + callback);
    // Do nothing.
  }

  @Override
  public void onLoadingChanged(boolean isLoading) {

    Log.d(TAG, "onLoadingChanged " + isLoading);
    // Do nothing.
  }

  @Override
  public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    Log.d(TAG, "onPlayerStateChanged " + playWhenReady + ", " + playbackState);

    if (!playWhenReady) {
      return;
    }

    switch (playbackState) {

      case STATE_BUFFERING:
        session.notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_BUFFERING);
        break;

      case STATE_READY:
        session.notifyVideoAvailable();
        break;
    }
  }

  @Override
  public void onRepeatModeChanged(int repeatMode) {

    Log.d(TAG, "onRepeatModeChanged " + repeatMode);
    // Do nothing.
  }

  @Override
  public void onTimelineChanged(@Nullable Timeline timeline, @Nullable Object manifest) {

    Log.d(TAG, "onTimelineChanged " + timeline + ", " + manifest);
    // Do nothing.
  }

  @Override
  public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    Log.d(TAG, "onTracksChanged " + trackGroups + ", " + trackSelections);
    // Do nothing.
  }

  @Override
  public void onPlayerError(@NonNull ExoPlaybackException error) {

    Log.e(TAG, "An error occurred during playback", error);
    session.notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
  }

  @Override
  public void onPositionDiscontinuity() {

    Log.d(TAG, "onPositionDiscontinuity");
    // Do nothing.
  }

  @Override
  public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    Log.d(TAG, "onPlaybackParametersChanged " + playbackParameters);
    // Do nothing.
  }
}
