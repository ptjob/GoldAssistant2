package com.parttime.mine;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.carson.constant.ConstantForSaveList;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.parttime.base.IntentManager;
import com.parttime.base.WithTitleActivity;
import com.parttime.net.BaseRequest;
import com.parttime.net.Callback;
import com.parttime.pojo.IntroItem;
import com.parttime.widget.FormItem;
import com.qingmu.jianzhidaren.R;
import com.quark.common.Url;
import com.quark.ui.widget.CustomDialog;
import com.quark.volley.VolleySington;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cjz on 2015/7/14.
 */
public class FreshManGuideActivity extends WithTitleActivity {
    @ViewInject(R.id.lv_helps)
    private ListView lv;
//    @ViewInject(R.id.btn_contact_service)
//    private Button btnHelp;

    private CustomDialog dlg;

    private IntroAdapter adapter;
    private List<IntroItem> introItems = new ArrayList<>();

    private View.OnClickListener onItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            IntroItem item = (IntroItem) view.getTag();
            if(item != null){
                IntentManager.intentToWebBrowser(FreshManGuideActivity.this, item.url);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_fresh_man_guide);
        ViewUtils.inject(this);
        super.onCreate(savedInstanceState);
        loadData();
    }

    /*@OnClick(R.id.btn_contact_service)
    public void contactCustmService(View v){
        dlg = createDialog(getString(R.string.friendly_tips), "确认拨号",
                getString(R.string.contact_customer_service), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dlg.dismiss();
                        Intent intent = new Intent(
                                Intent.ACTION_CALL,
                                Uri.parse("tel:"
                                        + ConstantForSaveList.CARSON_CALL_NUMBER));
                        FreshManGuideActivity.this.startActivity(intent);
                    }
                }, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dialog != null && dlg.isShowing()){
                            dlg.dismiss();
                        }
                    }
                });
        dlg.show();
    }*/

    protected void loadData(){
        showWait(true);
        Map<String, String> params = new HashMap<>();
        new BaseRequest().request(Url.COMPANY_INTRO_LIST, params, VolleySington.getInstance().getRequestQueue(), new Callback() {
            @Override
            public void success(Object obj) throws JSONException {

                JSONObject json = (JSONObject) obj;
                JSONArray introList = json.getJSONArray("introList");
                if(introList != null && introList.length() > 0){
                    Gson gson = new Gson();
                    List<IntroItem> iis = new ArrayList<IntroItem>();
                    for(int i = 0 ; i < introList.length(); ++i){
                        String s = introList.get(i).toString();
                        IntroItem introItem = gson.fromJson(s, IntroItem.class);
                        iis.add(introItem);
                    }
                    introItems.clear();
                    introItems.addAll(iis);
                    adapter.notifyDataSetChanged();
                    showWait(false);
                }
            }

            @Override
            public void failed(Object obj) {
                showWait(false);
            }
        });
    }

    @Override
    protected void initViews() {
        super.initViews();
        left(TextView.class, R.string.back);
        center(R.string.fresh_man_guide);

        adapter = new IntroAdapter(this, introItems);
        adapter.setActAsOnItemClick(onItemClick);
        lv.setDivider(null);
        lv.setAdapter(adapter);

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

    private class IntroAdapter extends BaseAdapter{

        private View.OnClickListener actAsOnItemClick;

        private Context context;
        private List<IntroItem> datas;
        private LayoutInflater inflater;

        private IntroAdapter(Context context, List<IntroItem> datas) {
            this.context = context;
            this.datas = datas;

            inflater = LayoutInflater.from(context);
        }

        public void setActAsOnItemClick(View.OnClickListener onClick){
            actAsOnItemClick = onClick;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FormItem item;
            if(convertView == null){
                item = new FormItem(context);
                item.setOnClickListener(actAsOnItemClick);
                item.hideTopDivider();
                item.hideBottomDivider();
                item.setMinimumHeight(context.getResources().getDimensionPixelSize(R.dimen.form_item_height));
                item.setBackgroundResource(R.drawable.item_selector);
                item.hideIcon();
                item.showBottomDivider();
                convertView = item;
            }else {
                item = (FormItem) convertView;
            }
            IntroItem introItem = (IntroItem) getItem(position);
            item.setTag(introItem);
            item.setTitle(introItem.title);

            return convertView;
        }

    }
}
