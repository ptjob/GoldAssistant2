package com.parttime.login.guide;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.parttime.login.FindPJLoginActivity;
import com.qingmu.jianzhidaren.R;
import com.quark.jianzhidaren.ApplicationControl;
import com.quark.jianzhidaren.BaseActivity;

public class GuideActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewPager);
        viewPager.setAdapter(new GuidePagerAdapter(getSupportFragmentManager(),this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    class GuidePagerAdapter extends FragmentPagerAdapter{

        private GuideActivity activity;

        public GuidePagerAdapter(FragmentManager fm, GuideActivity activity) {
            super(fm);
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Fragment getItem(int position) {

            return GuideFragment.newInstance(position,this,activity);
        }


    }


    static class  GuideFragment extends Fragment{

        private int position;
        private GuidePagerAdapter adapter;
        private GuideActivity activity;

        public static GuideFragment newInstance(int position, GuidePagerAdapter adapter,GuideActivity activity){
            GuideFragment fragment = new GuideFragment();
            fragment.position = position;
            fragment.adapter = adapter;
            fragment.activity = activity;
            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activitygruide,container,false);
            ImageView imageView = (ImageView)view.findViewById(R.id.image);
            switch (position % adapter.getCount()){
                case 0:
                    imageView.setBackgroundResource(R.drawable.guide_01);
                    break;
                case 1:
                    imageView.setBackgroundResource(R.drawable.guide_02);
                    break;
                case 2:
                    imageView.setBackgroundResource(R.drawable.guide_03);
                    break;
                case 3:
                    imageView.setBackgroundResource(R.drawable.guide_04);
                    Button enter = (Button)view.findViewById(R.id.enter);
                    enter.setVisibility(View.VISIBLE);
                    enter.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            Intent mainIntent = new Intent(activity,
                                    FindPJLoginActivity.class);
                            mainIntent.putExtra("from_startupact", true);// 从启动页传来
                            startActivity(mainIntent);
                            activity.finish();
                        }
                    });
                    break;


            }
            return view;
        }
    }


}
