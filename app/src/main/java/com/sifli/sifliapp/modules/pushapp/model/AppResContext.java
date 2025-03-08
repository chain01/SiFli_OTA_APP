package com.sifli.sifliapp.modules.pushapp.model;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2024/11/22
 * description
 */
public class AppResContext {
    private String imageFilePath;
    private int clipWidth;
    private int clipHeight;
    private String hexUID;
    private String ezipColor;
    private int ezipAlpha;
    private int ezipRotation;
    private int ezipBoardType;
    private String watchPath;
    private String appId;



    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }

    public int getClipWidth() {
        return clipWidth;
    }

    public void setClipWidth(int clipWidth) {
        this.clipWidth = clipWidth;
    }

    public int getClipHeight() {
        return clipHeight;
    }

    public void setClipHeight(int clipHeight) {
        this.clipHeight = clipHeight;
    }

    public String getHexUID() {
        return hexUID;
    }

    public void setHexUID(String hexUID) {
        this.hexUID = hexUID;
    }

    public String getEzipColor() {
        return ezipColor;
    }

    public void setEzipColor(String ezipColor) {
        this.ezipColor = ezipColor;
    }

    public int getEzipAlpha() {
        return ezipAlpha;
    }

    public void setEzipAlpha(int ezipAlpha) {
        this.ezipAlpha = ezipAlpha;
    }

    public int getEzipRotation() {
        return ezipRotation;
    }

    public void setEzipRotation(int ezipRotation) {
        this.ezipRotation = ezipRotation;
    }

    public int getEzipBoardType() {
        return ezipBoardType;
    }

    public void setEzipBoardType(int ezipBoardType) {
        this.ezipBoardType = ezipBoardType;
    }


    public String getWatchPath() {
        return watchPath;
    }

    public void setWatchPath(String watchPath) {
        this.watchPath = watchPath;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
