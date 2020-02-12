package com.example.administrator.shiyuji.ui.fragment.timelineResponse;


import android.app.Activity;
import android.os.Bundle;

import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.activity.base.SinaCommonActivity;
import com.example.administrator.shiyuji.ui.fragment.bean.PicUrls;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusComment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusComments;

import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.util.common.FragmentArgs;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;

import java.util.ArrayList;
import java.util.List;

public class TimelineResponsePagerFragment extends ATimelineResponsePagerFragment {



    public static void launch(Activity from, StatusComment statusComment) {
        FragmentArgs args = new FragmentArgs();

        args.add("statusComment", statusComment);

        SinaCommonActivity.launch(from, TimelineResponsePagerFragment.class, args);
    }

    public static TimelineResponsePagerFragment newInstance(String method) {
        TimelineResponsePagerFragment fragment = new TimelineResponsePagerFragment();

        Bundle args = new Bundle();
        args.putString("method", method);
        fragment.setArguments(args);

        return fragment;
    }

    public TimelineResponsePagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BaseActivity activity = (BaseActivity) getActivity();
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle("回复");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void requestData(RefreshMode mode) {
        new TimelineResponsePagerFragment.DefResponseTask(mode).execute();
    }


    class DefResponseTask extends AResponseTask {

        public DefResponseTask(RefreshMode mode) {
            super(mode);
        }


        @Override
        public StatusComments getStatusComments(Params params) throws TaskException {

            PicUrls picUrls1 = new PicUrls();

            picUrls1.setThumbnail_pic("http://wx2.sinaimg.cn/large/723dd72fly1g46v07dvajj20zl1r01l1.jpg");

            StatusComments s = new StatusComments();
            List<StatusComment> list = new ArrayList<>();
            list.add(ATimelineResponsePagerFragment.mStatusComment);
            s.setComments(list);

            return s;
//            throw new TaskException(TaskException.TaskError.resultIllegal.toString());
        }
//            http://wx2.sinaimg.cn/large/723dd72fly1g46v07dvajj20zl1r01l1.jpg


    }

}
