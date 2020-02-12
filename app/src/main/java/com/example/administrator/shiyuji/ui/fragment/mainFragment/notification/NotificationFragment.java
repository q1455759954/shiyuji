package com.example.administrator.shiyuji.ui.fragment.mainFragment.notification;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.sdk.base.ABizLogic;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.fragment.adapter.BasicRecycleViewAdapter;
import com.example.administrator.shiyuji.ui.fragment.bean.PicUrls;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContents;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.home.HomeFragment;
import com.example.administrator.shiyuji.ui.fragment.timeline.ATimelineFragment;
import com.example.administrator.shiyuji.ui.fragment.timeline.TimelineDefFragment;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.jauker.widget.BadgeView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends Fragment {


    private   NotificationDefFragment fragment;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragment = new NotificationDefFragment();
        FragmentManager fm=getChildFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if (!fragment.isAdded()){
            ft.add(R.id.notification_content, fragment).commit();
        }
//        getFragmentManager().beginTransaction().replace(R.id.notification_content, fragment, "NotificationFragment").commit();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            BasicRecycleViewAdapter adapter = (BasicRecycleViewAdapter) fragment.getAdapter();
            adapter.getHeaderView().onHiddenChanged();
        }
    }

}
