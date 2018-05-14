package com.tokenpocket.appupdater.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import com.tokenpocket.appupdater.R;
import com.tokenpocket.appupdater.common.UpgradeInfo;
import com.tokenpocket.appupdater.common.UpgradeUtils;

import static com.tokenpocket.appupdater.common.Constants.UPGRADE_MODE_SUGGEST;

/**
 * Created by sunjunxiong on 2018/5/12.
 */
public class NetworkRequestTask extends AsyncTask<String, Integer, UpgradeInfo> {
    private Context mContext = null;

    public NetworkRequestTask(Context context) {
        mContext = context;
    }

    @Override protected UpgradeInfo doInBackground(String... args) {
        //get upgrade info from server
        UpgradeInfo upgradeInfo = UpgradeUtils.requestUpgradeInfo();
        Log.i("sjx", "clearDownloadApk");
        if (!UpgradeUtils.needUpgrade(upgradeInfo)) {
            UpgradeUtils.clearDownloadApk(UpgradeUtils.getDownloadPath(mContext));
            return null;
        }
        return upgradeInfo;
    }

    @Override protected void onPostExecute(UpgradeInfo upgradeInfo) {
        super.onPostExecute(upgradeInfo);
        if (upgradeInfo != null) {
            //now we need upgrade , confirm from user
            showUpgradeAcceptDialog(mContext, upgradeInfo);
        }
    }

    private void showUpgradeAcceptDialog(final Context context, final UpgradeInfo upgradeInfo) {
        int upgradeMode = Integer.parseInt(upgradeInfo.getUpgradeMode());
        String apkSize = String.valueOf(upgradeInfo.getSize());
        String message = context.getResources().getString(R.string.update_confirm_msg_ex, apkSize);
        if (!UpgradeUtils.isWifiOk(context)) {
            String dataNetwork = context.getResources().getString(R.string.data_network);
            message = dataNetwork + "," + message;
        }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(R.string.update_confirm_title)
            .setMessage(message)
            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int i) {
                    startDownLoadTask(context, upgradeInfo);
                }
            });
        if (upgradeMode == UPGRADE_MODE_SUGGEST) {
            dialogBuilder.setCancelable(false)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int i) {
                    }
                });
        }
        dialogBuilder.show();
    }

    private void startDownLoadTask(final Context context, final UpgradeInfo upgradeInfo) {
        DownloadTask downloadTask = new DownloadTask(context, upgradeInfo);
        downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
