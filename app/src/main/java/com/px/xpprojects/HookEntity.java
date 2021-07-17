package com.px.xpprojects;

import android.util.Log;
import android.widget.TableRow;

import com.virjar.sekiro.business.api.ClusterSekiroClient;
import com.virjar.sekiro.business.api.SekiroClient;
import com.virjar.sekiro.business.api.interfaze.ActionHandler;
import com.virjar.sekiro.business.api.interfaze.AutoBind;
import com.virjar.sekiro.business.api.interfaze.SekiroRequest;
import com.virjar.sekiro.business.api.interfaze.SekiroResponse;

import java.lang.reflect.Field;
import java.util.UUID;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntity implements IXposedHookLoadPackage {

    public final static String TAG = "xp_projects";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if(lpparam.packageName.equals("com.px.xpprojects")){
            Log.d(TAG, "捕获指定包");
            ClassLoader clzloader = lpparam.classLoader;
            initiativeReflect(clzloader);
//            initativeReflectHookClass(clzloader);

//            initClient(clzloader);
//            String className = "com.px.xpprojects.MainActivity";
//            XposedHelpers.findAndHookMethod(className, clzloader, "getResult",
//                    int.class, int.class,
//                    new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            Log.d(TAG, "捕获指定方法类" + clzloader.getClass().getName());
//                            super.beforeHookedMethod(param);
//                            param.setResult("11");
//                        }
//                    });
        }
    }
    public void initClient(ClassLoader classLoader){
        Log.d(TAG, "服务链接中 ...");
        // 服务器地址
        String testHost = "10.2.1.22";
        // 客户端标识
        String clientId = UUID.randomUUID().toString();
        // 接口组名称
        String groupName = "addDemo";
        // 暴露接口名称
        String actionName = "add";
        SekiroClient sekiroClient = new SekiroClient("test_group", UUID.randomUUID().toString(), testHost, 5620);
        sekiroClient.setupSekiroRequestInitializer((sekiroRequest, handlerRegistry) -> handlerRegistry.registerSekiroHandler(new ActionHandler() {
            @Override
            public String action() {
                return "test";
            }
            @Override
            public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
                sekiroResponse.success("testParam:" + sekiroRequest.getString("testParam")
                        + " not Time:" + System.currentTimeMillis());
            }
        })).start();

//        new SekiroClient(groupName, clientId, testHost,5600)
//                // handler挂载器
//                .setupSekiroRequestInitializer((sekiroRequest, handlerRegistry) ->
//
//                        //注册handler
//                        handlerRegistry.registerSekiroHandler(new ActionHandler() {
//
//                            // 参数绑定规则，将参数的param赋值，如果没有传递，则设置为默认值defaultParam
//                            @AutoBind(defaultValue = "defaultParam")
//                            private String param;
//
//                            // 参数绑定规则，将参数的intParam赋值，如果没有传递，则设置为默认值12
//                            @AutoBind(defaultValue = "12")
//                            private Integer intParam;
//
//                            @Override
//                            public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
//                                Log.d(TAG, "捕获到请求");
//                                Class<?> clz = XposedHelpers.findClass("com.px.xpprojects.MainActivity", classLoader);
//                                int arg1 = sekiroRequest.getIntValue("arg1");
//                                int arg2 = sekiroRequest.getIntValue("arg2");
//                                Log.d(TAG, String.format("接受到参数：arg1-%d / arg2-%d",arg1, arg2));
//                                Object result = XposedHelpers.callMethod(clz, "getResult", arg1, arg2);
//                                Log.d(TAG, "result: " + result);
//                                sekiroResponse.success("param：" + param
//                                        + " intParam:" + intParam + " result:" + result);
//
//                            }
//
//                            @Override
//                            public String action() {
//                                // actionHandler通过这个函数指定action名称
//                                return actionName;
//                            }
//                        }))
//                // 启动SekiroClient
//                .start();
    }



    public void initiativeReflect(ClassLoader clzloader) throws Throwable{
        Log.d(TAG, "主动hook方法.");
        Class<?> clazz = XposedHelpers.findClass("com.px.xpprojects.MainActivity", clzloader);
        Log.d(TAG, clazz.getName());
        int arg1 = 15;
        int arg2 = 15;
        String name = "伞兵";
        Object result = XposedHelpers.callStaticMethod(clazz, "getResult", arg1, arg2);
        Log.d(TAG, String.format("主动hook静态方法:1+1=%s", result));
        Object result1 = XposedHelpers.callMethod(clazz.newInstance(), "getResult", name);
        Log.d(TAG, "主动hook非静态方法:" + result1);
    }


    public void initativeReflectHookClass(ClassLoader clzloader) throws Throwable {
        // 自定义方法捕获
        Log.d(TAG, "主动hook外部方法-自定义方法");
        // 捕获外部类
        Class<?> clazzs = clzloader.loadClass("com.px.xpprojects.HookClass");
        // 回调类中的静态方法
        Object result1 = XposedHelpers.callStaticMethod(clazzs, "getResult", "静态方法");
        Log.d(TAG, "自定义方法:静态方法-"+result1);
        // 对于非静态方法 对象调用
        Object demoClass = clazzs.newInstance();
        Object result = XposedHelpers.callMethod(demoClass, "getResult", "测试参数a", "测试参数b");
        Log.d(TAG, "自定义方法:非静态方法-"+result);
        // 获取对象类中的 参数 修改私有权限 编辑 参数值
        Field field = clazzs.getDeclaredField("c");
        field.setAccessible(true);
        field.set(demoClass, 250);
        Object result2 = XposedHelpers.callMethod(demoClass, "getResult");
        Log.d(TAG, "自定义方法:非静态方法-私有变量-"+result2);
    }
}
