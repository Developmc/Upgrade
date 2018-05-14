package com.tokenpocket.appupdater.helper;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.tokenpocket.appupdater.common.Constants;

/**
 * Created by sunjunxiong on 2018/5/12.
 */
public class DownloadHelper {
    private long mDownloadId;
    private Context mContext;
    private DownloadManager mDownloadManager;
    private DownloadManager.Query mDownloadQuerier = new DownloadManager.Query();
    private BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            checkStatus();
        }
    };

    private void checkStatus() {
        mDownloadQuerier.setFilterById(mDownloadId);
        Cursor cursor = mDownloadManager.query(mDownloadQuerier);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    break;
                case DownloadManager.STATUS_PENDING:
                    break;
                case DownloadManager.STATUS_RUNNING:
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Log.i(Constants.TAG, "checkStatus download success!");
                    mContext.unregisterReceiver(mDownloadReceiver);
                    break;
                case DownloadManager.STATUS_FAILED:
                    Log.w(Constants.TAG, "checkStatus download failed!");
                    mContext.unregisterReceiver(mDownloadReceiver);
                    break;
                default:
                    Log.w(Constants.TAG, "checkStatus unknown status!");
                    mContext.unregisterReceiver(mDownloadReceiver);
            }
        }
        cursor.close();
    }

    public DownloadHelper(Context context) {
        this.mContext = context;
    }

    public int getDownloadPercentage() {
        mDownloadQuerier.setFilterById(mDownloadId);
        Cursor cursor = mDownloadManager.query(mDownloadQuerier);
        if (cursor == null || !cursor.moveToNext()) {
            return 0;
        }
        float bytesDownloaded =
            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
        float bytesTotal =
            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        cursor.close();
        int downloadPercentage = (int) ((bytesDownloaded / bytesTotal) * 100);
        return downloadPercentage;
    }

    public void downloadAPK(String url, String apkName) {
        if (mContext == null || apkName == null || url == null) {
            Log.e(Constants.TAG, "DownloadHelper:downloadAPK context apkName or url is null");
            return;
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //check it
        request.setAllowedOverRoaming(false);
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        //set download path
        request.setDestinationInExternalFilesDir(mContext, Constants.DOWNLOAD_DIR, apkName);
        //get DownloadManager
        mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //enqueue the download request, get a downloadId to do sth
        mDownloadId = mDownloadManager.enqueue(request);
        mContext.registerReceiver(mDownloadReceiver,
            new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
}
