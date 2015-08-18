package com.quark.db;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.easemob.util.FileUtils;
import com.parttime.constants.SharedPreferenceConstants;
import com.parttime.receivers.DownloadManagerReceiver;
import com.parttime.utils.ApplicationUtils;
import com.parttime.utils.CheckUtils;
import com.parttime.utils.SharePreferenceUtil;
import com.quark.common.Url;
import com.quark.jianzhidaren.ApplicationControl;
import com.quark.utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 城市数据更新器
 * Created by wyw on 2015/8/8.
 */
public class CityUpdator {
    private static final String TAG = "CityUpdator";
    private static final String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    private static final String DOWNLOAD_FILE_NAME = "qm_cities_download.db";

    private static String newVersion;

    public void update(final String newVersion) {
        if (CheckUtils.isEmpty(newVersion)) {
            return;
        }

        String currentVersion = ApplicationUtils.getCityVersion();
        Logger.w(TAG, "[update]currentVersion=" + currentVersion + "; newVersion=" + newVersion);
        if (newVersion.compareTo(currentVersion) > 0) {
//        if (true) {
            CityUpdator.newVersion = newVersion;

            DownloadManager dm = (DownloadManager) ApplicationControl.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Url.ACTIVITY_GET_CITY_DB));
            File dir = new File(DOWNLOAD_DIR);
            if (!dir.exists()) {
                boolean mkdirStatus = dir.mkdir();
                if (!mkdirStatus) {
                    Logger.w(TAG, "[update]mkdir fail!");
                    return ;
                }
            }
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setVisibleInDownloadsUi(false);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

            File dbFile = new File(DOWNLOAD_DIR, DOWNLOAD_FILE_NAME);
            if (dbFile.exists()) {
                if (!dbFile.delete()) {
                    Logger.w(TAG, "[update]delete dbFile fail!");
                }
            }
            Uri destinationUri = Uri.fromFile(dbFile);
            Logger.w(TAG, "[update]destinationUri=" + destinationUri.toString());

            request.setDestinationUri(destinationUri);

            DownloadManagerReceiver.REQUEST_CITY = dm.enqueue(request);
            Logger.w(TAG, "[update]dm begin download, id=" + DownloadManagerReceiver.REQUEST_CITY);
        } else {
            Logger.w(TAG, "[update]do not download");
        }
    }

    public void saveVersion(String filename) {
        // 替换久的，然后更新版本
        File src = new File(CityDatabase.DATABASE_PATH, CityDatabase.DATABASE_FILENAME);
        if (src.exists()) {
            boolean deleteStatus = src.delete();
            if (!deleteStatus) {
                Logger.w(TAG, "[update]delete src fail!");
                return ;
            }
        }

        File dest = new File(filename);
        if (!dest.exists()) {
            Logger.w(TAG, "[update]dest not exists!");
            return;
        }

        if (!copyFile(dest.getPath(), src.getPath())) {
            Logger.w(TAG, "[update]copyFile fail!");
            return ;
        }

        if (!dest.delete()) {
            Logger.w(TAG, "[update]delete dest fail!");
        }

        SharePreferenceUtil.getInstance(ApplicationControl.getInstance()).saveSharedPreferences(SharedPreferenceConstants.CITY_DATABASE_VARSION, CityUpdator.newVersion);
        Logger.w(TAG, "[saveVersion]save, newVersion=" + CityUpdator.newVersion);
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：/fqf.txt
     * @param newPath String 复制后路径 如：/a/fqf.txt
     * @return boolean
     */
    public boolean copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                return true;
            }
        }
        catch (Exception e) {
            Logger.w(TAG, "复制单个文件操作出错");
            e.printStackTrace();

        }

        return false;
    }
}
