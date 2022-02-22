package com.px.xpinline.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.px.xpinline.BuildConfig;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Tool {

//    public static final String savePath = "/sdcard/Download/"+ BuildConfig.APPLICATION_ID + ".txt";
    public static final String savePath = Environment.getExternalStorageDirectory().getAbsolutePath()
        + "/" + BuildConfig.APPLICATION_ID + ".txt";
    public static String reverse(String hexString, int step){
        // 转换"/" 的十六进制地址字符串 反转
        String[] hexCodeArray = hexString.split("/"); // 切割出来进行处理
        StringBuilder key = new StringBuilder();

        for(String hexCode:hexCodeArray){
            String reverse = "";
            if(hexCode.length() == 7) {
                hexCode = "0" + hexCode;
            }else if(hexCode.length() < 7){
                // 异常 超出期望长度
                return "";
            }
            for(int i=0;i<hexCode.length();i=i+step){
                StringBuilder tmp = (new StringBuilder()).append(hexCode.charAt(i))
                        .append(hexCode.charAt(i+1)); // 连续的一个字节
                reverse = tmp.append(reverse).toString();
            }
            key.append(reverse);
        }

        return key.toString();
    }


    public static void write2Disk(String path, String str, Boolean flag){
        // 指定数据写入文件内
        try {
            FileOutputStream fout = new FileOutputStream(path, flag);
            byte[] bytes = str.getBytes();

            fout.write(bytes);
            fout.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String read7Disk(String path) {
        // 指定数据读出
        FileInputStream fis = null;
        StringBuilder res = new StringBuilder();
        try {
            fis = new FileInputStream(path);
            // 准备一个byte数组
            byte[] bytes = new byte[4];
            int readCount;
            while ((readCount = fis.read(bytes)) != -1) {
                res.append(new String(bytes, 0, readCount));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res.toString();
    }

    public static String getMySoPath(Context context){
        // 获取当前插件的路径 以便加载自己的so文件
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> pkgList = pm.getInstalledPackages(0);
        if(pkgList.size() > 0){
            for(PackageInfo pi:pkgList){
                if(pi.applicationInfo.publicSourceDir.startsWith("/data/app/" + BuildConfig.APPLICATION_ID)){
                    return pi.applicationInfo.publicSourceDir;
                }
            }
        }
        return null;
    }
}
