package com.example.administrator.shiyuji.ui.fragment.mainFragment.life.item;

import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.support.action.DoLikeAction;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfo;
import com.example.administrator.shiyuji.ui.fragment.support.ARecycleViewItemView;
import com.example.administrator.shiyuji.ui.widget.CircleImageView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.util.annotation.ViewInject;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.AppSettings;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * Created by Administrator on 2019/10/18.
 */

public class LifeItemView extends ARecycleViewItemView<LifeInfo> implements View.OnClickListener{

    public static final int LAYOUT_RES = R.layout.item_life_commodity;

    @ViewInject(id = R.id.imgPhoto)
    ImageView imgPhoto;
    @ViewInject(id = R.id.txt_title)
    TextView txt_title;
    @ViewInject(id = R.id.txt_content)
    TextView txt_content;
    @ViewInject(id = R.id.txt_price)
    TextView txt_price;
    @ViewInject(id = R.id.info)
    TextView info;

    //workinfo
    @ViewInject(id = R.id.tv_acv_title)
    TextView tv_acv_title;
    @ViewInject(id = R.id.tv_acv_content)
    TextView tv_acv_content;
    @ViewInject(id = R.id.iv_onlinehead)
    CircleImageView iv_onlinehead;
    @ViewInject(id = R.id.tv_user)
    TextView tv_user;
    @ViewInject(id = R.id.tv_releasetime)
    TextView tv_releasetime;

    private int textSize = 0;

    private ABaseFragment fragment;
    private BizFragment bizFragment;

    private LifeInfo data;

    public LifeItemView(View convertView, ABaseFragment fragment) {
        super(fragment.getActivity(), convertView);

        this.fragment = fragment;
        bizFragment = BizFragment.createBizFragment(fragment);

        textSize = AppSettings.getTextSize();

    }


    @Override
    public void onBindData(View convertView, LifeInfo data, int position) {
        this.data = data;
        //是商品信息
        if (data.getCommodity()!=null){
            txt_title.setText(data.getCommodity().getTitle());
            txt_content.setText(data.getCommodity().getContent());
            txt_price.setText(data.getCommodity().getPrice());
            BitmapLoader.getInstance().display(fragment, data.getCommodity().getPicUrls()[0].getThumbnail_pic(), imgPhoto, ImageConfigUtils.getLargePhotoConfig());

        }else if (data.getWorkInfo()!=null){
            info.setVisibility(View.GONE);
            tv_acv_title.setText(data.getWorkInfo().getTitle());
            tv_acv_content.setText(data.getWorkInfo().getContent());
            // desc时间
            String from = data.getWorkInfo().getCreate_at();
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//将毫秒级long值转换成日期格式
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(Long.parseLong(from));
            String dateStr = dateformat.format(gc.getTime());
            tv_releasetime.setText(dateStr);

            BitmapLoader.getInstance().display(fragment, AisenUtils.getUserPhoto(data.getWorkInfo().getUserInfo()), iv_onlinehead, ImageConfigUtils.getLargePhotoConfig());

            tv_user.setText(data.getWorkInfo().getUserInfo().getNickname());
        }

    }



    public static void setTextSize(TextView textView, float size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    @Override
    public void onClick(View v) {

    }

}
