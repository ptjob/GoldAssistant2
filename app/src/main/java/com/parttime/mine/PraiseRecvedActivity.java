package com.parttime.mine;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.parttime.base.LocalInitActivity;
import com.parttime.base.WithTitleActivity;
import com.parttime.net.BaseRequest;
import com.parttime.net.Callback;
import com.parttime.widget.RankView;
import com.qingmu.jianzhidaren.R;
import com.quark.common.Url;
import com.quark.volley.VolleySington;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cjz on 2015/7/14.
 */
public class PraiseRecvedActivity extends LocalInitActivity{
    private static final int TOTAL_SCORE = 5;
    @ViewInject(R.id.tv_sum)
    private TextView tvSum;
    @ViewInject(R.id.rv_sum)
    private RankView rvSum;

    @ViewInject(R.id.tv_close_to)
    private TextView tvCloseTo;
    @ViewInject(R.id.rv_close_to)
    private RankView rvCloseTo;

    @ViewInject(R.id.tv_reply_quickly)
    private TextView tvReplyQuick;
    @ViewInject(R.id.rv_reply_quickly)
    private RankView rvReplyQuick;

    @ViewInject(R.id.tv_sastification)
    private TextView tvSastify;
    @ViewInject(R.id.rv_sastification)
    private RankView rvSastify;

    private int sum;
    private int closeTo;
    private int replyQuick;
    private int sastification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_praise_recved);
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        loadData();
    }

    private void loadData(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("company_id", getCompanyId());
        new BaseRequest().request(Url.MY_RECEVED_PRAISE, params, VolleySington.getInstance().getRequestQueue(), new Callback() {
            @Override
            public void success(Object obj) {
                JSONObject json = (JSONObject) obj;
                try {
                    if (json != null) {
                        sum = json.getInt("point");
                        closeTo = json.getInt("accord_point");
                        replyQuick = json.getInt("response_point");
                        sastification = json.getInt("satisfaction_point");
                        updateViews();
                    }
                }catch (Exception e){

                }

            }

            @Override
            public void failed(Object obj) {

            }
        });
    }

    private void updateViews(){
        tvSum.setText(getString(R.string.total_praise) + " " + sum + "/" + TOTAL_SCORE);
        rvSum.rank(sum);

        tvCloseTo.setText(getString(R.string.close_to_job) + " " + closeTo + "/" + TOTAL_SCORE);
        rvCloseTo.rank(closeTo);

        tvReplyQuick.setText(getString(R.string.reply_quickly) + " " + replyQuick + "/" + TOTAL_SCORE);
        rvReplyQuick.rank(replyQuick);

        tvSastify.setText(getString(R.string.sastifition_rate) + " " + sastification + "/" + TOTAL_SCORE);
        rvSastify.rank(sastification);
    }

    @Override
    protected void initViews() {
        super.initViews();
        left(TextView.class, R.string.back);
        right(ImageView.class,R.drawable.wenhao,new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                show(v);
            }

        });
        center(R.string.praise_recved);
    }


    private void show(View view) {
        View popView = getLayoutInflater().inflate(R.layout.activity_wenhao_popup, null);
        final PopupWindow popupWindow = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));

        //设置popwindow显示位置
        popupWindow.showAsDropDown(view);
        /*popupWindow.showAtLocation(view, Gravity.TOP, -0,
                (int)getResources().getDimension(R.dimen.praise_activity_popup_margin));*/
        //获取popwindow焦点
        popupWindow.setFocusable(true);
        //设置popwindow如果点击外面区域，便关闭。
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
    }

    @Override
    protected ViewGroup getLeftWrapper() {
        return null;
    }

    @Override
    protected ViewGroup getRightWrapper() {
        return null;
    }

    @Override
    protected TextView getCenter() {
        return null;
    }
}
