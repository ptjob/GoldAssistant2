package com.parttime.main;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.carson.constant.ConstantForSaveList;
import com.droid.carson.Activity01;
import com.parttime.constants.SharedPreferenceConstants;
import com.parttime.net.BaseRequest;
import com.parttime.net.Callback;
import com.parttime.publish.JobBrokerChartsActivity;
import com.parttime.publish.JobManageActivity;
import com.parttime.publish.JobPlazaActivity;
import com.parttime.publish.JobTypeActivity;
import com.parttime.type.AccountType;
import com.parttime.utils.ApplicationUtils;
import com.parttime.utils.SharePreferenceUtil;
import com.parttime.widget.StepView;
import com.qingmu.jianzhidaren.R;
import com.quark.common.Url;
import com.quark.volley.VolleySington;

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

    private TextView mTxtCity;
    private String city;
    private String user_id;
    private int userType;
    private RelativeLayout mRLCity;
    protected RequestQueue queue = VolleySington.getInstance()
            .getRequestQueue();


    private RelativeLayout rlLeftTop;
    private RelativeLayout rlRightTop;
    private RelativeLayout rlLeftBottom;
    private RelativeLayout rlRightBottom;

    private View topDivider;
    private TextView tvRightTop;
    private ImageView ivRightTop;

    private  ImageView ivRightBottom;
    private TextView tvRightBottom;

    private ViewPager viewPager;
    private PagerAdapter adapter;

    private StepView svSteps;
    private int lastPos = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % 4;
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
        loadBanners();
    }

    private void initViews(View root){
        rlLeftTop = (RelativeLayout) root.findViewById(R.id.rl_left_top);
        rlRightTop = (RelativeLayout) root.findViewById(R.id.rl_right_top);
        rlLeftBottom = (RelativeLayout) root.findViewById(R.id.rl_left_bottom);
        rlRightBottom = (RelativeLayout) root.findViewById(R.id.rl_right_bottom);

        topDivider = root.findViewById(R.id.top_verticle_divider);

        ivRightTop = (ImageView) root.findViewById(R.id.iv_right_top);
        tvRightTop = (TextView) root.findViewById(R.id.tv_right_top);

        ivRightBottom = ( ImageView) root.findViewById(R.id.iv_right_bottom);
        tvRightBottom = (TextView) root.findViewById(R.id.tv_right_bottom);

        rlLeftTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), JobTypeActivity.class));
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
            topDivider.setVisibility(View.GONE);
            ivRightBottom.setImageResource(R.drawable.guanli);
            tvRightBottom.setText(R.string.manage_job);

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

    private void loadBanners(){
        Map<String, String> params = new HashMap<>();
        params.put("city", city);
        new BaseRequest().request(Url.COMPANY_GET_BANNER, params, VolleySington.getInstance().getRequestQueue(), new Callback() {
            @Override
            public void success(Object obj) throws JSONException {
                JSONObject json = (JSONObject) obj;
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
        svSteps.setStepCount(4);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        List<View> tvs = new ArrayList<>();
        for(int i = 0; i < 4; ++i){
            TextView tv = new TextView(getActivity());
            tv.setText("" + i);
            tv.setGravity(Gravity.CENTER);
            tvs.add(tv);
        }
        adapter = new BannerAdapter(tvs);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if (i - lastPos > 0) {
                    svSteps.forward();
                } else if (lastPos - i > 0) {
                    svSteps.backward();
                }
                lastPos = i;
            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        viewPager.setCurrentItem(lastPos);


        mRLCity = (RelativeLayout) view
                .findViewById(R.id.home_page_city_relayout);
        mRLCity.setOnClickListener(this);

        bindCity(view);

        return view;
    }

    private void initTitle(View view) {
        // 左侧文本框
        mTxtCity = (TextView) view.findViewById(R.id.home_page_city);
        mTxtCity.setText(city);
        // 隐藏标题右侧按钮
        LinearLayout right_layout = (LinearLayout) view
                .findViewById(R.id.right_layout);
        right_layout.setVisibility(View.GONE);
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
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
                intent.putExtra(Activity01.EXTRA_CITYLIST_CITY,
                        SharePreferenceUtil.getInstance(getActivity()).loadStringSharedPreference(
                                SharedPreferenceConstants.DINGWEICITY, DEF_LOCATION_FAIL));
                intent.setClass(getActivity(), Activity01.class);
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
                city = data.getExtras().getString(Activity01.EXTRA_CITY);
                if ((city != null) && (!city.equals(""))) {
                    mTxtCity.setText(city);
                    // 跟原来保存的城市对比
                    String old_city = ApplicationUtils.getCity();
                    ConstantForSaveList.change_city = !old_city.equals(city);
                    SharePreferenceUtil.getInstance(getActivity()).saveSharedPreferences(SharedPreferenceConstants.CITY, city);
                    // requestChangeCity();
                }
            }
        }
    }

    private void requestChangeCity() {
        // 切换到指定城市,访问后台传输城市
        String cityUrl;
        cityUrl = Url.CHANGE_CITY_CUSTOM + "?token="
                + MainTabActivity.token;

        StringRequest request = new StringRequest(
                Request.Method.POST, cityUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(
                    VolleyError volleyError) {
            }
        }) {
            @Override
            protected Map<String, String> getParams()
                    throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("company_id", user_id);
                map.put("city", city);
                return map;
            }
        };
        queue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                ConstantForSaveList.DEFAULTRETRYTIME * 1000, 1,
                1.0f));
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
        public List<View> views;

        public BannerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            return super.instantiateItem(container, position);
            ((ViewPager)container).addView(views.get(position % views.size()), 0);
            return views.get(position % views.size());
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            ((ViewPager)container).removeView(views.get(position % views.size()));
        }
    }
}
