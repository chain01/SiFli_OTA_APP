package com.sifli.sifliapp.modules.pushapp.model;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2024/12/9
 * description
 */
public class EzipSetting {
    private String color;
    private int noAlpha;
    private int noRotation;
    private int boardType;
    private int clipWidth;
    private int clipHeight;

    public EzipSetting(){
        this.color = "rgb565";
        this.noAlpha = 1;
        this.noRotation = 1;
        this.boardType = 2;
        this.clipWidth = 240;
        this.clipHeight = 240;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getNoAlpha() {
        return noAlpha;
    }

    public void setNoAlpha(int noAlpha) {
        this.noAlpha = noAlpha;
    }

    public int getNoRotation() {
        return noRotation;
    }

    public void setNoRotation(int noRotation) {
        this.noRotation = noRotation;
    }

    public int getBoardType() {
        return boardType;
    }

    public void setBoardType(int boardType) {
        this.boardType = boardType;
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
}
