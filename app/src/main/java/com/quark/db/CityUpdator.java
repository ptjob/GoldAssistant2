package com.quark.db;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import com.parttime.constants.SharedPreferenceConstants;
import com.parttime.receivers.DownloadManagerReceiver;
import com.parttime.utils.ApplicationUtils;
import com.parttime.utils.CheckUtils;
import com.parttime.utils.SharePreferenceUtil;
import com.quark.common.Url;
import com.quark.jianzhidaren.ApplicationControl;
import com.quark.utils.Logger;

import java.io.File;

/**
 * 城市数据更新器
 * Created by wyw on 2015/8/8.
 */
public class CityUpdator {
    private static final String TAG = "CityUpdator";

    private static String newVersion;

    public void update(final String newVersion) {
        if (CheckUtils.isEmpty(newVersion)) {
            return;
        }

        String currentVersion = ApplicationUtils.getCityVersion();
        Logger.i(TAG, "[update]currentVersion=" + currentVersion + "; newVersion=" + newVersion);
        if (newVersion.compareTo(currentVersion) > 0) {
            CityUpdator.newVersion = newVersion;

            DownloadManager dm = (DownloadManager) ApplicationControl.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Url.ACTIVITY_GET_CITY_DB));
            File dir = new File(CityDatabase.DATABASE_PATH);
            if (!dir.exists()) {
                boolean mkdirStatus = dir.mkdir();
                if (!mkdirStatus) {
                    Logger.w(TAG, "[update]mkdir fail!");
                    return ;
                }
            }
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setVisibleInDownloadsUi(false);

            Uri destinationUri = Uri.fromFile(new File(CityDatabase.DATABASE_PATH, CityDatabase.DATABASE_FILENAME));
            Logger.i(TAG, "[update]destinationUri=" + destinationUri.toString());

            request.setDestinationUri(destinationUri);

            DownloadManagerReceiver.REQUEST_CITY = dm.enqueue(request);
            Logger.i(TAG, "[update]dm begin download, id=" + DownloadManagerReceiver.REQUEST_CITY);
        } else {
            Logger.i(TAG, "[update]do not download");
        }
    }

    public void saveVersion(String filename) {
        // 替换久的，然后更新版本
        File src = new File(CityDatabase.DATABASE_PATH, CityDatabase.DATABASE_FILENAME);
        if (src.exists()) {
            boolean deleteStatus = src.delete();
            if (!deleteStatus) {
                Logger.w(TAG, "[update]delete fail!");
                return ;
            }
        }

        File dest = new File(filename);
        if (!dest.exists()) {
            Logger.w(TAG, "[update]dest not exists!");
            return;
        }

        boolean renameToStatus = dest.renameTo(src);
        if (!renameToStatus) {
            Logger.w(TAG, "[update]rename fail!");
            return ;
        }

        SharePreferenceUtil.getInstance(ApplicationControl.getInstance()).saveSharedPreferences(SharedPreferenceConstants.CITY_DATABASE_VARSION, CityUpdator.newVersion);
        Logger.i(TAG, "[saveVersion]save, newVersion=" + CityUpdator.newVersion);
    }
}
