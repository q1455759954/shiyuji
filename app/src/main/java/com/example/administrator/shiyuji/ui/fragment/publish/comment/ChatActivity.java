//package com.example.administrator.shiyuji.ui.fragment.publish.comment;
//
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Shader;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.AppCompatActivity;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.GridLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TabHost;
//import android.widget.Toast;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static android.R.id.tabcontent;
//
//public class ChatActivity extends AppCompatActivity {
//    DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
//        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//            Log.i("TAG", "键盘code---" + keyCode);
//            if (keyCode == KeyEvent.KEYCODE_BACK) {
//                dialog.dismiss();
//                return false;
//            } else if(keyCode == KeyEvent.KEYCODE_DEL){//删除键
////                if(dialog != null){
////                    mCustomDialog.del();
////                }
//                return false;
//            }else{
//                return true;
//
//            }
//        }
//    };
//    private CustomDialog mCustomDialog;
//    private boolean isDianHuaChecked =false;
//    private boolean isSouSuoChecked =false;
//    private boolean isYuYinChecked =false;
//    private boolean isPicChecked =false;
//    private boolean isShiPinChecked =false;
//    private boolean isDiZhiChecked =false;
//    private boolean isKeyBoarddActive =false;
//    private boolean isFirstClickedEditText = true ;
//    private boolean isFirstClickedGongJulan_bt = true;
//    private MsgAdapter chatAdapter;
//    /**
//     * 声明ListView
//     */
//    private ListView lv_chat_dialog;
//    /**
//     * 集合
//     */
//    private List<Msg> personChats = new ArrayList<Msg>();
//    private Handler handler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            int what = msg.what;
//            switch (what) {
//                case 1:
//                    /**
//                     * ListView条目控制在最后一行
//                     */
//                    lv_chat_dialog.setSelection(personChats.size());
//                    break;
//
//                default:
//                    break;
//            }
//        };
//    };
//    public void initJudge(){
//        isDianHuaChecked =false;
//        isSouSuoChecked =false;
//        isYuYinChecked =false;
//        isPicChecked =false;
//        isShiPinChecked =false;
//        isDiZhiChecked =false;
//    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.chat_activity);
//        //==此处设置聊天背景图片平铺=========================================================
////        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.liaotian_bg);
//////bitmap = Bitmap.createBitmap(100, 20, Config.ARGB_8888);
////        BitmapDrawable drawable = new BitmapDrawable(bitmap);
////        drawable.setTileModeXY(Shader.TileMode.REPEAT , Shader.TileMode.REPEAT );
////        drawable.setDither(true);
////        ((LinearLayout) findViewById(R.id.liaotian_mainInterface)).setBackgroundDrawable(drawable);
//        //========================================================================================
//        initButton();
//        //=========================================================================================
//        /**
//         * 虚拟4条发送方的消息
//         */
//        for (int i = 0; i <= 3; i++) {
//            Msg personChat = new Msg();
//            personChat.setMeSend(false);
//            personChats.add(personChat);
//        }
//        lv_chat_dialog = (ListView) findViewById(R.id.lv_chat_dialog);
////        Button btn_chat_message_send = (Button) findViewById(R.id.btn_chat_message_send);
////        final EditText et_chat_message = (EditText) findViewById(R.id.et_chat_message);
//        /**
//         *setAdapter
//         */
//        chatAdapter = new MsgAdapter(this, personChats);
//        lv_chat_dialog.setAdapter(chatAdapter);
//        /**
//         * 发送按钮的点击事件
//         */
////        btn_chat_message_send.setOnClickListener(new View.OnClickListener() {
////
////            @Override
////            public void onClick(View arg0) {
////                // TODO Auto-generated method stub
////                if (TextUtils.isEmpty(et_chat_message.getText().toString())) {
////                    Toast.makeText(ChatActivity.this, "发送内容不能为空", Toast.LENGTH_SHORT).show();
////                    return;
////                }
////                Msg personChat = new Msg();
////                //代表自己发送
////                personChat.setMeSend(true);
////                //得到发送内容
////                personChat.setChatMessage(et_chat_message.getText().toString());
////                //加入集合
////                personChats.add(personChat);
////                //清空输入框
////                et_chat_message.setText("");
////                //刷新ListView
////                chatAdapter.notifyDataSetChanged();
////                handler.sendEmptyMessage(1);
////            }
////        });
//        replaceFragment(new YuYin_Fragment());
//    }
//    private void replaceFragment(Fragment fragment){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.gongjulan,fragment);
//        transaction.commit();
//    }
//    private void hideKeyBoard() {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        // 得到InputMethodManager的实例
//        if (imm.isActive()) {
//            // 如果开启
//            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
//            // 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
//            // }
//        }
//    }
//    public void initButton(){
////        ((EditText) findViewById(R.id.et_chat_message)).requestFocus();
////        ((EditText) findViewById(R.id.et_chat_message)).requestFocusFromTouch();
////        ((EditText) findViewById(R.id.et_chat_message)).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {//这里有bug，第一次点击输入框无论如何不走这个，但是会弹出文本框
////                ((RelativeLayout) findViewById(R.id.gongjulan)).setVisibility(View.GONE);
////                if(isFirstClickedEditText){//第一次点击输入框
//////                    hideKeyBoard();
////                    isFirstClickedEditText = false ;
////                    isFirstClickedGongJulan_bt = false ;
////                    isKeyBoarddActive = true ;
////                }else if(isKeyBoarddActive==false&&isFirstClickedEditText==false){//不是第一次点击且软键盘没出现，肯定要切换状态让他显示
//////                    hideKeyBoard();
////                    isFirstClickedEditText = false ;
////                    isFirstClickedGongJulan_bt = false ;
////                    isKeyBoarddActive = true ;
////                }
////                initJudge();
////            }
////        });
////        ((ImageView) findViewById(R.id.yuyin)).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                replaceFragment(new YuYin_Fragment());
////                if (isYuYinChecked){
////                    ((RelativeLayout) findViewById(R.id.gongjulan)).setVisibility(View.GONE);
////                    initJudge();
////                }else {
////                    if(isFirstClickedGongJulan_bt){//第一次点击工具栏不用切换软键盘状态
////                        isFirstClickedGongJulan_bt = false ;
////                        isFirstClickedEditText = false ;
////                        isKeyBoarddActive = false ;
////                    }else {//不是第一次点击软件盘
////                        if(isKeyBoarddActive){//并且软键盘显示，切换软键盘状态
////                            hideKeyBoard();
////                            isKeyBoarddActive = false ;
////                            isFirstClickedGongJulan_bt = false ;
////                        }
////                        else {//否则不用切换状态
////                            isFirstClickedGongJulan_bt = false ;
////                            isFirstClickedEditText = false ;
////                        }
////                    }
////                    Toast.makeText(ChatActivity.this,"确实应该出现",Toast.LENGTH_SHORT).show();
////                    ((RelativeLayout) findViewById(R.id.gongjulan)).setVisibility(View.VISIBLE);
////                    isDianHuaChecked =false;
////                    isSouSuoChecked =false;
////                    isYuYinChecked =true;
////                    isPicChecked =false;
////                    isShiPinChecked =false;
////                    isDiZhiChecked =false;
////                }
////            }
////        });
////        ((ImageView) findViewById(R.id.dizhi)).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                replaceFragment(new DiZhi_Fragment());
//////                Toast.makeText(ChatActivity.this,v.getVisibility(),Toast.LENGTH_SHORT).show();
////                if (isDiZhiChecked){
////                    Toast.makeText(ChatActivity.this,"确实应该出现",Toast.LENGTH_SHORT).show();
////                    ((RelativeLayout) findViewById(R.id.gongjulan)).setVisibility(View.GONE);
////                    initJudge();
////                }else {
////                    Toast.makeText(ChatActivity.this,"确实应该隐藏",Toast.LENGTH_SHORT).show();
////                    ((RelativeLayout) findViewById(R.id.gongjulan)).setVisibility(View.VISIBLE);
////                    if(isFirstClickedGongJulan_bt){
////                        isFirstClickedGongJulan_bt = false ;
////                    }else {
////                        if(isKeyBoarddActive){
////                            hideKeyBoard();
////                            isKeyBoarddActive = false ;
////                            isFirstClickedGongJulan_bt = false ;
////                        }
////                    }
////                    isDianHuaChecked =false;
////                    isSouSuoChecked =false;
////                    isYuYinChecked =false;
////                    isPicChecked =false;
////                    isShiPinChecked =false;
////                    isDiZhiChecked =true;
////                }
////            }
////        });
////        ((ImageView) findViewById(R.id.shipin)).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                replaceFragment(new ShiPin_Fragment());
//////                Toast.makeText(ChatActivity.this,v.getVisibility(),Toast.LENGTH_SHORT).show();
////                if (isShiPinChecked){
////                    Toast.makeText(ChatActivity.this,"确实应该出现",Toast.LENGTH_SHORT).show();
////                    ((RelativeLayout) findViewById(R.id.gongjulan)).setVisibility(View.GONE);
////                    initJudge();
////                }else {
////                    Toast.makeText(ChatActivity.this,"确实应该隐藏",Toast.LENGTH_SHORT).show();
////                    ((RelativeLayout) findViewById(R.id.gongjulan)).setVisibility(View.VISIBLE);
////                    if(isFirstClickedGongJulan_bt){
////                        isFirstClickedGongJulan_bt = false ;
////                    }else {
////                        if(isKeyBoarddActive){
////                            hideKeyBoard();
////                            isKeyBoarddActive = false ;
////                            isFirstClickedGongJulan_bt = false ;
////                        }
////                    }
////                    isDianHuaChecked =false;
////                    isSouSuoChecked =false;
////                    isYuYinChecked =false;
////                    isPicChecked =false;
////                    isShiPinChecked =true;
////                    isDiZhiChecked =false;
////                }
////            }
////        });
////        ((ImageView) findViewById(R.id.dianhua)).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                replaceFragment(new DianHua_Fragment());
//////                Toast.makeText(ChatActivity.this,v.getVisibility(),Toast.LENGTH_SHORT).show();
////                if (isDianHuaChecked){
////                    Toast.makeText(ChatActivity.this,"确实应该出现",Toast.LENGTH_SHORT).show();
////                    ((RelativeLayout) findViewById(R.id.gongjulan)).setVisibility(View.GONE);
////                    initJudge();
////                }else {
////                    Toast.makeText(ChatActivity.this,"确实应该隐藏",Toast.LENGTH_SHORT).show();
////                    ((RelativeLayout) findViewById(R.id.gongjulan)).setVisibility(View.VISIBLE);
////                    if(isFirstClickedGongJulan_bt){
////                        isFirstClickedGongJulan_bt = false ;
////                    }else {
////                        if(isKeyBoarddActive){
////                            hideKeyBoard();
////                            isKeyBoarddActive = false ;
////                            isFirstClickedGongJulan_bt = false ;
////                        }
////                    }
////                    isDianHuaChecked =true;
////                    isSouSuoChecked =false;
////                    isYuYinChecked =false;
////                    isPicChecked =false;
////                    isShiPinChecked =false;
////                    isDiZhiChecked =false;
////                }
////            }
////        });
////        ((ImageView) findViewById(R.id.sousuo)).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                replaceFragment(new SouSuo_Fragment());
//////                Toast.makeText(ChatActivity.this,v.getVisibility(),Toast.LENGTH_SHORT).show();
////                if (isSouSuoChecked){
////                    Toast.makeText(ChatActivity.this,"确实应该出现",Toast.LENGTH_SHORT).show();
////                    ((RelativeLayout) findViewById(R.id.gongjulan)).setVisibility(View.GONE);
////                    initJudge();
////                }else {
////                    Toast.makeText(ChatActivity.this,"确实应该隐藏",Toast.LENGTH_SHORT).show();
////                    ((RelativeLayout) findViewById(R.id.gongjulan)).setVisibility(View.VISIBLE);
////                    if(isFirstClickedGongJulan_bt){
////                        isFirstClickedGongJulan_bt = false ;
////                    }else {
////                        if(isKeyBoarddActive){
////                            hideKeyBoard();
////                            isKeyBoarddActive = false ;
////                            isFirstClickedGongJulan_bt = false ;
////                        }
////                    }
////                    isDianHuaChecked =false;
////                    isSouSuoChecked =true;
////                    isYuYinChecked =false;
////                    isPicChecked =false;
////                    isShiPinChecked =false;
////                    isDiZhiChecked =false;
////                }
////            }
////        });
//
//        ((Button) findViewById(R.id.test_commentBT)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCustomDialog = new CustomDialog(ChatActivity.this, R.style.customdialogstyle);
//                mCustomDialog.setOnKeyListener(keylistener);
//                mCustomDialog.show();
//
//                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(findViewById(R.id.test_bug), InputMethodManager.RESULT_SHOWN);
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//            }
//        });
////        ((ImageView) findViewById(R.id.pic)).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                replaceFragment(new Pic_Fragment());
//////                Toast.makeText(ChatActivity.this,v.getVisibility(),Toast.LENGTH_SHORT).show();
////                if (isPicChecked){
////                    Toast.makeText(ChatActivity.this,"确实应该出现",Toast.LENGTH_SHORT).show();
////                    ((RelativeLayout) findViewById(R.id.gongjulan)).setVisibility(View.GONE);
////                    initJudge();
////                }else {
////                    Toast.makeText(ChatActivity.this,"确实应该隐藏",Toast.LENGTH_SHORT).show();
////                    ((RelativeLayout) findViewById(R.id.gongjulan)).setVisibility(View.VISIBLE);
////                    if(isFirstClickedGongJulan_bt){
////                        isFirstClickedGongJulan_bt = false ;
////                    }else {
////                        if(isKeyBoarddActive){
////                            hideKeyBoard();
////                            isKeyBoarddActive = false ;
////                            isFirstClickedGongJulan_bt = false ;
////                        }
////                    }
////                    isDianHuaChecked =false;
////                    isSouSuoChecked =false;
////                    isYuYinChecked =false;
////                    isPicChecked =true;
////                    isShiPinChecked =false;
////                    isDiZhiChecked =false;
////                }
////            }
////        });
//        //=========================================================================================
//    }
//}