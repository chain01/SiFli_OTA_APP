package com.sifli.sifliapp.modules.pushapp.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sifli.ezip.sifliEzipUtil;
import com.sifli.siflicore.error.SFException;
import com.sifli.siflicore.log.SFLog;
import com.sifli.siflicore.util.ByteUtil;
import com.sifli.siflicore.util.FileUtil;

import java.io.ByteArrayOutputStream;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2024/11/22
 * description  根据用户选择的图片，尺寸生成一个居中剪辑
 */
public class AppResClipMaker {
    private final static String TAG = "AppResClipMaker";
    private AppResContext resContext;
    public  AppResClipMaker(AppResContext resContext){
        this.resContext = resContext;
    }

    public Bitmap makeClip() {
        SFLog.i(TAG,"makeClip");
        int clipWidth = resContext.getClipWidth();
        int clipHeight = resContext.getClipHeight();
        byte[] srcData = FileUtil.getFileData(this.resContext.getImageFilePath());
        String hexData = ByteUtil.hexSummary(srcData);
        SFLog.i(TAG, "makeClip srcData=" + hexData);
// 将字节数组转换为Bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(srcData, 0, srcData.length);

// 如果需要对Bitmap进行裁剪，可以使用Bitmap.createBitmap方法
        int width = Math.min(clipWidth,bitmap.getWidth());
        int height = Math.min(clipHeight,bitmap.getHeight());
        Bitmap clippedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);

        return clippedBitmap;
    }

    public byte[] makePngData(Bitmap bitmap) {
        SFLog.i(TAG,"makePngData");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    public byte[] pngToEzip(byte[] pngData) throws SFException {
        SFLog.i(TAG,"pngToEzip");
        byte[] ezipResult = sifliEzipUtil.pngToEzip(pngData, resContext.getEzipColor(), resContext.getEzipAlpha(), resContext.getEzipRotation(),
                resContext.getEzipBoardType());
        if(ezipResult == null || ezipResult.length == 0){
            SFException ex = new SFException(AppResErrorCode.EZIP_FAIL,"ezip 执行失败");
            throw ex;
        }
        SFLog.i(TAG,"ezip result len=%d",ezipResult.length);
        return ezipResult;
    }
}
