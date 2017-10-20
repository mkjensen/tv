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

package com.github.mkjensen.tv.ondemand.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.DetailsSupportFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;

import com.github.mkjensen.tv.R;
import com.github.mkjensen.tv.inject.viewmodel.ViewModelProvider;
import com.github.mkjensen.tv.model.Broadcast;
import com.github.mkjensen.tv.ondemand.playback.PlaybackActivity;
import com.github.mkjensen.tv.ondemand.presenter.BroadcastDetailsDescriptionPresenter;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class DetailsFragment extends DetailsSupportFragment {

  @SuppressWarnings("WeakerAccess")
  @Inject
  ViewModelProvider viewModelProvider;

  private Broadcast broadcast;

  private DetailsOverviewRow detailsOverviewRow;

  private ArrayObjectAdapter detailsOverviewRowActionsAdapter;

  @Override
  public void onAttach(Context context) {

    AndroidSupportInjection.inject(this);

    super.onAttach(context);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    broadcast = getActivity().getIntent().getParcelableExtra(DetailsActivity.BROADCAST);

    createAdapters();
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {

    super.onActivityCreated(savedInstanceState);

    DetailsViewModel viewModel = viewModelProvider.get(this, DetailsViewModel.class);

    viewModel.getBroadcastDetails(broadcast.getId()).observe(this, broadcastDetails -> {

      if (broadcastDetails == null) {
        return;
      }

      broadcast = broadcastDetails.getBroadcast();

      detailsOverviewRow.setItem(broadcast);
      detailsOverviewRow.setActionsAdapter(detailsOverviewRowActionsAdapter);
    });
  }

  private void createAdapters() {

    FullWidthDetailsOverviewRowPresenter rowsAdapterPresenter =
        new FullWidthDetailsOverviewRowPresenter(new BroadcastDetailsDescriptionPresenter());
    rowsAdapterPresenter.setOnActionClickedListener(action -> {
      Intent intent = new Intent(getActivity(), PlaybackActivity.class);
      intent.putExtra(PlaybackActivity.BROADCAST, broadcast);
      startActivity(intent);
    });

    detailsOverviewRow = new DetailsOverviewRow(broadcast);

    ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(rowsAdapterPresenter);
    rowsAdapter.add(detailsOverviewRow);
    setAdapter(rowsAdapter);

    detailsOverviewRowActionsAdapter = new ArrayObjectAdapter();
    detailsOverviewRowActionsAdapter.add(new Action(0, getString(R.string.ondemand_details_play)));
  }
}
