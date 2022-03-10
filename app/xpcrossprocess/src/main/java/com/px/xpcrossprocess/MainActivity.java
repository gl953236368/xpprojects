package com.px.xpcrossprocess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ListView;

import com.px.xpcrossprocess.adapter.MainListViewAdapter;
import com.px.xpcrossprocess.bean.AppBean;
import com.px.xpcrossprocess.content.MultiprocessSharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    /**
     * 加载所有app列表，通过点击保存到xml中
     * hook通过contentprovider 跨进程
     * 获得保存的xml的值
     * 1. 配置androidmainfest.xml 中的provider服务
     * 2. MainActivity.java 设置权限 确保生效
     * 3. 通过com.px.xpcrossprocess.utils.Sputil 注册服务
     * 4. xposed服务通过 provider去请求数据（hook attach/不唯一）
     */
    private ArrayList<AppBean> mAllPackageList = new ArrayList<>();     // 全部应用
    private ArrayList<AppBean> CommonPackageList = new ArrayList<>();  // 非系统应用
    private ListView mLv_list;
    private CheckBox mCb_checkbox;
    private MainListViewAdapter mMainListViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 设置权限
        MultiprocessSharedPreferences.setAuthority("com.px.xpcrossprocess.provider");
        // 初始化app列表
        initData();
        // 初始化视图 以及监控
        initView();
    }


    private void initData(){
        mAllPackageList = getPackageList();
    }

    private void initView(){
        mLv_list = (ListView) findViewById(R.id.lv_apps);
        mCb_checkbox = (CheckBox) findViewById(R.id.toolbox);
        mMainListViewAdapter = new MainListViewAdapter(this, CommonPackageList);
        mCb_checkbox.setOnCheckedChangeListener((buttonView, isChecked)->{
            if(isChecked){
                mMainListViewAdapter.setData(mAllPackageList);
            }else {
                mMainListViewAdapter.setData(CommonPackageList);
            }
        });

        mLv_list.setAdapter(mMainListViewAdapter);


    }

    private ArrayList<AppBean> getPackageList() {
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);

        ArrayList<AppBean> appBeans = new ArrayList<>();

        for(PackageInfo packageInfo:packageInfos){
            AppBean appBean = new AppBean();
            appBean.appIcon = packageInfo.applicationInfo.loadIcon(packageManager);
            appBean.packageName = packageInfo.packageName;
            appBean.appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            // 判断是否为系统应用
            if((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags) == 0){
                // 非系统应用
                appBean.isSystemApp = false;
            }
            if(!appBean.isSystemApp){ // 加入非系统列表中
                CommonPackageList.add(appBean);
            }
            appBeans.add(appBean);

        }

        return appBeans;
    }

}