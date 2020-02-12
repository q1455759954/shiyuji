package com.example.administrator.shiyuji.ui.fragment.mainFragment.life;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.PicUrls;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapLoader;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapOwner;
import com.example.administrator.shiyuji.util.common.AisenUtils;
import com.example.administrator.shiyuji.util.common.utils.ImageConfigUtils;

import java.util.List;

/**
 * Created by Administrator on 2019/10/12.
 */

public class LooperPagerAdapter extends PagerAdapter{
    private Context context;
    private List<String> mPics = null;
    private StatusContent statusContent = new StatusContent();
    private  ImageView[] imageViews;
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//       container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = position%mPics.size();
        if (mPics.size()==1){
            //只有一张图片时会有问题，这里单独处理
            ImageView imageView = new ImageView(container.getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            BitmapLoader.getInstance().display((BitmapOwner) context,mPics.get(realPosition), imageView, ImageConfigUtils.getLargePhotoConfig());

            BizFragment.createBizFragment((Activity) context).previousPics(imageView, statusContent, realPosition);
            container.addView(imageView);
            return imageView;
        }

        if (imageViews[realPosition]==null){
            imageViews[realPosition] = new ImageView(container.getContext());
            imageViews[realPosition].setScaleType(ImageView.ScaleType.FIT_XY);
            BitmapLoader.getInstance().display((BitmapOwner) context,mPics.get(realPosition), imageViews[realPosition], ImageConfigUtils.getLargePhotoConfig());

            BizFragment.createBizFragment((Activity) context).previousPics(imageViews[realPosition], statusContent, realPosition);
            container.addView(imageViews[realPosition]);
        }else {
            container.removeView(imageViews[realPosition]);
            //设置数据之后，添加到容器
            container.addView(imageViews[realPosition]);
        }
//        imageView.setBackgroundColor(mColors.get(position));
//        Glide.with(container).load(mPics.get(realPosition)).into(imageView);


        return imageViews[realPosition];
    }

    @Override
    public int getCount() {
        if(mPics!=null){
            return Integer.MAX_VALUE;
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == object;
    }
    public void setData(List<String> colors, Activity activity){
        this.mPics = colors;
        PicUrls[] picUrlses = new PicUrls[mPics.size()];
        for (int i=0;i<mPics.size();i++){
            PicUrls picUrls = new PicUrls();
            picUrls.setThumbnail_pic(mPics.get(i));
            picUrlses[i]=picUrls;
        }
        statusContent.setPic_urls(picUrlses);
        context=activity;
        imageViews = new ImageView[mPics.size()];
    }
    public int getDataRealSize(){
        if(mPics!=null){
            return mPics.size();
        }
        return 0;
    }
}
