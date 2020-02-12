package com.example.administrator.shiyuji.ui.fragment.publish.comment;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.support.odps.udf.CodecCheck;
import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.service.PublishService;
import com.example.administrator.shiyuji.support.bean.Emotion;
import com.example.administrator.shiyuji.support.bean.PublishBean;
import com.example.administrator.shiyuji.support.sqlit.EmotionsDB;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusComment;
import com.example.administrator.shiyuji.ui.fragment.publish.EmotionFragment;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.TimelineDetailPagerFragment;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapUtil;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.utils.SystemUtils;


/**
 * Created by yu_midux on 2018/4/23.
 */

public class CustomDialog extends Dialog implements View.OnClickListener,EmotionFragment.OnEmotionSelectedListener{
    private EditText et_input;
    private Button btn_send;
    private TextView contentErrorHint;
    private LinearLayout layContainer;
//    private View layEmotion;
//    private View btnEmotion;
//    private ViewGroup layRoot;
//    private int emotionHeight;
    private Activity activity;
    /**
     * 回复的前缀，回复@xXX：
     */
    private String pre = "";
    private String ATUser;
    private String type;

    private String row_key;
    private final LayoutTransition transitioner = new LayoutTransition();
    private EmotionFragment emotionFragment;//表情
    private TimelineDetailPagerFragment timelineDetailPagerFragment;

    public static final int MAX_Content_LENGTH = 30;

    public CustomDialog(@NonNull Context context) {
        super(context, 0);
    }

    public CustomDialog(@NonNull Context context, TimelineDetailPagerFragment timelineDetailPagerFragment, int themeResId, String rowKey, String s, String ATUser,String type) {
        super(context, themeResId);
        this.row_key = rowKey;
        this.activity= (Activity) context;
        this.timelineDetailPagerFragment=timelineDetailPagerFragment;
        this.pre=s;
        this.ATUser= ATUser;
        this.type=type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_dialog_layout);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);//dialog底部弹出
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        et_input = findViewById(R.id.et_input);
        btn_send = findViewById(R.id.btn_send);
        layContainer = findViewById(R.id.layContainer);
//        layEmotion = findViewById(R.id.layEmotion);
        contentErrorHint = findViewById(R.id.contentErrorHint);
//        btnEmotion = findViewById(R.id.btnEmotion);
//        layRoot = findViewById(R.id.layRoot);
        btn_send.setOnClickListener(release_listener);

//        btnEmotion.setOnClickListener(this);
        // 更换表情
        et_input.setFilters(new InputFilter[] { emotionFilter });
        et_input.addTextChangedListener(editContentWatcher);
//        et_input.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                hideEmotionView(true);
//            }
//        });

//        if (savedInstanceState == null) {
//            emotionFragment = EmotionFragment.newInstance();
//            timelineDetailPagerFragment.getChildFragmentManager().beginTransaction().replace(R.id.layEmotion, emotionFragment, "Emotion").commit();
//        }
//        else {
//            emotionFragment = (EmotionFragment) activity.getFragmentManager().findFragmentByTag("Emotion");
//        }
//        emotionFragment.setOnEmotionListener(this);
    }


    View.OnClickListener release_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           if (contentErrorHint.getVisibility()==View.VISIBLE){
               Toast.makeText(getContext(),contentErrorHint.getText().toString(),Toast.LENGTH_SHORT).show();
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

        StatusComment statusComment = new StatusComment();
        statusComment.setUserInfo(AppContext.getAccount().getUserInfo());

        String text = pre+et_input.getText().toString();

        statusComment.setRow_key(row_key);
        statusComment.setText(text);
        statusComment.setStatus_row_key(row_key);
        statusComment.setATUser(ATUser);
        statusComment.setType(type);

        PublishBean bean = new PublishBean();
        bean.setPublishType(PublishBean.PublishType.commentCreate);
        bean.setStatusComment(statusComment);
        PublishService.publish(activity,bean);

        CustomDialog.this.dismiss();
    }

    /**
     * 检查输入是否为空
     * @return
     */
    boolean checkValid() {

        String content = et_input.getText().toString();

        if (TextUtils.isEmpty(content)) {
            Toast.makeText(getContext(),R.string.error_none_status,Toast.LENGTH_SHORT).show();
//            showMessage(R.string.error_none_status);
            return false;
        }

        return true;
    }
//    /**
//     * 切换表情跟键盘
//     *
//     * @param v
//     */
//    void switchEmotionSoftinput(View v) {
//        if (layEmotion.isShown()) {
//            hideEmotionView(true);
//        } else {
//            showEmotionView(SystemUtils.isKeyBoardShow(activity));
//        }
//    }
//
//    private void hideEmotionView(boolean showKeyBoard) {
//        if (layEmotion.isShown()) {
//            if (showKeyBoard) {
//                LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) layContainer.getLayoutParams();
//                localLayoutParams.height = layEmotion.getTop();
//                localLayoutParams.weight = 0.0F;
//                layEmotion.setVisibility(View.GONE);
//                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//
//                SystemUtils.showKeyBoard(activity, et_input);
//                et_input.postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        unlockContainerHeightDelayed();
//                    }
//
//                }, 200L);
//            } else {
//                layEmotion.setVisibility(View.GONE);
//                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//                unlockContainerHeightDelayed();
//            }
//        }
//    }
//
//    private void showEmotionView(boolean showAnimation) {
//        if (showAnimation) {
//            transitioner.setDuration(200);
//        } else {
//            transitioner.setDuration(0);
//        }
//
//        emotionHeight = SystemUtils.getKeyboardHeight(activity);
//
//        SystemUtils.hideSoftInput(activity, et_input);
//        layEmotion.getLayoutParams().height = emotionHeight;
//        layEmotion.setVisibility(View.VISIBLE);
//        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//
//
//        int lockHeight = SystemUtils.getAppContentHeight(activity);
//
//        lockContainerHeight(lockHeight);
//    }

    private void lockContainerHeight(int paramInt) {
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) layContainer.getLayoutParams();
        localLayoutParams.height = paramInt;
        localLayoutParams.weight = 0.0F;
    }

    public void unlockContainerHeightDelayed() {
        ((LinearLayout.LayoutParams) layContainer.getLayoutParams()).weight = 1.0F;
    }
    /**
     * 输入文本的过滤，根据输入替换库中的表情
     */
    private InputFilter emotionFilter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            // 是delete直接返回
            if ("".equals(source)) {
                return null;
            }

            byte[] emotionBytes = EmotionsDB.getEmotion(source.toString());
            // 输入的表情字符存在，则替换成表情图片
            if (emotionBytes != null) {
                byte[] data = emotionBytes;
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                int size = BaseActivity.getRunningActivity().getResources().getDimensionPixelSize(R.dimen.publish_emotion_size);
                bitmap = BitmapUtil.zoomBitmap(bitmap, size);
                SpannableString emotionSpanned = new SpannableString(source.toString());
                ImageSpan span = new ImageSpan(activity, bitmap);
                emotionSpanned.setSpan(span, 0, source.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return emotionSpanned;
            } else {
                return source;
            }
        }

    };
    /**
     * 内容监听，刷新提示信息
     */
    private TextWatcher editContentWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 设置长度提示
            String content = et_input.getText().toString() + appendContent();

            if (AisenUtils.getStrLength(content) > MAX_Content_LENGTH) {
                contentErrorHint.setVisibility(View.VISIBLE);
                contentErrorHint.setText(String.format("字数超过限制，请删除%d个字后再发布", AisenUtils.getStrLength(content) - MAX_Content_LENGTH));
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
    public void onClick(View v) {

    }

    @Override
    public void onEmotionSelected(Emotion emotion) {
        Editable editAble = et_input.getEditableText();
        int start = et_input.getSelectionStart();
        if ("[最右]".equals(emotion.getKey()))
            editAble.insert(start, "→_→");
        else
            editAble.insert(start, emotion.getKey());
    }
}
