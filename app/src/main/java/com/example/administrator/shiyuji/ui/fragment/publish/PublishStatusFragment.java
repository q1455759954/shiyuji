package com.example.administrator.shiyuji.ui.fragment.publish;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.service.PublishService;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.widget.MyTextView;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.support.bean.PublishBean;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.example.administrator.shiyuji.util.task.WorkTask;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2019/7/7.
 */

public class PublishStatusFragment extends APublishFragment {

    public static ABaseFragment newInstance(PublishBean bean) {
        PublishStatusFragment fragment = new PublishStatusFragment();
        Bundle args = new Bundle();

        if (bean != null) {
            args.putSerializable("bean", bean);
            fragment.setArguments(args);
        }

        return fragment;
    }

    @ViewInject(id = R.id.txtGroupHint)
    TextView txtGroupHint;
    @ViewInject(id = R.id.txtTiming)
    TextView txtTiming;



    @Override
    public int inflateContentView() {
        return R.layout.ui_publish_status;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);


        ((BaseActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_new_status);

        editContent.setHint(R.string.title_status_def);

        setVisiableHint();


        refreshUI();

        setHasOptionsMenu(true);
    }


    private void setVisiableHint() {

    }

    @Override
    PublishBean newPublishBean() {
        PublishBean bean = new PublishBean();
        return bean;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_publish, menu);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    boolean checkValid(PublishBean bean) {
        String content = editContent.getText().toString();

        if (TextUtils.isEmpty(content)) {
            showMessage(R.string.error_none_status);
            return false;
        }

        return true;
    }

    @Override
    protected void send() {
        super.send();
        PublishBean bean = getPublishBean();
        bean.setPublishType(PublishBean.PublishType.status);
        bean.getStatusContent().setRow_key(bean.getRe_rowKey());
        PublishService.publish(getActivity(),bean);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    class PublishTask extends WorkTask<Void, Void, Object> {

        public PublishTask(){}

        @Override
        public Object workInBackground(Void... var1) throws TaskException {

            return SinaSDK.getInstance().statusesUpdate(getPublishBean().getStatusContent());
        }
    }
}

