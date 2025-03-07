package com.example.sifliotademo;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2023/8/29
 * description
 */
public class ZipUtil {
    private final  static  String TAG = "ZipUtil";
    public static void unzipFolder(String zipFileString, String outPathString) {
        FileInputStream fis = null;
        ZipInputStream inZip = null;

        try {
            fis = new FileInputStream(zipFileString);
            inZip = new ZipInputStream(fis);

            ZipEntry zipEntry;
            String szName = "";

            File ft = new File(outPathString);
            outPathString = ft.getCanonicalPath();

            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();

                File f = new File(outPathString, szName);
                String canonicalPath = f.getCanonicalPath();
                //Log.d(TAG, "outPathString " + outPathString);
                //Log.d(TAG, "canonicalPath " + canonicalPath);
                if (!canonicalPath.startsWith(outPathString)) {
                    throw new SecurityException("zip path have traversal characters path");
                }

                if (zipEntry.isDirectory()) {
                    szName = szName.substring(0, szName.length() - 1);
                    File folder = new File(outPathString + File.separator + szName);
                    folder.mkdirs();
                } else {
                    String fileStr = outPathString + File.separator + szName;
                    int le = fileStr.lastIndexOf(File.separator);
                    fileStr = fileStr.substring(0, le);

                    File finalFolder = new File(fileStr);
                    if (!finalFolder.exists()) {
                        Log.w(TAG, "folder is null, mkdir now");
                        finalFolder.mkdirs();
                    }


                    File file = new File(outPathString + File.separator + szName);
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);

                    int length;
                    byte[] buffer = new byte[1024];
                    while ((length = inZip.read(buffer)) != -1) {
                        out.write(buffer, 0, length);
                        out.flush();
                    }
                    if (out != null) {
                        out.close();
                    }
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inZip != null) {
                try {
                    inZip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
