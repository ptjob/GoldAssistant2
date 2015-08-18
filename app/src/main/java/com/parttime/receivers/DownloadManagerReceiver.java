package com.parttime.receivers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.quark.db.CityUpdator;
import com.quark.utils.Logger;

import java.io.FileNotFoundException;

/**
 * 下载管理器监听器
 * Created by wyw on 2015/8/8.
 */
public class DownloadManagerReceiver extends BroadcastReceiver {

    private static final String TAG = "DownloadManagerReceiver";

    public static long REQUEST_CITY;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            Logger.w(TAG, "[onReceive]download complete");
            DownloadManager manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);

            DownloadManager.Query query = new DownloadManager.Query();
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            Logger.w(TAG, "[onReceive]download complete, id=" + id);
            query.setFilterById(id);
            Cursor c = manager.query(query);
            if(c.moveToFirst()) {
                //获取文件下载路径

                String filename = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                Logger.w(TAG, "[onReceive]filename=" + filename);
                //如果文件名不为空，说明已经存在了
                if(filename != null){
                    if (REQUEST_CITY == id) {
                        // 城市DB下载成功
                        new CityUpdator().saveVersion(filename);
                    } else {
                        // 其他下载任务
                        // TODO
                    }
                }
            }
        }
    }
}
