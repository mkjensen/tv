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

import com.github.mkjensen.tv.R;
import com.github.mkjensen.tv.inject.viewmodel.ViewModelProvider;
import com.github.mkjensen.tv.model.Broadcast;
import com.github.mkjensen.tv.ondemand.about.AboutItem;
import com.github.mkjensen.tv.ondemand.about.ContentLicensesAboutItem;
import com.github.mkjensen.tv.ondemand.about.ThirdPartyLicensesAboutItem;
import com.github.mkjensen.tv.ondemand.about.VersionAboutItem;
import com.github.mkjensen.tv.ondemand.presenter.AboutItemPresenter;
import com.github.mkjensen.tv.ondemand.presenter.BroadcastPresenter;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class BrowseFragment extends BrowseSupportFragment {

  private static final long ABOUT_HEADER_ID = 0;

  private static final long CONTENT_HEADER_ID = 1;

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

    adapter.add(new PageRow(new HeaderItem(CONTENT_HEADER_ID, getString(R.string.ondemand_browse_content))));
    adapter.add(new PageRow(new HeaderItem(ABOUT_HEADER_ID, getString(R.string.ondemand_browse_about))));

    setAdapter(adapter);
  }

  public static final class AboutFragment extends RowsSupportFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {

      super.onCreate(savedInstanceState);

      createAdapters();
      createListeners();
    }

    private void createAdapters() {

      ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

      ArrayObjectAdapter aboutItemsAdapter = new ArrayObjectAdapter(new AboutItemPresenter());
      aboutItemsAdapter.add(new ContentLicensesAboutItem());
      aboutItemsAdapter.add(new ThirdPartyLicensesAboutItem());
      aboutItemsAdapter.add(new VersionAboutItem());
      rowsAdapter.add(new ListRow(new HeaderItem(getString(R.string.ondemand_browse_about)), aboutItemsAdapter));

      setAdapter(rowsAdapter);
    }

    private void createListeners() {

      setOnItemViewClickedListener((itemViewHolder, item, rowViewHolder, row) -> {

        AboutItem aboutItem = (AboutItem) item;
        aboutItem.execute(getActivity());
      });
    }
  }

  public static final class ContentFragment extends RowsSupportFragment {

    private ArrayObjectAdapter lastChanceBroadcastsAdapter;

    private ArrayObjectAdapter latestNewsBroadcastsAdapter;

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
      viewModel.getLatestNewsBroadcasts().observe(this, createBroadcastsObserver(latestNewsBroadcastsAdapter));
      viewModel.getTopBroadcasts().observe(this, createBroadcastsObserver(topBroadcastsAdapter));
    }

    private void createAdapters() {

      ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

      BroadcastPresenter broadcastPresenter = new BroadcastPresenter();

      topBroadcastsAdapter = new ArrayObjectAdapter(broadcastPresenter);
      rowsAdapter.add(createRow(R.string.ondemand_browse_content_top, topBroadcastsAdapter));

      latestNewsBroadcastsAdapter = new ArrayObjectAdapter(broadcastPresenter);
      rowsAdapter.add(createRow(R.string.ondemand_browse_content_latestnews, latestNewsBroadcastsAdapter));

      lastChanceBroadcastsAdapter = new ArrayObjectAdapter(broadcastPresenter);
      rowsAdapter.add(createRow(R.string.ondemand_browse_content_lastchance, lastChanceBroadcastsAdapter));

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

  private static class ListRowFragmentFactory extends BrowseSupportFragment.ListRowFragmentFactory {

    @Override
    public RowsSupportFragment createFragment(Object rowObject) {

      Row row = (Row) rowObject;

      if (row.getId() == ABOUT_HEADER_ID) {
        return new AboutFragment();
      }

      if (row.getId() == CONTENT_HEADER_ID) {
        return new ContentFragment();
      }

      return null;
    }
  }
}
