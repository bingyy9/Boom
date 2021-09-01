package com.boom.android.ui.videos;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.boom.android.R;
import com.boom.android.log.Dogger;
import com.boom.android.util.BoomHelper;
import com.boom.model.interf.IRecordModel;
import com.boom.model.interf.impl.ModelBuilderManager;

import java.io.File;

import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoDetailFragment extends DialogFragment {
    public static final String FILENAME = "FILE_NAME";
    public static final String TAG = "VideoDetailFragment";
    private IRecordModel recordModel;

    View root;
    @BindView(R.id.i_frame)
    ImageView iFrame;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_last_modified_time)
    TextView tvLastModified;
    @BindView(R.id.share_video)
    TextView shareVideo;

    String fileName;

    public static DialogFragment newInstance(String filename) {
        Bundle args = new Bundle();
        args.putString(VideoDetailFragment.FILENAME, filename);
        DialogFragment fragment = new VideoDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.NewDialogFullScreen);
        recordModel = ModelBuilderManager.getModelBuilder().getRecordModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.video_details, container);
        Window window = getDialog().getWindow();
        ButterKnife.bind(this,root);
        if(getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
        }
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getDialog().setTitle(getResources().getString(R.string.video));
        initView();
        return root;
    }

    private void initView() {
        fileName = getArguments().getString(FILENAME);
        File file = new File(BoomHelper.getRecordDirectory() + fileName);
        Dogger.i(Dogger.BOOM, "file----" + file.getAbsolutePath(), "VideoDetailFragment", "initView");

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                window.setLayout(width, height);
            }
        }
    }
}
