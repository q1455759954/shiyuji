package com.example.administrator.shiyuji.ui.fragment.menu;

/**
 * Created by Administrator on 2019/6/29.
 */


public class CardMenuOptions {
    int dropDownGravity = -1;
    int dropDownHorizontalOffset;
    int dropDownVerticalOffset;
    int themeRes;
    int popupStyleAttr;
    int actionMenuLayoutRes;
    int actionMenuItemLayoutRes;

    public CardMenuOptions(int themeRes, int popupStyleAttr, int actionMenuLayoutRes, int actionMenuItemLayoutRes) {
        this.themeRes = themeRes;
        this.popupStyleAttr = popupStyleAttr;
        this.actionMenuLayoutRes = actionMenuLayoutRes;
        this.actionMenuItemLayoutRes = actionMenuItemLayoutRes;
    }

    public CardMenuOptions setGravity(int gravity) {
        this.dropDownGravity = gravity;
        return this;
    }

    public CardMenuOptions setDropDownHorizontalOffset(int offset) {
        this.dropDownHorizontalOffset = offset;
        return this;
    }

    public CardMenuOptions setDropDownVerticalOffset(int offset) {
        this.dropDownVerticalOffset = offset;
        return this;
    }
}
