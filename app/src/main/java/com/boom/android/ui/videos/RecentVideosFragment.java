package com.boom.android.ui.videos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boom.android.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RecentVideosFragment extends Fragment {
    public static RecentVideosFragment newInstance(String label) {
        Bundle args = new Bundle();
        args.putString("label", label);
        RecentVideosFragment fragment = new RecentVideosFragment();
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
