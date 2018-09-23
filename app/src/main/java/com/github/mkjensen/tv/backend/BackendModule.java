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

package com.github.mkjensen.tv.backend;

import android.app.Application;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.mkjensen.tv.model.JsonAdapterFactory;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;

import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module
public class BackendModule {

  /**
   * The maximum cache size in bytes for {@link OkHttpClient}.
   */
  private static final int OK_HTTP_CACHE_MAX_SIZE = 10 * 1024 * 1024;

  @CheckResult
  @NonNull
  @Provides
  @Singleton
  DrService drService(@NonNull Converter.Factory converterFactory,
                      @NonNull OkHttpClient okHttpClient) {

    return new Retrofit.Builder()
        .addConverterFactory(converterFactory)
        .baseUrl(DrService.BASE_URL)
        .client(okHttpClient)
        .build()
        .create(DrService.class);
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

    return new Cache(application.getCacheDir(), OK_HTTP_CACHE_MAX_SIZE);
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
  Converter.Factory retrofitConverterFactory(@NonNull Moshi moshi) {

    return MoshiConverterFactory.create(moshi);
  }
}
