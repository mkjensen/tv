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

package com.github.mkjensen.tv.ondemand.about;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.github.mkjensen.tv.R;

import de.psdev.licensesdialog.LicenseResolver;
import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.License;

public class ContentLicensesAboutItem extends AboutItem {

  @Override
  public void execute(@NonNull Context context) {

    LicenseResolver.registerLicense(new MediaLicence());
    new LicensesDialog.Builder(context)
        .setNotices(R.raw.content_licenses)
        .setTitle(R.string.ondemand_browse_about_content_title)
        .build()
        .show();

  }

  @Override
  public Drawable getIcon(@NonNull Context context) {

    return context.getDrawable(R.drawable.ic_about);
  }

  @Override
  public String getSubtitle(@NonNull Context context) {

    return context.getString(R.string.ondemand_browse_about_content_subtitle);
  }

  @Override
  public String getTitle(@NonNull Context context) {

    return context.getString(R.string.ondemand_browse_about_content_title);
  }

  private static final class MediaLicence extends License {

    @Override
    public String getName() {
      return "Media licence";
    }

    @Override
    public String readSummaryTextFromResources(Context context) {
      return readFullTextFromResources(context);
    }

    @Override
    public String readFullTextFromResources(Context context) {
      return context.getString(R.string.ondemand_browse_about_content_text);
    }

    @Override
    public String getVersion() {
      return "1.0";
    }

    @Override
    public String getUrl() {
      return "http://www.dr.dk/om-dr/licens";
    }
  }
}
