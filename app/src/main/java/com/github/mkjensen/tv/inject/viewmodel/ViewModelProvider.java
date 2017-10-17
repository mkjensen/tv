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

package com.github.mkjensen.tv.inject.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ViewModelProvider {

  private final android.arch.lifecycle.ViewModelProvider.Factory viewModelFactory;

  @Inject
  ViewModelProvider(@NonNull android.arch.lifecycle.ViewModelProvider.Factory viewModelFactory) {

    this.viewModelFactory = viewModelFactory;
  }

  public <T extends ViewModel> T get(@NonNull Fragment fragment, @NonNull Class<T> modelClass) {

    return ViewModelProviders.of(fragment, viewModelFactory).get(modelClass);
  }
}
