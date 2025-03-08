package com.sifli.sifliapp.modules.user;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2024/12/10
 * description
 */
public class SFLoginResult {
    private String mac;
    private String appId;
    private String resUID;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getResUID() {
        return resUID;
    }

    public void setResUID(String resUID) {
        this.resUID = resUID;
    }
}
