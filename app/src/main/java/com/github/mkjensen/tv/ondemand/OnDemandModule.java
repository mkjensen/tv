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

package com.github.mkjensen.tv.ondemand;

import android.arch.lifecycle.ViewModel;

import com.github.mkjensen.tv.inject.FragmentScope;
import com.github.mkjensen.tv.inject.viewmodel.ViewModelKey;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
@SuppressWarnings("unused")
public interface OnDemandModule {

  @Singleton
  @Binds
  @IntoMap
  @ViewModelKey(OnDemandViewModel.class)
  ViewModel onDemandViewModel(OnDemandViewModel onDemandViewModel);

  @FragmentScope
  @ContributesAndroidInjector
  BrowseFragment browseFragment();

  @FragmentScope
  @ContributesAndroidInjector
  DetailsFragment detailsFragment();

  @FragmentScope
  @ContributesAndroidInjector
  PlaybackFragment playbackFragment();
}
