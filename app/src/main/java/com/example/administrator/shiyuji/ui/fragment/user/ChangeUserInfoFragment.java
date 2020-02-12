package com.example.administrator.shiyuji.ui.fragment.user;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.sdk.SinaSDK;
import com.example.administrator.shiyuji.support.action.IAction;
import com.example.administrator.shiyuji.support.bean.AccountBean;
import com.example.administrator.shiyuji.support.permissions.SdcardPermissionAction;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.activity.base.SinaCommonActivity;
import com.example.administrator.shiyuji.ui.activity.user.UserActivity;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;

import com.example.administrator.shiyuji.ui.fragment.bean.PictureSize;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;

import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.AppointmentFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.mine.MineFragment;
import com.example.administrator.shiyuji.ui.fragment.picture.PictureFragment;
import com.example.administrator.shiyuji.ui.widget.CircleImageView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;

import com.example.administrator.shiyuji.util.accountutil.AccountUtils;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.FragmentArgs;
import com.example.administrator.shiyuji.util.common.GlobalContext;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.example.administrator.shiyuji.util.task.WorkTask;
import com.example.administrator.shiyuji.util.viewutil.InjectUtility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;
import static com.example.administrator.shiyuji.support.setting.SettingUtility.getSetting;

/**
 * Created by Administrator on 2019/9/12.
 */

public class ChangeUserInfoFragment extends ABaseFragment implements View.OnClickListener{

    public static final int REQUEST_CODE_PHOTO = 1012;
    private final int CHANGE_INTRODUCE = 1001;
    private final int CHANGE_NAME = 1002;

    public static void launch(Activity from, UserInfo userInfo) {
        UserActivity userActivity = (UserActivity) from;
        FragmentArgs args = new FragmentArgs();

        args.add("userInfo", userInfo);

        SinaCommonActivity.launchForResult(from, ChangeUserInfoFragment.class, args,1);
        AisenUtils.changeOpenActivityStyle(from);

    }
    @ViewInject(id = R.id.appbar)
    AppBarLayout appBarLayout;
    @ViewInject(id = R.id.avatar)
    RelativeLayout avatar;
    @ViewInject(id = R.id.name)
    RelativeLayout name;
    @ViewInject(id = R.id.gender)
    RelativeLayout gender;
    @ViewInject(id = R.id.birthday)
    RelativeLayout birthday;
    @ViewInject(id = R.id.address)
    RelativeLayout address;
    @ViewInject(id = R.id.school)
    RelativeLayout school;
    @ViewInject(id = R.id.introduce)
    RelativeLayout introduce;

    @ViewInject(id = R.id.avatar_)
    CircleImageView avatar_;
    @ViewInject(id = R.id.name_)
    TextView name_;
    @ViewInject(id = R.id.gender_)
    TextView gender_;
    @ViewInject(id = R.id.birthday_)
    TextView birthday_;
    @ViewInject(id = R.id.address_)
    TextView address_;
    @ViewInject(id = R.id.school_)
    TextView school_;
    @ViewInject(id = R.id.introduce_)
    TextView introduce_;
    @ViewInject(id = R.id.done)
    TextView done;

    private UserActivity userActivity;
    private UserInfo userInfo;
    Calendar calendar = Calendar.getInstance();
    private Bitmap bitmap;
    private boolean ifChangedAvatar = false;

    @Override
    public int inflateContentView() {
        return -1;
    }

    @Override
    public int inflateActivityContentView() {
        return R.layout.ui_mine_detail_info;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InjectUtility.initInjectedView(getActivity(), this, ((BaseActivity) getActivity()).getRootView());
        layoutInit(inflater, savedInstanceState);

        return null;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userInfo = savedInstanceState != null ? (UserInfo) savedInstanceState.getSerializable("userInfo")
                : (UserInfo) getArguments().getSerializable("userInfo");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        BaseActivity activity = (BaseActivity) getActivity();
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle("我的资料");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setHasOptionsMenu(true);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("userInfo", userInfo);
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceState) {
        super.layoutInit(inflater, savedInstanceState);
        BitmapLoader.getInstance().display(this, AisenUtils.getUserPhoto(userInfo), avatar_, ImageConfigUtils.getLargePhotoConfig());
        name_.setText(userInfo.getNickname());
        gender_.setText(userInfo.getGender());
        birthday_.setText(userInfo.getBirthday());
        school_.setText(userInfo.getSchool().equals("")?"还没有设置学校。":userInfo.getSchool());
        introduce_.setText(userInfo.getIntroduce().equals("")?"还没有简介。":userInfo.getIntroduce());


        avatar.setOnClickListener(this);
        gender.setOnClickListener(this);
        birthday.setOnClickListener(this);
        name.setOnClickListener(this);
        introduce.setOnClickListener(this);
        done.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.avatar:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                ChangeUserInfoFragment.this.startActivityForResult(intent, ChangeUserInfoFragment.REQUEST_CODE_PHOTO);
                break;
            case R.id.gender:
                showChooseGender();
                break;
            case R.id.birthday:
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        CharSequence date = DateFormat.format("yyy-MM-dd", calendar);
                        birthday_.setText(date.toString());
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            case R.id.name:
                ChangeUserName.launch(ChangeUserInfoFragment.this,userInfo,CHANGE_NAME);
                break;
            case R.id.introduce:
                ChangeUserIntroduce.launch(ChangeUserInfoFragment.this,userInfo,CHANGE_INTRODUCE);
                break;
            case R.id.done:
                new UpdateInfoTask().execute();
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_PHOTO:
                    Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
                    avatar_.setImageURI(uri);
                    try {
                        bitmap = getBitmapFormUri(getActivity(),uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ifChangedAvatar = true;
                    break;
                case CHANGE_NAME:
                    String nameIntent = data.getStringExtra("nameIntent");
                    name_.setText(nameIntent);
                    break;
                case CHANGE_INTRODUCE:
                    String introduceIntent = data.getStringExtra("introduceIntent");
                    introduce_.setText(introduceIntent);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }



    private void showChooseGender() {
        new IAction(getActivity(), new SdcardPermissionAction(((BaseActivity) getActivity()), null)) {

            @Override
            public void doAction() {
                new MaterialDialog.Builder(getActivity())
                        .items(R.array.gender)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                switch (which) {
                                    // 男
                                    case 0:
                                        gender_.setText("男");
                                        break;
                                    // 女
                                    case 1:
                                        gender_.setText("女");
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .show();
            }
        }.run();
    }

    /**
     * 通过uri获取图片并进行压缩
     *
     * @param uri
     */
    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    class UpdateInfoTask extends WorkTask<Void, Void, UserInfo> {

        @Override
        public UserInfo workInBackground(Void... params) throws TaskException {
            UserInfo userInfo = new UserInfo();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (bitmap!=null){
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            }
            byte[] datas = baos.toByteArray();

//            userInfo.setImg(datas);
            userInfo.setAvatar(Base64.encodeToString(datas, Base64.DEFAULT));
            userInfo.setNickname(name_.getText().toString());
            userInfo.setGender(gender_.getText().toString());
            userInfo.setBirthday(birthday_.getText().toString());
            userInfo.setIdentifier(AppContext.getAccount().getUserInfo().getIdentifier());

            if (ifChangedAvatar){
                userInfo.setIfChangedAvatar(true);
            }else {
                userInfo.setAvatar(AppContext.getAccount().getUserInfo().getAvatar());
            }
            userInfo = SinaSDK.getInstance(AppContext.getAccount().getAccessToken()).updateUserInfo(userInfo);

            return userInfo;
        }

        @Override
        protected void onSuccess(UserInfo userInfo) {
            super.onSuccess(userInfo);

            if (userInfo!=null&&userInfo.getId()!=0){
                Setting action = getSetting("base_url");
                String base_url = action.getValue().split("/")[2].split(":")[0];

                userInfo.setAvatar(userInfo.getAvatar().replaceAll("localhost",base_url));
                AccountBean accountBean = AppContext.getAccount();
                accountBean.setUserInfo(userInfo);
                AccountUtils.setLogedinAccount(accountBean);
                AppContext.setAccount(accountBean);//保存当前账户
                GlobalContext.getInstance().sendBroadcast(new Intent(UserActivity.CHANGE_USER_INFO));

            }else {
                Toast.makeText(getActivity(),"修改资料失败",Toast.LENGTH_SHORT).show();
            }

        }
    }

}
