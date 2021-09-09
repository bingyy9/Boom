package com.boom.android.ui.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.boom.android.BoomApplication;
import com.boom.android.R;
import com.boom.android.SettingsActivity;
import com.boom.android.util.ConfigUtil;
import com.boom.android.util.KeybordUtils;
import com.boom.android.util.PrefsUtil;
import com.boom.android.viewmodel.SettingsViewModel;
import com.boom.utils.StringUtils;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;


public class InputDialog extends DialogFragment {
    public static final String TAG = "InputDialog";
    public static final int TYPE_TIME_DELAY_BEFORE_RECORD = 1;

    private View rootView;
    private TextView tvTitle;
    private TextView tvMsg;
    private EditText etInput;
    private TextView btCancel;
    private TextView btOk;
    private int dlgType;

    SettingsViewModel settingsViewModel;

    public static DialogFragment newInstance(int type) {
        DialogFragment fragment = new InputDialog();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            dlgType = getArguments().getInt("type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        rootView = inflater.inflate(R.layout.input_dlg, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tvTitle = rootView.findViewById(R.id.tv_title);
        tvMsg = rootView.findViewById(R.id.tv_msg);
        etInput = rootView.findViewById(R.id.et_input);
        btCancel = rootView.findViewById(R.id.btn1);
        btOk = rootView.findViewById(R.id.btn2);

        settingsViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.AndroidViewModelFactory(BoomApplication.getInstance()))
                .get(SettingsViewModel.class);

        initView();
        return rootView;
    }

    private void initView(){
        if(dlgType == TYPE_TIME_DELAY_BEFORE_RECORD){
            tvTitle.setText(getResources().getString(R.string.delay_value_in_seconds));
            tvMsg.setVisibility(View.GONE);
            etInput.setFilters(new InputFilter[]{new InputFilterMinMax(1, ConfigUtil.MAX_DELAY_BEFORE_RECORD_SECONDS)});
            etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE && btOk != null) {
                        btOk.performClick();
                    }
                    return true;
                }
            });
            etInput.setText(PrefsUtil.getTimeDelayBeforeRecording(getActivity()));
            etInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        etInput.clearFocus();
        etInput.requestFocus();
        etInput.selectAll();
        KeybordUtils.toggleSoftInput(etInput);

        btOk.setOnClickListener((v)->{
            onOKClick();
        });

        btCancel.setOnClickListener((v)->{
            this.dismiss();
        });
    }

    private void onOKClick(){
        if (etInput != null) {
            if(dlgType == TYPE_TIME_DELAY_BEFORE_RECORD && !StringUtils.isEmpty(etInput.getText().toString())){
                PrefsUtil.setTimeDelayBeforeRecording(getActivity(), Integer.valueOf(etInput.getText().toString()));
                if(settingsViewModel != null){
                    settingsViewModel.updateTimeDelayBeforeRecording();
                }
            }
        }
        this.dismiss();
    }

    public class InputFilterMinMax implements InputFilter {
        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = 0;
                if(dstart > 0){
                    input = Integer.parseInt(dest.toString() + source.toString());
                } else {
                    input = Integer.parseInt(source.toString());
                }
                if (isInRange(min, max, input)) return null;
            } catch (NumberFormatException nfe) {
            }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}
