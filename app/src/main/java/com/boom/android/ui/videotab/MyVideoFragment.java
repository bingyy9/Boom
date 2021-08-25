package com.boom.android.ui.videotab;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boom.android.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MyVideoFragment extends Fragment {
    public static MyVideoFragment newInstance(String label) {
        Bundle args = new Bundle();
        args.putString("label", label);
        MyVideoFragment fragment = new MyVideoFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_video_tab, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        String label = getArguments().getString("label");
        TextView text = getView().findViewById(R.id.tv_bg);
        text.setText(label);
    }
}
