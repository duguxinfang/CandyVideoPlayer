package com.candy.videoplayer.view.drag;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.candy.videoplayer.R;
import com.candy.videoplayer.utils.UIUtils;


public class YoutubeLayout extends ViewGroup {

    private final ViewDragHelper mDragHelper;

    private DragStateCallback yDragStateCallback;

    private View mHeaderView;
    private View mDescView;

    private float mInitialMotionX;
    private float mInitialMotionY;

    private int mDragRangeY;
    private int mTop;
    public float mDragOffsetY;


    //add by ming
    private int mLeft;
    private int touchSlop;
    private int mDragRangeX;
    private int mDragRangeXToLeft;
    private float mDragOffsetX;
    private int headScale = 2;
    private int bottomHeight;
    private int headPadding = 10;

    public YoutubeLayout(Context context) {
        this(context, null);
    }

    public YoutubeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YoutubeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
        bottomHeight = UIUtils.dip2px(50);
        headPadding = UIUtils.dip2px(10);
        init();
    }

    private void init() {
        touchSlop = mDragHelper.getTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHeaderView = findViewById(R.id.fl_video_player);
        mDescView = findViewById(R.id.ll_layout);
    }

    //窗口是否最大化
    public boolean isMax() {
        return mDragOffsetY == 0.0f;
    }

    public void maximize() {
        smoothSlideTo(0f);
    }

    public void minimize() {
        if (null != yDragStateCallback)
            yDragStateCallback.startChangeCallback();
        smoothSlideTo(1f);
    }

    boolean smoothSlideTo(float slideOffset) {
        final int topBound = getPaddingTop();
        int y = (int) (topBound + slideOffset * mDragRangeY);
        if (mDragHelper
                .smoothSlideViewTo(mHeaderView, getPaddingLeft(), y)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public void onViewDragStateChanged(int state) {
            if (null != yDragStateCallback) {
                if (state == ViewDragHelper.STATE_DRAGGING)
                    yDragStateCallback.startChangeCallback();
                else if (state == ViewDragHelper.STATE_IDLE && mTop == 0)
                    yDragStateCallback.beenMaxCallback();
                else if (state == ViewDragHelper.STATE_IDLE && (mLeft == mDragRangeX || mLeft == mDragRangeXToLeft))
                    yDragStateCallback.closeViewCallback();
            }
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mHeaderView;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            mTop = top;
            mLeft = left;
            mDragOffsetY = (float) top / mDragRangeY;
            mDragOffsetX = (float) left / mDragRangeX;
            mHeaderView.setPivotX(mHeaderView.getWidth() - headPadding);
            mHeaderView.setPivotY(mHeaderView.getHeight() - headPadding);
            mHeaderView.setScaleX(1 - mDragOffsetY / headScale);
            mHeaderView.setScaleY(1 - mDragOffsetY / headScale);
            mDescView.setAlpha(1 - mDragOffsetY);
            float headAlpha = Math.abs(mDragOffsetX);
            if (headAlpha > 1)
                headAlpha = 1;
            mHeaderView.setAlpha(1 - headAlpha);
            requestLayout();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int topPadding = getPaddingTop();
            int leftPadding = getPaddingLeft();
            int cpX = leftPadding;
            int CPY = topPadding;
            if ((yvel == 0 && mDragOffsetY > 0.5f)) {
                CPY = mDragRangeY;
            }
            if ((xvel == 0 && mDragOffsetX > 0.5f)) {
                cpX = mDragRangeX;
                CPY = mDragRangeY;
            } else if ((xvel == 0 && mDragOffsetX < -0.5f)) {
                cpX = mDragRangeXToLeft;
                CPY = mDragRangeY;
            }

            if (Math.abs(mHeaderView.getLeft()) >= 2 * touchSlop && Math.abs(xvel) > yvel) { //最小化后横向拖动状态
                if (xvel > 0) {
                    cpX = mDragRangeX;
                    CPY = mDragRangeY;
                } else if (xvel < 0) {
                    cpX = mDragRangeXToLeft;
                    CPY = mDragRangeY;
                }
            } else {
                if (yvel > 0) {
                    CPY = mDragRangeY;
                }
            }

            mDragHelper.settleCapturedViewAt(cpX, CPY);
            invalidate();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mDragRangeY;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - mHeaderView.getHeight()
                    - mHeaderView.getPaddingBottom() - bottomHeight;
            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            return newTop;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
//            final int leftBound = getPaddingLeft();
//            final int rightBound = (getWidth() - mHeaderView.getPaddingRight()) / 2;
//            final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
            return left;
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {

            mDragHelper.processTouchEvent(ev);
            final int action = ev.getAction();
            final float x = ev.getX();
            final float y = ev.getY();
            boolean isHeaderViewUnder = mDragHelper.isViewUnder(mHeaderView,
                    (int) x, (int) y);
            switch (action & MotionEventCompat.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    mInitialMotionX = x;
                    mInitialMotionY = y;
                    break;
                }
                case MotionEvent.ACTION_MOVE:

                    break;

                case MotionEvent.ACTION_UP: {
                    final float dx = x - mInitialMotionX;
                    final float dy = y - mInitialMotionY;
                    if (dx * dx + dy * dy < touchSlop * touchSlop && isHeaderViewUnder) {
                        if (mDragOffsetY > 0.5f) {
                            smoothSlideTo(0f);
                        }
                    }
                    break;
                }
            }

            return isHeaderViewUnder && isViewHit(mHeaderView, (int) x, (int) y)
                    || isViewHit(mDescView, (int) x, (int) y);

        } catch (IllegalArgumentException e) {

        } catch(ArrayIndexOutOfBoundsException e) {

        }
        return false;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mDragRangeY = getHeight() - mHeaderView.getHeight() - bottomHeight;
        mDragRangeX = getWidth() / headScale;
        mDragRangeXToLeft = -getWidth();
        if (mTop >= mDragRangeY - touchSlop && mDragRangeY > 0) {
            if (Math.abs(mLeft) > touchSlop * 1.1f)
                mHeaderView.layout(mLeft, mDragRangeY, r + mLeft, mDragRangeY + mHeaderView.getMeasuredHeight());
            else
                mHeaderView.layout(mLeft, mTop, r + mLeft, mTop + mHeaderView.getMeasuredHeight());
        } else
            mHeaderView.layout(0, mTop, r, mTop + mHeaderView.getMeasuredHeight());
        if (mTop == mDragRangeY) {
            mDescView.layout(0, mTop + mHeaderView.getMeasuredHeight() + bottomHeight, r, mTop + b + bottomHeight);
        } else
            mDescView.layout(0, mTop + mHeaderView.getMeasuredHeight(), r, mTop + b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(
                resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    private boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0]
                && screenX < viewLocation[0] + view.getWidth()
                && screenY >= viewLocation[1]
                && screenY < viewLocation[1] + view.getHeight();
    }


    public void setyDragStateCallback(DragStateCallback yDragStateCallback) {
        this.yDragStateCallback = yDragStateCallback;
    }

    public interface DragStateCallback {
        void beenMaxCallback(); //窗口最大化

        void startChangeCallback();//窗口开始改变

        void closeViewCallback();
    }

}
