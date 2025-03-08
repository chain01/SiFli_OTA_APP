package com.sifli.sifliapp.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2023/12/16
 * description
 */
public class FileUtil {
    private final static String TAG = "FileUtil";

    public  static String getUrlName(Uri uri,Context context){

// 获取 ContentResolver 对象
        ContentResolver cr = context.getContentResolver();
// 查询文件的名字
        String fileName = null;
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
// 获取文件名的列索引
            int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
// 获取文件名
            fileName = cursor.getString(index);
// 关闭游标
            cursor.close();
        }
        return fileName;
    }

    public static String getFilePathFromURI(Context context, Uri contentUri){
        String scheme = contentUri.getScheme();
        if (scheme == null || scheme.equals("file")){
            return contentUri.getPath();
        }else if(scheme.equals("content")){
            String rootDataDir = FolderHelper.getDeviceWatchfacePath(context);
//        MyApplication.getMyContext().getExternalFilesDir(null).getPath();
            String fileName = getFileName(contentUri);
            if (!TextUtils.isEmpty(fileName)) {
                File copyFile = new File(rootDataDir + File.separator + fileName);
                copyFile(context, contentUri, copyFile);
                return copyFile.getAbsolutePath();
            }
        }else{
            throw  new InvalidParameterException("不支持的Uri类型:" + scheme);
        }
        return  null;
    }

    public static String getOtaFilePathFromURI(Context context, Uri contentUri){
        String scheme = contentUri.getScheme();
        if (scheme == null || scheme.equals("file")){
            return contentUri.getPath();
        }else if(scheme.equals("content")){
            String rootDataDir = FolderHelper.getDeviceOTAPath(context);
//        MyApplication.getMyContext().getExternalFilesDir(null).getPath();
            String fileName = getFileName(contentUri);
            if (!TextUtils.isEmpty(fileName)) {
                File copyFile = new File(rootDataDir + File.separator + fileName);
                copyFile(context, contentUri, copyFile);
                return copyFile.getAbsolutePath();
            }
        }else{
            throw  new InvalidParameterException("不支持的Uri类型:" + scheme);
        }
        return  null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static String getFileNameOfUrl(String url){
        Uri uri = Uri.parse(url);
        String fileName =  FileUtil.getFileName(uri);
        return  fileName;
    }

    public static String getFileNameOfPath(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        File file = new File(path);
        return file.getName();
    }

    public static String getFirstDirectoryOfPath(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        // 使用 File.separator 进行分割
        String[] parts = path.split(File.separator);
        // 检查分割后的数组是否有足够的元素
        if (parts.length > 1) {
            return parts[0].isEmpty() ? parts[1] : parts[0];
        }
        return "";
    }

    public static void copyFile(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int copyStream(InputStream input, OutputStream output) throws Exception, IOException {
        final int BUFFER_SIZE = 1024 * 2;
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(TAG, "out close error", e);
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.e(TAG, "in close error", e);
            }
        }
        return count;

    }
}
