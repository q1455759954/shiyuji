package com.example.administrator.shiyuji.ui.fragment.user;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.support.action.IAction;
import com.example.administrator.shiyuji.support.permissions.SdcardPermissionAction;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.activity.base.SinaCommonActivity;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.widget.AsToolbar;
import com.example.administrator.shiyuji.ui.widget.CircleImageView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.FragmentArgs;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;
import com.example.administrator.shiyuji.util.viewutil.InjectUtility;

import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2019/9/13.
 */

public class ChangeUserIntroduce extends ABaseFragment {


    public static void launch(ChangeUserInfoFragment from, UserInfo userInfo, int CHANGE_INTRODUCE) {
        FragmentArgs args = new FragmentArgs();

        args.add("userInfo", userInfo);

        SinaCommonActivity.launchForResult(from, ChangeUserIntroduce.class, args,CHANGE_INTRODUCE);
        AisenUtils.changeOpenActivityStyle(from.getActivity());

    }
    @ViewInject(id = R.id.toolbar)
    AsToolbar toolbar;
    @ViewInject(id = R.id.editText)
    EditText editText;
    @ViewInject(id = R.id.done)
    TextView done;

    private UserInfo userInfo;

    @Override
    public int inflateContentView() {
        return -1;
    }

    @Override
    public int inflateActivityContentView() {
        return R.layout.item_fragment_user_edit;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InjectUtility.initInjectedView(getActivity(), this, ((BaseActivity) getActivity()).getRootView());
        layoutInit(inflater, savedInstanceState);

        return null;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userInfo = savedInstanceState != null ? (UserInfo) savedInstanceState.getSerializable("userInfo")
                : (UserInfo) getArguments().getSerializable("userInfo");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        editText.setBackground(null);
        editText.setMaxLines(3);

        BaseActivity activity = (BaseActivity) getActivity();

        ((BaseActivity) getActivity()).getSupportActionBar().setTitle("修改简介");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.top_titlebar));
        setHasOptionsMenu(true);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("userInfo", userInfo);
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceState) {
        super.layoutInit(inflater, savedInstanceState);

        String introduce = userInfo.getIntroduce();
        if (introduce!=null){
            editText.setText(introduce);
            editText.setSelection(introduce.length());//设置光标位置

        }

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=getActivity().getIntent();
                intent.putExtra("introduceIntent",editText.getText().toString());
                getActivity().setResult(RESULT_OK,intent);
                getActivity().finish();
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }



}
