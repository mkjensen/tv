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

package com.github.mkjensen.tv.ui;

import com.google.android.media.tv.companionlibrary.ChannelSetupFragment;
import com.google.android.media.tv.companionlibrary.EpgSyncJobService;

import android.app.Activity;
import android.content.ComponentName;
import android.media.tv.TvInputInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mkjensen.tv.R;
import com.github.mkjensen.tv.live.EpgSyncJobServiceImpl;

import java.util.concurrent.TimeUnit;

public class LiveSetupFragment extends ChannelSetupFragment {

  private String inputId;

  private Integer scanErrorReason;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    inputId = getArguments().getString(TvInputInfo.EXTRA_INPUT_ID);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    setButtonText(R.string.live_setup_cancel);
    setChannelListVisibility(true);
    setDescription(R.string.live_setup_loading);
    setTitle(R.string.app_name);
    return view;
  }

  @Override
  public void onScanStarted() {

    Activity activity = getActivity();

    EpgSyncJobService.cancelAllSyncRequests(activity);

    EpgSyncJobService.requestImmediateSync(
        activity,
        inputId,
        new ComponentName(activity, EpgSyncJobServiceImpl.class)
    );
  }

  @Override
  public String getInputId() {

    return inputId;
  }

  @Override
  public void onScanFinished() {

    Activity activity = getActivity();

    EpgSyncJobService.cancelAllSyncRequests(activity);

    if (scanErrorReason != null) {
      activity.finish();
    }

    EpgSyncJobService.setUpPeriodicSync(
        activity,
        inputId,
        new ComponentName(activity, EpgSyncJobServiceImpl.class),
        TimeUnit.MINUTES.toMillis(30),
        TimeUnit.HOURS.toMillis(1)
    );

    activity.setResult(Activity.RESULT_OK);
    activity.finish();
  }

  @Override
  public void onScanError(int reason) {

    scanErrorReason = reason;
  }
}
