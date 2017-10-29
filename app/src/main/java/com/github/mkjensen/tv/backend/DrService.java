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

package com.github.mkjensen.tv.backend;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.mkjensen.tv.model.BroadcastDetails;
import com.github.mkjensen.tv.model.BroadcastList;
import com.github.mkjensen.tv.model.DrChannel;
import com.github.mkjensen.tv.model.DrSchedule;
import com.github.mkjensen.tv.model.MainBroadcasts;
import com.github.mkjensen.tv.model.Video;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface DrService {

  /**
   * @see <a href="https://www.dr.dk/mu-online/Help/1.4">DR MU-online API documentation - v. 1.4</a>
   */
  String BASE_URL = "https://www.dr.dk/mu-online/api/1.4/";

  // Live

  /**
   * @see <a href="http://www.dr.dk/mu-online/Help/1.4/Api/GET-api-apiVersion-channel-all-active-dr-tv-channels">Gets
   * all active tv channels. DR1, DR2, DR3, DR Ramasjang, DR Ultra and DR K</a>
   */
  @CheckResult
  @GET("channel/all-active-dr-tv-channels")
  @NonNull
  Call<List<DrChannel>> getChannels();

  /**
   * @see <a href="http://www.dr.dk/mu-online/Help/1.4/Api/GET-api-apiVersion-schedule-nownext-id">Gets
   * scheduled Now and Next programs for a given id, i.e. channel dr1</a>
   */
  @CheckResult
  @GET("schedule/{channelId}")
  @NonNull
  Call<DrSchedule> getScheduleForChannel(@Path("channelId") String channelId,
                                         @Query("broadcastdate") String date);

  // On-demand

  /**
   * @see <a href="https://www.dr.dk/mu-online/Help/1.4/Api/GET-api-1.4-page-tv-player-programcardId_seriesId_seasonId_includeitemsWithoutPublicPrimaryAsset_onlyIncludeItemsFromSameSeason">Endpoint
   * for data necessary to build a DRTV Playerpage for a specific episode</a>
   */
  @CheckResult
  @GET("page/tv/player/{broadcastId}")
  @NonNull
  Call<BroadcastDetails> getBroadcastDetails(@Path("broadcastId") String broadcastId);

  /**
   * @see <a href="https://www.dr.dk/mu-online/Help/1.4/Api/GET-api-1.4-page-tv-front">Endpoint for
   * data necessary to build a DRTV Frontpage</a>
   */
  @CheckResult
  @GET("page/tv/front")
  @NonNull
  Call<MainBroadcasts> getMainBroadcasts();

  /**
   * @see <a href="https://www.dr.dk/mu-online/Help/1.4/Api/GET-api-apiVersion-list-view-mostviewed_channel_channelType_limit_offset">Gets
   * a paged list of most viewed programcards within the latest 7 days filtered by channel and channeltype</a>
   */
  @CheckResult
  @GET("list/view/mostviewed")
  @NonNull
  Call<BroadcastList> getMostViewedBroadcasts();

  /**
   * @see <a href="https://www.dr.dk/mu-online/Help/1.4/ResourceModel?modelName=MuAsset">MuAsset</a>
   */
  @CheckResult
  @GET
  @NonNull
  Call<Video> getVideo(@Url String videoUrl);
}
