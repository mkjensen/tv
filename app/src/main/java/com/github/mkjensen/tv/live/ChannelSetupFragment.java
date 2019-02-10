/*
 * Copyright 2019 Martin Kamp Jensen
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

import com.google.android.media.tv.companionlibrary.setup.ChannelSetupStepSupportFragment;

import java.util.concurrent.TimeUnit;

public class ChannelSetupFragment extends ChannelSetupStepSupportFragment<EpgSyncJobServiceImpl> {

  @Override
  public Class<EpgSyncJobServiceImpl> getEpgSyncJobServiceClass() {

    return EpgSyncJobServiceImpl.class;
  }

  @Override
  public long getFullSyncWindowSec() {

    return TimeUnit.DAYS.toMillis(1);
  }

  @Override
  public long getFullSyncFrequencyMillis() {

    return TimeUnit.HOURS.toMillis(6);
  }
}
