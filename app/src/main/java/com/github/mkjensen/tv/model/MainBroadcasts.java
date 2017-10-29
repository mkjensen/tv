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
 * @see <a href="https://www.dr.dk/mu-online/Help/1.4/Api/GET-api-1.4-page-tv-front">FrontpageViewModel</a>
 */
@AutoValue
public abstract class MainBroadcasts {

  @SuppressWarnings("WeakerAccess")
  @CheckResult
  @NonNull
  public static JsonAdapter<MainBroadcasts> jsonAdapter(@NonNull Moshi moshi) {

    return new AutoValue_MainBroadcasts.MoshiJsonAdapter(moshi);
  }

  @CheckResult
  @Json(name = "LastChance")
  @NonNull
  public abstract BroadcastList getLastChanceBroadcasts();

  @CheckResult
  @Json(name = "News")
  @NonNull
  public abstract BroadcastList getLatestNewsBroadcasts();

  @CheckResult
  @Json(name = "TopSpots")
  @NonNull
  public abstract BroadcastList getTopBroadcasts();
}
