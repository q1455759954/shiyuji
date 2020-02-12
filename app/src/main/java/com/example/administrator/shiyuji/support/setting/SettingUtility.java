package com.example.administrator.shiyuji.support.setting;

import android.content.Context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2019/7/2.
 */

public class SettingUtility {

    private static Map<String, Setting> settingMap = new HashMap();

    private SettingUtility() {
    }

    /**
     * 读取配置文件，加载配置
     * @param context
     * @param settingsXmlName
     */
    public static void addSettings(Context context, String settingsXmlName) {
        Map newSettingMap = SettingsXmlParser.parseSettings(context, settingsXmlName);
        Set keySet = newSettingMap.keySet();
        Iterator var4 = keySet.iterator();

        while(var4.hasNext()) {
            String key = (String)var4.next();
            settingMap.put(key, (Setting) newSettingMap.get(key));
        }
    }

    /**
     * 通过类型得到setting对象
     * @param type
     * @return
     */
    public static Setting getSetting(String type) {
        return settingMap.containsKey(type)?(Setting)settingMap.get(type):null;
    }

    public static String getStringSetting(String type) {
        return settingMap.containsKey(type)?((Setting)settingMap.get(type)).getValue():null;
    }

}
