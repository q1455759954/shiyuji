package com.example.administrator.shiyuji.util.viewutil;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.administrator.shiyuji.util.annotation.OnClick;
import com.example.administrator.shiyuji.util.annotation.ViewInject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2019/6/28.
 */


public class InjectUtility {
    static final String TAG = "InjectUtility";

    public InjectUtility() {
    }

    public static void initInjectedView(Activity sourceActivity) {
        initInjectedView(sourceActivity, sourceActivity, sourceActivity.getWindow().getDecorView());
    }

    public static void initInjectedView(Context context, final Object injectedSource, View sourceView) {
        long start = System.currentTimeMillis();


        for (Class clazz = injectedSource.getClass();clazz!=Object.class && !clazz.getName().startsWith("android");clazz=clazz.getSuperclass()){
            Method[] methods = clazz.getDeclaredMethods();
            Method[] fields = methods;
            int length = fields.length;

            for (int i=0;i<length;++i){
                final Method method = fields[i];
                Class[] field = method.getParameterTypes();
                if (field!=null&&field.length==1&&field[0].getName().equals(View.class.getName())){
                    OnClick viewInject = (OnClick) method.getAnnotation(OnClick.class);
                    if (viewInject!=null){
                        int[] viewId = viewInject.value();
                        int[] e = viewId;
                        int packageName = viewId.length;

                        for (int j=0;j<packageName;++j){
                            int id = e[j];
                            if (id!=-1){
                                View view = sourceView.findViewById(id);
                                if (view!=null){
                                    view.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                method.setAccessible(true);
                                                method.invoke(injectedSource,new Object[]{v});
                                            } catch (IllegalAccessException e1) {
                                                e1.printStackTrace();
                                            } catch (InvocationTargetException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                }

            }

            Field[] f = clazz.getDeclaredFields();
            if (f!=null&&f.length>0){
                Field[] fie = f;
                for (int i=0;i<fie.length;++i){
                    Field field = fie[i];

                    ViewInject viewInject = (ViewInject)field.getAnnotation(ViewInject.class);
                    if(viewInject!=null){
                        int id = viewInject.id();
                        if (id==0){
                            String str = viewInject.idStr();
                            if (!TextUtils.isEmpty(str)){
                                try {
                                    String pac = context.getPackageName();
                                    Resources resource = context.getPackageManager().getResourcesForApplication(pac);
                                    id = resource.getIdentifier(str,"id",pac);
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (id!=0){
                            try {
                                field.setAccessible(true);
                                if (field.get(injectedSource)==null){
                                    field.set(injectedSource,sourceView.findViewById(id));
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

        }

    }
}

