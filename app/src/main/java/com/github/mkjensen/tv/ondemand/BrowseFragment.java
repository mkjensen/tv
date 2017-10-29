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

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v17.leanback.app.BrowseSupportFragment;
import android.support.v17.leanback.app.RowsSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.PageRow;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.SinglePresenterSelector;

import com.github.mkjensen.tv.R;
import com.github.mkjensen.tv.inject.viewmodel.ViewModelProvider;
import com.github.mkjensen.tv.model.Broadcast;
import com.github.mkjensen.tv.ondemand.presenter.BroadcastPresenter;
import com.github.mkjensen.tv.ondemand.presenter.SettingsItemPresenter;
import com.github.mkjensen.tv.ondemand.presenter.BadgeRowHeaderPresenter;
import com.github.mkjensen.tv.ondemand.settings.ContentLicensesSettingsItem;
import com.github.mkjensen.tv.ondemand.settings.RefreshSettingsItem;
import com.github.mkjensen.tv.ondemand.settings.SettingsItem;
import com.github.mkjensen.tv.ondemand.settings.ThirdPartyLicensesSettingsItem;
import com.github.mkjensen.tv.ondemand.settings.VersionSettingsItem;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class BrowseFragment extends BrowseSupportFragment {

  private static final long DR_TV_HEADER_ID = 0;

  private static final long SETTINGS_HEADER_ID = 1;

  @SuppressWarnings("WeakerAccess")
  @Inject
  ViewModelProvider viewModelProvider;

  private OnDemandViewModel viewModel;

  @Override
  public void onAttach(Context context) {

    AndroidSupportInjection.inject(this);

    super.onAttach(context);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    setTitle(getString(R.string.app_name));
    createAdapter();

    getMainFragmentRegistry().registerFragment(PageRow.class, new ListRowFragmentFactory());
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {

    super.onActivityCreated(savedInstanceState);

    viewModel = viewModelProvider.get(this, OnDemandViewModel.class);
  }

  private void createAdapter() {

    ArrayObjectAdapter adapter = new ArrayObjectAdapter(new ListRowPresenter());

    setHeaderPresenterSelector(new SinglePresenterSelector(new BadgeRowHeaderPresenter()));

    Resources resources = getResources();

    adapter.add(new PageRow(new BadgeHeaderItem(DR_TV_HEADER_ID,
        resources.getString(R.string.ondemand_browse_drtv),
        resources.getDrawable(R.drawable.ic_content))));

    adapter.add(new PageRow(new BadgeHeaderItem(SETTINGS_HEADER_ID,
        resources.getString(R.string.ondemand_browse_settings),
        resources.getDrawable(R.drawable.ic_settings))));

    setAdapter(adapter);
  }

  public static final class DrTvFragment extends RowsSupportFragment {

    private ArrayObjectAdapter lastChanceBroadcastsAdapter;

    private ArrayObjectAdapter latestNewsBroadcastsAdapter;

    private ArrayObjectAdapter mostViewedBroadcastsAdapter;

    private ArrayObjectAdapter topBroadcastsAdapter;

    private OnDemandViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {

      super.onCreate(savedInstanceState);

      createAdapters();
      createListeners();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

      super.onActivityCreated(savedInstanceState);

      viewModel = ((BrowseFragment) getParentFragment()).viewModel;

      viewModel.getLastChanceBroadcasts().observe(this, createBroadcastsObserver(lastChanceBroadcastsAdapter));
      viewModel.getMostViewedBroadcasts().observe(this, createBroadcastsObserver(mostViewedBroadcastsAdapter));
      viewModel.getLatestNewsBroadcasts().observe(this, createBroadcastsObserver(latestNewsBroadcastsAdapter));
      viewModel.getTopBroadcasts().observe(this, createBroadcastsObserver(topBroadcastsAdapter));
    }

    private void createAdapters() {

      ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

      BroadcastPresenter broadcastPresenter = new BroadcastPresenter();

      topBroadcastsAdapter = new ArrayObjectAdapter(broadcastPresenter);
      rowsAdapter.add(createRow(R.string.ondemand_browse_drtv_top, topBroadcastsAdapter));

      mostViewedBroadcastsAdapter = new ArrayObjectAdapter(broadcastPresenter);
      rowsAdapter.add(createRow(R.string.ondemand_browse_drtv_mostviewed, mostViewedBroadcastsAdapter));

      latestNewsBroadcastsAdapter = new ArrayObjectAdapter(broadcastPresenter);
      rowsAdapter.add(createRow(R.string.ondemand_browse_drtv_latestnews, latestNewsBroadcastsAdapter));

      lastChanceBroadcastsAdapter = new ArrayObjectAdapter(broadcastPresenter);
      rowsAdapter.add(createRow(R.string.ondemand_browse_drtv_lastchance, lastChanceBroadcastsAdapter));

      setAdapter(rowsAdapter);
    }

    private ListRow createRow(@StringRes int categoryStringResId, ArrayObjectAdapter adapter) {

      return new ListRow(new HeaderItem(getString(categoryStringResId)), adapter);
    }

    private void createListeners() {

      setOnItemViewClickedListener((itemViewHolder, item, rowViewHolder, row) -> {

        viewModel.setSelectedBroadcast((Broadcast) item);

        getActivity().getSupportFragmentManager().beginTransaction()
            .addToBackStack(null)
            .add(android.R.id.content, new DetailsFragment())
            .commit();
      });
    }

    private static Observer<List<Broadcast>> createBroadcastsObserver(ArrayObjectAdapter broadcastsAdapter) {

      return broadcasts -> {

        broadcastsAdapter.clear();

        if (broadcasts == null || broadcasts.isEmpty()) {
          return;
        }

        broadcastsAdapter.addAll(0, broadcasts);
      };
    }
  }

  public static final class SettingsFragment extends RowsSupportFragment {

    private OnDemandViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {

      super.onCreate(savedInstanceState);

      viewModel = ((BrowseFragment) getParentFragment()).viewModel;

      createAdapters();
      createListeners();
    }

    private void createAdapters() {

      ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

      ArrayObjectAdapter settingsItemsAdapter = new ArrayObjectAdapter(new SettingsItemPresenter());
      settingsItemsAdapter.add(new RefreshSettingsItem(viewModel));
      settingsItemsAdapter.add(new ContentLicensesSettingsItem());
      settingsItemsAdapter.add(new ThirdPartyLicensesSettingsItem());
      settingsItemsAdapter.add(new VersionSettingsItem());
      rowsAdapter.add(new ListRow(new HeaderItem(getString(R.string.ondemand_browse_settings)), settingsItemsAdapter));

      setAdapter(rowsAdapter);
    }

    private void createListeners() {

      setOnItemViewClickedListener((itemViewHolder, item, rowViewHolder, row) -> {

        SettingsItem settingsItem = (SettingsItem) item;
        settingsItem.execute(getActivity());
      });
    }
  }

  private static class ListRowFragmentFactory extends BrowseSupportFragment.ListRowFragmentFactory {

    @Override
    public RowsSupportFragment createFragment(Object rowObject) {

      Row row = (Row) rowObject;

      if (row.getId() == DR_TV_HEADER_ID) {
        return new DrTvFragment();
      }

      if (row.getId() == SETTINGS_HEADER_ID) {
        return new SettingsFragment();
      }

      return null;
    }
  }
}
