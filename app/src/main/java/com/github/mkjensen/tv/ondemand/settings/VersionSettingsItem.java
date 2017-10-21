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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.github.mkjensen.tv.BuildConfig;
import com.github.mkjensen.tv.R;

public class VersionSettingsItem extends SettingsItem {

  @Override
  public void execute(@NonNull Context context) {

    Toast.makeText(context, BuildConfig.VERSION_NAME, Toast.LENGTH_SHORT).show();
  }

  @Override
  public Drawable getIcon(@NonNull Context context) {

    return context.getDrawable(R.drawable.ic_about);
  }

  @Override
  public String getSubtitle(@NonNull Context context) {

    return null;
  }

  @Override
  public String getTitle(@NonNull Context context) {

    return context.getString(R.string.ondemand_browse_settings_version_title);
  }
}
