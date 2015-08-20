package com.parttime.common.Image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.qingmu.jianzhidaren.R;
import com.quark.jianzhidaren.ApplicationControl;
import com.quark.volley.VolleySington;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 *
 * Created by dehua on 15/7/28.
 */
public class ImageShowPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<String> pictures;
    private ArrayList<String> userIds;
    protected HashMap<String, Bitmap> cache = new HashMap<>();
    protected RequestQueue queue = VolleySington.getInstance().getRequestQueue();

    public ImageShowPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(ArrayList<String> pictures,ArrayList<String> userIds){

        this.pictures = new ArrayList<>(pictures);
        this.userIds = new ArrayList<>(userIds);

    }

    @Override
    public Fragment getItem(int position) {
        String picture = pictures.get(position);
        String userId = userIds.get(position);
        UserDetailFragment fragment = UserDetailFragment.newInstance(picture,userId);
        fragment.imageShowPagerAdapter = this;
        return fragment;
    }

    @Override
    public int getCount() {
        return pictures.size();
    }

    public static class UserDetailFragment extends Fragment implements View.OnClickListener{
        private static final String ARG_PICTURE = "picture";
        private static final String ARG_USER_ID = "userId";
        protected String picture;
        protected String userId;
        ImageShowPagerAdapter imageShowPagerAdapter;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static UserDetailFragment newInstance(String picture,String userId) {
            UserDetailFragment fragment = new UserDetailFragment();
            Bundle args = new Bundle();
            args.putString(ARG_PICTURE, picture);
            args.putString(ARG_USER_ID, userId);
            fragment.setArguments(args);
            return fragment;
        }

        public UserDetailFragment() {

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            picture = getArguments().getString(ARG_PICTURE);
            userId = getArguments().getString(ARG_USER_ID);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            FrameLayout rootView = (FrameLayout)inflater.inflate(R.layout.image_show_item, container, false);

            ImageView image = (ImageView)rootView.findViewById(R.id.image);
            Bitmap bitmap = imageShowPagerAdapter.cache.get(picture);
            rootView.findViewById(R.id.save).setOnClickListener(this);
            if(bitmap != null){
                image.setImageBitmap(bitmap);
            }else {
                ContactImageLoader.loadNativePhoto(userId,picture,image, imageShowPagerAdapter.queue);
            }
            return rootView;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){

                case R.id.save:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            File file = new File(ContactImageLoader.Image_Path, picture);
                            save(file);
                        }
                    }).start();

                    break;
            }
        }

        private void save(File file) {
            String path = file.getPath();
            if (!TextUtils.isEmpty(file.getPath())
                    && Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                try {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
                    String imageFileName = "IMG" + timeStamp + ".jpg";
                    String url = MediaStore.Images.Media.insertImage(
                            getActivity().getContentResolver(),
                            path,
                            imageFileName,
                            "jzdr");
                    if (!TextUtils.isEmpty(url)) {
                        final String filePath = path + imageFileName;
                        try {
                            ApplicationControl.getInstance().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
                            MediaScannerConnection.scanFile(ApplicationControl.getInstance(),
                                    new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + path},
                                    null,
                                    null);
                        }catch (Exception ignore){}
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"保存成功"+filePath,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError e) {
                    System.gc();
                }

            }
        }
    }

}
