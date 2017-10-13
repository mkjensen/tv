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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;

@SuppressWarnings({"SameParameterValue", "WeakerAccess"})
public final class Log {

  private Log() {
  }

  public static void e(@NonNull String tag, @NonNull String message) {

    e(tag, message, null);
  }

  public static void e(@NonNull String tag, @NonNull String message,
                       @Nullable Throwable throwable) {

    log(android.util.Log.ERROR, tag, message, throwable);
  }

  public static void w(@NonNull String tag, @NonNull String msg) {

    w(tag, msg, null);
  }

  public static void w(@NonNull String tag, @NonNull String message,
                       @Nullable Throwable throwable) {

    log(android.util.Log.WARN, tag, message, throwable);
  }

  public static void d(@NonNull String tag, @NonNull String msg) {

    d(tag, msg, null);
  }

  public static void d(String tag, @NonNull String message, @Nullable Throwable throwable) {

    log(android.util.Log.DEBUG, tag, message, throwable);
  }

  private static void log(int priority, @NonNull String tag, @NonNull String message,
                          @Nullable Throwable throwable) {

    Crashlytics.log(priority, tag, message);

    if (throwable != null) {
      Crashlytics.logException(throwable);
    }
  }
}
