/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.android.cookbook.aerodoc.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.app.DialogFragment;

public class ErrorDialogFragment extends DialogFragment {
  // Global field to contain the error dialog
  private Dialog mDialog;

  // Default constructor. Sets the dialog field to null
  public ErrorDialogFragment() {
    super();
    mDialog = null;
  }

  // Set the dialog to display
  public void setDialog(Dialog dialog) {
    mDialog = dialog;
  }

  // Return a Dialog to the DialogFragment.
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return mDialog;
  }
}
