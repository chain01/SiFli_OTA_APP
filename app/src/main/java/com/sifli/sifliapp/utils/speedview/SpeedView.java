package com.sifli.sifliapp.utils.speedview;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2024/1/25
 * description
 */
public class SpeedView {

    private  SpeedPoint firstPoint;
    //3秒前的速度点
    private SpeedPoint before3SecPoint;

    private double currentSpeed;
    private double averageSpeed;

    public SpeedView(){

    }
    public void viewSpeedByCompleteBytes(long completeBytes){
        long timeNow = this.timeNow();
        if(firstPoint == null){
            this.firstPoint = new SpeedPoint();
            this.firstPoint.setTimestamp(this.timeNow());
            this.firstPoint.setCompleteBytes(completeBytes);
        }else{
            long time = timeNow - firstPoint.getTimestamp();
            long size = completeBytes - firstPoint.getCompleteBytes();

            this.averageSpeed = getKbPerSec(size,time);
        }
        if(this.before3SecPoint == null){
            this.before3SecPoint = new SpeedPoint();
            this.before3SecPoint.setTimestamp(this.timeNow());
            this.before3SecPoint.setCompleteBytes(completeBytes);
        }else{
            long time = timeNow - before3SecPoint.getTimestamp();
            long size = completeBytes - before3SecPoint.getCompleteBytes();
            this.currentSpeed = getKbPerSec(size,time);
        }

        if(timeNow - this.before3SecPoint.getTimestamp() > 3 * 1000){
            this.before3SecPoint = new SpeedPoint();
            this.before3SecPoint.setTimestamp(this.timeNow());
            this.before3SecPoint.setCompleteBytes(completeBytes);
        }else{
            long time = timeNow - before3SecPoint.getTimestamp();
            long size = completeBytes - before3SecPoint.getCompleteBytes();
            this.currentSpeed = getKbPerSec(size,time);
        }
    }

    public void clear(){
        this.currentSpeed = 0;
        this.averageSpeed = 0;
        this.firstPoint = null;
        this.before3SecPoint = null;
    }

    public String getSpeedText(){
        String speedTxt = String.format("瞬时 %.1f kb/s 平均 %.1f kb/s",this.currentSpeed,this.averageSpeed);
        return speedTxt;
    }

    public String getSpeedText(long currentBytes,long totalBytes){
        double currentM = (double) currentBytes / (1024 * 1024);
        double totalM = (double) totalBytes / (1024 * 1024);
        String speedTxt = String.format("瞬时 %.1f kb/s 平均 %.1f kb/s %.1f/%.1fMb",this.currentSpeed,this.averageSpeed,currentM,totalM);
        return speedTxt;
    }

    private double getKbPerSec(long complteBytes,long ms){
        double kb = (double) complteBytes / 1024;
        double sec = (double) ms / 1000;
        if(sec == 0){
            return 0;
        }else{
            return kb / sec;
        }

    }


    private long timeNow(){
        return System.currentTimeMillis();
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }
}
