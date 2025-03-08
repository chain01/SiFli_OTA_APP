package com.sifli.sifliapp.modules.pushapp.model.filewriter;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2024/11/22
 * description
 */
public interface ISFBinStream {
    long getCurrentPosition();
    void writeBytes(byte[] data);
    void writeByte(byte data);
    void seekTo(long position);
    void seekToEnd();
    void close();
}
