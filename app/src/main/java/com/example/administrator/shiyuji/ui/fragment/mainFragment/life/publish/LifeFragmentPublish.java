package com.example.administrator.shiyuji.ui.fragment.mainFragment.life.publish;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.service.PublishService;
import com.example.administrator.shiyuji.support.bean.PublishBean;
import com.example.administrator.shiyuji.ui.activity.base.BaseActivity;
import com.example.administrator.shiyuji.ui.activity.base.SinaCommonActivity;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.Commodity;
import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfo;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.publish.AppointmentFragmentPublish;
import com.example.administrator.shiyuji.ui.fragment.picturepick.PicturePickFragment;
import com.example.administrator.shiyuji.ui.fragment.publish.APublishFragment;
import com.example.administrator.shiyuji.ui.widget.support.ImageConfig;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.TimelineBitmapCompress;
import com.example.administrator.shiyuji.ui.widget.support.download.ContentProviderDownloader;
import com.example.administrator.shiyuji.ui.widget.support.download.DownloadProcess;
import com.example.administrator.shiyuji.ui.widget.support.download.SdcardDownloader;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.FragmentArgs;
import com.example.administrator.shiyuji.util.common.utils.SystemUtils;
import com.example.administrator.shiyuji.util.viewutil.InjectUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.administrator.shiyuji.R.id.layPicContainer;
import static com.example.administrator.shiyuji.R.id.onlineTime;
import static com.example.administrator.shiyuji.R.id.scrollPicContainer;

/**
 * Created by Administrator on 2019/10/18.
 */

public class LifeFragmentPublish  extends ABaseFragment implements View.OnClickListener{


    public static void launch(Activity from) {
        FragmentArgs args = new FragmentArgs();

        SinaCommonActivity.launch(from, LifeFragmentPublish.class, args);
        AisenUtils.changeOpenActivityStyle(from);

    }

    Commodity info;

    public static final int MAX_Title_LENGTH = 15;
    public static final int MAX_Content_LENGTH = 100;

    @ViewInject(id = R.id.ic_back)
    ImageView ic_back;
    @ViewInject(id = R.id.titleErrorHint)
    TextView titleErrorHint;
    @ViewInject(id = R.id.contentErrorHint)
    TextView contentErrorHint;
    @ViewInject(id = R.id.onlineTime)
    EditText onlineTime;
    @ViewInject(id = R.id.event_title)
    EditText event_title;
    @ViewInject(id = R.id.event_content)
    EditText event_content;
    @ViewInject(id = R.id.event_price)
    EditText event_price;
    @ViewInject(id = R.id.release_event)
    TextView release_event;
    @ViewInject(id = R.id.activity_rg)
    RadioGroup activity_rg;
    @ViewInject(id = R.id.img_add)
    ImageView img_add;
    // 支持多图
    @ViewInject(id = R.id.layPicContainer)
    LinearLayout layPicContainer;
    @ViewInject(id = R.id.scrollPicContainer)
    HorizontalScrollView scrollPicContainer;

    private PublishBean bean = new PublishBean();

    public PublishBean getBean() {
        return bean;
    }

    public void setBean(PublishBean bean) {
        this.bean = bean;
    }

    @Override
    public int inflateContentView() {
        return -1;
    }

    @Override
    public int inflateActivityContentView() {
        return R.layout.ui_life_publish;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InjectUtility.initInjectedView(getActivity(), this, ((BaseActivity) getActivity()).getRootView());
        layoutInit(inflater, savedInstanceState);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        event_title.addTextChangedListener(editTitleWatcher);
        event_content.addTextChangedListener(editContentWatcher);
        release_event.setOnClickListener(release_listener);
        img_add.setOnClickListener(listener);
        return null;
    }

    View.OnClickListener release_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (titleErrorHint.getVisibility() == View.VISIBLE ) {
                showMessage(titleErrorHint.getText().toString());
                return;
            }else if (contentErrorHint.getVisibility()==View.VISIBLE){
                showMessage(contentErrorHint.getText().toString());
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
        info = new Commodity();
        info.setTitle(event_title.getText().toString());
        info.setContent(event_content.getText().toString());
        info.setOnLineTime(onlineTime.getText().toString());
        info.setCreate_at(String.valueOf(System.currentTimeMillis()));
        info.setPrice(event_price.getText().toString());

        info.setUserInfo(AppContext.getAccount().getUserInfo());
        PublishBean bean = getBean();
        LifeInfo lifeInfo = new LifeInfo();
        lifeInfo.setCommodity(info);
        bean.setLifeInfo(lifeInfo);
        bean.setPublishType(PublishBean.PublishType.life);

        PublishService.publish(getActivity(),bean);

        getActivity().finish();
    }

    /**
     * 检查输入是否为空
     * @return
     */
    boolean checkValid() {
        String title = event_title.getText().toString();

        if (TextUtils.isEmpty(title)) {
            showMessage(R.string.error_none_status);
            return false;
        }

        String content = event_content.getText().toString();

        if (TextUtils.isEmpty(content)) {
            showMessage(R.string.error_none_status);
            return false;
        }

        String price = event_price.getText().toString();

        if (TextUtils.isEmpty(price)) {
            showMessage(R.string.error_none_status);
            return false;
        }

        String time = onlineTime.getText().toString();

        if (TextUtils.isEmpty(time)) {
            showMessage(R.string.error_none_status);
            return false;
        }

        if (getBean().getPics()==null||getBean().getPics().length==0) {
            showMessage(R.string.error_pic_none);
            return false;
        }

        if (getBean().getPics()!=null&&getBean().getPics().length>3){
            showMessage(R.string.error_pics);
            return false;
        }

        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        BaseActivity activity = (BaseActivity) getActivity();
//        ((BaseActivity) getActivity()).getSupportActionBar().setTitle("活动详情");
//        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        setHasOptionsMenu(true);
    }

    /**
     * 添加图片监听器
     */
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getBean().getPics()==null){
                getBean().setPics(new String[]{});
            }
            String[] images = getBean().getPics();
            PicturePickFragment.launch(LifeFragmentPublish.this, picPickerSize(), images, 3333);
        }
    };
    int picPickerSize() {
        return 9;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
//            WeiBoUser user = (WeiBoUser) data.getSerializableExtra("bean");
//
//            Editable editAble = editContent.getEditableText();
//            int start = editContent.getSelectionStart();
//            editAble.insert(start, String.format("@%s ", user.getScreen_name()));
        }
        else if (requestCode == 3333 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String[] pics = data.getStringArrayExtra("pics");
                if (pics != null) {
                    getBean().setPics(pics);

//                    onPicChanged(pics);

                    refreshUI();
                }
            }
        }

    }

    /**
     * 刷新视图
     */
    void refreshUI() {
        if (getBean() == null)
            return;

        PublishBean bean = getBean();


        // 显示图片
        if (bean!= null && (bean.getPics() != null || bean.getParams().containsKey("url"))) {
            String[] images = bean.getPics();
            if (images == null) {
                images = new String[]{ bean.getParams().getParameter("url") };
            }

            if (TextUtils.isEmpty(event_content.getText().toString().trim()) &&
                    TextUtils.isEmpty(getBean().getText())) {
//				getPublishBean().setText(getString(R.string.publish_share_pic) + " ");
            }

            // 修改为支持多图
            if (true) {
//                layImageCover.setVisibility(View.GONE);
//                picShow.setVisibility(View.GONE);
            }

            ImageConfig config = new ImageConfig();
            config.setLoadfaildRes(R.drawable.bg_timeline_loading);
            config.setLoadingRes(R.drawable.bg_timeline_loading);
            config.setMaxWidth(SystemUtils.getScreenWidth(getActivity()));
            config.setMaxHeight(SystemUtils.getScreenHeight(getActivity()) / 2);
            config.setBitmapCompress(TimelineBitmapCompress.class);
            config.setProgress(new PublishDownloadProcess());

            if (layPicContainer != null) {
                layPicContainer.removeAllViews();
                scrollPicContainer.setVisibility(View.VISIBLE);
            }
            for (String path : images) {
                View itemView = View.inflate(getActivity(), R.layout.item_publish_pic, null);
                ImageView img = (ImageView) itemView.findViewById(R.id.img);

                itemView.setTag(path);
//                itemView.setOnClickListener(onPictureClickListener);

                if (path.toString().startsWith("content://")) {
//                    Logger.v(TAG, "相册图片地址, path = " + path);

                    config.setDownloaderClass(ContentProviderDownloader.class);
                }
                else if (path.toString().startsWith("http://") || path.toString().startsWith("https://")) {
//                    Logger.v(TAG, "网络图片地址, path = " + path);
                }
                else {
                    path = path.toString().replace("file://", "");
//                    Logger.v(TAG, "拍照图片地址, path = " + path);

                    // 扫描文件
                    SystemUtils.scanPhoto(getActivity(), new File(path));
                    config.setDownloaderClass(SdcardDownloader.class);
                }

                BitmapLoader.getInstance().display(this, path, img, config);

                if (layPicContainer != null) {
                    layPicContainer.addView(itemView,
                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        }
        else {
            if (scrollPicContainer != null)
                scrollPicContainer.setVisibility(View.GONE);
//            layImageCover.setVisibility(View.GONE);
//            picShow.setVisibility(View.GONE);
        }


    }
    /**
     * 标题内容监听，刷新提示信息
     */
    private TextWatcher editTitleWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 设置长度提示
            String content = event_title.getText().toString() + appendContent();

            if (AisenUtils.getStrLength(content) > MAX_Title_LENGTH) {
                titleErrorHint.setVisibility(View.VISIBLE);
                titleErrorHint.setText(String.format(getString(R.string.error_length_too_long), AisenUtils.getStrLength(content) - MAX_Title_LENGTH));
            }
            else {
                titleErrorHint.setVisibility(View.GONE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    };

    /**
     * 内容监听，刷新提示信息
     */
    private TextWatcher editContentWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 设置长度提示
            String content = event_content.getText().toString() + appendContent();

            if (AisenUtils.getStrLength(content) > MAX_Content_LENGTH) {
                contentErrorHint.setVisibility(View.VISIBLE);
                contentErrorHint.setText(String.format(getString(R.string.error_length_too_long), AisenUtils.getStrLength(content) - MAX_Content_LENGTH));
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceState) {
        super.layoutInit(inflater, savedInstanceState);
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    class PublishDownloadProcess extends DownloadProcess {

        @Override
        public void receiveLength(long length) {

        }

        @Override
        public void receiveProgress(long progressed) {

        }

        @Override
        public void prepareDownload(String url) {

        }

        @Override
        public void finishedDownload(byte[] bytes) {

        }

        @Override
        public void downloadFailed(Exception e) {
//            getPublishBean().getExtras().remove("images");
            String[] pics = getBean().getPics();
            List<String> picList = new ArrayList<String>();
            for (String pic : pics)
                picList.add(pic);
            picList.remove(0);
            pics = picList.toArray(new String[0]);
            if (pics.length > 0)
                getBean().setPics(pics);
            else
                getBean().setPics(null);

            showMessage(R.string.publish_pic_none);
        }

    }

}

