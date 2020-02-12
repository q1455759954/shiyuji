package com.example.administrator.shiyuji.util.common.utils;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.ui.widget.support.DefaultDisplayer;
import com.example.administrator.shiyuji.ui.widget.support.ImageConfig;

/**
 * Created by Administrator on 2019/7/4.
 */

public class ImageConfigUtils {

    public static ImageConfig getPhotoConfig() {
        ImageConfig config = new ImageConfig();

        config.setLoadingRes(R.drawable.user_placeholder);
        config.setLoadfaildRes(R.drawable.user_placeholder);
        config.setDisplayer(new DefaultDisplayer());

        return config;
    }

    public static ImageConfig getLargePhotoConfig() {
        ImageConfig config = new ImageConfig();

        config.setId("large");
        config.setDisplayer(new DefaultDisplayer());
        config.setLoadingRes(R.drawable.user_placeholder);
        config.setLoadfaildRes(R.drawable.user_placeholder);
//		config.setMaxWidth(GlobalContext.getInstance().getResources().getDimensionPixelSize(R.dimen.menu_icon));

        return config;
    }

    public static ImageConfig getPhotoCoverConfig() {
        ImageConfig config = new ImageConfig();

        config.setLoadingRes(R.drawable.bg_banner_dialog);
        config.setLoadfaildRes(R.drawable.bg_banner_dialog);

        return config;
    }

}

