package com.px.xpcrossprocess.hook;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.px.xpcrossprocess.utils.SpUtil;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.px.xpcrossprocess.config.Key.APP_INFO;

public class HookEntity implements IXposedHookLoadPackage {

    private static final String TEST =  "TEST";
    private static  Context mContext = null;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        try {
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    mContext = (Context) param.args[0];

                    // 通过provider读取 值
                    String InvokePackage = (String) SpUtil.readObjectByProvider(mContext, APP_INFO, String.class);

                    if (!"".equals(InvokePackage) && lpparam.packageName.equals(InvokePackage)) {
                        Log.d(TEST,"发现被Hook App:" + InvokePackage);
                        // hook内容


                    }
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            Log.d(TEST,"Test  Exception  " + e.toString());
        }

    }
}
