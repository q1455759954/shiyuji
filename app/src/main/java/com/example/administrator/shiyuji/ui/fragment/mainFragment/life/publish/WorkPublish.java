package com.example.administrator.shiyuji.ui.fragment.mainFragment.life.publish;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.service.PublishService;
import com.example.administrator.shiyuji.support.bean.PublishBean;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.activity.base.SinaCommonActivity;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.WorkInfo;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.FragmentArgs;
import com.example.administrator.shiyuji.util.viewutil.InjectUtility;

import java.util.Calendar;

/**
 * Created by Administrator on 2019/12/23.
 */

public class WorkPublish extends ABaseFragment implements View.OnClickListener{


    public static void launch(Activity from) {
        FragmentArgs args = new FragmentArgs();

        SinaCommonActivity.launch(from, WorkPublish.class, args);
        AisenUtils.changeOpenActivityStyle(from);

    }

    public static final int MAX_Title_LENGTH = 15;
    public static final int MAX_Content_LENGTH = 100;

    WorkInfo info;

    @ViewInject(id = R.id.ic_back)
    ImageView ic_back;
    @ViewInject(id = R.id.titleErrorHint)
    TextView titleErrorHint;
    @ViewInject(id = R.id.contentErrorHint)
    TextView contentErrorHint;
    @ViewInject(id = R.id.event_title)
    EditText event_title;
    @ViewInject(id = R.id.event_content)
    EditText event_content;
    @ViewInject(id = R.id.release_event)
    TextView release_event;



    Calendar calendar = Calendar.getInstance();
    private Bitmap bitmap;
    private boolean ifChangedAvatar = false;

    @Override
    public int inflateContentView() {
        return -1;
    }

    @Override
    public int inflateActivityContentView() {
        return R.layout.ui_life_publish_work;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InjectUtility.initInjectedView(getActivity(), this, ((BaseActivity) getActivity()).getRootView());
        layoutInit(inflater, savedInstanceState);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        event_title.addTextChangedListener(editTitleWatcher);
        event_content.addTextChangedListener(editContentWatcher);
        release_event.setOnClickListener(release_listener);
        return null;
    }

    View.OnClickListener release_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (titleErrorHint.getVisibility() == View.VISIBLE ) {
                showMessage(titleErrorHint.getText().toString());
                return;
            }else if (contentErrorHint.getVisibility()==View.VISIBLE){
                showMessage(contentErrorHint.getText().toString());
                return;
            }

            if (checkValid())
                send();
        }
    };

    /**
     * 发布
     */
    private void send() {
        info = new WorkInfo();
        info.setTitle(event_title.getText().toString());
        info.setContent(event_content.getText().toString());
        info.setCreate_at(String.valueOf(System.currentTimeMillis()));

        info.setUserInfo(AppContext.getAccount().getUserInfo());
        PublishBean bean = new PublishBean();
        bean.setPublishType(PublishBean.PublishType.life);
        LifeInfo lifeInfo = new LifeInfo();
        lifeInfo.setWorkInfo(info);
        bean.setLifeInfo(lifeInfo);
        PublishService.publish(getActivity(),bean);

        getActivity().finish();
    }

    /**
     * 检查输入是否为空
     * @return
     */
    boolean checkValid() {
        String title = event_title.getText().toString();

        if (TextUtils.isEmpty(title)) {
            showMessage(R.string.error_none_status);
            return false;
        }

        String content = event_content.getText().toString();

        if (TextUtils.isEmpty(content)) {
            showMessage(R.string.error_none_status);
            return false;
        }

        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        BaseActivity activity = (BaseActivity) getActivity();
//        ((BaseActivity) getActivity()).getSupportActionBar().setTitle("活动详情");
//        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        setHasOptionsMenu(true);
    }


    /**
     * 标题内容监听，刷新提示信息
     */
    private TextWatcher editTitleWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 设置长度提示
            String content = event_title.getText().toString() + appendContent();

            if (AisenUtils.getStrLength(content) > MAX_Title_LENGTH) {
                titleErrorHint.setVisibility(View.VISIBLE);
                titleErrorHint.setText(String.format(getString(R.string.error_length_too_long), AisenUtils.getStrLength(content) - MAX_Title_LENGTH));
            }
            else {
                titleErrorHint.setVisibility(View.GONE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    };

    /**
     * 内容监听，刷新提示信息
     */
    private TextWatcher editContentWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 设置长度提示
            String content = event_content.getText().toString() + appendContent();

            if (AisenUtils.getStrLength(content) > MAX_Content_LENGTH) {
                contentErrorHint.setVisibility(View.VISIBLE);
                contentErrorHint.setText(String.format(getString(R.string.error_length_too_long), AisenUtils.getStrLength(content) - MAX_Content_LENGTH));
            }
            else {
                contentErrorHint.setVisibility(View.GONE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    };

    protected String appendContent() {
        return "";
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceState) {
        super.layoutInit(inflater, savedInstanceState);
    }

    @Override
    public void onClick(View v) {

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