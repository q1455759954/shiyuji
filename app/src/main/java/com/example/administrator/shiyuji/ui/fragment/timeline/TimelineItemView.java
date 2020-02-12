package com.example.administrator.shiyuji.ui.fragment.timeline;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.support.action.DoLikeAction;
import com.example.administrator.shiyuji.support.bean.LikeBean;
import com.example.administrator.shiyuji.support.bean.PublishBean;
import com.example.administrator.shiyuji.support.sqlit.LikeDB;
import com.example.administrator.shiyuji.ui.activity.publish.PublishActivity;
import com.example.administrator.shiyuji.ui.activity.user.UserActivity;
import com.example.administrator.shiyuji.ui.fragment.timelineComments.TimelineDetailPagerFragment;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.AppSettings;
import com.example.administrator.shiyuji.util.common.GlobalContext;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.support.ARecycleViewItemView;
import com.example.administrator.shiyuji.ui.widget.MyTextView;
import com.example.administrator.shiyuji.ui.widget.TimelinePicsView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * 微博内容
 * Created by Administrator on 2019/6/29.
 */


public class TimelineItemView extends ARecycleViewItemView<StatusContent> implements View.OnClickListener, DoLikeAction.OnLikeCallback {

    public static final int LAYOUT_RES = R.layout.item_timeline;

    @ViewInject(id = R.id.cardView)
    CardView cardView;

    @ViewInject(id = R.id.imgPhoto)
    ImageView imgPhoto;
    @ViewInject(id = R.id.imgVideo)
    JCVideoPlayerStandard imgVideo;
    @ViewInject(id = R.id.txtName)
    TextView txtName;
    @ViewInject(id = R.id.imgVerified)
    ImageView imgVerified;
    @ViewInject(id = R.id.txtDesc)
    TextView txtDesc;

    @ViewInject(id = R.id.btnLike)
    View btnLike;
    @ViewInject(id = R.id.imgLike)
    ImageView imgLike;
    @ViewInject(id = R.id.txtLike)
    TextView txtLike;
    @ViewInject(id = R.id.btnRepost)
    View btnRepost;
    @ViewInject(id = R.id.txtRepost)
    protected TextView txtRepost;
    @ViewInject(id = R.id.btnCmt)
    View btnComment;
    @ViewInject(id = R.id.txtComment)
    protected TextView txtComment;
    @ViewInject(id = R.id.layPicturs)
    TimelinePicsView layPicturs;
    @ViewInject(id = R.id.txtContent)
    MyTextView txtContent;


    @ViewInject(id = R.id.layRe)
    View layRe;

    @ViewInject(id = R.id.txtReContent)
    MyTextView txtReContent;

    @ViewInject(id = R.id.btnMenus)
    View btnMenus;


    private int textSize = 0;
    private static Map<String, String> groupMap;
    private int vPadding;

    private ABaseFragment fragment;
    private BizFragment bizFragment;

    private StatusContent data;

    public TimelineItemView(View convertView, ABaseFragment fragment) {
        super(fragment.getActivity(), convertView);

        this.fragment = fragment;
        bizFragment = BizFragment.createBizFragment(fragment);

        textSize = AppSettings.getTextSize();

    }


    @Override
    public void onBindData(View convertView, StatusContent data, int position) {
        this.data = data;

        final UserInfo user = data.getUserInfo();

        // userInfo
        setUserInfo(user, txtName, imgPhoto, imgVerified);

        // desc时间
        String from = data.getCreated_at();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//将毫秒级long值转换成日期格式
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(Long.parseLong(from));
        String dateStr = dateformat.format(gc.getTime());


//        if (!TextUtils.isEmpty(data.getSource()))
//            from = String.format("%s", Html.fromHtml(data.getSource()));
        txtDesc.setText(dateStr);

        // counter
        txtRepost.setText(String.valueOf(data.getReposts_count()));
        txtComment.setText(String.valueOf(data.getComments_count()));
        txtLike.setText(String.valueOf(data.getAttitudes_count()));



        //点赞信息
        setLikeView();

        imgVideo.setVisibility(View.GONE);
        //设置视频信息
        if (data.getVideoInfo()!=null&&data.getVideoInfo().getTitle()!=null){
            imgVideo.setVisibility(View.VISIBLE);
            boolean setUp = imgVideo.setUp(data.getVideoInfo().getVideo_url(), JCVideoPlayer.SCREEN_LAYOUT_LIST, data.getVideoInfo().getTitle());
            if (setUp) {
                Glide.with(getContext()).load(data.getVideoInfo().getPic_url()).into(imgVideo.thumbImageView);
            }
        }


        if (btnRepost != null) {
            btnRepost.setTag(data);
            btnRepost.setOnClickListener(this);//转发监听器

            if (btnComment != null) {
                btnComment.setTag(data);
                btnComment.setOnClickListener(this);//评论监听器
            }
            btnLike.setOnClickListener(this);//点赞按钮监听器
//        setLikeView();
            // 文本
            String text = data.getText();
            txtContent.setContent(text);
            setTextSize(txtContent, 50);

            // reContent
            StatusContent reContent = data.getRetweeted_status();
            if (reContent == null) {
                layPicturs.setPics(data, fragment);
                layRe.setVisibility(View.GONE);
            } else {
                layRe.setVisibility(View.VISIBLE);
                layRe.setTag(reContent);

                UserInfo reUser = reContent.getUserInfo();

                String reUserName = "";
                if (reUser != null && !TextUtils.isEmpty(reUser.getNickname()))
                    reUserName = String.format("@%s :", reUser.getNickname());
                txtReContent.setContent(reUserName + reContent.getText());

                layPicturs.setPics(reContent, fragment);

                // 正文
//                setTextSize(txtReContent, textSize);
//                txtReContent.setMovementMethod(LinkMovementMethod.getInstance());// 此行必须，否则超链接无法点
            }


            btnMenus.setTag(data);
            btnMenus.setOnClickListener(this);

            //是在个人主页就不设置监听器
            if (data.isHotStatus()){
                imgPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), UserActivity.class);
                        intent.putExtra("userInfo",user);
                        getContext().startActivity(intent);
                    }
                });
            }
        }
    }


    /**
     * 显示收藏成功结果
     */
    public void setCollectResult(){
        LikeBean collect = DoLikeAction.likeCache.get(data.getRow_key()+"_collect");
        if (collect==null){
            collect= LikeDB.get(data.getRow_key()+"_collect");
        }
        if (collect != null && collect.isLiked()) {
            Toast.makeText(getContext(),"收藏成功",Toast.LENGTH_SHORT).show();
        }else if (collect==null && data.getCollect()){
            Toast.makeText(getContext(),"收藏成功",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(),"取消收藏成功",Toast.LENGTH_SHORT).show();
        }
        setLikeView();
    }

    /**
     * 设置点赞信息和收藏信息
     * 收藏信息也用的likeAction，参数为rowkey_collect
     */
    public void setLikeView() {
        LikeBean likeBean = DoLikeAction.likeCache.get(data.getRow_key());
        if (likeBean==null){
            likeBean= LikeDB.get(data.getRow_key());
        }
        if (btnLike != null) {
            btnLike.setTag(data);
            btnLike.setOnClickListener(this);

            if (likeBean != null && likeBean.isLiked()) {
                imgLike.setSelected(true);

                if (data.getAttitudes_count() > 0)
                    txtLike.setText(AisenUtils.getCounter(data.getAttitudes_count(), "+1"));
                else
                    txtLike.setText("1");
            }else if (likeBean==null && data.getFavorited()){
                imgLike.setSelected(data.getFavorited());

                if (data.getAttitudes_count() > 0)
                    txtLike.setText(AisenUtils.getCounter(data.getAttitudes_count(), "+1"));
                else
                    txtLike.setText("1");
            } else {
                imgLike.setSelected(false);

                if (data.getAttitudes_count() > 0)
                    txtLike.setText(AisenUtils.getCounter(data.getAttitudes_count() , ""));
                else
                    txtLike.setText("");
            }
        }
        //收藏信息
        LikeBean collect = DoLikeAction.likeCache.get(data.getRow_key()+"_collect");
        if (collect==null){
            collect= LikeDB.get(data.getRow_key()+"_collect");
        }
        if (collect != null && collect.isLiked()) {
            data.setCollect(true);
        }else if (collect==null && data.getCollect()){
            data.setCollect(true);
        } else {
            data.setCollect(false);
        }
    }


    private void setUserInfo(final UserInfo user, TextView txtName, ImageView imgPhoto, ImageView imgVerified) {
        if (user != null) {
            txtName.setText(AisenUtils.getUserScreenName(user));

            if (imgPhoto != null) {
                BitmapLoader.getInstance().display(fragment, AisenUtils.getUserPhoto(user), imgPhoto, ImageConfigUtils.getLargePhotoConfig());
            }

        }

    }

    public static void setTextSize(TextView textView, float size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            Log.i("TAG", "键盘code---" + keyCode);
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss();
                return false;
            } else if(keyCode == KeyEvent.KEYCODE_DEL){//删除键
//                if(dialog != null){
//                    mCustomDialog.del();
//                }
                return false;
            }else{
                return true;

            }
        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnRepost:
                String re_txt = "转发"+"<a href='/n/BanneyZ'>"+"@"+data.getUserInfo().getNickname()+"</a>的微博";
                PublishBean bean = new PublishBean();
                bean.setRe_txt(re_txt);
                bean.setRe_rowKey(data.getRow_key());

                PublishActivity.publishStatus(getContext(),bean);
                break;
            case R.id.btnCmt:
//                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(findViewById(R.id.test_bug), InputMethodManager.RESULT_SHOWN);
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//
                TimelineDetailPagerFragment.launch(getContext(),data);
                break;
            case R.id.btnLike:
                StatusContent s = (StatusContent) v.getTag();

                LikeBean likeBean = DoLikeAction.likeCache.get(s.getRow_key());
                if (likeBean==null){
                    likeBean=LikeDB.get(s.getRow_key());
                }
                boolean like = likeBean == null? !s.getFavorited():!likeBean.isLiked();

                v.findViewById(R.id.imgLike).setSelected(like);

                bizFragment.doLike(s, like, v, this);
                break;
            case R.id.btnMenus:
                final String[] timelineMenuArr = GlobalContext.getInstance().getResources().getStringArray(R.array.timeline_menus);
                final StatusContent status = (StatusContent) v.getTag();

                List<String> menuList = new ArrayList<String>();
//			menuList.add(timelineMenuArr[3]);
//			if (status.getVisible() == null || "0".equals(status.getVisible().getType()))
//				menuList.add(timelineMenuArr[2]);
                //判断是否收藏,
                if (!status.getCollect()){
                    menuList.add(timelineMenuArr[0]);
                }else {
                    menuList.add(timelineMenuArr[1]);
                }

                menuList.add(timelineMenuArr[3]);
                if (status.getUserInfo() != null && status.getUserInfo().getId().equals(AppContext.getAccount().getUserInfo().getId()))
                    menuList.add(timelineMenuArr[2]);

//                if (fragment instanceof MentionTimelineFragment)
//                    menuList.add(timelineMenuArr[7]);

                final String[] menus = new String[menuList.size()];
                for (int i = 0; i < menuList.size(); i++)
                    menus[i] = menuList.get(i);

                AisenUtils.showMenuDialog(fragment,
                        v,
                        menus,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AisenUtils.timelineMenuSelected(fragment,bizFragment, menus[which], status,TimelineItemView.this);
                            }
                        });
                break;
        }
    }



    @Override
    public void onLikeFaild(BizFragment.ILikeBean data) {
        setLikeView();
    }

    @Override
    public void onLikeSuccess(BizFragment.ILikeBean data, View likeView) {
        if (fragment.getActivity() == null)
            return;

        if (data.getLikeId().endsWith("_collect")){
            setCollectResult();
            return;
        }

        setLikeView();

        if (likeView.getTag() == data) {
            bizFragment.animScale(likeView);
        }
    }
}
