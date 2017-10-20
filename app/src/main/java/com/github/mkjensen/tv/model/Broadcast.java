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
import android.support.annotation.Nullable;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import timber.log.Timber;

/**
 * @see <a href="https://www.dr.dk/mu-online/Help/1.4/ResourceModel?modelName=MuListItem">MuListItem</a>
 * @see <a href="https://www.dr.dk/mu-online/Help/1.4/ResourceModel?modelName=MuPublicationListItem">MuPublicationListItem</a>
 * @see <a href="https://www.dr.dk/mu-online/Help/1.4/ResourceModel?modelName=MuProgramCardListItem">MuProgramCardListItem</a>
 * @see <a href="https://www.dr.dk/mu-online/Help/1.4/ResourceModel?modelName=MuProgramCard">MuProgramCard</a>
 */
@AutoValue
public abstract class Broadcast {

  @SuppressWarnings("WeakerAccess")
  @CheckResult
  @NonNull
  public static JsonAdapter<Broadcast> jsonAdapter(@NonNull Moshi moshi) {

    return new AutoValue_Broadcast.MoshiJsonAdapter(moshi);
  }

  @Override
  public int hashCode() {

    return getId().hashCode();
  }

  @Override
  public boolean equals(Object obj) {

    if (!(obj instanceof Broadcast)) {
      return false;
    }

    Broadcast other = (Broadcast) obj;
    return getId().equals(other.getId());
  }

  @CheckResult
  @Json(name = "Description")
  @Nullable
  public abstract String getDescription();

  @CheckResult
  @Json(name = "Slug")
  @NonNull
  public abstract String getId();

  @CheckResult
  @Json(name = "PrimaryImageUri")
  @NonNull
  public abstract String getImageUri();

  @CheckResult
  @Json(name = "Subtitle")
  @Nullable
  public abstract String getSubtitle();

  @CheckResult
  @Json(name = "Title")
  @NonNull
  public abstract String getTitle();

  @CheckResult
  @NonNull
  public String getVideoDetailsUrl() {

    Asset asset = getAsset();

    if (asset == null) {
      Timber.e("No video details URL for broadcast: " + getId());
      return "";
    }

    return asset.getVideoDetailsUrl();
  }

  @CheckResult
  @Json(name = "PrimaryAsset")
  @Nullable
  abstract Asset getAsset();

  /**
   * @see <a href="https://www.dr.dk/mu-online/Help/1.4/ResourceModel?modelName=MuAsset">MuAsset</a>
   */
  @AutoValue
  static abstract class Asset {

    @SuppressWarnings("WeakerAccess")
    @CheckResult
    @NonNull
    public static JsonAdapter<Asset> jsonAdapter(@NonNull Moshi moshi) {

      return new AutoValue_Broadcast_Asset.MoshiJsonAdapter(moshi);
    }

    @CheckResult
    @Json(name = "Uri")
    @NonNull
    abstract String getVideoDetailsUrl();
  }
}
