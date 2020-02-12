package com.example.administrator.shiyuji;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.administrator.shiyuji.support.genereter.GenerateTestUserSig;
import com.tencent.av.TIMAvManager;

import com.tencent.connect.common.Constants;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.config.CustomFaceConfig;
import com.tencent.qcloud.tim.uikit.config.GeneralConfig;
import com.tencent.qcloud.tim.uikit.config.TUIKitConfigs;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationListLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

/**
 * Created by Administrator on 2019/9/5.
 */

public class TestActivity extends AppCompatActivity {

    public static final String SDKAPPID = "1400252694";//im的sdkid

    private ConversationLayout mConversationLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_mine_main_page);
        if (!TextUtils.isEmpty(TIMManager.getInstance().getLoginUser())) {
            Log.d("看qqq","用户不为空！");
        }

//        TIMConversation conversation = TIMManager.getInstance().getConversation(
//                TIMConversationType.C2C,    //会话类型：单聊
//                "1455759954");
//        //构造一条消息
//        TIMMessage msg = new TIMMessage();
//
//        //添加文本内容
//        TIMTextElem elem = new TIMTextElem();
//        elem.setText("我发的消息，可以了吗？！？！");
//
//        //将elem添加到消息
//        if(msg.addElement(elem) != 0) {
//            return;
//        }
//
//        //发送消息
//        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
//            @Override
//            public void onError(int code, String desc) {//发送消息失败
//                //错误码 code 和错误描述 desc，可用于定位请求失败原因
//                //错误码 code 含义请参见错误码表
//                Log.d("看", "send message failed. code: " + code + " errmsg: " + desc);
//            }
//
//            @Override
//            public void onSuccess(TIMMessage msg) {//发送消息成功
//                Log.e("看", "SendMsg ok");
//            }
//        });



//        initView();

    }

    private void initView() {
        // 从布局文件中获取会话列表面板
        mConversationLayout = findViewById(R.id.conversation_layout);
//        mMenu = new Menu(getActivity(), (TitleBarLayout) mConversationLayout.getTitleBar(), Menu.MENU_TYPE_CONVERSATION);
        // 会话列表面板的默认UI和交互初始化
        mConversationLayout.initDefault();
        // 通过API设置ConversataonLayout各种属性的样例，开发者可以打开注释，体验效果
//        ConversationLayoutHelper.customizeConversation(mConversationLayout);
        mConversationLayout.getConversationList().setOnItemClickListener(new ConversationListLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, ConversationInfo conversationInfo) {
                //此处为demo的实现逻辑，更根据会话类型跳转到相关界面，开发者可根据自己的应用场景灵活实现
                startChatActivity(conversationInfo);
            }
        });
        mConversationLayout.getConversationList().setOnItemLongClickListener(new ConversationListLayout.OnItemLongClickListener() {
            @Override
            public void OnItemLongClick(View view, int position, ConversationInfo conversationInfo) {
//                startPopShow(view, position, conversationInfo);
            }
        });
//        initTitleAction();
//        initPopMenuAction();

    }

    private void startChatActivity(ConversationInfo conversationInfo) {
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setType(conversationInfo.isGroup() ? TIMConversationType.Group : TIMConversationType.C2C);
        chatInfo.setId(conversationInfo.getId());
        chatInfo.setChatName(conversationInfo.getTitle());
//        Intent intent = new Intent(TestActivity.this, ChatActivity.class);
//        intent.putExtra("chatInfo", chatInfo);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }

}
