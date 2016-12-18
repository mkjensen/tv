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

package com.github.mkjensen.tv.inject;

import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.TransferListener;

import android.app.Application;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.mkjensen.tv.backend.DrService;
import com.github.mkjensen.tv.model.JsonAdapterFactory;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Rfc3339DateJsonAdapter;

import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.internal.Version;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module
public class BackendModule {

  @CheckResult
  @NonNull
  @Provides
  @Singleton
  DrService drService(@NonNull Retrofit retrofit) {

    return retrofit.create(DrService.class);
  }

  @CheckResult
  @NonNull
  @Provides
  @Singleton
  BandwidthMeter exoPlayerBandwidthMeter(@NonNull DefaultBandwidthMeter defaultBandwidthMeter) {

    return defaultBandwidthMeter;
  }

  @CheckResult
  @NonNull
  @Provides
  @Singleton
  DataSource.Factory exoPlayerDataSourceFactory(@NonNull OkHttpClient okHttpClient,
                                                @NonNull TransferListener<? super DataSource> transferListener) {

    return new OkHttpDataSourceFactory(okHttpClient, Version.userAgent(), transferListener);
  }

  @CheckResult
  @NonNull
  @Provides
  @Singleton
  DefaultBandwidthMeter exoPlayerDefaultBandwidthMeter() {

    return new DefaultBandwidthMeter();
  }

  @CheckResult
  @NonNull
  @Provides
  @Singleton
  TransferListener<? super DataSource> exoPlayerTransferListener(@NonNull DefaultBandwidthMeter defaultBandwidthMeter) {

    return defaultBandwidthMeter;
  }

  @CheckResult
  @NonNull
  @Provides
  @Singleton
  Moshi moshi(@NonNull JsonAdapter.Factory jsonAdapterFactory) {

    return new Moshi.Builder()
        .add(Date.class, new Rfc3339DateJsonAdapter())
        .add(jsonAdapterFactory)
        .build();
  }

  @CheckResult
  @NonNull
  @Provides
  @Singleton
  JsonAdapter.Factory moshiJsonAdapterFactory() {

    return JsonAdapterFactory.create();
  }

  @CheckResult
  @NonNull
  @Provides
  @Singleton
  Cache okHttpCache(@NonNull Application application) {

    int maxSize = 10 * 1024 * 1024;
    return new Cache(application.getCacheDir(), maxSize);
  }

  @CheckResult
  @NonNull
  @Provides
  @Singleton
  OkHttpClient okHttpClient(@NonNull Cache cache) {

    return new OkHttpClient.Builder()
        .cache(cache)
        .build();
  }

  @CheckResult
  @NonNull
  @Provides
  @Singleton
  Retrofit retrofit(@NonNull Converter.Factory converterFactory,
                    @NonNull OkHttpClient okHttpClient) {

    return new Retrofit.Builder()
        .addConverterFactory(converterFactory)
        .baseUrl(DrService.BASE_URL)
        .client(okHttpClient)
        .build();
  }

  @CheckResult
  @NonNull
  @Provides
  @Singleton
  Converter.Factory retrofitConverterFactory(@NonNull Moshi moshi) {

    return MoshiConverterFactory.create(moshi);
  }
}
