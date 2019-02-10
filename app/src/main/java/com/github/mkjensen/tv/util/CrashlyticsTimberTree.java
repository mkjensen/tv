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

package com.github.mkjensen.tv.util;

import com.crashlytics.android.Crashlytics;

import androidx.annotation.NonNull;
import timber.log.Timber;

public class CrashlyticsTimberTree extends Timber.Tree {

  private static final String PRIORITY_KEY = "priority";

  private static final String TAG_KEY = "tag";

  private static final String MESSAGE_KEY = "message";

  @Override
  protected void log(int priority, String tag, @NonNull String message, Throwable throwable) {

    if (priority < android.util.Log.WARN) {
      return;
    }

    Crashlytics.setInt(PRIORITY_KEY, priority);
    Crashlytics.setString(TAG_KEY, tag);
    Crashlytics.setString(MESSAGE_KEY, message);

    if (throwable == null) {
      Crashlytics.logException(new Exception(message));
    } else {
      Crashlytics.logException(throwable);
    }
  }
}
