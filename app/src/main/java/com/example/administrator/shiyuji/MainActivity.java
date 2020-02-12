package com.example.administrator.shiyuji;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.AppointmentFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.home.HomeFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.life.LifeFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.mine.MineFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.notification.NotificationFragment;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.util.annotation.ViewInject;

public class MainActivity extends BaseActivity {

    public static final String ACTION_NOTIFICATION_LIKE = "ACTION_NOTIFICATION_LIKE";
    public static final String ACTION_NOTIFICATION_FANS = "ACTION_NOTIFICATION_FANS";


    @ViewInject(id = R.id.appbar)
    AppBarLayout appBarLayout;
    private RadioGroup btn;

    private Fragment homeFragment;
    private Fragment lifeFragment;
    private Fragment appointmentFragment;
    private Fragment notificationFragment;
    private Fragment mineFragment;
    private Fragment curFragment;


    public static void lunch(Context context) {
        Intent intent = new Intent(context,MainActivity.class);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_navigation_bar);
        homeFragment = HomeFragment.newInstance();
        lifeFragment = LifeFragment.newInstance();
        appointmentFragment = AppointmentFragment.newInstance();
        notificationFragment = new NotificationFragment();
        mineFragment = MineFragment.newInstance();

        curFragment = savedInstanceState==null?null:(Fragment) savedInstanceState.getSerializable("curFragment");
        if (curFragment==null){
            getFragmentManager().beginTransaction().add(R.id.main_content, homeFragment).commit();
            curFragment = homeFragment;
            initView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 点击通知发生跳转
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent == null)
            return;

        String action = intent.getAction();
        //点赞
        if (ACTION_NOTIFICATION_LIKE.equals(action)){
            RadioButton r1 = (RadioButton)findViewById(R.id.notification);
            r1.performClick();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void initView(){
//        homeFragment=new HomeFragment();
//        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, homeFragment).commit();

        btn = (RadioGroup) findViewById(R.id.menu);
        btn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup,  int checkedId) {
                switch (checkedId){
                    case R.id.home:

                        if (!switchFragment(curFragment,homeFragment)){
                            //当前页面就是点击的那一个，那么就刷新
                            getFragmentManager().beginTransaction().remove(homeFragment).commit();
                            homeFragment = new HomeFragment();
                            getFragmentManager().beginTransaction().add(R.id.main_content, homeFragment).commit();
                        }
//                        Fragment fragment = HomeFragment.newInstance();
//                        getFragmentManager().beginTransaction().replace(R.id.main_content, fragment, "MainFragment").commit();
//                        Fragment homeFragment=new HomeFragment();
//                        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, homeFragment).commit();
                        break;
                    case R.id.life:

                        if (!switchFragment(curFragment,lifeFragment)){
                            //当前页面就是点击的那一个，那么就刷新
                            getFragmentManager().beginTransaction().remove(lifeFragment).commit();
                            lifeFragment = new LifeFragment();
                            getFragmentManager().beginTransaction().add(R.id.main_content, lifeFragment).commit();
                        }
//                        lifeFragment=new LifeFragment();
//                        fragment = new LifeFragment();
//                        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, lifeFragment).commit();
//                        getFragmentManager().beginTransaction().replace(R.id.main_content, fragment, "MainFragment").commit();
                        break;
                    case R.id.appointment:

                        if (!switchFragment(curFragment,appointmentFragment)){
                            //当前页面就是点击的那一个，那么就刷新
                            getFragmentManager().beginTransaction().remove(appointmentFragment).commit();
                            appointmentFragment = new AppointmentFragment();
                            getFragmentManager().beginTransaction().add(R.id.main_content, appointmentFragment).commit();
                        }
//                        fragment=new OnlineAppointmentFragment();
//                        getFragmentManager().beginTransaction().replace(R.id.main_content, fragment, "MainFragment").commit();
                        break;
                    case R.id.notification:

                        if (!switchFragment(curFragment,notificationFragment)){
                            getFragmentManager().beginTransaction().remove(notificationFragment).commit();
                            notificationFragment = new NotificationFragment();
                            getFragmentManager().beginTransaction().add(R.id.main_content, notificationFragment).commit();
                        }
//                        fragment=new NotificationFragment();
//                        getFragmentManager().beginTransaction().replace(R.id.main_content, fragment, "MainFragment").commit();
                        break;
                    case R.id.mine:

                        if (!switchFragment(curFragment,mineFragment)){
                            getFragmentManager().beginTransaction().remove(mineFragment).commit();
                            mineFragment = new MineFragment();
                            getFragmentManager().beginTransaction().add(R.id.main_content, mineFragment).commit();
                        }
//                        fragment=new MineFragment();
//                        getFragmentManager().beginTransaction().replace(R.id.main_content, fragment, "MainFragment").commit();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private boolean switchFragment(Fragment fromFragment, Fragment nextFragment) {
        if (curFragment != nextFragment) {
            curFragment = nextFragment;
            if (nextFragment != null) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                //判断 nextFragment 是否添加
                if (!nextFragment.isAdded()) {
                    //隐藏当前 Fragment
                    if (fromFragment != null) {
                        transaction.hide(fromFragment);
                    }
                    transaction.add(R.id.main_content, nextFragment).commit();
                } else {
                    //隐藏当前 Fragment
                    if (fromFragment != null) {
                        transaction.hide(fromFragment);
                    }
                    transaction.show(nextFragment).commit();
                }
            }
            return true;
        }else {
            return false;
        }
    }

}
