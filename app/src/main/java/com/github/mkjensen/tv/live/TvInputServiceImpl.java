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

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.ModelUtils;

import android.content.Context;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputService;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dagger.android.AndroidInjection;
import timber.log.Timber;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_READY;

public class TvInputServiceImpl extends TvInputService {

  @SuppressWarnings("WeakerAccess")
  @Inject
  HlsMediaSource.Factory hlsMediaSourceFactory;

  @SuppressWarnings("WeakerAccess")
  @Inject
  SimpleExoPlayer simpleExoPlayer;

  @Override
  public void onCreate() {

    AndroidInjection.inject(this);

    super.onCreate();
  }

  @Nullable
  @Override
  public Session onCreateSession(String inputId) {

    Timber.d("onCreateSession: %s", inputId);

    return new TvInputServiceSessionImpl(this, hlsMediaSourceFactory, simpleExoPlayer);
  }

  private static final class TvInputServiceSessionImpl extends TvInputService.Session {

    private final Context context;

    private final HlsMediaSource.Factory hlsMediaSourceFactory;

    private final Player.EventListener playerEventListener;

    private final SimpleExoPlayer simpleExoPlayer;

    private TvInputServiceSessionImpl(@NonNull Context context, @NonNull HlsMediaSource.Factory hlsMediaSourceFactory,
                                      @NonNull SimpleExoPlayer simpleExoPlayer) {

      super(context);

      this.context = context;
      this.hlsMediaSourceFactory = hlsMediaSourceFactory;
      this.playerEventListener = new PlayerEventListener(this);
      this.simpleExoPlayer = simpleExoPlayer;

      simpleExoPlayer.addListener(playerEventListener);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        notifyTimeShiftStatusChanged(TvInputManager.TIME_SHIFT_STATUS_UNSUPPORTED);
      }
    }

    @Override
    public void onRelease() {

      Timber.d("onRelease");

      simpleExoPlayer.removeListener(playerEventListener);
      simpleExoPlayer.release();
    }

    @Override
    public boolean onSetSurface(@Nullable Surface surface) {

      Timber.d("onSetSurface: %s", surface);

      simpleExoPlayer.setVideoSurface(surface);

      return true;
    }

    @Override
    public void onSetStreamVolume(float volume) {

      Timber.d("onSetStreamVolume: %s", volume);

      simpleExoPlayer.setVolume(volume);
    }

    @Override
    public boolean onTune(Uri channelUri) {

      Timber.d("onTune, channelUri: %s", channelUri);

      notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_TUNING);

      Channel channel = ModelUtils.getChannel(context.getContentResolver(), channelUri);
      String videoUrl = channel.getInternalProviderData().getVideoUrl();

      Timber.d("onTune, videoUrl: %s", videoUrl);

      HlsMediaSource hlsMediaSource = hlsMediaSourceFactory.createMediaSource(Uri.parse(videoUrl));
      simpleExoPlayer.prepare(hlsMediaSource);
      simpleExoPlayer.setPlayWhenReady(true);

      return true;
    }

    @Override
    public void onSetCaptionEnabled(boolean enabled) {

      Timber.d("onSetCaptionEnabled: %s", enabled);

      // Do nothing.
    }

    private static final class PlayerEventListener extends Player.DefaultEventListener {

      private final Session session;

      private PlayerEventListener(@NonNull Session session) {

        this.session = session;
      }

      @Override
      public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        Timber.d("onPlayerStateChanged: %s, %d", playWhenReady, playbackState);

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

          default:
            // Do nothing.
            break;
        }
      }

      @Override
      public void onPlayerError(ExoPlaybackException error) {

        Timber.e(error, "An error occurred during playback");

        session.notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
      }
    }
  }
}
