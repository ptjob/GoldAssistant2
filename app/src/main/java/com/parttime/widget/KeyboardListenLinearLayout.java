package com.parttime.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.LinearLayout;


/**
 * Created by cjz on 2015/8/15.
 */
public class KeyboardListenLinearLayout extends LinearLayout {

    public static final byte KEYBOARD_STATE_SHOW = -3;
    public static final byte KEYBOARD_STATE_HIDE = -2;
    public static final byte KEYBOARD_STATE_INIT = -1;
    private static final String TAG = "KeyboardListenLinearLayout";
    private boolean mHasInit = false;
    private boolean mHasKeyboard = false;
    private int mHeight;
    private int mKeyBoardHeight;
    private DisplayMetrics metrics = new DisplayMetrics();

    private IOnKeyboardStateChangedListener onKeyboardStateChangedListener;

    public KeyboardListenLinearLayout(Context context) {
        super(context);
        metrics = context.getResources().getDisplayMetrics();
        Log.i(TAG, "metrics:" + metrics.heightPixels);
    }

    public KeyboardListenLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        metrics = context.getResources().getDisplayMetrics();
        Log.i(TAG, "metrics:" + metrics.heightPixels);
    }

    public void setOnKeyboardStateChangedListener(IOnKeyboardStateChangedListener onKeyboardStateChangedListener) {
        this.onKeyboardStateChangedListener = onKeyboardStateChangedListener;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!mHasInit) {
            mHasInit = true;
            mHeight = b;
            if (onKeyboardStateChangedListener != null) {
                onKeyboardStateChangedListener.onKeyboardStateChanged(KEYBOARD_STATE_INIT);
            }
        } else {
            mHeight = mHeight * 0.8 < b ? b : mHeight;
        }

        if (mHasInit && mHeight > b && mHeight > metrics.heightPixels / 5) {
            mHasKeyboard = true;
            if (onKeyboardStateChangedListener != null) {
                onKeyboardStateChangedListener.onKeyboardStateChanged(KEYBOARD_STATE_SHOW);
            }
            return;
        }
        if (mHasInit && mHasKeyboard && mHeight == b) {
            mHasKeyboard = false;
            if (onKeyboardStateChangedListener != null) {
                onKeyboardStateChangedListener.onKeyboardStateChanged(KEYBOARD_STATE_HIDE);
            }
        }
    }

    public interface IOnKeyboardStateChangedListener {
        public void onKeyboardStateChanged(int state);
    }
}