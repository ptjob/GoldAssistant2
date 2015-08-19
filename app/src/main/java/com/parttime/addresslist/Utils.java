package com.parttime.addresslist;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qingmu.jianzhidaren.R;

/**
 *
 * Created by dehua on 15/7/31.
 */
public class Utils {

    public static void addStars(String creditworthiness,LinearLayout container, Activity activity, int drawableResId){
        int cre = Integer.valueOf(creditworthiness);
        addStars(cre, container, activity, drawableResId);

    }

    public static void addStars(int cre, LinearLayout container, Activity activity, int drawableResId) {
        container.removeAllViews();
        int num = (int)Math.round(cre * 1.0 / 10);
        for(int i = 0 ; i < num; i ++){
            container.addView(newStar(activity,drawableResId));
        }
    }

    /**
     * 不清除已经加入的view
     */
    public static void addStarsNotRemoveChildView(int cre, LinearLayout container, Activity activity, int drawableResId) {
        for(int i = 0 ; i < cre; i ++){
            container.addView(newStar(activity,drawableResId));
        }
    }

    /**
     * 不清除已经加入的view
     */
    public static void addStars(int num, LinearLayout container, Activity activity) {
        int crown = num / 10;
        int heart = num % 10;
        int crownDrawable = R.drawable.huangguan;
        int heartDrawable = R.drawable.icon_heart;
        for(int i = 0 ; i < crown; i ++){
            container.addView(newStar(activity,crownDrawable));
        }
        if(crown == 0) {
            for (int i = 0; i < heart; i++) {
                container.addView(newStar(activity, heartDrawable));
            }
        }
    }


    private static ImageView newStar(Activity activity, int drawableResId){
        ImageView star = new ImageView(activity);
        float width = activity.getResources().getDimension(R.dimen.star_width);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (int)width,
                (int)width);
        params.rightMargin = 10;
        star.setLayoutParams(params);
        star.setImageResource(drawableResId);
        return star;
    }
}
