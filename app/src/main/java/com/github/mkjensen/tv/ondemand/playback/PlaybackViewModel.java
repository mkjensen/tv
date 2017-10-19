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

package com.github.mkjensen.tv.ondemand.playback;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.github.mkjensen.tv.backend.OnDemandRepository;
import com.github.mkjensen.tv.model.Video;

import javax.inject.Inject;

public class PlaybackViewModel extends ViewModel {

  private final MutableLiveData<String> videoUrl;

  private final LiveData<Video> video;

  @Inject
  PlaybackViewModel(@NonNull OnDemandRepository onDemandRepository) {

    this.videoUrl = new MutableLiveData<>();

    this.video = Transformations.switchMap(videoUrl, onDemandRepository::getVideo);
  }

  LiveData<Video> getVideo() {

    return video;
  }

  void setVideoUrl(String videoUrl) {

    this.videoUrl.setValue(videoUrl);
  }
}
