package com.boom.android.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AppDialogFragment extends DialogFragment {
    public static final String TAG = "AppDialogFragment";
    public static final int TYPE_TIME_DELAY_BEFORE_RECORD = 1;
    public static final int TYPE_FILE_NAME_FORMAT_SELECT = 2;

    protected int type;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }
}
