package com.example.administrator.shiyuji.ui.activity.tuikit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.ui.fragment.tuikit.ChatFragment;
import com.tencent.connect.common.Constants;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;

/**
 *
 * 聊天界面
 * Created by Administrator on 2019/9/6.
 */

public class ChatActivity extends Activity {

    private ChatFragment mChatFragment;
    private ChatInfo mChatInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_chat_activity);

        Bundle bundle = getIntent().getExtras();

        mChatInfo = (ChatInfo) bundle.getSerializable("chatInfo");

        mChatFragment = new ChatFragment();
        mChatFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.empty_view, mChatFragment).commitAllowingStateLoss();
    }

}