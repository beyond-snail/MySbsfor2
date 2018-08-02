package com.zfsbs.adapter;

import android.content.Context;

import com.zfsbs.R;
import com.zfsbs.model.Menu;

import java.util.List;

/**
 * Created by Administrator on 2017/11/15 0015.
 */

public class MyMenuListAdapter extends CommonAdapter<Menu> {

    private static final String TAG = "MyCouponsAdapter";
    private List<Menu> datas;

    public MyMenuListAdapter(Context context, List<Menu> datas, int layoutId) {
        super(context, datas, layoutId);
        this.datas = datas;
    }

    @Override
    public void convert(Context context, ViewHolder holder, Menu menu) {
        holder.setText(R.id.id_menu_name, menu.getName());
    }
}
