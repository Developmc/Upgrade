package com.tokenpocket.appupdater.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.tokenpocket.appupdater.common.Constants;
import com.tokenpocket.appupdater.common.UpgradeInfo;
import com.tokenpocket.appupdater.common.UpgradeUtils;

import static com.tokenpocket.appupdater.common.Constants.PROGRESS_FINISH;
import static com.tokenpocket.appupdater.common.Constants.UPGRADE_MODE_NO;

/**
 * Created by sunjunxiong on 2018/5/12.
 */
public class DownloadTask extends AsyncTask<String, Integer, Boolean> {
    private DownloadHelper mDownloadHelper;
    private Context mContext = null;
    private String mApkName = null;
    private int mUpgradeMode = UPGRADE_MODE_NO;
    private UpgradeInfo mUpgradeInfo = null;

    public DownloadTask(Context context, UpgradeInfo upgradeInfo) {
        mContext = context;
        mUpgradeInfo = upgradeInfo;
    }

    @Override protected Boolean doInBackground(String... args) {
        Boolean needUpgrade = false;
        mUpgradeMode = Integer.parseInt(mUpgradeInfo.getUpgradeMode());
        //name of the download apk
        mApkName = "TokenPocket_V" + mUpgradeInfo.getVersion() + ".apk";
        //if (UpgradeUtils.isNewestApkDownloaded(Constants.DOWNLOAD_PATH, mUpgradeInfo.getHaseCode())) {
        //    needUpgrade = true; // install apk directly
        //    return needUpgrade;
        //}
        if (doDownload(mContext, mUpgradeInfo)) {
            publishProgress(PROGRESS_FINISH);
            needUpgrade = true; // download success
            return needUpgrade;
        }
        // download fail
        Log.w(Constants.TAG, "fail to download!");
        UpgradeUtils.clearDownloadApk(mApkName);
        return needUpgrade;
    }

    @Override protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.d("DownloadTask", String.valueOf(values[0]));
    }

    @Override protected void onPostExecute(Boolean needUpgrade) {
        super.onPostExecute(needUpgrade);
        if (needUpgrade) {
            //when download finished
            UpgradeUtils.startInstall(mContext, mApkName);
        }
    }

    private boolean doDownload(Context context, UpgradeInfo upgradeInfo) {
        if (context == null) {
            Log.e(Constants.TAG, "DownloadTask: doDownload " + "packageName or context is null");
            return false;
        }
        mDownloadHelper = new DownloadHelper(context);
        if (upgradeInfo == null) {
            Log.e(Constants.TAG, "DownloadTask: failed to get upgrade info, ");
            return false;
        }
        mDownloadHelper.downloadAPK(upgradeInfo.getUrl(), mApkName);
        while (mDownloadHelper.getDownloadPercentage() < PROGRESS_FINISH) {
            try {
                Thread.sleep(Constants.PROCESS_UPDATE_DURATION);
            } catch (InterruptedException e) {
                Log.w(Constants.TAG, "DownloadAndInstallTask:install task cancel ");
            }
            publishProgress(mDownloadHelper.getDownloadPercentage());
        }
        return true;
    }
}
