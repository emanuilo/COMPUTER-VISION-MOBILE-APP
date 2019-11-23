package com.example.emanu.diplomskiadmin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Created by emanu on 8/22/2018.
 */

public class ExpandableGridView extends GridView {

    private boolean isExpanded;

    public ExpandableGridView(Context context) {
        super(context);
    }

    public ExpandableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setExpanded(){
        this.isExpanded = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(isExpanded){
            int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        }
        else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
