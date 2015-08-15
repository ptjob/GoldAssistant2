package com.parttime.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qingmu.jianzhidaren.R;

/**
 * Created by cjz on 2015/8/15.
 */
public class CreditView extends LinearLayout{
    private Context context;

    private boolean initExecuted;

    private int scale;
    private int imgs;
    private int[] imgIds;
    private int innerMargin;
    private int itemWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int itemHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

    public CreditView(Context context) {
        super(context);
        if(!initExecuted){
            initExecuted = true;
            init(context, null);
        }
    }

    public CreditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!initExecuted){
            initExecuted = true;
            init(context, attrs);
        }
    }

    public CreditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!initExecuted){
            initExecuted = true;
            init(context, attrs);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CreditView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if(!initExecuted){
            initExecuted = true;
            init(context, attrs);
        }
    }

    private void init(Context context, AttributeSet attrs){
        this.context = context;
        setOrientation(LinearLayout.HORIZONTAL);
        assignDefaultAttrs();
        assignXmlAttrs(attrs);


        TypedArray typedArray = context.getResources().obtainTypedArray(imgs);
        int length = typedArray.length();
        imgIds = new int[length];
        for(int i = 0 ; i < length; ++i){
            imgIds[i] = typedArray.getResourceId(i, R.drawable.red_heart);
        }

    }



    private void assignDefaultAttrs(){
        innerMargin = context.getResources().getDimensionPixelSize(R.dimen.rank_view_inner_margin_default);
        imgs = R.array.credit_view_default;
        scale = 10;
    }

    private void assignXmlAttrs(AttributeSet attrs){
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CreditView);
            if(typedArray != null){
                innerMargin = typedArray.getDimensionPixelSize(R.styleable.CreditView_credit_view_innerMargin, innerMargin);
                itemWidth = typedArray.getDimensionPixelSize(R.styleable.CreditView_credit_view_itemWidth, itemWidth);
                itemHeight = typedArray.getDimensionPixelSize(R.styleable.CreditView_credit_view_itemHeight, itemHeight);
                scale = typedArray.getInt(R.styleable.CreditView_credit_view_creditScale, scale);
                imgs = typedArray.getResourceId(R.styleable.CreditView_credit_view_imgArray, imgs);

            }
        }
    }

    private ImageView makeIv(){
        ImageView iv = new ImageView(context);
        return iv;
    }

    private LinearLayout.LayoutParams makeLayoutParams(){
        return makeLayoutParams(0);
    }

    private LinearLayout.LayoutParams makeLayoutParams(int marginLeft){
        LayoutParams lp = new LayoutParams(itemWidth, itemHeight);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.leftMargin = marginLeft;
        return lp;
    }

    public void setImages(int[] imgs){
        imgIds = imgs;
    }

    public void setScale(int scale){
        this.scale = scale;
    }

    public void credit(int points){
        removeAllViews();
        int aspect;
        int n;
        for(int i = imgIds.length - 1; i >= 0; --i){
            aspect = (int) Math.pow(scale, i);
            if(points >= aspect){
                n = points / aspect;
                points -= (n * aspect);

                for(int j = 0; j < n; ++j) {
                    ImageView imageView = makeIv();
                    imageView.setImageResource(imgIds[i]);
                    ViewGroup.LayoutParams lp;
                    if (getChildCount() == 0) {
                        lp = makeLayoutParams();
                    } else {
                        lp = makeLayoutParams(innerMargin);
                    }
                    addView(imageView, lp);
                }
            }
        }
    }

}
