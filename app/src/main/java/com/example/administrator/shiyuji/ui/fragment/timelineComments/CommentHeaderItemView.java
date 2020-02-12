package com.example.administrator.shiyuji.ui.fragment.timelineComments;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.AppSettings;
import com.example.administrator.shiyuji.util.common.GlobalContext;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusComment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.support.ARecycleViewItemView;
import com.example.administrator.shiyuji.ui.widget.MyTextView;
import com.example.administrator.shiyuji.ui.widget.TimelinePicsView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.Utils;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * Created by Administrator on 2019/7/4.
 */

public class CommentHeaderItemView extends ARecycleViewItemView<StatusComment> implements View.OnClickListener {

    public static final int COMMENT_HEADER_01 = 1001;

    public static final int COMMENT_HEADER_01_RES = R.layout.item_timeline_comment_header;

    @ViewInject(id = R.id.imgPhoto)
    ImageView imgPhoto;
    @ViewInject(id = R.id.txtName)
    TextView txtName;
    @ViewInject(id = R.id.imgVerified)
    ImageView imgVerified;
    @ViewInject(id = R.id.txtDesc)
    TextView txtDesc;

    @ViewInject(id = R.id.txtContent)
    MyTextView txtContent;

    @ViewInject(id = R.id.layRe)
    View layRe;

    @ViewInject(id = R.id.txtReContent)
    MyTextView txtReContent;

    @ViewInject(id = R.id.layPicturs)
    public TimelinePicsView layPicturs;

    @ViewInject(id = R.id.txtPics)
    TextView txtPics;
    @ViewInject(id = R.id.layReStatusContainer)
    View layReStatusContainer;

    private int textSize = 0;
    private static Map<String, String> groupMap;
    private int vPadding;

    private ABaseFragment fragment;

    private StatusContent statusContent;

    public CommentHeaderItemView(ABaseFragment fragment, View itemView, StatusContent statusContent) {
        super(fragment.getActivity(), itemView);

        this.fragment = fragment;
        this.statusContent = statusContent;

        textSize = AppSettings.getTextSize();
        vPadding = GlobalContext.getInstance().getResources().getDimensionPixelSize(R.dimen.comm_v_gap);
//
//        Groups groups = AppContext.getAccount().getGroups();
//        if (groups != null && (groupMap == null || groupMap.size() != groups.getLists().size())) {
//            groupMap = new HashMap<>();
//
//            for (Group group : groups.getLists())
//                groupMap.put(group.getIdstr(), group.getName());
//        }

        onBindView(itemView);
        onBindData(itemView, null, 0);
    }

    @Override
    public void onBindData(View convertView, StatusComment comment, int position) {
        UserInfo user = statusContent.getUserInfo();
        // userInfo
        setUserInfo(user, txtName, imgPhoto, imgVerified);

        // desc
//        String createAt = "";
//        if (!TextUtils.isEmpty(statusContent.getCreated_at()))
//            createAt = AisenUtils.convDate(statusContent.getCreated_at());

        String from = statusContent.getCreated_at();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//将毫秒级long值转换成日期格式
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(Long.parseLong(from));
        String dateStr = dateformat.format(gc.getTime());
        txtDesc.setText(dateStr);

//        if (!TextUtils.isEmpty(statusContent.getSource()))
//            from = String.format("%s", Html.fromHtml(statusContent.getSource()));

        // 文本
        txtContent.setContent(statusContent.getText());
        setTextSize(txtContent, 50);

        // reContent
        StatusContent reContent = statusContent.getRetweeted_status();
        if (reContent == null) {
            layRe.setVisibility(View.GONE);
        }
        else {
            layRe.setVisibility(View.VISIBLE);
            layRe.setTag(reContent);

            UserInfo reUser = reContent.getUserInfo();

            String reUserName = "";
            if (reUser != null && !TextUtils.isEmpty(reUser.getNickname()))
                reUserName = String.format("@%s :", reUser.getNickname());
            txtReContent.setContent(reUserName + reContent.getText());
            // 正文
            setTextSize(txtReContent, 50);
        }

        layPicturs.setPics(statusContent, fragment);

        // 如果没有原微博和图片，把bottom的间隙都去掉
        if (statusContent.getRetweeted_status() == null &&
                (statusContent.getPic_urls() == null || statusContent.getPic_urls().length == 0)) {
            txtContent.setPadding(txtContent.getPaddingLeft(), txtContent.getPaddingTop(), txtContent.getPaddingRight(), 0);
                layReStatusContainer.setVisibility(View.GONE);
        }
        // 如果没有图片，有原微博，底部加点空隙
        if (statusContent.getRetweeted_status() != null &&
                (statusContent.getPic_urls() == null || statusContent.getPic_urls().length == 0)) {
            txtReContent.setPadding(txtReContent.getPaddingLeft(), txtReContent.getPaddingTop(), txtReContent.getPaddingRight(), Utils.dip2px(getContext(), 8));
        }
    }

    private void setUserInfo(UserInfo user, TextView txtName, ImageView imgPhoto, ImageView imgVerified) {
        if (user != null) {
            txtName.setText(AisenUtils.getUserScreenName(user));

            if (imgPhoto != null) {
                BitmapLoader.getInstance().display(fragment, AisenUtils.getUserPhoto(user), imgPhoto, ImageConfigUtils.getLargePhotoConfig());
            }
            imgVerified.setVisibility(View.GONE);
        }
    }

    public static void setTextSize(TextView textView, float size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    @Override
    public void onClick(View v) {

    }

}
