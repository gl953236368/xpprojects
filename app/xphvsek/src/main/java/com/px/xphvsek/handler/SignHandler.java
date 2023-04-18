package com.px.xphvsek.handler;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.px.xphvsek.hook.TestDemo;
import com.virjar.sekiro.business.api.fastjson.JSONObject;
import com.virjar.sekiro.business.api.interfaze.Action;
import com.virjar.sekiro.business.api.interfaze.AutoBind;
import com.virjar.sekiro.business.api.interfaze.RequestHandler;
import com.virjar.sekiro.business.api.interfaze.SekiroRequest;
import com.virjar.sekiro.business.api.interfaze.SekiroResponse;

import java.util.HashMap;
import java.util.Map;


import de.robv.android.xposed.XposedHelpers;

@Action("test")
public class SignHandler implements RequestHandler {

    @AutoBind
    Integer param1;

    @AutoBind
    Integer param2;

    public static String getSign(ClassLoader classLoader, int args1, int args2)throws Throwable{
        Log.d(TestDemo.TAG, "主动捕获方法");
        // 定位 class
        Class<?> clazz = classLoader.loadClass("com.px.xphvsek.sign.TestFunction");
        // 静态方法 直接调用
        String result = (String) XposedHelpers.callStaticMethod(clazz, "getSign", args1, args2);
        Log.d(TestDemo.TAG, "调用静态方法：" + result);
        return result;
    }

    @Override
    public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
        Log.d(TestDemo.TAG, "sekiro捕获客户端请求");

        // 避免线程检测：放到主线程中
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Map<String, String> result = new HashMap<>();
                result.put("调用类", "com.px.xphvsek.sign.TestFunction");
                result.put("调用方法", "getSign");
                result.put("请求参数：", String.format("args1:%d;args2:%d", param1, param2));

                try {
                    if (TestDemo.currentClassloader != null){
                        String cc = getSign(TestDemo.currentClassloader, param1, param2);
                        result.put("调用状态", "成功");
                        result.put("调用结果", cc);
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                sekiroResponse.send(JSONObject.toJSONString(result));
            }
        });

    }
}
