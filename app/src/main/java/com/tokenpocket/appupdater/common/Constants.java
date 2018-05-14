package com.tokenpocket.appupdater.common;
/**
 * Created by sunjunxiong on 2018/5/12.
 */
public class Constants {
    public static String TAG = "TokenUpdater";
    public static boolean DEBUG = true;
    public static final int UPGRADE_MODE_SUGGEST = 0;
    public static final int UPGRADE_MODE_FORCE = 1;
    public static final int UPGRADE_MODE_NO = 2;
    public static final int PROCESS_UPDATE_DURATION = 200;
    public static final int PROGRESS_FINISH = 100;
    public static final String DOWNLOAD_DIR = "/download";
    public static final String SERVER_BASE_URL = "http://testserver.mytokenpocket.vip/v1/upgrade";
}
