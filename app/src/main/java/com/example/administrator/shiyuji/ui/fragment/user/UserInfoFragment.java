package com.example.administrator.shiyuji.ui.fragment.user;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.home.HomeFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.mine.MineFragment;
import com.example.administrator.shiyuji.ui.widget.CircleImageView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;
import com.example.administrator.shiyuji.util.viewutil.InjectUtility;

/**
 * Created by Administrator on 2019/9/12.
 */

public class UserInfoFragment extends Fragment {

    @ViewInject(id = R.id.gender)
    TextView gender;
    @ViewInject(id = R.id.age)
    TextView age;
    @ViewInject(id = R.id.address)
    TextView address;
    @ViewInject(id = R.id.school)
    TextView school;
    @ViewInject(id = R.id.introduce)
    TextView introduce;
    @ViewInject(id = R.id.name)
    TextView name;

    private View rootView;

    public static UserInfoFragment newInstance() {
        UserInfoFragment fragment = new UserInfoFragment();
//        Bundle args = new Bundle();
//
//        args.("userInfo", userInfo);
//        fragment.setArguments(args);
        return fragment;
    }

    public UserInfoFragment() {
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
        rootView = inflater.inflate(R.layout.item_fragment_user, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InjectUtility.initInjectedView(this.getActivity(),this,rootView);
        Bundle bundle = getActivity().getIntent().getExtras();
        UserInfo userInfo = (UserInfo) bundle.getSerializable("userInfo");
        name.setText("昵称 ：" + userInfo.getNickname());
        age.setText("年龄 ：" + String.valueOf(userInfo.getAge()));
        gender.setText("性别 ：" + userInfo.getGender());
        school.setText("大学 ：" + userInfo.getSchool());
        introduce.setText(userInfo.getIntroduce());
    }

}
