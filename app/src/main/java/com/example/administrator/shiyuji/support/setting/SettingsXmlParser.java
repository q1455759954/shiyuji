package com.example.administrator.shiyuji.support.setting;

import android.content.Context;
import android.content.res.Resources;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 读取配置文件
 * Created by Administrator on 2019/7/2.
 */

public class SettingsXmlParser {
    private static final String TAG = "AppSettingsXmlParser";

    private SettingsXmlParser() {
    }

    static Map<String, Setting> parseSettings(Context context, String fileName) {
        HashMap settingMap = new HashMap();
        ArrayList settingArray = new ArrayList();
        ArrayList settingExtras = null;
        Setting readSetting = null;
        SettingArray readSettingArray = null;
        SettingExtra readSettingExtra = null;
        XmlPullParser xmlResParser = null;

        try {
            String e = context.getPackageName();
            Resources array = context.getPackageManager().getResourcesForApplication(e);
//            Logger.d("read xml resource, filename = " + fileName);
            int setting = array.getIdentifier(fileName, "raw", e);
            xmlResParser = Xml.newPullParser();
            xmlResParser.setInput(array.openRawResource(setting), "utf-8");

            for(int eventType = xmlResParser.getEventType(); eventType != 1; eventType = xmlResParser.next()) {
                switch(eventType) {
                    case 2:
                        if("setting-array".equals(xmlResParser.getName())) {
                            readSettingArray = new SettingArray();
                            readSettingArray.setType(xmlResParser.getAttributeValue((String)null, "type"));
                            readSettingArray.setIndex(Integer.parseInt(xmlResParser.getAttributeValue((String)null, "index")));
                        }

                        if("setting".equals(xmlResParser.getName())) {
                            readSetting = new Setting();
                            readSetting.setType(xmlResParser.getAttributeValue((String)null, "type"));
                        }

                        if("extras".equals(xmlResParser.getName())) {
                            settingExtras = new ArrayList();
                        }

                        if("extra".equals(xmlResParser.getName())) {
                            readSettingExtra = new SettingExtra();
                            readSettingExtra.setType(xmlResParser.getAttributeValue((String)null, "type"));
                        }

                        if("des".equals(xmlResParser.getName())) {
                            if(readSettingExtra != null) {
                                readSettingExtra.setDescription(xmlResParser.nextText());
                            } else if(readSetting != null) {
                                readSetting.setDescription(xmlResParser.nextText());
                            } else if(readSettingArray != null) {
                                readSettingArray.setDescription(xmlResParser.nextText());
                            }
                        }

                        if("value".equals(xmlResParser.getName())) {
                            if(readSettingExtra != null) {
                                readSettingExtra.setValue(xmlResParser.nextText());
                            } else if(readSetting != null) {
                                readSetting.setValue(xmlResParser.nextText());
                            }
                        }
                        break;
                    case 3:
                        if("setting".equals(xmlResParser.getName())) {
                            if(readSetting != null) {
                                if(readSettingArray != null) {
                                    readSettingArray.getSettingArray().add(readSetting);
                                } else {
                                    settingMap.put(readSetting.getType(), readSetting);
                                }
                            }

//                            Logger.d("AppSettingsXmlParser", String.format("parse new setting --->%s", new Object[]{JSON.toJSONString(readSetting)}));
                            readSetting = null;
                        }

                        if("setting-array".equals(xmlResParser.getName())) {
                            settingArray.add(readSettingArray);
//                            Logger.d("AppSettingsXmlParser", String.format("parse new settingArray --->%s", new Object[]{JSON.toJSONString(readSettingArray)}));
                            readSettingArray = null;
                        }

                        if("extras".equals(xmlResParser.getName())) {
                            if(readSetting != null) {
                                Iterator var13 = settingExtras.iterator();

                                while(var13.hasNext()) {
                                    SettingExtra extra = (SettingExtra)var13.next();
                                    readSetting.getExtras().put(extra.getType(), extra);
                                }
                            }

                            settingExtras = null;
                        }

                        if("extra".equals(xmlResParser.getName())) {
                            settingExtras.add(readSettingExtra);
//                            Logger.d("AppSettingsXmlParser", String.format("parse new settingExtra --->%s", new Object[]{JSON.toJSONString(settingExtras)}));
                            readSettingExtra = null;
                        }
                }
            }
        } catch (Exception var18) {
//            Logger.printExc(SettingsXmlParser.class, var18);
        } finally {
            ;
        }

        Iterator e1 = settingArray.iterator();

        while(e1.hasNext()) {
            SettingArray array1 = (SettingArray)e1.next();
            if(array1.getSettingArray().size() > array1.getIndex()) {
                Setting setting1 = (Setting)array1.getSettingArray().get(array1.getIndex());
                setting1.setType(array1.getType());
                settingMap.put(setting1.getType(), setting1);
            }
        }

        return settingMap;
    }
}

