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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.boom.android.BoomApplication;
import com.boom.android.R;
import com.boom.android.log.Dogger;
import com.boom.android.ui.adapter.repo.SingleSelectBean;
import com.boom.android.ui.adapter.repo.SingleSelectRecycleAdapter;
import com.boom.android.ui.videos.WrapContentLinearLayoutManager;
import com.boom.android.ui.view.RecycleViewDecoration;
import com.boom.android.util.ConfigUtil;
import com.boom.android.util.KeybordUtils;
import com.boom.android.util.PrefsUtil;
import com.boom.android.viewmodel.SettingsViewModel;
import com.boom.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class SingleSelectDialog extends AppDialogFragment implements SingleSelectRecycleAdapter.Listener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;
    @BindView(R.id.btn1)
    View cancelButton;

    SettingsViewModel settingsViewModel;
    SingleSelectRecycleAdapter adapter;
    int checkedIndex;

    public static DialogFragment newInstance(int type) {
        DialogFragment fragment = new SingleSelectDialog();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            type = getArguments().getInt("type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        View rootView = inflater.inflate(R.layout.dlg_single_select, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ButterKnife.bind(this, rootView);
        settingsViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.AndroidViewModelFactory(BoomApplication.getInstance()))
                .get(SettingsViewModel.class);

        initView();
        initList();
        return rootView;
    }

    private void initView(){
        if(type == TYPE_FILE_NAME_FORMAT_SELECT){
            tvTitle.setText(getResources().getString(R.string.file_name_format));
        }

        cancelButton.setOnClickListener((v)->{
            this.dismiss();
        });
    }

    private void initList(){
        if(recyclerView == null){
            Dogger.i(Dogger.BOOM, "recyclerView is null", "SingleSelectDialog", "initList");
            return;
        }

        adapter = new SingleSelectRecycleAdapter(getActivity());
        adapter.setListener(this);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecycleViewDecoration(getActivity(), RecycleViewDecoration.VERTICAL_LIST));
        recyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }
            @Override
            public void onViewDetachedFromWindow(View v) {
                if(recyclerView != null) {
                    recyclerView.clearFocus();
                }
            }
        });

        //the first time need to build data in UI thread, otherwise the UI will flash issue
        updateView();

    }

    private List<SingleSelectBean> buildListData() {
        if(ConfigUtil.fileNameFormat == null || ConfigUtil.fileNameFormat.size() == 0){
            return null;
        }
        List<SingleSelectBean> beans = new ArrayList<>();
        for(int i = 0; i<ConfigUtil.fileNameFormat.size(); i++){
            boolean checked = StringUtils.contentEquals(ConfigUtil.fileNameFormat.get(i), PrefsUtil.getFileNameFormat(getActivity()));
            beans.add(new SingleSelectBean(ConfigUtil.fileNameFormat.get(i), checked));
            if(checked){
                checkedIndex = i;
            }
        }
        return beans;
    }

    private void updateView(){
        List<SingleSelectBean> languages = buildListData();
        if(adapter != null && adapter.getDataList() != null && languages != null) {
            adapter.getDataList().clear();
            adapter.getDataList().addAll(languages);
            adapter.notifyDataSetChanged();
        }

        if(checkedIndex > 8){
            recyclerView.scrollToPosition(checkedIndex - 1);
        } else {
            //first screen can display, needn't scroll
        }
    }

    @Override
    public void onItemSelected(SingleSelectBean bean) {
        this.dismiss();
    }
}
