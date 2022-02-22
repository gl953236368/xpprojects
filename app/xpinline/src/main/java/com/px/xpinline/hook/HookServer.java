package com.px.xpinline.hook;

import android.util.Log;

import com.px.xpinline.BuildConfig;
import com.px.xpinline.model.InitContextModel;
import com.px.xpinline.utils.Tool;
import com.virjar.sekiro.business.api.SekiroClient;
import com.virjar.sekiro.business.api.fastjson.JSONObject;
import com.virjar.sekiro.business.api.interfaze.ActionHandler;
import com.virjar.sekiro.business.api.interfaze.SekiroRequest;
import com.virjar.sekiro.business.api.interfaze.SekiroResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class HookServer {
    public final static String Tag = "ZY-TEST-Server";


    public static void entry(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 开启服务
        // 1.先加载自己的so 确保native层的参数已捕获到
        HookEntity.entry(lpparam);
        // 2.开启服务 等待调用
        initSekiroClient();
    }

    public static String getSign(ClassLoader classLoader)throws Throwable{
        Log.d(Tag, "主动捕获方法");
        // 定位 class
        Class<?> clazz = classLoader.loadClass("com.izuiyou.network.NetCrypto");
        // 静态方法 直接调用
        String result = (String) XposedHelpers.callStaticMethod(clazz, "getProtocolKey");
        String keys = Tool.reverse(HookEntity.testFunc(),2); // 对hex进行补位 以及字节翻转
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("duck", result);
        resultMap.put("key", keys);
        return resultMap.toString();
    }


    public static void initSekiroClient() throws Throwable{
        // 基础配置 组名/客户端id/服务器ip/服务器port/拦截的action
        String groupName = "TestDemo";
        String clientId = UUID.randomUUID().toString();
        String serverHost = BuildConfig.HOST;
        int serverPort = BuildConfig.PORT;
        String actionName = "test";

        new SekiroClient(groupName, clientId, serverHost, serverPort)
                .setupSekiroRequestInitializer((sekiroRequest, handlerRegistry) -> {
                    handlerRegistry.registerSekiroHandler(new ActionHandler() {

                        @Override
                        public String action() {
                            return actionName;
                        }

                        @Override
                        public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
                            Log.d(Tag, "sekiro捕获客户端请求");
                            InitContextModel initContextModel = InitContextModel.newInstance();
                            Map<String, String> result = new HashMap<>();
                            result.put("调用类", "com.izuiyou.network.NetCrypto");
                            result.put("调用方法", "getProtocolKey");
                            result.put("请求参数：", "暂无");

                            String status = "failed";
                            try {
                                String res = "暂无";
                                if(initContextModel.getClassLoader() != null){
                                    // 维持的目标loader存在 则去主动调用方法并返回
                                    status = "success";
                                    res = getSign(initContextModel.getClassLoader());
                                }
                                result.put("调用状态", status);
                                result.put("调用结果", res);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                            sekiroResponse.success(JSONObject.toJSONString(result));
                        }
                    });

                }).start();
    }

}
