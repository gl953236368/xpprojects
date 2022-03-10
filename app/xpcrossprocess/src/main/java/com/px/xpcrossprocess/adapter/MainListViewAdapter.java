package com.px.xpcrossprocess.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.px.xpcrossprocess.R;
import com.px.xpcrossprocess.bean.AppBean;
import com.px.xpcrossprocess.utils.SpUtil;

import java.util.ArrayList;

import static com.px.xpcrossprocess.config.Key.APP_INFO;

public class MainListViewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<AppBean> mAppBeans;
    @Override
    public int getCount() {
        return mAppBeans == null ? 0:mAppBeans.size();
    }

    @Override
    public Object getItem(int i) {
        return mAppBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            // 加载列表的视图
            view = View.inflate(mContext, R.layout.activity_list_item, null);
        }
        ViewHolder holder = ViewHolder.getHolder(view);
        AppBean appBean = mAppBeans.get(i);

        holder.imageView.setImageBitmap(drawable2Bitmap(appBean.appIcon));
        holder.packageName.setText(appBean.packageName);
        holder.appName.setText(appBean.appName);

        holder.linearLayout.setOnClickListener(View -> save(i));

        return view;
    }


    public MainListViewAdapter(Context context, ArrayList<AppBean> appBeans) {
        mContext = context;
        mAppBeans = appBeans;
    }

    public void setData(ArrayList<AppBean> mAllPackageList){
        mAppBeans = mAllPackageList;
        // 在修改适配器绑定的数组后，不用重新刷新Activity，通知Activity更新数据
        notifyDataSetChanged();
    }

    private void save(int i) {
        SpUtil.putString(mContext, APP_INFO , mAppBeans.get(i).packageName);
        Toast.makeText(mContext.getApplicationContext(),
                "保存成功"+ mAppBeans.get(i).packageName,
                Toast.LENGTH_SHORT).show();
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    public static class ViewHolder{
        TextView appName, packageName;
        LinearLayout linearLayout;
        ImageView imageView;


        ViewHolder(View convertView){
            linearLayout = convertView.findViewById(R.id.list_item);
            appName = convertView.findViewById(R.id.tv_appName);
            packageName = convertView.findViewById(R.id.tv_packName);
            imageView = convertView.findViewById(R.id.iv_appIcon);
        }

        public static ViewHolder getHolder(View convertView){
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if(holder == null){
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }

    }
}
