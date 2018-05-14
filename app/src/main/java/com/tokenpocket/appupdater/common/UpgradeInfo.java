package com.tokenpocket.appupdater.common;
/**
 * Created by sunjunxiong on 2018/5/12.
 */
public class UpgradeInfo {
    private String mPlatform;
    private String mVersion;
    private String mDownloadUrl;
    private String mHashCode;
    private String mUpgradeMode;
    private int mSize;

    public String getPlatform() {
        return mPlatform;
    }

    public UpgradeInfo setPlatform(String platform) {
        this.mPlatform = platform;
        return this;
    }

    public String getVersion() {
        return mVersion;
    }

    public UpgradeInfo setVersion(String version) {
        this.mVersion = version;
        return this;
    }

    public String getUrl() {
        return mDownloadUrl;
    }

    public UpgradeInfo setUrl(String url) {
        this.mDownloadUrl = url;
        return this;
    }

    public int getSize() {
        return mSize;
    }

    public UpgradeInfo setSize(int size) {
        this.mSize = size;
        return this;
    }

    public String getHaseCode() {
        return mHashCode;
    }

    public UpgradeInfo setHaseCode(String haseCode) {
        this.mHashCode = haseCode;
        return this;
    }

    public String getUpgradeMode() {
        return mUpgradeMode;
    }

    public UpgradeInfo setUpgradeMode(String upgradeMode) {
        this.mUpgradeMode = upgradeMode;
        return this;
    }

    public String toString() {
        return "[platform = " + mPlatform + ";version = " + mVersion + ";downloadUrl=" + mDownloadUrl + ";hashCode=" + mHashCode + ";upgradeMode=" + mUpgradeMode + "]";
    }
}
