package com.sifli.sifliapp.utils.speedview;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2024/1/25
 * description 速度统计的记录点
 */
public class SpeedPoint {
    private long timestamp;
    private long completeBytes;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getCompleteBytes() {
        return completeBytes;
    }

    public void setCompleteBytes(long completeBytes) {
        this.completeBytes = completeBytes;
    }
}
