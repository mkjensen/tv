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

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.github.mkjensen.tv.model.Broadcast;

public class BroadcastDetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

  @Override
  protected void onBindDescription(ViewHolder vh, Object item) {

    Broadcast broadcast = (Broadcast) item;

    vh.getTitle().setText(broadcast.getTitle());
    vh.getSubtitle().setText(broadcast.getSubtitle());
    vh.getBody().setText(broadcast.getDescription());
  }
}
