package com.droid.carson;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.droid.carson.Activity02.LocateIn;
import com.droid.carson.MyLetterListView.OnTouchingLetterChangedListener;
import com.parttime.common.head.ActivityHead;
import com.parttime.constants.SharedPreferenceConstants;
import com.parttime.utils.CheckUtils;
import com.parttime.utils.SharePreferenceUtil;
import com.qingmu.jianzhidaren.R;
import com.quark.db.CityService;
import com.quark.jianzhidaren.BaseActivity;
import com.quark.utils.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class CityActivity extends BaseActivity implements OnItemClickListener {
    private static final String TAG = "CityActivity";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_CITY = "city";
    private BaseAdapter adapter;
    private ListView personList;
    private TextView overlay; // 对话框首字母textview
    private MyLetterListView letterListView; // A-Z listview
    private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
    private String[] sections;// 存放存在的汉语拼音首字母
    private Handler handler;
    private OverlayThread overlayThread; // 显示首字母对话框
    protected ArrayList<City> allCity_lists; // 所有城市列表
    private ArrayList<City> city_lists;// 城市列表
    ListAdapter.TopViewHolder topViewHolder;
    private String lngCityName;
    //    private ImageButton backImb;// 返回
    private ClearEditText clearEdt;
    WindowManager windowManager;
    private LocationManagerProxy mLocationManagerProxy;

    //    private RelativeLayout topLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_main);
        super.onCreate(savedInstanceState);


//        topLayout = (RelativeLayout) findViewById(R.id.title);
//        topLayout.setBackgroundColor(getResources().getColor(
//                R.color.guanli_common_color));

        ActivityHead activityHead = new ActivityHead(this);

        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra(EXTRA_TITLE);
            if (CheckUtils.isEmpty(title)) {
                title = getString(R.string.city_choose_title);
            }
            activityHead.setCenterTxt1(title);
        }

        // 若没有定位过，则显示定位中
        lngCityName = SharePreferenceUtil.getInstance(this).loadStringSharedPreference(SharedPreferenceConstants.DINGWEICITY, getString(R.string.location_ing));
//        backImb = (ImageButton) findViewById(R.id.backImb);
//        backImb.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                finish();
//            }
//        });
        clearEdt = (ClearEditText) findViewById(R.id.filter_edit);
        clearEdt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                filterData(arg0.toString());
            }
        });
        personList = (ListView) findViewById(R.id.list_view);
        allCity_lists = new ArrayList<City>();
        letterListView = (MyLetterListView) findViewById(R.id.MyLetterListView01);
        letterListView
                .setOnTouchingLetterChangedListener(new LetterListViewListener());
        // Activity02.setLocateIn(new GetCityName());
        alphaIndexer = new HashMap<String, Integer>();
        handler = new Handler();
        overlayThread = new OverlayThread();
        personList.setOnItemClickListener(this);
        personList.setAdapter(adapter);
        initOverlay();
        hotCityInit();
        setAdapter(allCity_lists);

        initGaoDe();
    }

    /**
     * 初始化定位
     */
    private void initGaoDe() {
        // 初始化定位，只采用网络定位
        mLocationManagerProxy = LocationManagerProxy.getInstance(this);
        mLocationManagerProxy.setGpsEnable(false);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用destroy()方法
        // 其中如果间隔时间为-1，则定位只定一次,
        // 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, 60 * 1000, 3, new MyLocationListener());

    }

    /**
     * 热门城市
     */
    public void hotCityInit() {
        City city = new City("", "-");
        // allCity_lists.add(city);
        // city = new City("", "-");
        allCity_lists.add(city);
        allCity_lists.addAll(CityService.getHotCity(this));
        city_lists = CityService.getCityList(this);
        allCity_lists.addAll(city_lists);
    }

    private void setAdapter(List<City> list) {
        adapter = new ListAdapter(this, list);
        personList.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String currentCity = allCity_lists.get(position).getName();
        if (!"".equals(currentCity)) {
            // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
            Toast.makeText(getApplication(),
                    "成功切换到城市:" + allCity_lists.get(position).getName(),
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra(EXTRA_CITY, allCity_lists.get(position).getName());
            CityActivity.this.setResult(RESULT_OK, intent);
            CityActivity.this.finish();
        }

    }

    public class ListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<City> list;
        final int VIEW_TYPE = 2;

        public ListAdapter(Context context, List<City> list) {
            this.inflater = LayoutInflater.from(context);
            this.list = list;
            alphaIndexer = new HashMap<String, Integer>();
            sections = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                // 当前汉语拼音首字母
                String currentStr = getAlpha(list.get(i).getPinyi());
                // 上一个汉语拼音首字母，如果不存在为“ ”
                String previewStr = (i - 1) >= 0 ? getAlpha(list.get(i - 1)
                        .getPinyi()) : " ";
                if (!previewStr.equals(currentStr)) {
                    String name = getAlpha(list.get(i).getPinyi());
                    alphaIndexer.put(name, i);
                    sections[i] = name;
                }
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            // TODO Auto-generated method stub
            int type = 0;
            // if (position == 0) {
            // type = 2;
            // } else if (position == 1) {
            // type = 1;
            // }
            if (position == 0)
                type = 1;
            return type;
        }

        @Override
        public int getViewTypeCount() {// 这里需要返回需要集中布局类型，总大小为类型的种数的下标
            return VIEW_TYPE;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            int viewType = getItemViewType(position);
            if (viewType == 1) {
                if (convertView == null) {
                    topViewHolder = new TopViewHolder();
                    convertView = inflater.inflate(R.layout.frist_list_item,
                            null);
                    topViewHolder.alpha = (TextView) convertView
                            .findViewById(R.id.alpha);
                    topViewHolder.name = (TextView) convertView
                            .findViewById(R.id.lng_city);
                    convertView.setTag(topViewHolder);
                } else {
                    topViewHolder = (TopViewHolder) convertView.getTag();
                }

                topViewHolder.name.setText(lngCityName);
                topViewHolder.alpha.setVisibility(View.VISIBLE);
                topViewHolder.alpha.setText("定位城市");
                topViewHolder.name.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (!getString(R.string.location_fail).equals(lngCityName)) {
                            // ToastUtil.showLongToast(lngCityName + "");
                            topViewHolder.name.setClickable(false);
                            Toast.makeText(getApplication(),
                                    "成功切换到城市:" + lngCityName,
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            // intent.putExtra("province", province);
                            intent.putExtra(EXTRA_CITY, lngCityName);
                            CityActivity.this.setResult(RESULT_OK, intent);
                            CityActivity.this.finish();
                        }
                    }
                });
            }
            // else if (viewType == 2) {
            // final ShViewHolder shViewHolder;
            // if (convertView == null) {
            // shViewHolder = new ShViewHolder();
            // convertView = inflater.inflate(R.layout.search_item, null);
            // shViewHolder.editText = (ClearEditText) convertView
            // .findViewById(R.id.sh);
            // convertView.setTag(shViewHolder);
            // } else {
            // shViewHolder = (ShViewHolder) convertView.getTag();
            // }

            else {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.list_item, null);
                    holder = new ViewHolder();
                    holder.alpha = (TextView) convertView
                            .findViewById(R.id.alpha);
                    holder.name = (TextView) convertView
                            .findViewById(R.id.name);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if (position >= 0) {
                    holder.name.setText(list.get(position).getName());
                    String currentStr = getAlpha(list.get(position).getPinyi());
                    String previewStr = (position - 1) >= 0 ? getAlpha(list
                            .get(position - 1).getPinyi()) : " ";
                    if (!previewStr.equals(currentStr)) {
                        holder.alpha.setVisibility(View.VISIBLE);
                        if (currentStr.equals("#")) {
                            currentStr = "热门城市";
                        }
                        holder.alpha.setText(currentStr);
                    } else {
                        holder.alpha.setVisibility(View.GONE);
                    }
                }
            }
            return convertView;
        }

        private class ViewHolder {
            TextView alpha; // 首字母标题
            TextView name; // 城市名字
        }

        private class TopViewHolder {
            TextView alpha; // 首字母标题
            TextView name; // 城市名字
        }

        private class ShViewHolder {
            ClearEditText editText;

        }
    }

    // 初始化汉语拼音首字母弹出提示框
    private void initOverlay() {
        LayoutInflater inflater = LayoutInflater.from(this);
        overlay = (TextView) inflater.inflate(R.layout.overlay, null);
        overlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        windowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlay, lp);
    }

    @Override
    protected void onDestroy() {
        if (overlay != null && windowManager != null) {
            windowManager.removeView(overlay);
        }
        // 销毁定位
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.destroy();
        }
        super.onDestroy();
    }

    private class LetterListViewListener implements
            OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);
                personList.setSelection(position);
                overlay.setText(sections[position]);
                overlay.setVisibility(View.VISIBLE);
                handler.removeCallbacks(overlayThread);
                // 延迟一秒后执行，让overlay为不可见
                handler.postDelayed(overlayThread, 1500);
            }
        }

    }

    // 设置overlay不可见
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }

    }

    // 获得汉语拼音首字母
    private String getAlpha(String str) {

        if (str.equals("-")) {
            return "&";
        }
        if (str == null) {
            return "#";
        }
        if (str.trim().length() == 0) {
            return "#";
        }
        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else {
            return "#";
        }
    }

    private class GetCityName implements LocateIn {
        @Override
        public void getCityName(String name) {
            System.out.println(name);
            if (topViewHolder.name != null) {
                lngCityName = name;
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {

        Log.e("tag", "filter data");
        if (TextUtils.isEmpty(filterStr)) {
            allCity_lists.clear();
            hotCityInit();
            setAdapter(allCity_lists);

        } else {
            allCity_lists.clear();
            city_lists = CityService.getCityList(this, filterStr);
            allCity_lists.addAll(city_lists);
            setAdapter(allCity_lists);
        }

    }

    public static interface DiyAction extends Serializable {
        void clicked(int index, String city, Serializable extra, BaseActivity activity);
    }

    private class MyLocationListener implements AMapLocationListener {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null
                    && amapLocation.getAMapException().getErrorCode() == 0) {
                // 定位成功回调信息，设置相关消息
                if (amapLocation.getCity() != null) {
                    mLocationManagerProxy.removeUpdates(this);
                    String curCity = amapLocation.getCity();
                    if (curCity.endsWith("市")) {
                        curCity = curCity.substring(0, curCity.length() - 1);
                    }
                    Logger.i(TAG, "[onLocationChanged]curCity=" + curCity);
                    SharePreferenceUtil.getInstance(CityActivity.this).saveSharedPreferences(SharedPreferenceConstants.FIRST_LOCATION, false);
                    SharePreferenceUtil.getInstance(CityActivity.this).saveSharedPreferences(SharedPreferenceConstants.DINGWEICITY, curCity);
                    lngCityName = curCity;
                    adapter.notifyDataSetChanged();
                }
            } else {
                // 定位失败
                Logger.i(TAG, "[onLocationChanged]location fail!");
                lngCityName = getString(R.string.location_fail);
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}