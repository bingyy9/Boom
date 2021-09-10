package com.boom.android.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.boom.android.R;
import com.boom.android.log.Dogger;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class MaxLimitRecyclerView extends RecyclerView {
    private int mMaxHeight;
    private static final String TAG = "MaxLimitRecyclerView";
    public MaxLimitRecyclerView(Context context) {
        this(context, null);
    }
    public MaxLimitRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MaxLimitRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }
    private void init(AttributeSet attrs) {
        if (getContext() != null && attrs != null) {
            TypedArray arr = null;
            try {
                arr = getContext().obtainStyledAttributes(attrs, R.styleable.MaxLimitRecyclerView);
                mMaxHeight = arr.getLayoutDimension(R.styleable.MaxLimitRecyclerView_limit_maxHeight, mMaxHeight);
            } catch (Exception e) {
                Dogger.e(Dogger.BOOM, "", "MaxLimitRecyclerView", "init", e);
            } finally {
                if (arr != null) {
                    arr.recycle();
                }
            }
        }
    }
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        heightSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpec);
    }
}
