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

package com.github.mkjensen.tv.ondemand.presenter;

import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;

abstract class AbstractImageCardViewPresenter<T> extends Presenter {

  @Override
  public final ViewHolder onCreateViewHolder(ViewGroup parent) {

    return new ViewHolder(new ImageCardView(parent.getContext()));
  }

  @SuppressWarnings("unchecked")
  @Override
  public final void onBindViewHolder(ViewHolder viewHolder, Object item) {

    onBindViewHolder((ImageCardView) viewHolder.view, (T) item);
  }

  @Override
  public final void onUnbindViewHolder(ViewHolder viewHolder) {

    ImageCardView view = (ImageCardView) viewHolder.view;
    view.setMainImage(null);
  }

  protected abstract void onBindViewHolder(ImageCardView view, T item);
}
