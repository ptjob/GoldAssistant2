package com.parttime.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.qingmu.jianzhidaren.R;

/**
 * Created by cjz on 2015/8/7.
 */
public class StepView extends LinearLayout{
    protected Context context;
    private boolean initExec;


    private int itemWidth;
    private int itemHeight;
    private int itemMargin;
    private int normalColor;
    private int currentColor;
    private int count;

    private int curStep;

    public StepView(Context context) {
        super(context);
        if(!initExec){
            initExec = true;
            init(context, null);
        }
    }

    public StepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!initExec){
            initExec = true;
            init(context, attrs);
        }
    }

    public StepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!initExec){
            initExec = true;
            init(context, attrs);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StepView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if(!initExec){
            initExec = true;
            init(context, attrs);
        }
    }

    protected void init(Context context, AttributeSet attrs){
        this.context = context;

        assignDefaultXmlAttrs();
        assignXmlAttrs(attrs);
        setSelfAttrs();
        buildSteps();

    }


    protected void assignDefaultXmlAttrs(){
        Resources res = context.getResources();
        itemWidth = res.getDimensionPixelSize(R.dimen.sv_item_width_default);
        itemHeight = res.getDimensionPixelSize(R.dimen.sv_item_height_default);
        itemMargin = res.getDimensionPixelSize(R.dimen.sv_item_margin_default);
        normalColor = res.getColor(R.color.common_gray_4);
        currentColor = res.getColor(R.color.common_orange);
    }

    protected void assignXmlAttrs(AttributeSet attrs){
        if(attrs == null){
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StepView);
        if(typedArray != null){
            itemWidth = typedArray.getDimensionPixelSize(R.styleable.StepView_sv_itemWidth, itemWidth);
            itemHeight = typedArray.getDimensionPixelSize(R.styleable.StepView_sv_itemHeight, itemHeight);
            itemMargin = typedArray.getDimensionPixelSize(R.styleable.StepView_sv_itemMargin, itemMargin);
            normalColor = typedArray.getColor(R.styleable.StepView_sv_normalColor, normalColor);
            currentColor = typedArray.getColor(R.styleable.StepView_sv_currentColor, currentColor);
            count = typedArray.getInt(R.styleable.StepView_sv_stepCount, count);
            curStep = typedArray.getInt(R.styleable.StepView_sv_defaultStep, curStep);
        }
    }

    protected void setSelfAttrs(){
        setOrientation(LinearLayout.HORIZONTAL);
    }

    protected void buildSteps(){
        removeAllViews();
        View view;
        LinearLayout.LayoutParams lp;
        for(int i = 0; i < count; ++i){
            view = makeStep();
            lp = makeLp();
            if(i > 0){
                lp.leftMargin = itemMargin;
            }
            addView(view, lp);
        }
        updateColor();
    }

    protected void updateColor(){
        for(int i = 0; i < getChildCount(); ++i){
            View view = getChildAt(i);
            if(i == curStep % count){
                view.setBackgroundColor(currentColor);
            }else {
                view.setBackgroundColor(normalColor);
            }
        }
    }

    protected View makeStep(){
        View view = new View(context);

        return view;
    }

    protected LinearLayout.LayoutParams makeLp(){
        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(itemWidth, itemHeight);
        return lllp;
    }

    public void setStepCount(int stepCount){
        count = stepCount;
        buildSteps();
    }

    public void forward(){
        if(count <= 0){
            return;
        }
        curStep = ++curStep % count;
        updateColor();
    }

    public void backward(){
        if(count <= 0){
            return;
        }
        curStep = (--curStep + count) % count;
        updateColor();
    }

    public void move(int step){

    }

    public void current(int position){
        if(count <= 0){
            return;
        }
        curStep = position % count;
        updateColor();
    }
}
