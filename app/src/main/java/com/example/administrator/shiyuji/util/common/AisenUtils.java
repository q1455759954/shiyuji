package com.example.administrator.shiyuji.util.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.support.action.DeleteAction;
import com.example.administrator.shiyuji.support.action.DoLikeAction;
import com.example.administrator.shiyuji.support.bean.LikeBean;
import com.example.administrator.shiyuji.support.setting.SettingUtility;
import com.example.administrator.shiyuji.support.sqlit.LikeDB;
import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.base.BizFragment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.menu.CardMenuBuilder;
import com.example.administrator.shiyuji.ui.fragment.menu.CardMenuOptions;
import com.example.administrator.shiyuji.ui.fragment.report.ReportFragment;
import com.example.administrator.shiyuji.ui.fragment.timeline.TimelineDefFragment;
import com.example.administrator.shiyuji.ui.fragment.timeline.TimelineItemView;
import com.example.administrator.shiyuji.ui.widget.support.bitmap.BitmapDecoder;
import com.example.administrator.shiyuji.util.common.utils.FileUtils;
import com.example.administrator.shiyuji.util.common.utils.SystemUtils;
import com.example.administrator.shiyuji.util.network.bean.Params;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2019/6/29.
 */

public class AisenUtils {


    public static CardMenuOptions getCardMenuOptions() {
        return getCardMenuOptions(1);
    }

    public static CardMenuOptions getCardMenuOptions(int theme) {
        return new CardMenuOptions(theme,
                android.support.v7.appcompat.R.attr.actionOverflowMenuStyle,
                android.support.v7.appcompat.R.layout.abc_action_menu_layout,
                android.support.v7.appcompat.R.layout.abc_action_menu_item_layout);
    }


    /**
     * 动态菜单点击事件
     * @param fragment
     * @param bizFragment
     * @param selectedItem
     * @param status
     * @param timelineItemView
     */
    public static void timelineMenuSelected(final ABaseFragment fragment, BizFragment bizFragment, String selectedItem, final StatusContent status, TimelineItemView timelineItemView) {
        final String[] timelineMenuArr = GlobalContext.getInstance().getResources().getStringArray(R.array.timeline_menus);

        try {
            int position = 0;
            for (int i = 0; i < timelineMenuArr.length; i++) {
                if (timelineMenuArr[i].equals(selectedItem)) {
                    position = i;
                    break;
                }
            }

            switch (position) {
                // 收藏
                case 0:
                    StatusContent s = new StatusContent();
                    s.setFavorited(status.getFavorited());
                    s.setRow_key(status.getRow_key()+"_collect");
                    s.setCollect(status.getCollect());

                    LikeBean likeBean = DoLikeAction.likeCache.get(s.getRow_key()+"_collect");
                    if (likeBean==null){
                        likeBean= LikeDB.get(s.getRow_key()+"_collect");
                    }
                    boolean like = likeBean == null? !s.getCollect():!likeBean.isLiked();

                    bizFragment.doLike(s, like, null, timelineItemView);
                    break;
                // 取消收藏
                case 1:
                    StatusContent sa = new StatusContent();
                    sa.setFavorited(status.getFavorited());
                    sa.setRow_key(status.getRow_key()+"_collect");
                    sa.setCollect(status.getCollect());

                    LikeBean bean = DoLikeAction.likeCache.get(sa.getRow_key()+"_collect");
                    if (bean==null){
                        bean= LikeDB.get(sa.getRow_key()+"_collect");
                    }
                    boolean r = bean == null? !sa.getCollect():!bean.isLiked();

                    bizFragment.doLike(sa, r, null, timelineItemView);
                    break;
                // 删除
                case 2:
                    Params params = new Params();
                    params.addParameter("row_key",status.getRow_key());
                    params.addParameter("user", String.valueOf(status.getUserInfo().getId()));
                    params.addParameter("type","time_line");
                    DeleteAction action = new DeleteAction(bizFragment.getActivity(),params);
                    action.run();
                    TimelineDefFragment timelineDefFragment = (TimelineDefFragment) fragment;
                    timelineDefFragment.getAdapter().getDatas().remove(status);
                    timelineDefFragment.getAdapter().notifyDataSetChanged();

                    break;
                // 投诉
                case 3:
                    ReportFragment.launch(fragment.getActivity(),status.getUserInfo(),status.getRow_key(),"status");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示菜单
     * @param fragment
     * @param targetView
     * @param menuArr
     * @param onItemClickListener
     */
    public static void showMenuDialog(final ABaseFragment fragment, final View targetView,
                                      String[] menuArr, final DialogInterface.OnClickListener onItemClickListener) {
        CardMenuBuilder builder = new CardMenuBuilder(fragment.getActivity(), targetView, getCardMenuOptions());
        for (int i = 0; i < menuArr.length; i++) {
            builder.add(i, menuArr[i]);
        }
        builder.setOnCardMenuCallback(new CardMenuBuilder.OnCardMenuCallback() {

            @Override
            public boolean onCardMenuItemSelected(MenuItem menuItem) {
                onItemClickListener.onClick(null, menuItem.getItemId());
                return true;
            }

        });
        builder.show();
//        new AlertDialogWrapper.Builder(fragment.getActivity())
//                .setItems(menuArr, onItemClickListener)
//                .show();
    }

    /**
     * 加载关闭activity时的动态效果
     * @param activity
     */
    public static void changeOpenActivityStyle(Activity activity) {
        //加载关闭activity时的动态效果
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    /**
     * 计算文本有几个字
     * @param content
     * @return
     */
    public static int getStrLength(String content) {
        if (TextUtils.isEmpty(content))
            return 0;

        int length = 0;
        int tempLength = 0;
        for (int i = 0; i < content.length(); i++) {
            String temp = content.charAt(i) + "";//char转化为String
            if (temp.getBytes().length == 3) {
                length++;
            } else {
                tempLength++;//字母或符号，两个算一个字
            }
        }
        length += tempLength / 2 + ((tempLength % 2) == 0 ? 0 : 1);
        return length;
    }

    /**
     * 返回用户昵称
     * @param user
     * @return
     */
    public static String getUserScreenName(UserInfo user) {
        return user.getNickname();
    }

    public static String getId(Object t) {
        try {
            Field idField = t.getClass().getDeclaredField("row_key");
            idField.setAccessible(true);
            return idField.get(t).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 显示高清头像
     *
     * @param user
     * @return
     */
    public static String getUserPhoto(UserInfo user) {
        if (user == null)
            return "";

//        if (AppSettings.isLargePhoto() && !TextUtils.isEmpty(user.getAvatar_large())) {
//            return user.getAvatar_large();
//        }

        return user.getAvatar();
    }


    public static String getCounter(int count, String append) {
        Resources res = GlobalContext.getInstance().getResources();
        int num = append.equals("")?0:1;

        if (count < 10000)
            return String.valueOf(count+num);
        else if (count < 100 * 10000)
            return new DecimalFormat("#.0").format(count * 1.0f / 10000) + append + res.getString(R.string.msg_ten_thousand);
        else
            return new DecimalFormat("#").format(count * 1.0f / 10000) + append + res.getString(R.string.msg_ten_thousand);
    }


    /**
     * 压缩文件
     * @param context
     * @param source
     * @return
     */
    public static File getUploadFile(Context context, File source) {

        if (source.getName().toLowerCase().endsWith(".gif")) {
            return source;
        }

        File file = null;

        String imagePath = GlobalContext.getInstance().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + File.separator +
                SettingUtility.getStringSetting("draft") + File.separator;

        int sample = 1;
        int maxSize = 0;

        int type = 2;
        // 自动，WIFI时原图，移动网络时高
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(source.getAbsolutePath(), opts);
        switch (type) {
            // 原图
            case 1:
                file = source;
                break;
            // 高
            case 2:
                sample = BitmapDecoder.calculateInSampleSize(opts, 1920, 1080);
                maxSize = 700 * 1024;
                imagePath = imagePath + "高" + File.separator + source.getName();
                file = new File(imagePath);
                break;
            // 中
            case 3:
                sample = BitmapDecoder.calculateInSampleSize(opts, 1280, 720);
                maxSize = 300 * 1024;
                imagePath = imagePath + "中" + File.separator + source.getName();
                file = new File(imagePath);
                break;
            // 低
            case 4:
                sample = BitmapDecoder.calculateInSampleSize(opts, 1280, 720);
                maxSize = 100 * 1024;
                imagePath = imagePath + "低" + File.separator + source.getName();
                file = new File(imagePath);
                break;
            default:
                break;
        }

        // 压缩图片
        if (type != 1 && !file.exists()) {
            byte[] imageBytes = FileUtils.readFileToBytes(source);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                out.write(imageBytes);
            } catch (Exception e) {
            }

            if (imageBytes.length > maxSize) {
                // 尺寸做压缩
                BitmapFactory.Options options = new BitmapFactory.Options();

                if (sample > 1) {
                    options.inSampleSize = sample;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
                    out.reset();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    imageBytes = out.toByteArray();
                }

                options.inSampleSize = 1;
                if (imageBytes.length > maxSize) {
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);

                    int quality = 90;
                    out.reset();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
                    while (out.toByteArray().length > maxSize) {
                        out.reset();
                        quality -= 10;
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
                    }
                }

            }

            try {
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();

                FileOutputStream fo = new FileOutputStream(file);
                fo.write(out.toByteArray());
                fo.flush();
                fo.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return file;
    }
}
