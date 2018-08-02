package com.tool.utils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * ScrollView/ListView 嵌套 GridView
 * 功能：使 MyListView 在内部显示全
 * eg:
 * 布局xml:
 * <cn.myapp.mobile.owner.widget.MyListView>
 * </cn.myapp.mobile.owner.widget.MyListView>
 * 类文件
 * MyListView listView = (MyListView) findViewById(R.id.myListView);
 * @author mac
 *
 */

public class MyListView extends ListView {

public MyListView(Context context, AttributeSet attrs) {
    super(context, attrs);

}

@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

    super.onMeasure(widthMeasureSpec, expandSpec);
}


}
