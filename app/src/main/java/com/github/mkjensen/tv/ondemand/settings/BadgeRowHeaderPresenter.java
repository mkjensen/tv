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

package com.github.mkjensen.tv.ondemand.settings;

import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowHeaderPresenter;
import android.support.v17.leanback.widget.RowHeaderView;
import android.view.ViewGroup;

import com.github.mkjensen.tv.ondemand.BadgeHeaderItem;

public class BadgeRowHeaderPresenter extends RowHeaderPresenter {

  @Override
  public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {

    return new ViewHolder(super.onCreateViewHolder(parent));
  }

  @Override
  public void onBindViewHolder(Presenter.ViewHolder presenterViewHolder, Object item) {

    super.onBindViewHolder(presenterViewHolder, item);

    ViewHolder viewHolder = (ViewHolder) presenterViewHolder;
    BadgeHeaderItem badgeHeaderItem = (BadgeHeaderItem) ((Row) item).getHeaderItem();
    viewHolder.rowHeaderView.setCompoundDrawablePadding(20);
    viewHolder.rowHeaderView.setCompoundDrawablesRelativeWithIntrinsicBounds(badgeHeaderItem.getBadgeDrawable(), null, null, null);
  }

  @Override
  public void onUnbindViewHolder(Presenter.ViewHolder presenterViewHolder) {

    super.onUnbindViewHolder(presenterViewHolder);

    ViewHolder viewHolder = (ViewHolder) presenterViewHolder;
    viewHolder.rowHeaderView.setCompoundDrawablesRelative(null, null, null, null);
  }

  private static class ViewHolder extends RowHeaderPresenter.ViewHolder {

    final RowHeaderView rowHeaderView;

    ViewHolder(Presenter.ViewHolder presenterViewHolder) {

      super(presenterViewHolder.view);

      // mTitleView in RowHeaderPresenter.ViewHolder has package-private access.
      rowHeaderView = view.findViewById(android.support.v17.leanback.R.id.row_header);
    }
  }
}
