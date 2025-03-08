package com.sifli.sifliapp.modules.debug.ota;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2024/1/6
 * description
 */
public class SFOtaImageItem {
    private String localPath;
    private int ImageID = -1;
    private String fileName;
    private String imageIDName = "<SELECT TYPE>";
    private String hexOffset = "";
    public SFOtaImageItem(){

    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public int getImageID() {
        return ImageID;
    }

    public void setImageID(int imageID) {
        ImageID = imageID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getImageIDName() {
        return imageIDName;
    }

    public void setImageIDName(String imageIDName) {
        this.imageIDName = imageIDName;
    }

    public String getHexOffset() {
        return hexOffset;
    }

    public void setHexOffset(String hexOffset) {
        this.hexOffset = hexOffset;
    }
}
