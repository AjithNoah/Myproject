package com.reward.dne.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MyLinearLayout extends LinearLayout {

    private RelativeLayout relativeView;
    private ViewDragHelper viewDragHelper;
    private int mDragRange;
    private int mTop;
    private float mDragOffset;

    public MyLinearLayout (Context context) {
        this(context, null);
    }

    public MyLinearLayout (Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLinearLayout (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        viewDragHelper = ViewDragHelper.create(this, 1, new DragHelperCallback());
    }


    @Override
    protected void onFinishInflate() {
        Log.i("Places", "onFinishInflate");
        super.onFinishInflate();

        //relativeView = (RelativeLayout) findViewById(R.id.relativeView);
       // filtersListView = (ListView) findViewById(R.id.filters_list);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            viewDragHelper.cancel();
            return false;
        }
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("Places", "onTouchEvent");
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            Log.i("Places", "tryCaptureView " + (child == relativeView));
            return child == relativeView;
        }
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - relativeView.getHeight();

            final int newTop = Math.min(Math.max(top, topBound), bottomBound);

            return newTop;
        }

       /* @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            Log.d("DragLayout", "clampViewPositionHorizontal " + left + "," + dx);

            final int leftBound = getPaddingLeft();
            final int rightBound = getWidth() - myView.getWidth();

            final int newLeft = Math.min(Math.max(left, leftBound), rightBound);

            return newLeft;
        }*/


        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            Log.i("Places", "onViewPositionChanged " + changedView.getClass().getName());
            super.onViewPositionChanged(changedView, left, top, dx, dy);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if(yvel>0) {
                viewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), relativeView.getMeasuredHeight()-releasedChild.getMeasuredHeight());
            } else {
                viewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), 0);
            }
            invalidate();

        }
    }
}