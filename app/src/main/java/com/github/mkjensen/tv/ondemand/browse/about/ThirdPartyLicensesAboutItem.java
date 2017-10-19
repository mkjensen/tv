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

package com.github.mkjensen.tv.ondemand.browse.about;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.github.mkjensen.tv.R;

import de.psdev.licensesdialog.LicensesDialog;

public class ThirdPartyLicensesAboutItem extends AboutItem {

  @Override
  public void execute(@NonNull Context context) {

    new LicensesDialog.Builder(context)
        .setNotices(R.raw.thirdparty_licenses)
        .setTitle(R.string.ondemand_browse_about_thirdparty_title)
        .build()
        .show();

  }

  @Override
  public Drawable getIcon(@NonNull Context context) {

    return context.getDrawable(R.drawable.ic_about);
  }

  @Override
  public String getSubtitle(@NonNull Context context) {

    return context.getString(R.string.ondemand_browse_about_thirdparty_subtitle);
  }

  @Override
  public String getTitle(@NonNull Context context) {

    return context.getString(R.string.ondemand_browse_about_thirdparty_title);
  }
}
