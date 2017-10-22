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

import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v17.leanback.media.PlaybackTransportControlGlue;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.PlaybackControlsRow;

public class ExoPlayerAdapterPlaybackTransportControlGlue extends PlaybackTransportControlGlue<LeanbackPlayerAdapter> {

  private static final int REWIND_AND_FAST_FORWARD_TIME_IN_MILLISECONDS = 10_000;

  public ExoPlayerAdapterPlaybackTransportControlGlue(@NonNull Context context,
                                                      @NonNull LeanbackPlayerAdapter leanbackPlayerAdapter) {

    super(context, leanbackPlayerAdapter);
  }

  @Override
  protected void onCreatePrimaryActions(ArrayObjectAdapter primaryActionsAdapter) {

    super.onCreatePrimaryActions(primaryActionsAdapter);

    primaryActionsAdapter.add(new PlaybackControlsRow.RewindAction(getContext()));
    primaryActionsAdapter.add(new PlaybackControlsRow.FastForwardAction(getContext()));
  }

  @Override
  public void onActionClicked(Action action) {

    if (action instanceof PlaybackControlsRow.RewindAction) {
      rewind();
      return;
    }

    if (action instanceof PlaybackControlsRow.FastForwardAction) {
      fastForward();
      return;
    }

    super.onActionClicked(action);
  }

  private void rewind() {

    long newPosition = getCurrentPosition() - REWIND_AND_FAST_FORWARD_TIME_IN_MILLISECONDS;
    newPosition = (newPosition < 0) ? 0 : newPosition;
    getPlayerAdapter().seekTo(newPosition);
  }

  private void fastForward() {

    long duration = getDuration();
    long newPosition = getCurrentPosition() + REWIND_AND_FAST_FORWARD_TIME_IN_MILLISECONDS;
    newPosition = (newPosition > duration) ? duration : newPosition;
    getPlayerAdapter().seekTo(newPosition);
  }
}
