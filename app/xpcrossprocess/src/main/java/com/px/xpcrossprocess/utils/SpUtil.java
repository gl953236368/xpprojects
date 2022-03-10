package com.px.xpcrossprocess.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.px.xpcrossprocess.content.MultiprocessSharedPreferences;

public class SpUtil {
    // 声明为 static volatile，会迫使线程每次读取时作为一个全局变量读取
    private static volatile SpUtil spUtil = null;
    private static SharedPreferences sp = null;
    private static final String XML_NAME = "config";

    private SpUtil(Context context) {
        // 上下文、配置名称(data/data/shared_prefs )、
        sp = MultiprocessSharedPreferences.
                getSharedPreferences(context, XML_NAME, Context.MODE_PRIVATE);
    }

    public static SpUtil newInstance(Context context) {
        if(spUtil == null){
            synchronized (SpUtil.class){
                while (spUtil == null){
                    spUtil = new SpUtil(context);
                }
            }
        }
        return spUtil;
    }

    /**
     * 存储字符串
     * @param context
     * @param key
     * @param value
     */
    public static void putString(Context context, String key, String value){
        //(存储节点文件名称,读写方式)
        if(spUtil == null){
            spUtil = newInstance(context);
        }
        sp.edit().putString(key, value).commit();
    }

    /**
     * 获取字符串
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(Context context, String key, String defValue){
        //(存储节点文件名称,读写方式)
        if(spUtil == null){
            spUtil = newInstance(context);
        }
        return sp.getString(key, defValue);
    }

    /**
     * 存储boolean
     * @param context
     * @param key
     * @param value
     */
    public static void putBoolean(Context context, String key, boolean value){
        //(存储节点文件名称,读写方式)
        if(spUtil == null){
            spUtil = newInstance(context);
        }
        sp.edit().putBoolean(key, value).commit();
    }


    /**
     * 获取boolean
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static boolean getBoolean(Context context,String key,boolean defValue){
        //(存储节点文件名称,读写方式)
        if(spUtil == null){
            spUtil = newInstance(context);
        }
        return sp.getBoolean(key, defValue);
    }

    /**
     * 存储int
     * @param context
     * @param key
     * @param defValue
     */
    public static void putInt(Context context,String key, int defValue){
        //(存储节点文件名称,读写方式)
        if(spUtil == null){
            spUtil = newInstance(context);
        }
        sp.edit().putInt(key, defValue).commit();
    }


    /**
     * 获取int
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static int getInt(Context context,String key,int defValue){
        //(存储节点文件名称,读写方式)
        if(spUtil == null){
            spUtil = newInstance(context);
        }
        return sp.getInt(key, defValue);
    }

    /**
     * 删除节点
     * @param context
     * @param key
     */
    public static void remove(Context context, String key) {
        if(spUtil == null){
            spUtil = newInstance(context);
        }
        sp.edit().remove(key).commit();
    }

    /**
     * 根据类型 通过provider 返回结果
     * @param context 传递当前上下文
     * @param key 传递key
     * @param objType 存储key的类型
     * @return 对应key-value
     */
    public static Object readObjectByProvider(Context context, String key, Class objType){
        StringBuilder stringBuilder = new StringBuilder("content://");
        stringBuilder.append("com.px.xpcrossprocess.provider/"); // 包名
        stringBuilder.append(XML_NAME + "/");
        //(存储节点文件名称,读写方式) 追加方式 参考MultiprocessSharedPreferences中
        // eg. content://com.px.xpcrossprocess.provider/config/getString 获取config.xml中的字符串类型的
        // TODO
        if(objType.getName() == "java.lang.String"){
            stringBuilder.append("getString");
        }else if(objType.getName() == "java.lang.Boolean"){
            stringBuilder.append("getBoolean");
        }else if(objType.getName() == "int"){
            stringBuilder.append("getInt");
        }


        Uri uri = Uri.parse(stringBuilder.toString());
        ContentResolver provider = context.getContentResolver();
        Cursor cursorTid;
        try {
            //第一个参数是否是安全模式，一般为0。第二个参数读取的key，第三个参数defaultValue
            String[] selectionArgs = {"0", key, ""};
            cursorTid = provider.query(uri, null, null, selectionArgs, null);
            Bundle extras = cursorTid.getExtras();
            Object res = extras.get("value");
            return res;
        }catch (Exception e){
            Log.e("crossprocess",e.getMessage()+"\t"+e.toString());
            e.printStackTrace();
        }

        return null;
    }
}
