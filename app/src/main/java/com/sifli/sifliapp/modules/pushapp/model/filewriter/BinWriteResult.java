package com.sifli.sifliapp.modules.pushapp.model.filewriter;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2024/11/22
 * description
 */
public class BinWriteResult {
    private long address;
    private long dataLen;

    public long getAddress() {
        return address;
    }

    public void setAddress(long address) {
        this.address = address;
    }

    public long getDataLen() {
        return dataLen;
    }

    public void setDataLen(long dataLen) {
        this.dataLen = dataLen;
    }
}
