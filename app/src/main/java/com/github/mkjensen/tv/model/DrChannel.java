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

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

/**
 * @see <a href="http://www.dr.dk/mu-online/Help/1.3/Api/GET-api-apiVersion-channel-all-active-dr-tv-channels">Gets
 * all active tv channels. DR1, DR2, DR3, DR Ramasjang, DR Ultra and DR K</a>
 */
@AutoValue
public abstract class DrChannel {

  private String streamUrl;

  @CheckResult
  @NonNull
  public static JsonAdapter<DrChannel> jsonAdapter(@NonNull Moshi moshi) {

    return new AutoValue_DrChannel.MoshiJsonAdapter(moshi);
  }

  @CheckResult
  @Nullable
  public String getStreamUrl() {

    if (streamUrl == null) {
      streamUrl = findStreamUrl();
    }

    return streamUrl;
  }

  @CheckResult
  @Nullable
  private String findStreamUrl() {

    for (Server server : getServers()) {

      if (!"HLS".equals(server.getType())) {
        continue;
      }

      int bestKilobitRate = 0;
      String bestManifest = null;

      for (Server.Quality quality : server.getQualities()) {

        int kilobitRate = quality.getKilobitRate();

        if (kilobitRate > bestKilobitRate) {
          bestKilobitRate = kilobitRate;
          List<Server.Quality.Stream> streams = quality.getStreams();
          bestManifest = streams.get(0).getManifest();
        }
      }

      if (bestManifest != null) {
        return String.format("%s/%s", server.getBaseUrl(), bestManifest);
      }
    }

    return null;
  }

  @CheckResult
  @Json(name = "Slug")
  @NonNull
  public abstract String getId();

  @CheckResult
  @Json(name = "PrimaryImageUri")
  @NonNull
  public abstract String getImageUrl();

  @CheckResult
  @Json(name = "Title")
  @NonNull
  public abstract String getTitle();

  @CheckResult
  @Json(name = "WebChannel")
  public abstract boolean isWebChannel();

  @CheckResult
  @Json(name = "StreamingServers")
  @NonNull
  abstract List<Server> getServers();

  @AutoValue
  static abstract class Server {

    @CheckResult
    @NonNull
    public static JsonAdapter<Server> jsonAdapter(@NonNull Moshi moshi) {

      return new AutoValue_DrChannel_Server.MoshiJsonAdapter(moshi);
    }

    @CheckResult
    @Json(name = "LinkType")
    @NonNull
    abstract String getType();

    @CheckResult
    @Json(name = "Qualities")
    @NonNull
    abstract List<Quality> getQualities();

    @CheckResult
    @Json(name = "Server")
    @NonNull
    abstract String getBaseUrl();

    @AutoValue
    static abstract class Quality {

      @CheckResult
      @NonNull
      public static JsonAdapter<Quality> jsonAdapter(@NonNull Moshi moshi) {

        return new AutoValue_DrChannel_Server_Quality.MoshiJsonAdapter(moshi);
      }

      @CheckResult
      @Json(name = "Kbps")
      abstract int getKilobitRate();

      @CheckResult
      @Json(name = "Streams")
      @NonNull
      abstract List<Stream> getStreams();

      @AutoValue
      static abstract class Stream {

        @CheckResult
        @NonNull
        public static JsonAdapter<Stream> jsonAdapter(@NonNull Moshi moshi) {

          return new AutoValue_DrChannel_Server_Quality_Stream.MoshiJsonAdapter(moshi);
        }

        @CheckResult
        @Json(name = "Stream")
        @NonNull
        abstract String getManifest();
      }
    }
  }
}
