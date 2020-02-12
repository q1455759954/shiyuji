package com.example.administrator.shiyuji.ui.fragment.menu;

import android.app.Activity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/**
 * Created by Administrator on 2019/6/29.
 */


public class CardMenuBuilder {
    private final Activity mContext;
    private final CardMenuPresenter mPresenter;
    private final MenuBuilder mMenuBuilder;
    private final CardMenuOptions options;
    private CardMenuBuilder.OnCardMenuCallback onCardMenuCallback;
    private MenuItem.OnMenuItemClickListener onMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
        public boolean onMenuItemClick(MenuItem item) {
            return CardMenuBuilder.this.onCardMenuCallback != null?CardMenuBuilder.this.onCardMenuCallback.onCardMenuItemSelected(item):false;
        }
    };

    public CardMenuBuilder(Activity context, View anchorView, CardMenuOptions options) {
        this.mContext = context;
        this.options = options;
        this.mMenuBuilder = new MenuBuilder(context);
        this.mPresenter = new CardMenuPresenter(context, anchorView, this, options);
        this.mMenuBuilder.addMenuPresenter(this.mPresenter, new ContextThemeWrapper(context, options.themeRes));
    }

    public MenuItem add(int group, int id, int categoryOrder, CharSequence title) {
        MenuItem item = this.mMenuBuilder.add(group, id, categoryOrder, title);
        item.setOnMenuItemClickListener(this.onMenuItemClickListener);
        return item;
    }

    public SubMenu addSubMenu(int group, int id, int categoryOrder, CharSequence title) {
        SubMenu subMenu = this.mMenuBuilder.addSubMenu(group, id, categoryOrder, title);
        if(subMenu.getItem() instanceof MenuItemImpl) {
            subMenu.getItem().setOnMenuItemClickListener(this.onMenuItemClickListener);
        }

        return subMenu;
    }

    public MenuItem addSubMenuItem(SubMenu subMenu, int groupId, int itemId, int order, CharSequence title) {
        MenuItem item = subMenu.add(groupId, itemId, order, title);
        item.setOnMenuItemClickListener(this.onMenuItemClickListener);
        return item;
    }

    public CardMenuBuilder inflate(int menuRes) {
        this.mContext.getMenuInflater().inflate(menuRes, this.mMenuBuilder);
        return this;
    }

    public CardMenuBuilder add(int id, int titleRes) {
        this.add(1, id, 1, this.mContext.getString(titleRes));
        return this;
    }

    public CardMenuBuilder add(int id, String title) {
        this.add(1, id, 1, title);
        return this;
    }

    public CardMenuBuilder setOnCardMenuCallback(CardMenuBuilder.OnCardMenuCallback onCardMenuCallback) {
        this.onCardMenuCallback = onCardMenuCallback;
        return this;
    }

    public void show() {
        this.mPresenter.showCardMenu();
    }

    public CardMenuOptions getOptions() {
        return this.options;
    }

    public interface OnCardMenuCallback {
        boolean onCardMenuItemSelected(MenuItem var1);
    }
}
