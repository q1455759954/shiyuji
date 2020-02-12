package com.example.administrator.shiyuji.ui.fragment.mainFragment.mine;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.ui.activity.user.UserActivity;
import com.example.administrator.shiyuji.ui.fragment.search.user.UserDefFragment;
import com.example.administrator.shiyuji.ui.fragment.timeline.TimelineDefFragment;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.TimelineDetailPagerFragment;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;
import com.example.administrator.shiyuji.ui.widget.CircleImageView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapOwner;

import static android.app.Activity.RESULT_OK;

public class MineFragment extends Fragment implements BitmapOwner {

    CircleImageView user_img;
    TextView textView;
    TextView user_intro;
    TextView attend_txt;
    TextView status_txt;
    TextView fans_txt;
    LinearLayout attend_li;
    LinearLayout fas_li;
    LinearLayout status_li;
    RelativeLayout mine_collection;
    RelativeLayout mine_like;
    RelativeLayout mine_secondUsed;
    RelativeLayout mine_part_time_job;


    public static final String CHANGE_USER_INFO = "com.shiyuji.CHANGE_USER_INFO";

    public MineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        textView = (TextView)getActivity().findViewById(R.id.user_name);
        user_img = (CircleImageView)getActivity().findViewById(R.id.user_img);
        user_intro = (TextView)getActivity().findViewById(R.id.user_intro);
        attend_li = (LinearLayout)getActivity().findViewById(R.id.attend_li);
        fas_li = (LinearLayout)getActivity().findViewById(R.id.fans_li);
        status_li = (LinearLayout)getActivity().findViewById(R.id.status_li);
        attend_txt= (TextView)getActivity().findViewById(R.id.attend_txt);
        fans_txt= (TextView)getActivity().findViewById(R.id.fans_txt);
        mine_collection = (RelativeLayout)getActivity().findViewById(R.id.mine_collection);
        mine_like = (RelativeLayout)getActivity().findViewById(R.id.mine_like);
        mine_secondUsed = (RelativeLayout)getActivity().findViewById(R.id.mine_secondUsed);
        mine_part_time_job = (RelativeLayout)getActivity().findViewById(R.id.mine_part_time_job);
        status_txt= (TextView)getActivity().findViewById(R.id.status_txt);

        String attend = String.valueOf(AppContext.getAccount().getUserInfo().getAttends());
        String fans = String.valueOf(AppContext.getAccount().getUserInfo().getFans());
        String statusNum =  String.valueOf(AppContext.getAccount().getUserInfo().getStatus());
        attend_txt.setText(attend);
        fans_txt.setText(fans);
        status_txt.setText(statusNum);

        fas_li.setOnClickListener(fanListener);
        attend_li.setOnClickListener(attendListener);
        mine_collection.setOnClickListener(mine_collectionListener);
        mine_like.setOnClickListener(mine_likeListener);
        status_li.setOnClickListener(userListener);
        mine_secondUsed.setOnClickListener(userListener);
        mine_part_time_job.setOnClickListener(userListener);

        String headUrl = AppContext.getAccount().getUserInfo().getAvatar();
        String name = AppContext.getAccount().getUserInfo().getNickname();
        textView.setText(name);
        String intro = "简介："+AppContext.getAccount().getUserInfo().getIntroduce();
        user_intro.setText(intro);
        BitmapLoader.getInstance().display(this,headUrl, user_img, ImageConfigUtils.getLargePhotoConfig());
        user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserActivity.class);
                intent.putExtra("userInfo",AppContext.getAccount().getUserInfo());
                intent.putExtra("flag",true);
                getActivity().startActivity(intent);
            }
        });
    }

    View.OnClickListener mine_likeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimelineDefFragment.launch("getMyLike",getActivity());
        }
    };

    View.OnClickListener mine_collectionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimelineDefFragment.launch("getCollection",getActivity());
        }
    };

    View.OnClickListener attendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserDefFragment.launch("attends",getActivity());
        }
    };

    View.OnClickListener fanListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserDefFragment.launch("fans",getActivity());
        }
    };

    View.OnClickListener userListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), UserActivity.class);
            intent.putExtra("userInfo",AppContext.getAccount().getUserInfo());
            intent.putExtra("flag",true);
            getActivity().startActivityForResult(intent,1);
        }
    };

    public void changeInfo(){
        String headUrl = AppContext.getAccount().getUserInfo().getAvatar();
        String name = AppContext.getAccount().getUserInfo().getNickname();
        textView.setText(name);
        String attend = String.valueOf(AppContext.getAccount().getUserInfo().getAttends());
        String fans = String.valueOf(AppContext.getAccount().getUserInfo().getFans());
        String statusNum =  String.valueOf(AppContext.getAccount().getUserInfo().getStatus());
        attend_txt.setText(attend);
        fans_txt.setText(fans);
        status_txt.setText(statusNum);

        String intro = "简介："+AppContext.getAccount().getUserInfo().getIntroduce();
        user_intro.setText(intro);

        BitmapLoader.getInstance().display(this,headUrl, user_img, ImageConfigUtils.getLargePhotoConfig());
        user_img.setOnClickListener(userListener);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
           changeInfo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean canDisplay() {
        return true;
    }

    public static Fragment newInstance() {
        return new MineFragment();
    }

    /**
     * 发布完成后发布广播，这里接受广播进行更新
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent != null ? intent.getAction() : "";

            if (CHANGE_USER_INFO.equals(action) ) {
                changeInfo();
            }
        }
    };

}
