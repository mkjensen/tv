/*
 * Copyright 2018 Martin Kamp Jensen
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

package com.github.mkjensen.tv.about;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;

import com.github.mkjensen.tv.BuildConfig;
import com.github.mkjensen.tv.R;

import de.psdev.licensesdialog.LicenseResolver;
import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.License;

import java.util.List;

public class AboutFragment extends GuidedStepFragment {

  private static final long CONTENT_ID = 0;

  private static final long THIRD_PARTY_ID = 1;

  @NonNull
  @Override
  public GuidanceStylist.Guidance onCreateGuidance(@Nullable Bundle savedInstanceState) {

    Resources resources = getResources();

    String title = resources.getString(R.string.app_name);
    String description = getString(R.string.about_description);
    String breadcrumb = resources.getString(R.string.about_breadcrumb);
    Drawable icon = resources.getDrawable(R.drawable.ic_about, null);

    return new GuidanceStylist.Guidance(title, description, breadcrumb, icon);
  }

  @Override
  public void onCreateActions(@NonNull List<GuidedAction> actions,
                              @Nullable Bundle savedInstanceState) {

    Activity activity = getActivity();

    actions.add(new GuidedAction.Builder(activity)
        .description(R.string.about_content_description)
        .id(CONTENT_ID)
        .title(R.string.about_content_title)
        .build());

    actions.add(new GuidedAction.Builder(activity)
        .description(R.string.about_thirdparty_description)
        .id(THIRD_PARTY_ID)
        .title(R.string.about_thirdparty_title)
        .build());

    actions.add(new GuidedAction.Builder(activity)
        .description(BuildConfig.VERSION_NAME)
        .infoOnly(true)
        .title(R.string.about_version_title)
        .build());
  }

  @Override
  public void onGuidedActionClicked(@NonNull GuidedAction action) {

    long actionId = action.getId();

    if (actionId == CONTENT_ID) {
      LicenseResolver.registerLicense(new MediaLicence());
      new LicensesDialog.Builder(getActivity())
          .setNotices(R.raw.content_licenses)
          .setTitle(R.string.about_content_title)
          .build()
          .show();
    } else if (actionId == THIRD_PARTY_ID) {
      new LicensesDialog.Builder(getActivity())
          .setNotices(R.raw.thirdparty_licenses)
          .setTitle(R.string.about_thirdparty_title)
          .build()
          .show();
    }
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
      return context.getString(R.string.about_content_text);
    }

    @Override
    public String getVersion() {
      return "1.0";
    }

    @Override
    public String getUrl() {
      return "https://www.dr.dk/om-dr/licens";
    }
  }
}
