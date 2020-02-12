package com.example.administrator.shiyuji.ui.activity.tuikit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.example.administrator.shiyuji.MainActivity;
import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMFriendGenderType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.qcloud.tim.uikit.base.ITitleBarLayout;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.component.action.PopActionClickListener;
import com.tencent.qcloud.tim.uikit.component.action.PopDialogAdapter;
import com.tencent.qcloud.tim.uikit.component.action.PopMenuAction;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationListLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.tencent.qcloud.tim.uikit.utils.PopWindowUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * 会话列表界面
 * Created by Administrator on 2019/9/6.
 */

public class ConversationActivity extends BaseActivity{


    public static final String SDKAPPID = "1400252694";//im的sdkid

    private ConversationLayout mConversationLayout;
    private List<PopMenuAction> mConversationPopActions = new ArrayList<>();
    private ListView mConversationPopList;
    private PopupWindow mConversationPopWindow;
    private View mBaseView;
    private PopDialogAdapter mConversationPopAdapter;
    private Menu mMenu;

    public static void lunch(Context context) {


//        TIMConversation conversation = TIMManager.getInstance().getConversation(
//                TIMConversationType.C2C,    //会话类型：单聊
//                "6527651791");
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


        //偷懒了，每次进入会话列表都修改一下头像，昵称
        ConversationActivity.changeInfo();

        Intent intent = new Intent(context,ConversationActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_chat_list);
        this.mBaseView = this.getRootView();
        if (!TextUtils.isEmpty(TIMManager.getInstance().getLoginUser())) {
            Log.d("看qqq","用户不为空！");
        }

//        TIMConversation conversation = TIMManager.getInstance().getConversation(
//                TIMConversationType.C2C,    //会话类型：单聊
//                "6527651791");
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



        initView();

    }

    private void initView() {
        // 从布局文件中获取会话列表面板
        mConversationLayout = findViewById(R.id.conversation_layout);
//        mMenu = new Menu(getActivity(), (TitleBarLayout) mConversationLayout.getTitleBar(), Menu.MENU_TYPE_CONVERSATION);
        // 会话列表面板的默认UI和交互初始化
        mConversationLayout.initDefault();

        // 从CoversationLayout获取会话列表
        ConversationListLayout listLayout = mConversationLayout.getConversationList();
        listLayout.enableItemRoundIcon(true);// 设置 item 头像是否显示圆角，默认是方形

        // 获取 TitleBarLayout
        TitleBarLayout titleBarLayout = mConversationLayout.findViewById(R.id.conversation_title);
        // 设置标题
        titleBarLayout.setTitle(getResources().getString(R.string.conversation_title), TitleBarLayout.POSITION.MIDDLE);

        // 显示左侧 Group
        titleBarLayout.getLeftGroup().setVisibility(View.VISIBLE);
        titleBarLayout.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
                startPopShow(view, position, conversationInfo);
            }
        });
//        initTitleAction();
        initPopMenuAction();

    }

    private void startPopShow(View view, int position, ConversationInfo info) {
        showItemPopMenu(position, info, view.getX(), view.getY() + view.getHeight() / 2);
    }


    /**
     * 长按会话item弹框
     *
     * @param index       会话序列号
     * @param conversationInfo 会话数据对象
     * @param locationX   长按时X坐标
     * @param locationY   长按时Y坐标
     */
    private void showItemPopMenu(final int index, final ConversationInfo conversationInfo, float locationX, float locationY) {
        if (mConversationPopActions == null || mConversationPopActions.size() == 0)
            return;
        View itemPop = LayoutInflater.from(ConversationActivity.this).inflate(R.layout.pop_menu_layout, null);
        mConversationPopList = itemPop.findViewById(R.id.pop_menu_list);
        mConversationPopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PopMenuAction action = mConversationPopActions.get(position);
                if (action.getActionClickListener() != null) {
                    action.getActionClickListener().onActionClick(index, conversationInfo);
                }
                mConversationPopWindow.dismiss();
            }
        });

        for (int i = 0; i < mConversationPopActions.size(); i++) {
            PopMenuAction action = mConversationPopActions.get(i);
            if (conversationInfo.isTop()) {
                if (action.getActionName().equals(getResources().getString(R.string.chat_top))) {
                    action.setActionName(getResources().getString(R.string.quit_chat_top));
                }
            } else {
                if (action.getActionName().equals(getResources().getString(R.string.quit_chat_top))) {
                    action.setActionName(getResources().getString(R.string.chat_top));
                }

            }
        }
        mConversationPopAdapter = new PopDialogAdapter();
        mConversationPopList.setAdapter(mConversationPopAdapter);
        mConversationPopAdapter.setDataSource(mConversationPopActions);
        mConversationPopWindow = PopWindowUtil.popupWindow(itemPop, mBaseView, (int) locationX, (int) locationY);
        mBaseView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mConversationPopWindow.dismiss();
            }
        }, 10000); // 10s后无操作自动消失
    }


    private void startChatActivity(ConversationInfo conversationInfo) {
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setType(conversationInfo.isGroup() ? TIMConversationType.Group : TIMConversationType.C2C);
        chatInfo.setId(conversationInfo.getId());
        chatInfo.setChatName(conversationInfo.getTitle());
        Intent intent = new Intent(ConversationActivity.this, ChatActivity.class);
        intent.putExtra("chatInfo", chatInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


//    private void initTitleAction() {
//        mConversationLayout.getTitleBar().setOnRightClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mMenu.isShowing()) {
//                    mMenu.hide();
//                } else {
//                    mMenu.show();
//                }
//            }
//        });
//    }

    private void initPopMenuAction() {

        // 设置长按conversation显示PopAction
        List<PopMenuAction> conversationPopActions = new ArrayList<PopMenuAction>();
        PopMenuAction action = new PopMenuAction();
        action.setActionName(getResources().getString(R.string.chat_top));
        action.setActionClickListener(new PopActionClickListener() {
            @Override
            public void onActionClick(int position, Object data) {
                mConversationLayout.setConversationTop(position, (ConversationInfo) data);
            }
        });
        conversationPopActions.add(action);
        action = new PopMenuAction();
        action.setActionClickListener(new PopActionClickListener() {
            @Override
            public void onActionClick(int position, Object data) {
                mConversationLayout.deleteConversation(position, (ConversationInfo) data);
            }
        });
        action.setActionName(getResources().getString(R.string.chat_delete));
        conversationPopActions.add(action);
        mConversationPopActions.clear();
        mConversationPopActions.addAll(conversationPopActions);
    }

    /**
     * 偷懒，修改信息
     */
    private static void changeInfo() {
        HashMap<String, Object> profileMap = new HashMap<>();
        profileMap.put(TIMUserProfile.TIM_PROFILE_TYPE_KEY_NICK, AppContext.getAccount().getUserInfo().getNickname());
        profileMap.put(TIMUserProfile.TIM_PROFILE_TYPE_KEY_FACEURL, AppContext.getAccount().getUserInfo().getAvatar());
        TIMFriendshipManager.getInstance().modifySelfProfile(profileMap, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
            }

            @Override
            public void onSuccess() {
            }
        });
    }

}
