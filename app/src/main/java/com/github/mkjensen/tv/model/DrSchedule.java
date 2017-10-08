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

package com.github.mkjensen.tv.model;

import com.google.auto.value.AutoValue;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.Date;
import java.util.List;

/**
 * @see <a href="http://www.dr.dk/mu-online/Help/1.4/Api/GET-api-apiVersion-schedule-id_broadcastDate_onlinegenretext">Gets
 * a schedule for a given channel and broadcast date.</a>
 */
@AutoValue
public abstract class DrSchedule {

  private static final String TAG = "DrSchedule";

  @CheckResult
  @NonNull
  public static JsonAdapter<DrSchedule> jsonAdapter(@NonNull Moshi moshi) {

    return new AutoValue_DrSchedule.MoshiJsonAdapter(moshi);
  }

  @CheckResult
  @Json(name = "Broadcasts")
  @NonNull
  public abstract List<DrProgram> getProgrammes();

  @AutoValue
  public static abstract class DrProgram {

    @CheckResult
    @NonNull
    public static JsonAdapter<DrProgram> jsonAdapter(@NonNull Moshi moshi) {

      return new AutoValue_DrSchedule_DrProgram.MoshiJsonAdapter(moshi);
    }

    @CheckResult
    @Json(name = "EndTime")
    @Nullable
    abstract Date getActualEndTime();

    @CheckResult
    @Json(name = "StartTime")
    @Nullable
    abstract Date getActualStartTime();

    @CheckResult
    @Json(name = "AnnouncedEndTime")
    @Nullable
    abstract Date getAnnouncedEndTime();

    @CheckResult
    @Json(name = "AnnouncedStartTime")
    @Nullable
    abstract Date getAnnouncedStartTime();

    @CheckResult
    @Json(name = "Description")
    @NonNull
    public abstract String getDescription();

    @CheckResult
    @Json(name = "ProgramCard")
    @Nullable
    public abstract DrProgramDetails getDetails();

    @CheckResult
    @NonNull
    public Date getEndTime() {

      Date actualEndTime = getActualEndTime();

      if (actualEndTime != null) {
        return actualEndTime;
      }

      Date announcedEndTime = getAnnouncedEndTime();

      if (announcedEndTime != null) {
        return announcedEndTime;
      }

      Log.e(TAG, "Both actual and announced end times are null for: " + getTitle());
      return new Date();
    }

    @CheckResult
    @NonNull
    public Date getStartTime() {

      Date actualStartTime = getActualStartTime();

      if (actualStartTime != null) {
        return actualStartTime;
      }

      Date announcedStartTime = getAnnouncedStartTime();

      if (announcedStartTime != null) {
        return announcedStartTime;
      }

      Log.e(TAG, "Both actual and announced start times are null for: " + getTitle());
      return new Date();
    }

    @CheckResult
    @Json(name = "Title")
    @NonNull
    public abstract String getTitle();

    @AutoValue
    public static abstract class DrProgramDetails {

      @CheckResult
      @NonNull
      public static JsonAdapter<DrProgramDetails> jsonAdapter(@NonNull Moshi moshi) {

        return new AutoValue_DrSchedule_DrProgram_DrProgramDetails.MoshiJsonAdapter(moshi);
      }

      @CheckResult
      @Json(name = "PrimaryImageUri")
      @NonNull
      public abstract String getImageUrl();
    }
  }
}
