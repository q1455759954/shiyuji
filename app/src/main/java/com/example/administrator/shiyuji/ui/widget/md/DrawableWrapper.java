package com.example.administrator.shiyuji.ui.widget.md;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2019/7/4.
 */

class DrawableWrapper extends Drawable implements Drawable.Callback {
    static final PorterDuff.Mode DEFAULT_MODE;
    private ColorStateList mTintList;
    private PorterDuff.Mode mTintMode;
    private int mCurrentColor;
    Drawable mDrawable;

    DrawableWrapper(Drawable drawable) {
        this.mTintMode = DEFAULT_MODE;
        this.mCurrentColor = -2147483648;
        this.setWrappedDrawable(drawable);
    }

    public void draw(Canvas canvas) {
        this.mDrawable.draw(canvas);
    }

    protected void onBoundsChange(Rect bounds) {
        this.mDrawable.setBounds(bounds);
    }

    public void setChangingConfigurations(int configs) {
        this.mDrawable.setChangingConfigurations(configs);
    }

    public int getChangingConfigurations() {
        return this.mDrawable.getChangingConfigurations();
    }

    public void setDither(boolean dither) {
        this.mDrawable.setDither(dither);
    }

    public void setFilterBitmap(boolean filter) {
        this.mDrawable.setFilterBitmap(filter);
    }

    public void setAlpha(int alpha) {
        this.mDrawable.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter cf) {
        this.mDrawable.setColorFilter(cf);
    }

    public boolean isStateful() {
        return this.mTintList != null && this.mTintList.isStateful() || this.mDrawable.isStateful();
    }

    public boolean setState(int[] stateSet) {
        boolean handled = this.mDrawable.setState(stateSet);
        handled = this.updateTint(stateSet) || handled;
        return handled;
    }

    public int[] getState() {
        return this.mDrawable.getState();
    }

    public Drawable getCurrent() {
        return this.mDrawable.getCurrent();
    }

    public boolean setVisible(boolean visible, boolean restart) {
        return super.setVisible(visible, restart) || this.mDrawable.setVisible(visible, restart);
    }

    public int getOpacity() {
        return this.mDrawable.getOpacity();
    }

    public Region getTransparentRegion() {
        return this.mDrawable.getTransparentRegion();
    }

    public int getIntrinsicWidth() {
        return this.mDrawable.getIntrinsicWidth();
    }

    public int getIntrinsicHeight() {
        return this.mDrawable.getIntrinsicHeight();
    }

    public int getMinimumWidth() {
        return this.mDrawable.getMinimumWidth();
    }

    public int getMinimumHeight() {
        return this.mDrawable.getMinimumHeight();
    }

    public boolean getPadding(Rect padding) {
        return this.mDrawable.getPadding(padding);
    }

    public Drawable mutate() {
        Drawable wrapped = this.mDrawable;
        Drawable mutated = wrapped.mutate();
        if(mutated != wrapped) {
            this.setWrappedDrawable(mutated);
        }

        return this;
    }

    public void invalidateDrawable(Drawable who) {
        this.invalidateSelf();
    }

    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        this.scheduleSelf(what, when);
    }

    public void unscheduleDrawable(Drawable who, Runnable what) {
        this.unscheduleSelf(what);
    }

    protected boolean onLevelChange(int level) {
        return this.mDrawable.setLevel(level);
    }

    public void setTint(int tint) {
        this.setTintList(ColorStateList.valueOf(tint));
    }

    public void setTintList(ColorStateList tint) {
        this.mTintList = tint;
        this.updateTint(this.getState());
    }

    public void setTintMode(PorterDuff.Mode tintMode) {
        this.mTintMode = tintMode;
        this.updateTint(this.getState());
    }

    private boolean updateTint(int[] state) {
        if(this.mTintList != null && this.mTintMode != null) {
            int color = this.mTintList.getColorForState(state, this.mTintList.getDefaultColor());
            if(color != this.mCurrentColor) {
                this.setColorFilter(color, this.mTintMode);
                this.mCurrentColor = color;
                return true;
            }
        }

        return false;
    }

    public Drawable getWrappedDrawable() {
        return this.mDrawable;
    }

    public void setWrappedDrawable(Drawable drawable) {
        if(this.mDrawable != null) {
            this.mDrawable.setCallback((Callback)null);
        }

        this.mDrawable = drawable;
        if(drawable != null) {
            drawable.setCallback(this);
        }

        this.invalidateSelf();
    }

    static {
        DEFAULT_MODE = PorterDuff.Mode.SRC_IN;
    }
}
