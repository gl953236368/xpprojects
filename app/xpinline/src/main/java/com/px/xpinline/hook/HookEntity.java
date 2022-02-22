package com.px.xpinline.hook;

import android.util.Log;

import com.px.xpinline.model.InitContextModel;
import com.px.xpinline.utils.Tool;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntity {
    public final static String Tag = "ZY-TEST";
    public final static String packageName = "cn.xiaochuankeji.tieba"; // 最右
    public final static String soName = "libnet_crypto.so"; // 需要调用的so
    public static String mySoNamePath = "libhook.so"; // 自写so

    public static native int hookMain(); // so中开启 inline-hook

    public static native String testFunc(); // 获取目标值


    public static void entry(XC_LoadPackage.LoadPackageParam lpparam) {
        // hook 入口
        hookSoLoad(lpparam); // 加载自写so文件
        hookFunc(lpparam); // hook 目标方法 java层 并对so层拦截的值返回，初始化单例对象
    }


    public static void hookSoLoad(XC_LoadPackage.LoadPackageParam lpparam){
        // hook到目标so文件
        int version = android.os.Build.VERSION.SDK_INT;
        Log.i(Tag, "当前系统版本号：" + version);
        XposedHelpers.findAndHookMethod(Runtime.class,
                "doLoad",
                String.class,
                ClassLoader.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String name = (String) param.args[0].toString();
                        if(name.contains(soName)){
                            Log.i(Tag, "命中目标so：" + name);
                            initMySo(param.args[1]);
                            int res = hookMain(); // 启动inline-hook钩子
                            if(res == 0){
                                Log.i(Tag, "注入目标so方法成功");
                            }
                        }

                    }
                });
    }

    private static void hookFunc(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("com.izuiyou.network.NetCrypto",
                lpparam.classLoader,"getProtocolKey",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param){
                        String xc = (String) param.getResult();
                        String keys = Tool.reverse(testFunc(),2); // 对hex进行补位 以及字节翻转
                        Log.i(Tag, "获得请求头:" + xc);
                        Log.i(Tag, "执行结果:" + keys);
                        InitContextModel initContextModel = InitContextModel.newInstance();
                        if(initContextModel.getClassLoader() == null){
                            // 单例初始化 目标classloader 用于服务捕获
                            initContextModel.setClassLoader(lpparam.classLoader);
                        }
                    }
                });

    }


    private static void initMySo(Object args){
        // 注入自己的so文件

        String soPath = (Tool.read7Disk(Tool.savePath)).replace("base.apk", "lib/arm/"+mySoNamePath); // 调用本地so文件
        if(!"".equals(soPath)){
            Log.i(Tag, "注入目标so成功（注入路径）：" + soPath);
            // 两种注入方式
            // 1.直接注入 需要完整路径
            System.load(soPath);

            //2.xposed 注入 android 9.0没有 doLoad 方法
//            if (version >= 28) {
//                XposedHelpers.callMethod(Runtime.getRuntime(), "nativeLoad", soPath, args);
//            }else {
//                XposedHelpers.callMethod(Runtime.getRuntime(), "doLoad", soPath, args);
//            }
        }
    }



}
