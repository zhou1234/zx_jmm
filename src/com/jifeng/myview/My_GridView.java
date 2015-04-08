package com.jifeng.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class My_GridView extends GridView { 

    public My_GridView(Context context, AttributeSet attrs) { 
        super(context, attrs); 
    } 

    public My_GridView(Context context) { 
        super(context); 
    } 

    public My_GridView(Context context, AttributeSet attrs, int defStyle) { 
        super(context, attrs, defStyle); 
    } 

    @Override 
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST); 
        super.onMeasure(widthMeasureSpec, expandSpec); 
    } 
} 
