package com.example.administrator.shiyuji.ui.activity.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.util.task.ITaskManager;
import com.example.administrator.shiyuji.util.task.WorkTask;
import com.example.administrator.shiyuji.util.viewutil.InjectUtility;
import com.example.administrator.shiyuji.ui.widget.AsToolbar;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapOwner;
import com.example.administrator.shiyuji.util.viewutil.ViewUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2019/6/28.
 */

public class BaseActivity extends AppCompatActivity implements BitmapOwner, ITaskManager, AsToolbar.OnToolbarDoubleClick {

    static final String TAG = "Activity-Base";

    private Map<String, WeakReference<ABaseFragment>> fragmentRefs;
    private BaseActivityHelper mHelper;//权限观察者模式主题
    private static Class<? extends BaseActivityHelper> mHelperClass;//BaseActivityHelper的子类
    private static BaseActivity runningActivity;
    private boolean isDestory;
    private View rootView;
    @ViewInject(
            idStr = "toolbar"
    )
    Toolbar mToolbar;


    /**
     * 设置helper
     * @param clazz
     */
    public static void setHelper(Class<? extends BaseActivityHelper> clazz) {
        mHelperClass = clazz;
    }

    public BaseActivityHelper getActivityHelper() {
        return this.mHelper;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(this.mHelper == null) {
            try {
                if(mHelperClass != null) {
                    this.mHelper = (BaseActivityHelper)mHelperClass.newInstance();
                    this.mHelper.bindActivity(this);
                }
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

        if(this.mHelper != null) {
            this.mHelper.onCreate(savedInstanceState);
        }
        this.fragmentRefs = new HashMap();
        super.onCreate(savedInstanceState);


    }
    public void setContentView(View view) {
        super.setContentView(view, new ViewGroup.LayoutParams(-1, -1));
        this.rootView = view;
        InjectUtility.initInjectedView(this, this, this.rootView);
        this.mToolbar = (Toolbar)this.findViewById(R.id.toolbar);
        if(this.mToolbar != null) {
            this.setSupportActionBar(this.mToolbar);
        }
    }

    public void setBlackToolbar(){
        this.mToolbar.setVisibility(View.GONE);
        this.mToolbar = (Toolbar)this.findViewById(R.id.blackToolbar);
        if(this.mToolbar != null) {
            this.setSupportActionBar(this.mToolbar);
        }
        this.mToolbar.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏标题栏
     */
    public void hideToolbar(){
        getSupportActionBar().hide();
    }
    public void setContentView(int layoutResID) {
        //顶部栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
        this.setContentView(View.inflate(this, layoutResID, (ViewGroup)null));
    }
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        this.rootView = view;

        InjectUtility.initInjectedView(this, this, this.rootView);
        this.mToolbar = (Toolbar)this.findViewById(R.id.toolbar);
        if(this.mToolbar != null) {
            this.setSupportActionBar(this.mToolbar);
        }

    }
    public void showMessage(int msgId) {
        this.showMessage(this.getText(msgId));
    }
    public void showMessage(CharSequence msg) {
        ViewUtils.showMessage(this, msg.toString());
    }

    protected void onResume() {
        super.onResume();
        if(this.mHelper != null) {
            this.mHelper.onResume();
        }

        setRunningActivity(this);
//        if(this.theme == this.configTheme()) {
//            String languageStr = SettingUtility.getPermanentSettingAsStr("language", Locale.getDefault().getLanguage());
//            String country = SettingUtility.getPermanentSettingAsStr("language-country", Locale.getDefault().getCountry());
//            if(this.language == null || !this.language.getLanguage().equals(languageStr) || !country.equals(this.language.getCountry())) {
//                Logger.i("language changed, reload()");
//                this.reload();
//            }
//        } else {
//            Logger.i("theme changed, reload()");
//            this.reload();
//        }
    }
    public void onDestroy() {
        this.isDestory = true;
        this.removeAllTask(true);
        if(BitmapLoader.getInstance() != null) {
            BitmapLoader.getInstance().cancelPotentialTask(this);
        }

        super.onDestroy();
//        if(this.mHelper != null) {
//            this.mHelper.onDestroy();
//        }

    }


    /**
     * 左上角返回按钮
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case 16908332:
                if(this.onHomeClick()) {
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 按下返回按钮，返回
     * @return
     */
    protected boolean onHomeClick() {

        Set keys1 = this.fragmentRefs.keySet();
        Iterator var2 = keys1.iterator();

        ABaseFragment fragment;
        do {
            if(!var2.hasNext()) {
                return this.onBackClick();
            }

            String key = (String)var2.next();
            WeakReference fragmentRef = (WeakReference)this.fragmentRefs.get(key);
            fragment = (ABaseFragment)fragmentRef.get();
        } while(fragment == null || !fragment.onHomeClick());

        return true;
    }
    /**
     * 按下返回按钮，返回
     * @return
     */
    public boolean onBackClick() {

        Set keys1 = this.fragmentRefs.keySet();
        Iterator var2 = keys1.iterator();

        ABaseFragment fragment;
        do {
            if(!var2.hasNext()) {
                this.finish();
                return true;
            }

            String key = (String)var2.next();
            WeakReference fragmentRef = (WeakReference)this.fragmentRefs.get(key);
            fragment = (ABaseFragment)fragmentRef.get();
        } while(fragment == null || !fragment.onBackClick());

        return true;
    }



    public Toolbar getToolbar() {
        return this.mToolbar;
    }
    public View getRootView() {
        return this.rootView;
    }

    public void removeFragment(String tag) {
        this.fragmentRefs.remove(tag);
    }

    public void addFragment(String tag, ABaseFragment fragment) {
        this.fragmentRefs.put(tag, new WeakReference(fragment));
    }

    public static BaseActivity getRunningActivity() {
        return runningActivity;
    }
    public static void setRunningActivity(BaseActivity activity) {
        runningActivity = activity;
    }

    public boolean onToolbarDoubleClick() {
        Set keys = this.fragmentRefs.keySet();
        Iterator var2 = keys.iterator();

        ABaseFragment fragment;
        do {
            if(!var2.hasNext()) {
                return false;
            }

            String key = (String)var2.next();
            WeakReference fragmentRef = (WeakReference)this.fragmentRefs.get(key);
            fragment = (ABaseFragment)fragmentRef.get();
        } while(fragment == null || !(fragment instanceof AsToolbar.OnToolbarDoubleClick) || !((AsToolbar.OnToolbarDoubleClick)fragment).onToolbarDoubleClick());

        return true;
    }

    public boolean isDestory() {
        return this.isDestory;
    }

    @Override
    public void addTask(WorkTask var1) {

    }

    @Override
    public void removeTask(String var1, boolean var2) {

    }

    @Override
    public void removeAllTask(boolean var1) {

    }

    @Override
    public int getTaskCount(String var1) {
        return 0;
    }

    @Override
    public boolean canDisplay() {
        return true;
    }
}
