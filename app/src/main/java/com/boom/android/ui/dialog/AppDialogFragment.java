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
    public static final int TYPE_FRAME_RATE = 3;
    public static final int TYPE_RESOLUTION = 4;
    public static final int TYPE_BITRATE = 5;
    public static final int TYPE_AUDIO_BITRATE = 6;
    public static final int TYPE_AUDIO_SAMPLE_RATE = 7;
    public static final int TYPE_AUDIO_CHANNEL = 8;
    public static final int TYPE_CAMERA_ID = 9;

    protected int type;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }
}
