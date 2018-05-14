package com.tokenpocket.appupdater.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import static com.tokenpocket.appupdater.common.Constants.DOWNLOAD_DIR;
import static com.tokenpocket.appupdater.common.Constants.SERVER_BASE_URL;
import static com.tokenpocket.appupdater.common.Constants.UPGRADE_MODE_NO;

/**
 * Created by sunjunxiong on 2018/5/12.
 */
public class UpgradeUtils {
    private static final int WIFI_MAX_STRENGTH = 5;

    public static boolean needUpgrade(UpgradeInfo upgradeInfo) {
        if (upgradeInfo == null
            || upgradeInfo.getUpgradeMode() == null
            || upgradeInfo.getUpgradeMode().equals("" + UPGRADE_MODE_NO)) {
            return false;
        }
        return true;
    }

    public static void clearDownloadApk(String path) {
        File file = new File(path);

        if (file.exists() && file.isDirectory()) {
            File files[] = file.listFiles();
            if (files == null) {
                return;
            }
            for (File f : files) {
                if (f.isFile() && f.getName().contains("TokenPocket")) {
                    if (!f.delete()) {
                        Log.w(Constants.TAG, "clear apk failed!");
                    }
                }
            }
        }
    }

    public static void startInstall(Context context, String apkName) {
        if (apkName == null || context == null) {
            Log.w(Constants.TAG, "startInstall apkName or context is null");
            return;
        }
        File apkFile = new File(UpgradeUtils.getDownloadPath(context), apkName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri =
                FileProvider.getUriForFile(context, "com.tokenpocket.appupdater.fileProvider",
                    apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static boolean isNewestApkDownloaded(String downloadPath, String hashCode) {
        if (downloadPath == null || hashCode == null) {
            return false;
        }
        return true;
        //        File dir = new File(downloadPath);
        //        if (dir.isDirectory()) {
        //            File[] files = dir.listFiles();
        //            for (File f : files) {
        //                if (f.getName().contains("TokenPocket") && hashCode.equals(getMd5ByFile(f))) {
        //                    return true;
        //                }
        //            }
        //        }
        //        return false;
    }

    public static UpgradeInfo requestUpgradeInfo() {
        UpgradeInfo upgradeInfo = null;
        String version = "0.0.7";// getLocalVersion();
        String platform = "1"; // Android platform;
        String systemVersion = "15"; // getSystemVersion();
        String uri = SERVER_BASE_URL
            + "?platform="
            + platform
            + "&sys_ver="
            + systemVersion
            + "&software_version="
            + version;
        try {
            HttpGet httpGet = new HttpGet(uri);
            HttpResponse response = new DefaultHttpClient().execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, HTTP.UTF_8);
                JSONObject jsonObject = new JSONObject(result);
                jsonObject = new JSONObject(jsonObject.getString("data"));
                upgradeInfo = new UpgradeInfo();
                upgradeInfo.setPlatform(jsonObject.getString("platform"))
                    .setUpgradeMode(jsonObject.getString("upgrade_way"))
                    .setHaseCode(jsonObject.getString("hash"))
                    .setUrl(jsonObject.getString("download_url"))
                    .setVersion(jsonObject.getString("software_version"))
                    .setSize(jsonObject.getInt("size"));
                if (Constants.DEBUG) {
                    Log.i(Constants.TAG, "DownloadHelper: upgradeInfo =  " + upgradeInfo);
                }
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, "DownloadHelper: request info error e = " + e);
        }
        return upgradeInfo;
    }

    public static boolean isWifiOk(Context context) {
        if (context == null) {
            Log.e(Constants.TAG, "UpdateUtils: isWifiOk context is null");
            return false;
        }
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().
            getSystemService(Context.WIFI_SERVICE);
        if (WifiManager.WIFI_STATE_ENABLED != wifiManager.getWifiState()) {
            return false;
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int wifiInfoRssi = wifiInfo.getRssi();
        if (Constants.DEBUG) {
            Log.i(Constants.TAG, "UpdateUtils:isWifiOk wifi strength = " + wifiInfoRssi);
        }
        return WifiManager.calculateSignalLevel(wifiInfoRssi, WIFI_MAX_STRENGTH) > 0;
    }

    public static void sendInstallStatus(Handler uiHandler, int status) {
        Message message = uiHandler.obtainMessage();
        message.what = status;
        uiHandler.sendMessage(message);
    }

    public static String getMd5ByFile(File file) {
        String value = "";
        if (file == null) {
            return value;
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Log.w(Constants.TAG, "file not found exception!");
        }

        if (in == null) {
            return value;
        }
        try {
            MappedByteBuffer byteBuffer =
                in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (NoSuchAlgorithmException e) {
            Log.w(Constants.TAG, "no such algorithm exception!");
        } catch (IOException e) {
            Log.w(Constants.TAG, "io exception!");
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.w(Constants.TAG, "close file exception!");
                }
            }
        }
        return value;
    }

    public static String getDownloadPath(Context context) {
        return context.getExternalFilesDir(null) + DOWNLOAD_DIR;
    }
}
