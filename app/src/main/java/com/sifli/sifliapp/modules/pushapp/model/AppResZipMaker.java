package com.sifli.sifliapp.modules.pushapp.model;

import android.app.Application;

import com.sifli.siflicore.error.SFException;
import com.sifli.siflicore.log.SFLog;
import com.sifli.siflicore.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2024/11/22
 * description
 */
public class AppResZipMaker {
    private final static String TAG = "AppResZipMaker";
    private String cacheFileDir;
    public AppResZipMaker(Application context){
        File outputDirFile = new File(context.getExternalFilesDir(null), "AppResZip");
        try{
            cacheFileDir = outputDirFile.getCanonicalPath();
            SFLog.i(TAG,"cacheFileDir:%s",cacheFileDir);
        }catch (Exception ex){
            ex.printStackTrace();
            SFLog.e(TAG,"init cacheFileDir fail." + ex.getMessage());
        }
    }

    public String makeZip(String srcDir) throws SFException {
        SFLog.i(TAG, "makeZip %s");
        FileUtil.reCreateDir(this.cacheFileDir);
        File zipFile = new File(this.cacheFileDir, "appres.zip");

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            File srcFile = new File(srcDir);
            zipDirectory(srcFile, srcFile.getName(), zos);
        } catch (IOException e) {
            e.printStackTrace();
            SFException error = new SFException(AppResErrorCode.MAKE_ZIP_ERROR,"makeZip error." + e.getMessage());
            throw  error;
        }
        return zipFile.getAbsolutePath();

    }

    private static void zipDirectory(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    zipDirectory(file, parentFolder + "/" + file.getName(), zos);
                } else {
                    zipFile(file, parentFolder, zos);
                }
            }
        }
    }

    private static void zipFile(File file, String parentFolder, ZipOutputStream zos) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            String zipEntryName = parentFolder + "/" + file.getName();
            ZipEntry zipEntry = new ZipEntry(zipEntryName);
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
        }
    }

    private static void reCreateDir(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists()) {
            deleteDir(dir);
        }
        dir.mkdirs();
    }

    private static void deleteDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDir(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }
}
