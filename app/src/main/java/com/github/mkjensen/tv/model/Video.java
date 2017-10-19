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

import java.util.List;

import timber.log.Timber;

/**
 * @see <a href="https://www.dr.dk/mu-online/Help/1.4/ResourceModel?modelName=MuManifest">MuManifest</a>
 */
@AutoValue
public abstract class Video {

  @SuppressWarnings("WeakerAccess")
  @CheckResult
  @NonNull
  public static JsonAdapter<Video> jsonAdapter(@NonNull Moshi moshi) {

    return new AutoValue_Video.MoshiJsonAdapter(moshi);
  }

  @CheckResult
  @NonNull
  public String getUrl() {

    for (Link link : getLinks()) {
      if ("HLS".equals(link.getType())) {
        return link.getUrl();
      }
    }

    Timber.e("No HLS video found in links: " + getLinks());
    return "";
  }

  @CheckResult
  @Json(name = "Links")
  @NonNull
  abstract List<Link> getLinks();

  /**
   * @see <a href="https://www.dr.dk/mu-online/Help/1.4/ResourceModel?modelName=MuMediaLink">MuMediaLink</a>
   */
  @AutoValue
  static abstract class Link {

    @SuppressWarnings("WeakerAccess")
    @CheckResult
    @NonNull
    public static JsonAdapter<Link> jsonAdapter(@NonNull Moshi moshi) {

      return new AutoValue_Video_Link.MoshiJsonAdapter(moshi);
    }

    @CheckResult
    @Json(name = "Target")
    @NonNull
    abstract String getType();

    @CheckResult
    @Json(name = "Uri")
    @NonNull
    abstract String getUrl();
  }
}
