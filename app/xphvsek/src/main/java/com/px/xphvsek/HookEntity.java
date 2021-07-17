package com.px.xphvsek;

import android.util.Log;

import com.px.xphvsek.hook.TestDemo;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * hook实体
 */
public class HookEntity implements IXposedHookLoadPackage {
    private static String Tag = "HOOK_demo";
//    private static XC_LoadPackage.LoadPackageParam loadPackageParam;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//        HookEntity.loadPackageParam = lpparam;

        // hookmethod 和 hookmain 分离
        if(lpparam.packageName.equals("com.px.xphvsek")){
            Log.d(Tag, "xposed加载捕获到 目标程序包");
            TestDemo.entity(lpparam);
        }
    }


}
