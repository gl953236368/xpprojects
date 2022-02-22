package com.px.xpinline;

import android.util.Log;

import com.px.xpinline.hook.HookEntity;
import com.px.xpinline.hook.HookServer;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage {

    public final static String Tag = "xpinline";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // hook 方法分支
        if(lpparam.packageName.equals("cn.xiaochuankeji.tieba")){
            // 5.7.3版本
            Log.d(Tag, "xposed加载捕获到 目标程序包:cn.xiaochuankeji.tieba");
            HookEntity.entry(lpparam); // hook 插件
//            HookServer.entry(lpparam); // hook 服务
        }
    }
}
