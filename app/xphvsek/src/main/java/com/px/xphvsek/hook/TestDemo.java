package com.px.xphvsek.hook;

import android.icu.text.SimpleDateFormat;
import android.icu.text.TimeZoneFormat;
import android.util.Log;

import com.px.xphvsek.config.BuildConfig;
import com.px.xphvsek.handler.SignHandler;
import com.virjar.sekiro.business.api.SekiroClient;
import com.virjar.sekiro.business.api.fastjson.JSONObject;
import com.virjar.sekiro.business.api.interfaze.ActionHandler;
import com.virjar.sekiro.business.api.interfaze.HandlerRegistry;
import com.virjar.sekiro.business.api.interfaze.SekiroRequest;
import com.virjar.sekiro.business.api.interfaze.SekiroRequestInitializer;
import com.virjar.sekiro.business.api.interfaze.SekiroResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.UUID;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TestDemo {
    public static final String TAG = "HOOK_TESTDEMO";
    public static ClassLoader currentClassloader = null;

    public static void entity(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        // 当前正在运行的process 包名 和 加载的 package 是否相同
        if (loadPackageParam.processName.equals(loadPackageParam.packageName)){
            // 调用 hookmethod
            Log.d(TAG, "调用客户端");
            ClassLoader classLoader = loadPackageParam.classLoader;
//            initSekiroClient(classLoader);
            initSekiroClient();
        }
    }

    public static String getSign(ClassLoader classLoader, int args1, int args2)throws Throwable{
        Log.d(TAG, "主动捕获方法");
        // 定位 class
        Class<?> clazz = classLoader.loadClass("com.px.xphvsek.sign.TestFunction");
        // 静态方法 直接调用
        String result = (String) XposedHelpers.callStaticMethod(clazz, "getSign", args1, args2);
        Log.d(TAG, "调用静态方法：" + result);
        return result;
    }

    public static void initSekiroClient(ClassLoader classLoader) throws Throwable{
        // 基础配置 组名/客户端id/服务器ip/服务器port/拦截的action
        String groupName = "TestDemo";
        String clientId = UUID.randomUUID().toString();
        String serverHost = BuildConfig.HOST;
        int serverPort = BuildConfig.PORT;
        String actionName = "test";

        currentClassloader = classLoader;

        new SekiroClient(groupName, clientId, serverHost, serverPort)
                .setupSekiroRequestInitializer((sekiroRequest, handlerRegistry) -> {
                    handlerRegistry.registerSekiroHandler(new ActionHandler() {
                        @Override
                        public String action() {
                            return actionName;
                        }

                        @Override
                        public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
                            Log.d(TAG, "sekiro捕获客户端请求");
                            int args1 = sekiroRequest.getIntValue("param1");
                            int args2 = sekiroRequest.getIntValue("param2");
                            Map<String, String> result = new HashMap<>();
                            result.put("调用类", "com.px.xphvsek.sign.TestFunction");
                            result.put("调用方法", "getSign");
                            result.put("请求参数：", String.format("args1:%d;args2:%d", args1,args2));
                            try {
                                String cc = getSign(classLoader, args1, args2);
                                result.put("调用状态", "成功");
                                result.put("调用结果", cc);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                            sekiroResponse.success(JSONObject.toJSONString(result));
                        }
                    });

                }).start();
    }

    /**
     * 外部注册handler
     */
    public static void initSekiroClient(){
        // 基础配置 组名/客户端id/服务器ip/服务器port/拦截的action
        String groupName = "TestDemo";
        String clientId = UUID.randomUUID().toString();
        String serverHost = BuildConfig.HOST;
        int serverPort = BuildConfig.PORT;

        SekiroClient sekiroClient = new SekiroClient(groupName, clientId, serverHost, serverPort);

        sekiroClient.setupSekiroRequestInitializer(new SekiroRequestInitializer() {
            @Override
            public void onSekiroRequest(SekiroRequest sekiroRequest, HandlerRegistry handlerRegistry) {
                 handlerRegistry.registerSekiroHandler(new SignHandler());
            }
        });

        sekiroClient.start();
    }
}
