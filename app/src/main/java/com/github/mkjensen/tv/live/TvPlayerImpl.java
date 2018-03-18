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
import com.google.android.exoplayer2.Player.DefaultEventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.media.tv.companionlibrary.TvPlayer;

import android.media.PlaybackParams;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputService;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Surface;

import javax.inject.Inject;

import timber.log.Timber;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_READY;

class TvPlayerImpl extends DefaultEventListener implements TvPlayer {

  private final HlsMediaSource.Factory hlsMediaSourceFactory;

  private final SimpleExoPlayer simpleExoPlayer;

  private TvInputService.Session session;

  @Inject
  TvPlayerImpl(@NonNull HlsMediaSource.Factory hlsMediaSourceFactory, @NonNull SimpleExoPlayer simpleExoPlayer) {

    this.hlsMediaSourceFactory = hlsMediaSourceFactory;
    this.simpleExoPlayer = simpleExoPlayer;
    this.simpleExoPlayer.addListener(this);
  }

  void setSession(@NonNull TvInputService.Session session) {

    this.session = session;
  }

  void play(@NonNull String url) {

    HlsMediaSource hlsMediaSource = hlsMediaSourceFactory.createMediaSource(
        Uri.parse(url),
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

    Timber.d("registerCallback: %s", callback);
    // Do nothing.
  }

  @Override
  public void unregisterCallback(@NonNull Callback callback) {

    Timber.d("unregisterCallback: %s", callback);
    // Do nothing.
  }

  @Override
  public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    Timber.d("onPlayerStateChanged: " + playWhenReady + ", " + playbackState);

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

    Timber.d("onRepeatModeChanged: %s", repeatMode);
    // Do nothing.
  }

  @Override
  public void onPlayerError(@NonNull ExoPlaybackException error) {

    Timber.e(error, "An error occurred during playback");
    session.notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
  }
}
