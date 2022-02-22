package com.px.xpinline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.px.xpinline.utils.Tool;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        save2File(); // 首次安装 需要退出再进才能生效
    }


    public static void verifyStoragePermissions(Activity activity) {
        // 主动授权
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框 有效
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void save2File(){
        // 写入当前包名 解决xposed跨进程问题 读取路径
        String path = Tool.getMySoPath(this);
        Log.i("ZY-TEST", "当前path:"+path);
        Tool.write2Disk(Tool.savePath, path, false);
        Log.i("ZY-TEST", "保存成功path:"+Tool.savePath);
    }
}