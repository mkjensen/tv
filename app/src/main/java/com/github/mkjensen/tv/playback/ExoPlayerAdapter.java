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

package com.github.mkjensen.tv.playback;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;

import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v17.leanback.media.PlaybackGlueHost;
import android.support.v17.leanback.media.PlayerAdapter;
import android.support.v17.leanback.media.SurfaceHolderGlueHost;
import android.view.SurfaceHolder;

import com.github.mkjensen.tv.inject.FragmentScope;

import javax.inject.Inject;

import timber.log.Timber;

@FragmentScope
public class ExoPlayerAdapter extends PlayerAdapter implements SurfaceHolder.Callback, Player.EventListener {

  private static final long PROGRESS_UPDATE_DELAY_IN_MILLISECONDS = 16;

  private final DataSource.Factory dataSourceFactory;

  private final SimpleExoPlayer player;

  private final Handler handler;

  private final Runnable progressUpdateRunnable;

  private SurfaceHolderGlueHost surfaceHolderGlueHost;

  @Inject
  ExoPlayerAdapter(@NonNull DataSource.Factory dataSourceFactory,
                   @NonNull SimpleExoPlayer simpleExoPlayer) {

    this.dataSourceFactory = dataSourceFactory;

    this.handler = new Handler();

    this.player = simpleExoPlayer;

    this.progressUpdateRunnable = new Runnable() {

      @Override
      public void run() {

        Callback callback = getCallback();
        callback.onCurrentPositionChanged(ExoPlayerAdapter.this);
        callback.onBufferedPositionChanged(ExoPlayerAdapter.this);

        handler.postDelayed(this, PROGRESS_UPDATE_DELAY_IN_MILLISECONDS);
      }
    };
  }

  /**
   * @see com.google.android.exoplayer2.ExoPlayer#prepare(com.google.android.exoplayer2.source.MediaSource)
   * @see HlsMediaSource
   */
  public void prepare(String url) {

    HlsMediaSource mediaSource = new HlsMediaSource(Uri.parse(url), dataSourceFactory, null, null);
    player.prepare(mediaSource);
  }

  // PlayerAdapter required

  @Override
  public void play() {

    player.setPlayWhenReady(true);
  }

  @Override
  public void pause() {

    player.setPlayWhenReady(false);
  }

  // PlayerAdapter optional

  @Override
  public boolean isPrepared() {

    return player.getPlaybackState() == Player.STATE_READY;
  }

  @Override
  public void seekTo(long positionInMs) {

    player.seekTo(positionInMs);
  }

  @Override
  public void setProgressUpdatingEnabled(boolean enable) {

    handler.removeCallbacks(progressUpdateRunnable);

    if (enable) {
      handler.postDelayed(progressUpdateRunnable, PROGRESS_UPDATE_DELAY_IN_MILLISECONDS);
    }
  }

  @Override
  public boolean isPlaying() {

    return player.getPlaybackState() == Player.STATE_READY && player.getPlayWhenReady();
  }

  @Override
  public long getDuration() {

    return player.getDuration();
  }

  @Override
  public long getCurrentPosition() {

    return player.getCurrentPosition();
  }

  @Override
  public long getBufferedPosition() {

    return player.getBufferedPosition();
  }

  @Override
  public void onAttachedToHost(PlaybackGlueHost host) {

    if (host instanceof SurfaceHolderGlueHost) {
      surfaceHolderGlueHost = (SurfaceHolderGlueHost) host;
      surfaceHolderGlueHost.setSurfaceHolderCallback(this);
    }

    player.addListener(this);
  }

  @Override
  public void onDetachedFromHost() {

    player.removeListener(this);
    player.release();

    if (surfaceHolderGlueHost != null) {
      surfaceHolderGlueHost.setSurfaceHolderCallback(null);
      surfaceHolderGlueHost = null;
    }
  }

  // SurfaceHolder.Callback

  @Override
  public void surfaceCreated(SurfaceHolder holder) {

    player.setVideoSurfaceHolder(holder);
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    // Do nothing.
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {

    player.clearVideoSurfaceHolder(holder);
  }

  // Player.EventListener

  @Override
  public void onTimelineChanged(Timeline timeline, Object manifest) {

    Callback callback = getCallback();
    callback.onBufferedPositionChanged(this);
    callback.onCurrentPositionChanged(this);
    callback.onDurationChanged(this);
  }

  @Override
  public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    // Do nothing.
  }

  @Override
  public void onLoadingChanged(boolean isLoading) {

    getCallback().onBufferingStateChanged(this, isLoading);
  }

  @Override
  public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    Callback callback = getCallback();

    if (playbackState == Player.STATE_ENDED) {
      return;
    }

    callback.onPlayStateChanged(this);
    callback.onPreparedStateChanged(this);
  }

  @Override
  public void onRepeatModeChanged(int repeatMode) {

    // Do nothing.
  }

  @Override
  public void onPlayerError(ExoPlaybackException error) {

    Timber.e(error, "ExoPlayer error");

    getCallback().onError(this, error.type, error.getMessage());
  }

  @Override
  public void onPositionDiscontinuity() {

    Callback callback = getCallback();
    callback.onBufferedPositionChanged(this);
    callback.onCurrentPositionChanged(this);
  }

  @Override
  public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    // Do nothing.
  }
}
