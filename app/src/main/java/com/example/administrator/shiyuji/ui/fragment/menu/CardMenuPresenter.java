package com.example.administrator.shiyuji.ui.fragment.menu;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.view.menu.BaseMenuPresenter;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.menu.SubMenuBuilder;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by Administrator on 2019/6/29.
 */


public class CardMenuPresenter extends BaseMenuPresenter {
    private CardMenuPresenter.OpenOverflowRunnable mPostedOpenRunnable;
    private CardMenuPresenter.OverflowPopup mOverflowPopup;
    private CardMenuPresenter.ActionButtonSubmenu mActionButtonPopup;
    private View anchorView;
    int mOpenSubMenuId;
    CardMenuBuilder cardMenuBuilder;
    private final int popupStyleAttr;

    public CardMenuPresenter(Context context, View anchorView, CardMenuBuilder cardMenuBuilder, CardMenuOptions options) {
        super(context, options.actionMenuLayoutRes, options.actionMenuItemLayoutRes);
        this.anchorView = anchorView;
        this.cardMenuBuilder = cardMenuBuilder;
        this.popupStyleAttr = options.popupStyleAttr;
    }

    public void bindItemView(MenuItemImpl item, MenuView.ItemView itemView) {
    }

    public boolean onSubMenuSelected(SubMenuBuilder subMenu) {
        if(!subMenu.hasVisibleItems()) {
            return false;
        } else {
            for(SubMenuBuilder topSubMenu = subMenu; topSubMenu.getParentMenu() != this.mMenu; topSubMenu = (SubMenuBuilder)topSubMenu.getParentMenu()) {
                ;
            }

            this.mOpenSubMenuId = subMenu.getItem().getItemId();
            boolean preserveIconSpacing = false;
            int count = subMenu.size();

            for(int i = 0; i < count; ++i) {
                MenuItem childItem = subMenu.getItem(i);
                if(childItem.isVisible() && childItem.getIcon() != null) {
                    preserveIconSpacing = true;
                    break;
                }
            }

            subMenu.addMenuPresenter(this, this.mContext);
            this.mActionButtonPopup = new CardMenuPresenter.ActionButtonSubmenu(this.mContext, subMenu, this.anchorView);
            this.mActionButtonPopup.setForceShowIcon(preserveIconSpacing);
            this.mActionButtonPopup.show();
            super.onSubMenuSelected(subMenu);
            return true;
        }
    }

    public Parcelable onSaveInstanceState() {
        CardMenuPresenter.SavedState state = new CardMenuPresenter.SavedState();
        state.openSubMenuId = this.mOpenSubMenuId;
        return state;
    }

    public void onRestoreInstanceState(Parcelable state) {
        CardMenuPresenter.SavedState saved = (CardMenuPresenter.SavedState)state;
        if(saved.openSubMenuId > 0) {
            MenuItem item = this.mMenu.findItem(saved.openSubMenuId);
            if(item != null) {
                SubMenuBuilder subMenu = (SubMenuBuilder)item.getSubMenu();
                this.onSubMenuSelected(subMenu);
            }
        }

    }

    public boolean showCardMenu() {
        if(!this.isOverflowMenuShowing() && this.mMenu != null && this.mPostedOpenRunnable == null && !this.mMenu.getNonActionItems().isEmpty()) {
            CardMenuPresenter.OverflowPopup popup = new CardMenuPresenter.OverflowPopup(this.mContext, this.mMenu, this.anchorView, true, this.popupStyleAttr);
            this.mPostedOpenRunnable = new CardMenuPresenter.OpenOverflowRunnable(popup);
            this.anchorView.post(this.mPostedOpenRunnable);
            super.onSubMenuSelected((SubMenuBuilder)null);
            return true;
        } else {
            return false;
        }
    }

    public boolean isOverflowMenuShowing() {
        return this.mOverflowPopup != null && this.mOverflowPopup.isShowing();
    }

    public boolean hideOverflowMenu() {
        if(this.mPostedOpenRunnable != null && this.mMenuView != null) {
            ((View)this.mMenuView).removeCallbacks(this.mPostedOpenRunnable);
            this.mPostedOpenRunnable = null;
            return true;
        } else {
            CardMenuPresenter.OverflowPopup popup = this.mOverflowPopup;
            if(popup != null) {
                popup.dismiss();
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean dismissPopupMenus() {
        boolean result = this.hideOverflowMenu();
        result |= this.hideSubMenus();
        return result;
    }

    public boolean hideSubMenus() {
        if(this.mActionButtonPopup != null) {
            this.mActionButtonPopup.dismiss();
            return true;
        } else {
            return false;
        }
    }

    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        this.dismissPopupMenus();
        super.onCloseMenu(menu, allMenusAreClosing);
    }

    private static class SavedState implements Parcelable {
        public int openSubMenuId;
        public static final Creator<CardMenuPresenter.SavedState> CREATOR = new Creator() {
            public CardMenuPresenter.SavedState createFromParcel(Parcel in) {
                return new CardMenuPresenter.SavedState(in);
            }

            public CardMenuPresenter.SavedState[] newArray(int size) {
                return new CardMenuPresenter.SavedState[size];
            }
        };

        SavedState() {
        }

        SavedState(Parcel in) {
            this.openSubMenuId = in.readInt();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.openSubMenuId);
        }
    }

    private class OpenOverflowRunnable implements Runnable {
        private CardMenuPresenter.OverflowPopup mPopup;

        public OpenOverflowRunnable(CardMenuPresenter.OverflowPopup popup) {
            this.mPopup = popup;
        }

        public void run() {
            CardMenuPresenter.this.mMenu.changeMenuMode();
            View menuView = CardMenuPresenter.this.anchorView;
            if(menuView != null && menuView.getWindowToken() != null && this.mPopup.tryShow()) {
                CardMenuPresenter.this.mOverflowPopup = this.mPopup;
            }

            CardMenuPresenter.this.mPostedOpenRunnable = null;
        }
    }

    private class OverflowPopup extends MenuPopupHelper {
        OverflowPopup(Context context, MenuBuilder menu, View anchorView, boolean overflowOnly, int popupStyleAttr) {
            super(context, menu, anchorView, overflowOnly, popupStyleAttr);
            if(CardMenuPresenter.this.cardMenuBuilder.getOptions().dropDownGravity != -1) {
                this.setGravity(CardMenuPresenter.this.cardMenuBuilder.getOptions().dropDownGravity);
            } else {
                this.setGravity(8388613);
            }

        }

        public void onDismiss() {
            super.onDismiss();
            if(CardMenuPresenter.this.mMenu != null) {
                CardMenuPresenter.this.mMenu.close();
            }

            CardMenuPresenter.this.mOverflowPopup = null;
        }
    }

    private class ActionButtonSubmenu extends MenuPopupHelper {
        public ActionButtonSubmenu(Context context, SubMenuBuilder subMenu, View anchorView) {
            super(context, subMenu, anchorView, false, CardMenuPresenter.this.popupStyleAttr);
            if(CardMenuPresenter.this.cardMenuBuilder.getOptions().dropDownGravity != -1) {
                this.setGravity(CardMenuPresenter.this.cardMenuBuilder.getOptions().dropDownGravity);
            } else {
                this.setGravity(8388613);
            }

        }

        public void onDismiss() {
            CardMenuPresenter.this.mActionButtonPopup = null;
            CardMenuPresenter.this.mOpenSubMenuId = 0;
            super.onDismiss();
        }
    }
}

