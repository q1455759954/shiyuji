package com.example.administrator.shiyuji.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.example.administrator.shiyuji.R;

/**
 * Layout that draws a dimmed overlay.
 */
public class DimOverlayFrameLayout extends FrameLayout {

    public DimOverlayFrameLayout(Context context) {
        super(context);
        init();
    }

    public DimOverlayFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DimOverlayFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.sheetfab_dim_overlay, this);
    }
}
