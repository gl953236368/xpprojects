package com.px.xplvsek;

import android.util.Log;

import com.virjar.sekiro.api.SekiroClient;
import com.virjar.sekiro.api.SekiroRequest;
import com.virjar.sekiro.api.SekiroRequestHandler;
import com.virjar.sekiro.api.SekiroResponse;

import java.util.UUID;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntity implements IXposedHookLoadPackage {
    public final static String TAG = "xp_projects";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if(lpparam.packageName.equals("com.px.xplvsek")){
            Log.d(TAG, "捕获指定包");
            ClassLoader clzloader = lpparam.classLoader;
            initClient(clzloader);
        }
    }

    public void initClient(ClassLoader clzloader){
        String host = "10.2.2.25";
        String clientId = UUID.randomUUID().toString();
        String group = "test";
        String action = "t1";
        SekiroClient.start(host,clientId,group)
                .registerHandler(action,new SekiroRequestHandler(){
                    @Override
                    public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse){

                        Log.d(TAG, "主动hook方法.");
                        Class<?> clazz = XposedHelpers.findClass("com.px.xplvsek.MainActivity", clzloader);
                        Log.d(TAG, clazz.getName());
                        int arg1 = sekiroRequest.getInt("arg1");
                        int arg2 = sekiroRequest.getInt("arg2");
                        Object result = XposedHelpers.callStaticMethod(clazz, "getResult", arg1, arg2);
                        Log.d(TAG, String.format("主动hook静态方法:1+1=%s", result));
                        sekiroResponse.success(" now:"+System.currentTimeMillis()+ " your param1:" + result);
                    }
                });
    }

}
