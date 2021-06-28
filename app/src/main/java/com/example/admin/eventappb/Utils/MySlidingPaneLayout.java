package com.example.admin.eventappb.Utils;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by JC on 4/2/2018.
 */

public class MySlidingPaneLayout extends SlidingPaneLayout{

    public MySlidingPaneLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public MySlidingPaneLayout(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public MySlidingPaneLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    /*
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
    */

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //return !isSlideable() || super.onInterceptTouchEvent(ev);
        return false;
    }

    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("MySlidingPanelLayout", "onTouch:");
        if (this.isOpen()) {
            this.closePane();
        }
        return false; // here it returns false so that another event's listener
        // should be called, in your case the MapFragment
        // listener
    }
    */

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isSlideable()) {
            if (!getChildAt(0).dispatchTouchEvent(ev)) {
                ev.offsetLocation(-getChildAt(0).getWidth(),0);
                getChildAt(1).dispatchTouchEvent(ev);
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }
}