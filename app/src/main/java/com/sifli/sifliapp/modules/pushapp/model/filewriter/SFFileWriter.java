package com.sifli.sifliapp.modules.pushapp.model.filewriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2024/11/22
 * description
 */
public class SFFileWriter implements ISFBinStream {
    private String filePath;
    private RandomAccessFile file;

    public SFFileWriter(String filePath) {
        File filed = new File(filePath);

// 创建父目录
        File parentDir = filed.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        this.filePath = filePath;
        try {
            file = new RandomAccessFile(new File(filePath), "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getCurrentPosition() {
        try {
            return file.getFilePointer();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void writeBytes(byte[] data) {
        try {
            file.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeByte(byte data) {
        try {
            file.writeByte(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void seekTo(long position) {
        try {
            file.seek(position);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void seekToEnd() {
        try {
            file.seek(file.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {

            file.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}

