/*
 * Copyright 2016 Martin Kamp Jensen
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

package com.github.mkjensen.tv.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.github.mkjensen.tv.R;

public class OkDialogFragment extends DialogFragment {

  private static final String TITLE_ID = "titleId";

  private static final String MESSAGE_ID = "messageId";

  public static OkDialogFragment newInstance(@StringRes int titleId, @StringRes int messageId) {

    Bundle args = new Bundle();
    args.putInt(TITLE_ID, titleId);
    args.putInt(MESSAGE_ID, messageId);

    OkDialogFragment fragment = new OkDialogFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

    Bundle arguments = getArguments();

    int titleId = arguments.getInt(TITLE_ID);
    int messageId = arguments.getInt(MESSAGE_ID);

    return new AlertDialog.Builder(getActivity())
        .setMessage(messageId)
        .setPositiveButton(R.string.okdialog_ok, null)
        .setTitle(titleId)
        .create();
  }
}
