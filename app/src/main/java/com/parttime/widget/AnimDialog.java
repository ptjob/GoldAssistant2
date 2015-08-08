package com.parttime.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.qingmu.jianzhidaren.R;

/**
 * Created by cjz on 2015/8/8.
 */
public class AnimDialog extends Dialog{
    private static int default_width = 200; // 默认宽度
    private static int default_height = 200;// 默认高度

    private OnShowListener onShowListener;

    private ImageView ivAnim;
    public AnimDialog(Context context, int anim) {
        this(context, default_width, default_height, anim, R.style.wait_dialog_style);
    }

    public AnimDialog(Context context, int anim, int style) {
        this(context, default_width, default_height,  anim, style);
    }

    public AnimDialog(Context context, int width, int height, int anim,
                      int style) {
        super(context, style);
        // set content
        View view = LayoutInflater.from(context).inflate(R.layout.dlg_ani, null);
        ivAnim = (ImageView) view.findViewById(R.id.iv_anim);
//        ivAnim.setBackgroundResource(anim);
        ivAnim.setImageResource(anim);
        setContentView(view);
        // set window params
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        // set width,height by density and gravity
        float density = getDensity(context);
        params.width = (int) (width * density);
        params.height = (int) (height * density);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        super.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                AnimationDrawable anim = (AnimationDrawable) ivAnim.getDrawable();
                anim.setOneShot(true);
                anim.start();
                onShowListener.onShow(dialog);
            }
        });
    }

    @Override
    public void setOnShowListener(OnShowListener listener) {
        onShowListener = listener;
    }

    private float getDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.density;
    }
}
