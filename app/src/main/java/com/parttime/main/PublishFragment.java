package com.parttime.main;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.carson.constant.ConstantForSaveList;
import com.droid.carson.CityActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parttime.common.Image.ContactImageLoader;
import com.parttime.constants.ActionConstants;
import com.parttime.constants.SharedPreferenceConstants;
import com.parttime.net.BaseRequest;
import com.parttime.net.Callback;
import com.parttime.pojo.BannerItem;
import com.parttime.net.DefaultCallback;
import com.parttime.net.ErrorHandler;
import com.parttime.net.PublishRequest;
import com.parttime.pojo.PublishAvailabilityStatus;
import com.parttime.publish.JobBrokerChartsActivity;
import com.parttime.publish.JobManageActivity;
import com.parttime.publish.JobPlazaActivity;
import com.parttime.publish.JobTypeActivity;
import com.parttime.type.AccountType;
import com.parttime.type.BannerClickType;
import com.parttime.utils.ApplicationUtils;
import com.parttime.utils.CheckUtils;
import com.parttime.utils.IntentManager;
import com.parttime.utils.SharePreferenceUtil;
import com.parttime.widget.StepView;
import com.qingmu.jianzhidaren.R;
import com.quark.common.ToastUtil;
import com.quark.common.Url;
import com.quark.ui.widget.CustomDialog;
import com.quark.utils.WaitDialog;
import com.quark.volley.VolleySington;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 招人主界面
 *
 * @author wyw
 */
public class PublishFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String DEF_LOCATION_FAIL = "定位失败";
    public static final int REQUEST_CODE_LOCATION = 100;

    private String mParam1;
    private String mParam2;

    private WaitDialog dialog;

    private TextView mTxtCity;
    private String city;
    private String user_id;
    private int userType;
    private RelativeLayout mRLCity;
    private RequestQueue queue = VolleySington.getInstance()
            .getRequestQueue();


    private RelativeLayout rlLeftTop;
    private RelativeLayout rlRightTop;
    private RelativeLayout rlLeftBottom;
    private RelativeLayout rlRightBottom;
    private RelativeLayout rlLeftTop2;

    private TextView tvLeftTop;
    private ImageView ivLeftTop;

    private View topDivider;
    private TextView tvRightTop;
    private ImageView ivRightTop;

    private  ImageView ivRightBottom;
    private TextView tvRightBottom;

    private ViewPager viewPager;
    private PagerAdapter adapter;

    private StepView svSteps;
    private int lastPos;

    private AutoSlideManager autoSlideManager;
    private Handler handler;

    private float density;

    private Activity activity;

    private List<ImageView> bannerIvs = new ArrayList<>();
    private List<BannerItem> banners = new ArrayList<>();
    Gson gson = new Gson();
    private CityChangeReceiver cityChangeReceiver;

    //    private List<Bitmap> bannerPics = new ArrayList<>();
    private Map<Integer, Bitmap> bannerPics = new HashMap<>();
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PublishFragment.
     */
    public static PublishFragment newInstance(String param1, String param2) {
        PublishFragment fragment = new PublishFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PublishFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        SharePreferenceUtil spu = SharePreferenceUtil.getInstance(getActivity());
        userType = spu.loadIntSharedPreference(SharedPreferenceConstants.USER_TYPE);
        user_id = spu.loadStringSharedPreference(
                SharedPreferenceConstants.USER_ID, "");
        city = spu.loadStringSharedPreference(SharedPreferenceConstants.CITY);
        handler = new Handler();

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        if(activity != null){
            this.density = activity.getResources().getDisplayMetrics().density;
        }
    }



    private void initViews(View root){
        rlLeftTop = (RelativeLayout) root.findViewById(R.id.rl_left_top);
        rlRightTop = (RelativeLayout) root.findViewById(R.id.rl_right_top);
        rlLeftBottom = (RelativeLayout) root.findViewById(R.id.rl_left_bottom);
        rlRightBottom = (RelativeLayout) root.findViewById(R.id.rl_right_bottom);
        rlLeftTop2 = (RelativeLayout) root.findViewById(R.id.rl_left_top2);

        tvLeftTop = (TextView) root.findViewById(R.id.tv_left_top);
        ivLeftTop = (ImageView) root.findViewById(R.id.iv_left_top);


        topDivider = root.findViewById(R.id.top_verticle_divider);

        ivRightTop = (ImageView) root.findViewById(R.id.iv_right_top);
        tvRightTop = (TextView) root.findViewById(R.id.tv_right_top);

        ivRightBottom = ( ImageView) root.findViewById(R.id.iv_right_bottom);
        tvRightBottom = (TextView) root.findViewById(R.id.tv_right_bottom);

        rlLeftTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPublish();
            }
        });
        rlLeftBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), JobPlazaActivity.class));
            }
        });
        if(userType != AccountType.AGENT){
            rlRightTop.setVisibility(View.GONE);
            rlLeftTop.setVisibility(View.GONE);
            rlLeftTop2.setVisibility(View.VISIBLE);
            topDivider.setVisibility(View.GONE);
            ivRightBottom.setImageResource(R.drawable.guanli);
            tvRightBottom.setText(R.string.manage_job);
            rlLeftTop2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickPublish();
                }
            });

            rlRightBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), JobManageActivity.class));
                }
            });
        }else {
            rlRightTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), JobManageActivity.class));
                }
            });
            rlRightBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), JobBrokerChartsActivity.class));
                }
            });
        }
    }

    private void clickPublish() {
        showWait(true);
        new PublishRequest().publishAvailability(queue, new DefaultCallback() {
            @Override
            public void success(Object obj) {
                showWait(false);
                if (CheckUtils.isSafe(getActivity())) {
                    if (obj instanceof PublishAvailabilityStatus) {
                        PublishAvailabilityStatus status = (PublishAvailabilityStatus) obj;

                        switch (status) {
                            case SUCCESS:
                                // 免费，直接成功
                                startActivity(new Intent(getActivity(), JobTypeActivity.class));
                                break;
                            case SHOULD_VERIFY:
                                // 未认证
                                CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                                builder.setMessage(R.string.publish_tab_should_verify_msg);
                                builder.setTitle(R.string.publish_tab_should_verify_title);

                                builder.setPositiveButton(R.string.i_get_it,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                builder.create().show();
                                break;
                            case VERIFYING:
                                // 认证中
                                ToastUtil.showShortToast(R.string.publish_tab_verifying_tip);
                                break;
                            case LOCK_OF_MONEY:
                                // 余额不足
                                builder = new CustomDialog.Builder(getActivity());
                                builder.setMessage(R.string.lack_of_money);
                                builder.setTitle(R.string.prompt);

                                builder.setPositiveButton(R.string.to_recharge,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                IntentManager.intentToRecharge(getActivity());
                                                dialog.dismiss();
                                            }
                                        });
                                builder.setNegativeButton(R.string.cancel,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                builder.create().show();
                                break;
                            case SHOULD_PAY:
                                // 需付费
                                builder = new CustomDialog.Builder(getActivity());
                                builder.setMessage(R.string.publish_tab_pay_tip_msg);
                                builder.setTitle(R.string.pay_tip);

                                builder.setPositiveButton(R.string.aloud,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(getActivity(), JobTypeActivity.class));
                                                dialog.dismiss();
                                            }
                                        });
                                builder.setNegativeButton(R.string.cancel,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                builder.create().show();

                                break;
                        }

                    }
                }
            }

            @Override
            public void failed(Object obj) {
                showWait(false);
                if (CheckUtils.isSafe(getActivity())) {
                    new ErrorHandler((com.quark.jianzhidaren.BaseActivity) getActivity(), obj).showToast();
                }
            }
        });

    }

    private void showWait(boolean isShow) {
        if (isShow) {
            if (null == dialog) {
                dialog = new WaitDialog(getActivity());
            }
            dialog.show();
        } else {
            if (null != dialog) {
                dialog.dismiss();
            }
        }
    }

    private boolean hasNativeBanner;

    private void loadFromNative(){
        String s = SharePreferenceUtil.getInstance(activity).loadStringSharedPreference(SharedPreferenceConstants.BANNERS_INFO + "_" + city);
        if(!TextUtils.isEmpty(s)) {
            try {
                List<BannerItem> bis = gson.fromJson(s, new TypeToken<List<BannerItem>>() {
                }.getType());
                if(bis != null && bis.size() > 0){
                    hasNativeBanner = true;
                    banners.clear();
                    banners.addAll(bis);
                    startBanners(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadBanners(){
        loadFromNative();
        Map<String, String> params = new HashMap<>();

        params.put("city", city != null ? city : "深圳");
        new BaseRequest().request(Url.COMPANY_GET_BANNER, params, VolleySington.getInstance().getRequestQueue(), new Callback() {
            @Override
            public void success(Object obj) throws JSONException {
                if(activity.isFinishing() || !isAdded()){
                    return;
                }
                JSONObject json = (JSONObject) obj;
                JSONArray bannerList = json.getJSONArray("bannerList");
                if (bannerList != null) {
                    List<BannerItem> bis = new ArrayList<BannerItem>();

                    for (int i = 0; i < bannerList.length(); ++i) {
                        String s = bannerList.getJSONObject(i).toString();
                        BannerItem bannerItem = gson.fromJson(s, BannerItem.class);
                        bis.add(bannerItem);
                    }
                    banners.clear();
                    banners.addAll(bis);
                    SharePreferenceUtil.getInstance(activity).saveSharedPreferences(SharedPreferenceConstants.BANNERS_INFO + "_" + city, gson.toJson(bis));
                    startBanners(true);
                }

            }

            @Override
            public void failed(Object obj) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_publish, container, false);
        initTitle(view);

        initViews(view);
        svSteps = (StepView) view.findViewById(R.id.sv_steps);
//        svSteps.setStepCount(4);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
//        viewPager.setOffscreenPageLimit(0);
        /*List<View> tvs = new ArrayList<>();
        for(int i = 0; i < 4; ++i){
            TextView tv = new TextView(getActivity());
            tv.setBackgroundColor(0xaa000000 | (0xff << (i *8)));
            tv.setText("" + i);
            tv.setGravity(Gravity.CENTER);
            tvs.add(tv);
        }*/

        adapter = new BannerAdapter(bannerIvs);

//        svSteps.setStepCount(bannerIvs.size());
//        svSteps.current(viewPager.getCurrentItem());
        autoSlideManager = new AutoSlideManager(handler, viewPager, svSteps, bannerIvs);
        viewPager.setOnPageChangeListener(autoSlideManager);
        viewPager.setOnTouchListener(autoSlideManager);

        lastPos = /*Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % 4*/0;

        mRLCity = (RelativeLayout) view
                .findViewById(R.id.home_page_city_relayout);
        mRLCity.setOnClickListener(this);

        bindCity(view);

        loadBanners();

        return view;
    }

    protected View.OnClickListener onBannerItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            if(tag instanceof BannerItem){
                BannerItem item = (BannerItem) tag;
                if(item.click_type == BannerClickType.WEB){
                    com.parttime.base.IntentManager.intentToWebBrowser((com.quark.jianzhidaren.BaseActivity) getActivity(), item.para);
                }else if(item.click_type == BannerClickType.ACTIVITY_DETAIL){
                    try {
                        int jobId = Integer.parseInt(item.para);
                        IntentManager.openJobDetailActivity(getActivity(), jobId, null);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else if(item.click_type == BannerClickType.FILTER){
                    IntentManager.goToJobPlaza(getActivity());
                }
            }
        }
    };

    private ImageView mkIv(int index){
        ImageView imageView = new ImageView(activity);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        if (index % banners.size() < banners.size()){
            imageView.setTag(banners.get(index % banners.size()));
        }
        return imageView;

    }

    protected void startBanners(final boolean net){
        if(activity.isFinishing()){
            return;
        }
        int oldSize = bannerIvs.size();
        int need = banners.size()/* <= 3 ? banners.size() * 2 : banners.size()*/;
        if(oldSize > need){
            do {
                bannerIvs.remove(bannerIvs.size() - 1);
            }while(bannerIvs.size() > need);
        }else if(oldSize < need){
            for(int i = 0; i < need - oldSize; ++i){
                ImageView imageView = mkIv(i + oldSize);
                bannerIvs.add(imageView);
            }
        }
//        for(int i = 0;)
//        bannerIvs.clear();
//        for(int i = 0 ; i < (banners.size() <= 3 ? banners.size() * 2 : banners.size()) ; ++i){


//            ImageView imageView = mkIv(i);
//            if(net && !hasNativeBanner) {
//                imageView.setImageResource(R.drawable.banner_loading);
//            }
            /*imageView.setOnClickListener(onBannerItemClick);*/
//            ContactImageLoader.loadNativePhoto(null, bannerItem.pic, imageView, VolleySington.getInstance().getRequestQueue());

//            bannerIvs.add(imageView);
//        }
        if(viewPager.getAdapter() == null){
            viewPager.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
        for(int i = 0 ; i < banners.size(); ++i){
            BannerItem bannerItem = banners.get(i);
            ContactImageLoader.loadImage(bannerItem.pic, net, VolleySington.getInstance().getRequestQueue(), new ContactImageLoader.ImageCallback() {
                @Override
                public void imageLoaded(final Bitmap bm, final int tag) {
                    bannerPics.put(tag, bm);
                    if (activity.isFinishing() || !isAdded()) {
                        return;
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(!net && bm == null){
                                bannerIvs.get(tag).setImageResource(R.drawable.banner_loading);

                                if(bannerIvs.size() == banners.size() * 2){
                                    bannerIvs.get(tag + banners.size()).setImageResource(R.drawable.banner_loading);
                                }
                                return;
                            }
                            bannerIvs.get(tag).setImageBitmap(bm);

                            if(bannerIvs.size() == banners.size() * 2){
                                bannerIvs.get(tag + banners.size()).setImageBitmap(bm);
                            }

                            adapter.notifyDataSetChanged();
                        }
                    });

                }

                @Override
                public void failed(int tag) {

                }
            }, i);
        }

        if(bannerIvs.size() <= 0){
            viewPager.setVisibility(View.GONE);
        }else {
            viewPager.setVisibility(View.VISIBLE);
        }

        autoSlideManager.setInitPosition(lastPos);

        viewPager.setCurrentItem(lastPos);
        svSteps.setStepCount(banners.size());
        svSteps.current(lastPos);


    }

    @Override
    public void onStart() {
        super.onStart();
        autoSlideManager.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        autoSlideManager.stop();
        hasNativeBanner = false;
    }


    private void initTitle(View view) {
        // 左侧文本框
        mTxtCity = (TextView) view.findViewById(R.id.home_page_city);
        mTxtCity.setText(city);
        // 隐藏标题右侧按钮
        LinearLayout right_layout = (LinearLayout) view
                .findViewById(R.id.right_layout);
        right_layout.setVisibility(View.GONE);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(R.string.publish_title);
        // 头部设置成灰色
        RelativeLayout reLayout = (RelativeLayout) view
                .findViewById(R.id.home_common_guangchang_relayout);
        reLayout.setBackgroundColor(getResources().getColor(
                R.color.guanli_common_color));
    }

    private void bindCity(View view) {
        // 当前城市
        mTxtCity.setText(ApplicationUtils.getCity());
    }


    @Override
    public void onResume() {
        super.onResume();

        // 注册城市切换监听器
        cityChangeReceiver = new CityChangeReceiver();
        getActivity().registerReceiver(cityChangeReceiver, new IntentFilter(ActionConstants.ACTION_CITY_CHANGE));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cityChangeReceiver != null) {
            getActivity().unregisterReceiver(cityChangeReceiver);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.home_page_city_relayout:
                Intent intent = new Intent();
                // 传值当前定位城市
                intent.setClass(getActivity(), CityActivity.class);
                startActivityForResult(intent, REQUEST_CODE_LOCATION);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_LOCATION) {
                // String province = data.getExtras().getString("province");
                String oldCity = city;
                city = data.getExtras().getString(CityActivity.EXTRA_CITY);
                if ((city != null) && (!city.equals(""))) {
                    mTxtCity.setText(city);
                    // 跟原来保存的城市对比
                    String old_city = ApplicationUtils.getCity();
                    ConstantForSaveList.change_city = !old_city.equals(city);
                    SharePreferenceUtil.getInstance(getActivity()).saveSharedPreferences(SharedPreferenceConstants.CITY, city);
                    // requestChangeCity();
                    if(!city.equals(oldCity)){
                        loadBanners();
                        new PublishRequest().changeCity(city, queue);
                    }
                }
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class BannerAdapter extends PagerAdapter {
        public List<ImageView> views;

        public BannerAdapter(List<ImageView> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size()/* <= 0 ? 0 : Integer.MAX_VALUE*/;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            return super.instantiateItem(container, position);
//            if (views.size() > 0) {
//                try {
//                views.get(position % views.size()).setImageBitmap(bannerPics.get(position % views.size()));
            if(views.size() > 0) {
                ViewPager pager = ((ViewPager) container);
                ImageView imageView = views.get(position % views.size());
                if(pager.indexOfChild(imageView) >= 0){
                    pager.removeView(imageView);
                }
                pager.addView(imageView, 0);
                return views.get(position % views.size());
            }else {
                return null;
            }
//                }catch (IllegalStateException e){
//                    Log.e("test", "exception");
//                    int count = views.size();
//                    for(int i = 0; i < count; ++i){
//                        ImageView imageView = mkIv(i);
//                        imageView.setImageDrawable(views.get(i).getDrawable());
//                        views.add(imageView);
//                    }
//                    ((ViewPager) container).addView(views.get(position % views.size()), 0);
//                    return views.get(position % views.size());
//                }
//            } else {
//                return null;
//            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            if (views.size() > 0) {
                ((ViewPager) container).removeView(views.get(position % views.size()));
            }
        }
    }

    private class AutoSlideManager implements ViewPager.OnPageChangeListener, View.OnTouchListener{
        private ViewPager pager;
        private StepView stepView;
        private long duration = 3000;
        private Handler handler;

        private List<ImageView> views;

        private boolean stop;

        private int position;

        private AutoSlideManager(Handler handler, ViewPager pager, StepView stepView, List<ImageView> views) {
            this.pager = pager;
            this.stepView = stepView ;
            this.handler = handler;
            this.views = views;
        }

        public void setDuration(long millis){
            duration = millis;
        }

        public void setInitPosition(int position){
            this.position = position;
        }

        public void start(){
            stop = false;
            if(handler != null){
                handler.postDelayed(worker, duration);
            }
        }

        public void stop(){
            stop = true;
        }

        protected void post(){

        }

        private Runnable worker = new Runnable() {
            @Override
            public void run() {
                if(!stop) {

                    if(views.size() <= 0){
                        return;
                    }
                    int currentItem = (pager.getCurrentItem() + 1) % views.size();
                    pager.setCurrentItem(currentItem , true);
                    stepView.current(currentItem );
                    handler.postDelayed(this, duration);
                }
            }
        };

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            lastPos = position;
            this.position = position;
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if(state == pager.SCROLL_STATE_IDLE){

                stepView.current(position);
            }
        }

        private boolean moved;
        private float downX, downY, upX, upY;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){

                case MotionEvent.ACTION_DOWN:
                    moved = false;
                    downX = event.getX();
                    downY = event.getY();
                    handler.removeCallbacks(worker);
                    break;
                case MotionEvent.ACTION_MOVE:
                    moved = true;
                    handler.removeCallbacks(worker);
                    break;
                case MotionEvent.ACTION_UP:
                    handler.postDelayed(worker, duration);
                    upX = event.getX();
                    upY = event.getY();
                    if(!moved || Math.sqrt(Math.pow(upX - downX, 2) + Math.pow(upY - downY, 2)) < 6) {
                        int currentItem = pager.getCurrentItem();
                        if(views.size() > 0){
                            onBannerItemClick.onClick(views.get(currentItem % views.size()));
                        }
//                        if (currentItem < views.size()) {
//                            onBannerItemClick.onClick(views.get(currentItem));
//                        }
                    }
                    break;
            }
            return false;
        }
    }

    class CityChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ActionConstants.ACTION_CITY_CHANGE)) {
                String city = intent.getStringExtra(ActionConstants.EXTRA_CITY_CHANGE);
                mTxtCity.setText(city);
            }
        }
    }

}
