package com.example.administrator.shiyuji.ui.fragment.report;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.support.action.IAction;
import com.example.administrator.shiyuji.support.bean.AccountBean;
import com.example.administrator.shiyuji.support.permissions.SdcardPermissionAction;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.activity.base.SinaCommonActivity;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.user.ChangeUserIntroduce;
import com.example.administrator.shiyuji.ui.fragment.user.ChangeUserName;
import com.example.administrator.shiyuji.ui.widget.CircleImageView;
import com.example.administrator.shiyuji.ui.widget.MyTextView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.util.accountutil.AccountUtils;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.FragmentArgs;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.example.administrator.shiyuji.util.task.WorkTask;
import com.example.administrator.shiyuji.util.viewutil.InjectUtility;
import com.tencent.qcloud.tim.uikit.modules.contact.ContactListView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.administrator.shiyuji.support.setting.SettingUtility.getSetting;

/**
 * Created by Administrator on 2019/12/27.
 */


public class ReportFragment extends ABaseFragment implements View.OnClickListener{

    public static final int REQUEST_CODE_PHOTO = 1012;
    private final int CHANGE_INTRODUCE = 1001;
    private final int CHANGE_NAME = 1002;

    public static void launch(Activity from, UserInfo userInfo,String row_key,String t) {
        FragmentArgs args = new FragmentArgs();

        args.add("userInfo", userInfo);
        args.add("row_key", row_key);
        args.add("t", t);

        SinaCommonActivity.launch(from, ReportFragment.class, args);
        AisenUtils.changeOpenActivityStyle(from);

    }

    @ViewInject(id = R.id.appbar)
    AppBarLayout appBarLayout;
    @ViewInject(id = R.id.report_user)
    MyTextView report_user;
    @ViewInject(id = R.id.submit)
    Button submit;
    @ViewInject(id = R.id.checkbox1)
    CheckBox checkbox1;
    @ViewInject(id = R.id.checkbox2)
    CheckBox checkbox2;
    @ViewInject(id = R.id.checkbox3)
    CheckBox checkbox3;
    @ViewInject(id = R.id.checkbox4)
    CheckBox checkbox4;
    @ViewInject(id = R.id.checkbox5)
    CheckBox checkbox5;
    @ViewInject(id = R.id.checkbox6)
    CheckBox checkbox6;
    @ViewInject(id = R.id.checkbox7)
    CheckBox checkbox7;
    @ViewInject(id = R.id.checkbox8)
    CheckBox checkbox8;
    @ViewInject(id = R.id.checkbox9)
    CheckBox checkbox9;
    @ViewInject(id = R.id.ic_back)
    ImageView ic_back;

    List<CheckBox> list;
    private String type ;
    private UserInfo userInfo;
    private String row_key;
    private String t;//被举报的类型，动态、约。。。


    @Override
    public int inflateContentView() {
        return -1;
    }

    @Override
    public int inflateActivityContentView() {
        return R.layout.fragment_report;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InjectUtility.initInjectedView(getActivity(), this, ((BaseActivity) getActivity()).getRootView());
        layoutInit(inflater, savedInstanceState);

        String text = null;
        if (t.equals("status")){
            text = "投诉"+"<a href='/n/BanneyZ'>"+"@"+userInfo.getNickname()+"</a>的微博";
        }else if (t.equals("comment")){
            text = "投诉"+"<a href='/n/BanneyZ'>"+"@"+userInfo.getNickname()+"</a>的评论";
        }else if (t.equals("response")){
            text = "投诉"+"<a href='/n/BanneyZ'>"+"@"+userInfo.getNickname()+"</a>的回复";
        }
        if (text!=null){
            report_user.setContent(text);
        }
        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return null;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list = new ArrayList<>();

        userInfo = savedInstanceState != null ? (UserInfo) savedInstanceState.getSerializable("userInfo")
                : (UserInfo) getArguments().getSerializable("userInfo");
        row_key = savedInstanceState != null ? (String) savedInstanceState.getSerializable("row_key")
                : (String) getArguments().getSerializable("row_key");
        t = savedInstanceState != null ? (String) savedInstanceState.getSerializable("t")
                : (String) getArguments().getSerializable("t");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        BaseActivity activity = (BaseActivity) getActivity();
//        ((BaseActivity) getActivity()).getSupportActionBar().setTitle("举报");
//        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setHasOptionsMenu(true);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("userInfo", userInfo);
        outState.putSerializable("row_key", row_key);
        outState.putSerializable("t", t);
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceState) {
        super.layoutInit(inflater, savedInstanceState);

        list.add(checkbox1);
        list.add(checkbox2);
        list.add(checkbox3);
        list.add(checkbox4);
        list.add(checkbox5);
        list.add(checkbox6);
        list.add(checkbox7);
        list.add(checkbox8);
        list.add(checkbox9);

        submit.setOnClickListener(listener);
        for (CheckBox c :list){
            c.setOnCheckedChangeListener(checkListener);

        }

    }



    CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                for (CheckBox c : list){
                    c.setChecked(false);
                }
                buttonView.setChecked(true);
            }
        }
    };

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (check()){
               new ReportTask().execute();
                getActivity().finish();
            }
        }
    };

    private boolean check() {

        for (CheckBox c :list){
            if (c.isChecked()){
                type=c.getText().toString();
                return true;
            }
        }
        Toast.makeText(getContext(),"请选择类型",Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }



    class ReportTask extends WorkTask<Void, Void, String> {

        @Override
        public String workInBackground(Void... params) throws TaskException {

            Params p = new Params();
            p.addParameter("row_key",row_key);
            p.addParameter("userId", String.valueOf(userInfo.getId()));
            p.addParameter("type",type);
            p.addParameter("t",t);
            String result = SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).report(p);

            return result;
        }

        @Override
        protected void onSuccess(String result) {
            super.onSuccess(result);
            if (result.endsWith("ok")){
                Toast.makeText(getContext(),"举报成功",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getContext(),"举报失败",Toast.LENGTH_SHORT).show();

            }


        }
    }

}
