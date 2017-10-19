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

package com.github.mkjensen.tv.model;

import com.google.auto.value.AutoValue;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * @see <a href="https://www.dr.dk/mu-online/Help/1.4/ResourceModel?modelName=PlayerpageViewModel">PlayerpageViewModel</a>
 */
@AutoValue
public abstract class BroadcastDetails {

  @SuppressWarnings("WeakerAccess")
  @CheckResult
  @NonNull
  public static JsonAdapter<BroadcastDetails> jsonAdapter(@NonNull Moshi moshi) {

    return new AutoValue_BroadcastDetails.MoshiJsonAdapter(moshi);
  }

  @CheckResult
  @Json(name = "ProgramCard")
  @NonNull
  public abstract Broadcast getBroadcast();
}
