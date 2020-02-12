package com.example.administrator.shiyuji.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.administrator.shiyuji.MainActivity;
import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.TestActivity;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.support.bean.AccessToken;
import com.example.administrator.shiyuji.support.bean.AccountBean;
import com.example.administrator.shiyuji.support.config.ConfigHelper;
import com.example.administrator.shiyuji.support.genereter.GenerateTestUserSig;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.ui.activity.base.ActivityHelper;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.util.accountutil.AccountUtils;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.GlobalContext;
import com.example.administrator.shiyuji.support.genereter.KeyGenerator;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.example.administrator.shiyuji.util.task.WorkTask;
import com.example.administrator.shiyuji.util.viewutil.ViewUtils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.administrator.shiyuji.support.setting.SettingUtility.getSetting;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private static final String APP_ID = "1109539107";//qq登录官方获取的APPID
    public static final String SDKAPPID = "1400252694";//im的sdkid

    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;
    private  com.example.administrator.shiyuji.ui.fragment.bean.UserInfo userInfo;

    @ViewInject(id = R.id.btn_login)
    Button btn_login;
    @ViewInject(id = R.id.account)
    EditText accountEdit;
    @ViewInject(id = R.id.password)
    EditText passwordEdit;
    @ViewInject(id = R.id.btn_register)
    TextView btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_login_main);

        //判断是否存在账户不用登录
        if (AppContext.isLoggedIn()){
            //判断是否过期
            if (System.currentTimeMillis()-AppContext.getAccount().getAccessToken().getCreate_at()<=24 * 60 * 60 * 1000){
                AccountBean accountBean = AppContext.getAccount();
                try {
                    com.example.administrator.shiyuji.ui.fragment.bean.UserInfo u = SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).getUserInfo(String.valueOf(accountBean.getUserInfo().getId()));
                    if (u!=null){
                        accountBean.setUserInfo(u);
                        AccountUtils.setLogedinAccount(accountBean);
                    }

                } catch (TaskException e) {
                    e.printStackTrace();
                }
                if (accountBean.getUserInfo().getState()==0){
                    MainActivity.lunch(LoginActivity.this);
                    finish();
                }else {
                    //冻结
                    new MaterialDialog.Builder(LoginActivity.this)
                            .forceStacking(true)
                            .content(R.string.illegal_user)
                            .positiveText(R.string.login_ok)
                            .negativeText(R.string.login_exit)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {

                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                }

                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {

                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    finish();
                                }

                            })
                            .show();
                }
                //线程登录tuikit
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loginTUIkit(AppContext.getAccount());
                    }
                }).start();
            }
        }

        //传入参数APPID和全局Context上下文
        mTencent = Tencent.createInstance(APP_ID,LoginActivity.this.getApplicationContext());
        passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());


    }

    private void loginTUIkit(final AccountBean accountBean){

        TUIKit.login(accountBean.getUserInfo().getIdentifier(), accountBean.getUserSig(), new IUIKitCallBack() {
            @Override
            public void onError(String module, final int code, final String desc) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (code==6206){
                            String userSig = GenerateTestUserSig.genTestUserSig(accountBean.getUserInfo().getIdentifier());
                            accountBean.setUserSig(userSig);
                            loginTUIkit(accountBean);
                        }
                        Log.d("看dddddddd", String.valueOf(code));
                        Log.d("看dddddddd", accountBean.getUserInfo().getIdentifier());
                        Toast.makeText(LoginActivity.this, "登录失败, errCode = " + code + ", errInfo = " + desc, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onSuccess(Object data) {
                //                AccountUtils.newAccount(accountBean);
                AccountUtils.setLogedinAccount(accountBean);
                AppContext.setAccount(accountBean);//保存当前账户
            }
        });
    }

    public void buttonLogin(View v){
        /**通过这句代码，SDK实现了QQ的登录，这个方法有三个参数，第一个参数是context上下文，第二个参数SCOPO 是一个String类型的字符串，表示一些权限
         官方文档中的说明：应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
         第三个参数，是一个事件监听器，IUiListener接口的实例，这里用的是该接口的实现类 */
        mIUiListener = new BaseUiListener();
        //all表示获取所有权限
        mTencent.login(LoginActivity.this,"all", mIUiListener);
    }

    /**
     * 自定义监听器实现IUiListener接口后，需要实现的3个方法
     * onComplete完成 onError错误 onCancel取消
     *{
     "ret":0,
     "openid":"943E3CD917E1C55DDE626BA64BAC6809",
     "access_token":"8C504D93E9AB8D84A8662E384D3DA26C",
     "pay_token":"E9042988B70738EA3987C4C2425E3BE1",
     "expires_in":7776000,
     "pf":"desktop_m_qq-10000144-android-2002-",
     "pfkey":"8cd945f6b7a7ff241333ed1868e7e2b8",
     "msg":"",
     "login_cost":83,
     "query_authority_cost":134,
     "authority_cost":7990,
     "expires_time":1575181194686
     }
     * {
     "ret":0,
     "msg":"",
     "is_lost":0,
     "nickname":"3y。",
     "gender":"男",
     "province":"",
     "city":"赫拉特",
     "year":"2018",
     "constellation":"",
     "figureurl":"http://qzapp.qlogo.cn/qzapp/1109539107/943E3CD917E1C55DDE626BA64BAC6809/30",
     "figureurl_1":"http://qzapp.qlogo.cn/qzapp/1109539107/943E3CD917E1C55DDE626BA64BAC6809/50",
     "figureurl_2":"http://qzapp.qlogo.cn/qzapp/1109539107/943E3CD917E1C55DDE626BA64BAC6809/100",
     "figureurl_qq_1":"http://thirdqq.qlogo.cn/g?b=oidb&k=b6WHjZjElwIpWnrTlSicXsQ&s=40&t=1562831761",
     "figureurl_qq_2":"http://thirdqq.qlogo.cn/g?b=oidb&k=b6WHjZjElwIpWnrTlSicXsQ&s=100&t=1562831761",
     "figureurl_qq":"http://thirdqq.qlogo.cn/g?b=oidb&k=b6WHjZjElwIpWnrTlSicXsQ&s=140&t=1562831761",
     "figureurl_type":"1",
     "is_yellow_vip":"0",
     "vip":"0",
     "yellow_vip_level":"0",
     "level":"0",
     "is_yellow_year_vip":"0"
     }
     */
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            Toast.makeText(LoginActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "response:" + response);
            JSONObject obj = (JSONObject) response;
            try {
                final String openID = obj.getString("openid");
                final String accessToken = obj.getString("access_token");
                //将accessToken保存
                ActivityHelper.putShareData(GlobalContext.getInstance(),"accessToken",accessToken);
                String expires = obj.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken,expires);
                QQToken qqToken = mTencent.getQQToken();
                mUserInfo = new UserInfo(getApplicationContext(),qqToken);
                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        Log.e(TAG,"登录成功"+response.toString());
                        Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                        JSONObject oo= (JSONObject) response;
                        try {
                            String userName = oo.getString("nickname");
//                            name.setText(na);
                            String userHeadUrl=oo.getString("figureurl_2");
                            String gender = oo.getString("gender");
                            Date date = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //格式化
                            String birthday = sdf.format(date).toString();

                            userInfo = new  com.example.administrator.shiyuji.ui.fragment.bean.UserInfo(0,userName,userHeadUrl,gender,birthday);
                            //将账号，头像保存
                            ActivityHelper.putShareData(GlobalContext.getInstance(),"account",userName);
                            ActivityHelper.putShareData(GlobalContext.getInstance(),"userHeadUrl",userHeadUrl);

                            mLoadAccountTask = new LoadAccountTask();
                            mLoadAccountTask.execute("qq",openID,accessToken);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(UiError uiError) {
                        Log.e(TAG,"登录失败"+uiError.toString());
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG,"登录取消");

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();

        }

    }

    /**
     * 在调用Login的Activity或者Fragment中重写onActivityResult方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_LOGIN){
            Tencent.onActivityResultData(requestCode,resultCode,data,mIUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 1、获取授权的TOKEN
     * 2、加载用户信息
     */
    LoadAccountTask mLoadAccountTask;
    class LoadAccountTask extends WorkTask<String, Integer, AccountBean> {

        LoadAccountTask() {
            mLoadAccountTask = this;
        }

        @Override
        protected void onPrepare() {
            super.onPrepare();

            ViewUtils.createProgressDialog(LoginActivity.this, getString(R.string.login_loading_token), R.color.colorBlue).show();
        }

        @Override
        public AccountBean workInBackground(String... params) throws TaskException {
            Log.d("看params",params.toString());
            Log.d("看",params[0]);
            AccountBean accountBean = new AccountBean();
            AccessToken accessToken = null;
            if (params[0].equals("qq")){
                String openId = params[1];
                String access_token = params[2];
                Params p = new Params();
                p.addParameter("type","qq");
                p.addParameter("openId",openId);
                p.addParameter("token",access_token);
                p.addParameter("gender",userInfo.getGender());
                p.addParameter("avatar",userInfo.getAvatar());
                p.addParameter("name",userInfo.getNickname());
                p.addParameter("birthday", userInfo.getBirthday());
                accessToken= SinaSDK.getInstance().login(p);
            }else {
                String mAccount = params[1];
                String mPassword = params[2];
                String mPassword_md5 = KeyGenerator.generateMD5(mPassword);
                Params p = new Params();
                p.addParameter("type","phone");
                p.addParameter("account",mAccount);
                p.addParameter("password",mPassword_md5);
                accessToken= SinaSDK.getInstance().login(p);
            }
            if (accessToken.getUid()==null)
                return null;

            Log.d("看11223",accessToken.toString());
            accountBean.setUid(accessToken.getUid());
            accountBean.setAccessToken(accessToken);
            //根据accessToken获取userInfo
            accountBean.setUserInfo(SinaSDK.getInstance(accessToken).getUserInfo(accessToken.getUid()));
            if (accountBean.getUserInfo().getIdentifier().equals("") || accountBean.getUserInfo().getIdentifier()==null )
                return null;
            accountBean.setAccount(accountBean.getUserInfo().getIdentifier());
            Setting action = getSetting("base_url");
            String base_url = action.getValue().split("/")[2].split(":")[0];
            Log.d("看ssssssssssssss",base_url);
            accountBean.getUserInfo().setAvatar(accountBean.getUserInfo().getAvatar().replaceAll("localhost",base_url));

            return accountBean;
        }

        @Override
        protected void onSuccess(final AccountBean accountBean) {
            super.onSuccess(accountBean);

            if (accountBean==null||accountBean.getUid()==null){
                onFailure(null);
            }else {
                // 获取userSig函数
                String userSig = GenerateTestUserSig.genTestUserSig(accountBean.getUserInfo().getIdentifier());
                accountBean.setUserSig(userSig);
                loginTUIkit(accountBean);
                MainActivity.lunch(LoginActivity.this);
                finish();
            }
        }

        @Override
        protected void onFailure(TaskException exception) {
            super.onFailure(exception);

            new MaterialDialog.Builder(LoginActivity.this)
                    .forceStacking(true)
                    .content(R.string.login_faild_remind)
                    .positiveText(R.string.login_ok)
                    .negativeText(R.string.login_exit)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {

                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                        }

                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {

                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            finish();
                        }

                    })
                    .show();
        }

        @Override
        protected void onFinished() {
            super.onFinished();

            ViewUtils.dismissProgressDialog();

//            if (isActivityRunning()) {
//                getActivity().finish();
//            }
        }

    }






}
