package com.example.administrator.shiyuji.ui.activity.publish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.publish.PublishStatusFragment;
import com.example.administrator.shiyuji.support.bean.PublishBean;
import com.example.administrator.shiyuji.support.bean.PublishType;

/**
 * Created by Administrator on 2019/7/7.
 */

public class PublishActivity extends BaseActivity  {

    /**
     * 发布微博
     *  @param from
     * @param bean
     */
    public static void publishStatus(Activity from, PublishBean bean) {
        Intent intent = new Intent(from, PublishActivity.class);
        intent.putExtra("type", PublishType.status.toString());
        if (bean != null)
            intent.putExtra("bean", bean);
        from.startActivity(intent);
    }


    private String typeStr;
    private PublishBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_fragment_container);

        getSupportActionBar().setDisplayShowHomeEnabled(false);


        // 接收分享
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(Intent.ACTION_SEND) && !TextUtils.isEmpty(type)) {
//                    if (!AppContext.isLoggedIn()) {
//                		AccountFragment.launch(this);

//                        showMessage(R.string.publish_please_login);

//                        finish();
//                        return;
//                    }

                    if ("text/plain".equals(type)) {
                        handleSendText(intent);
                    } else if (type.startsWith("image/")) {
                        handleSendImage(intent);
                    } else {
                        finish();
                    }
                    return;
                }
            }
        }

        typeStr = savedInstanceState == null ? getIntent().getStringExtra("type") : savedInstanceState.getString("type");

        bean = savedInstanceState == null ? (PublishBean) getIntent().getSerializableExtra("bean") : (PublishBean) savedInstanceState.getSerializable("bean");

        if (savedInstanceState == null) {
            if (TextUtils.isEmpty(typeStr)) {
                finish();
                return;
            }

            PublishType type = PublishType.valueOf(typeStr);

            ABaseFragment fragment = null;
            switch (type) {
                case status:
                    fragment = PublishStatusFragment.newInstance(bean);
                    break;
                case commentReply:
//                    fragment = PublishCommentReplyFragment.newInstance(bean);
                    break;
                case commentCreate:
//                    fragment = PublishStatusCommentFragment.newInstance(bean);
                    break;
                case statusRepost:
//                    fragment = PublishStatusRepostFragment.newInstance(bean);
                    break;
                default:
                    break;
            }

            if (fragment != null)
                getFragmentManager().beginTransaction().add(R.id.fragmentContainer, fragment, "PublishFragment").commit();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("type", typeStr);
        outState.putSerializable("bean", bean);
    }

    private void handleSendText(Intent intent) {

    }

    private void handleSendImage(Intent intent) {

    }



}
