package com.px.justtruemex.hook;

import android.util.Log;

import com.px.justtruemex.justtrueme.Main;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntity extends Main {
    private static final String TAG = "JustTrustMeX";
    public Class CertificatePinner = null;
    public Class RealConnection = null;
    public Class OkHostnameVerifier = null;
    public HashMap<String,Boolean> searched = new HashMap<String,Boolean> ();


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        final ClassLoader classLoader = lpparam.classLoader;
        Class openSSLSocketFactoryImpl = classLoader.loadClass("com.android.org.conscrypt.OpenSSLSocketFactoryImpl");
        /*
        * 通过com.android.org.conscrypt.OpenSSLSocketFactoryImpl->createSocket 堆栈
        *
        * 捕获okhttp3.internal.connection.RealConnection 对象
        * 通过RealConnection构造函数捕获 okhttp3.Route 对象
        * 通过Route构造函数捕获 okhttp3.Address 对象
        * 通过Address构造函数捕获 CertificatePinner 对象/HostnameVerifier (此处为接口)需要实例化调用
        *
        * CertificatePinner 对象捕获并重写方法 public void check(String hostname, List<Certificate> peerCertificates)
        * HostnameVerifier 由于捕获的不是对象，需要xposed hook构造函数 动态获取实例
              并重写方法 boolean verify(String var1, SSLSession var2)
        *
        * 从而实现对混淆的app，根据目标的类特征进行对比（比如有几个构造函数，构造函数有几个参数，构造函数的参数分别是哪些，
            类成员有哪些，对比的前提是你要拿到可疑的类），自动捕获证书校验的部分并关闭校验（仅对okhttp3）
        * TODO 兼容其他版本okhttp （学习文章：https://bbs.pediy.com/thread-267839-1.htm）
        * */
        XposedBridge.hookAllMethods(openSSLSocketFactoryImpl, "createSocket",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if(CertificatePinner == null){
                            Throwable throwable = new Throwable();
                            throwable.printStackTrace();
                            StackTraceElement[] stackTraceElements = throwable.getStackTrace();

                            for(StackTraceElement stackTraceElement:stackTraceElements){
                                try {
                                    // 拿到堆栈名称
                                    String className = stackTraceElement.getClassName();
                                    if(!searched.containsKey(className) || !searched.get(className)){
                                        searched.put(className, true);
                                        Class thisObj = classLoader.loadClass(className);
                                        Field[] fields = thisObj.getDeclaredFields();
                                        String superName = thisObj.getSuperclass().getName();

                                        if("java.lang.Object".equals(superName)){
                                            continue;
                                        }

                                        int classListNum = 0;
                                        int classSocketNum = 0;
                                        int classBooleanNum = 0;
                                        int classLongNum = 0;
                                        int classIntNum = 0;

                                        // 遍历目标 成员
                                        for(Field field:fields){
                                            String fieldType = field.getType().getName();
                                            boolean isStatic = Modifier.isStatic(field.getModifiers());
                                            boolean isFinal = Modifier.isFinal(field.getModifiers());

                                            if("java.util.List".equals(fieldType) && !isStatic && isFinal ){
                                                classListNum++;
                                            }
                                            if("java.net.Socket".equals(fieldType) && !isStatic && !isFinal ){
                                                classSocketNum++;
                                            }
                                            if("boolean".equals(fieldType) && !isStatic && !isFinal ){
                                                classBooleanNum++;
                                            }
                                            if("long".equals(fieldType) && !isStatic && !isFinal ){
                                                classLongNum++;
                                            }
                                            if("int".equals(fieldType) && !isStatic && !isFinal ){
                                                classIntNum++;
                                            }
                                        }

                                        //RealConnection类 有4个非静态int，1个fincal list，2个socket，1个boolean，1个long
                                        if(classIntNum == 4 && classListNum == 1 && classSocketNum == 2
                                                && classBooleanNum == 1 && classLongNum == 1){
                                            RealConnection = thisObj;
                                            foundRealConnection();
                                            break;
                                        }else {
                                            Log.d(TAG, "okhttp3.RealConnection类 有4个非静态int，1个fincal list，2个socket，1个boolean，1个long");
                                            Log.d(TAG, className+"——"+classIntNum+"_"+classListNum+"_"+classSocketNum+"_"+classBooleanNum+"_"+classLongNum);
                                        }

                                    }
                                }catch (Exception e){ XposedBridge.log(e); }
                            }
                        }
                    }
        });
        // 原始 justtrueme 可hook其他部分种类的http包
        super.handleLoadPackage(lpparam);
        super.processHttpClientAndroidLib(classLoader);
        super.processOkHttp(classLoader);
        super.processXutils(classLoader);
    }

    private void foundRealConnection() {
        Log.d(TAG, "找到RealConnection类:" + RealConnection.getName());
        Constructor[] constructors = RealConnection.getConstructors();
        // 过滤构造函数（1个
        if(constructors.length == 1){
            Class[] parameterTypes = constructors[0].getParameterTypes();
            //构造函数只有2个参数,第二个参数就是Route
            if(parameterTypes.length == 2){
                Class Route = parameterTypes[1];
                foundRoute(Route);
            }
        }
        foundCertificatePinner();
    }

    private void foundRoute(Class Route) {
        Log.d(TAG, "找到Route类:" + Route.getName());
        Constructor[] declaredConstructors = Route.getDeclaredConstructors();
        // 过滤构造函数 （1个
        if(declaredConstructors.length == 1){
            Class[] parameterTypes = declaredConstructors[0].getParameterTypes();
            //构造函数有3个参数，第一个参数就是Address
            if(parameterTypes.length == 3){
                Class Address = parameterTypes[0];
                foundAddress(Address);
            }
        }
    }

    private void foundAddress(Class Address) {
        Log.d(TAG,"找到Address类"+ Address.getName());
        Constructor[] declaredConstructors = Address.getDeclaredConstructors();
        //Address只有一个构造函数
        if(declaredConstructors.length == 1){
            Class[] parameterTypes = declaredConstructors[0].getParameterTypes();
            //构造函数有12个参数，第6个参数是OkHostnameVerifier，但是声明是接口类，
            // 只能通过hook拿实例类，第7个参数就是CertificatePinner,
            if(parameterTypes.length == 12){
                CertificatePinner = parameterTypes[6];
                foundCertificatePinner();
            }
            XposedBridge.hookAllConstructors(Address, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if(OkHostnameVerifier == null){
                        OkHostnameVerifier = param.args[5].getClass();
                        foundOkHostnameVerifier();
                    }

                    super.beforeHookedMethod(param);
                }
            });
        }
    }

    private void foundOkHostnameVerifier() {
        Log.d(TAG,"找到OkHostnameVerifier类"+ OkHostnameVerifier.getName());
        hookOkHostnameVerifier(OkHostnameVerifier);
    }

    private void hookOkHostnameVerifier(Class okHostnameVerifier) {
        // HostnameVerifier 实例化的接口里只有一个verify方法 直接hook 替换
        XposedBridge.hookAllMethods(okHostnameVerifier, "verify", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return true;
            }
        });
    }

    private void foundCertificatePinner() {
        Log.d(TAG,"找到CertificatePinner类" + CertificatePinner.getName());
        hookCertificatePinner(CertificatePinner);
    }

    private void hookCertificatePinner(Class certificatePinner) {
        Method[] declaredMethods = certificatePinner.getDeclaredMethods();
        Method targetMethod = null;

        // 遍历所有函数 通过类型过滤目标函数（String，List
        for(Method method:declaredMethods){
            Class<?> returnType = method.getReturnType();
            if(!"void".equals(returnType.getName())){
                continue;
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            if(parameterTypes.length != 2){
                continue;
            }
            if(!"java.lang.String".equals(parameterTypes[0].getName())){
                continue;
            }
            if(!"java.util.List".equals(parameterTypes[1].getName())){
                continue;
            }
            targetMethod = method;
        }

        if(targetMethod != null){
            // check这个方法 如果检测不到信任证书则会抛出异常，这里不让它做操作
            XposedBridge.hookMethod(targetMethod, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return null;
                }
            });
        }
    }
}
