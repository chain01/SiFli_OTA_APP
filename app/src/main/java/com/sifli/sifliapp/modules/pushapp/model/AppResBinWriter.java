package com.sifli.sifliapp.modules.pushapp.model;

import android.app.Application;


import com.sifli.sifliapp.modules.pushapp.model.filewriter.BinWriteResult;
import com.sifli.sifliapp.modules.pushapp.model.filewriter.ISFBinStream;
import com.sifli.sifliapp.modules.pushapp.model.filewriter.SFFileWriter;
import com.sifli.siflicore.error.SFException;
import com.sifli.siflicore.log.SFLog;
import com.sifli.siflicore.util.ByteUtil;
import com.sifli.siflicore.util.FileUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2024/11/22
 * description
 */
public class AppResBinWriter {
    private  final static String TAG = "AppResBinWriter";
    private final static long DATA_LEAN_ADDRESS = 4;
    private final static long HEADER_ADDRESS = 8;
    private AppResContext resContext;
    private String cacheFileDir;
    private String appResBinPath;
    private String appResZipFolder;
    private ISFBinStream binStream;
    public AppResBinWriter(Application context, AppResContext resContext){
        this.resContext = resContext;
        File outputDirFile = new File(context.getExternalFilesDir(null), "AppRes");
        try{
            cacheFileDir = outputDirFile.getCanonicalPath();
            SFLog.i(TAG,"cacheFileDir:%s",cacheFileDir);
        }catch (Exception ex){
            ex.printStackTrace();
            SFLog.e(TAG,"init cacheFileDir fail." + ex.getMessage());
        }
    }

    public String getResZipFoler(){
        return this.appResZipFolder;
    }

    public String  makeResBin(byte[] ezipResult) throws SFException {
        try{
            SFLog.i(TAG,"makeResBin ezipResult len=%d",ezipResult.length);
            String firstDir = com.sifli.sifliapp.utils.FileUtil.getFirstDirectoryOfPath(this.resContext.getWatchPath());
            FileUtil.reCreateDir(this.cacheFileDir);
            File appResBinFile = new File(this.cacheFileDir,this.resContext.getWatchPath());
            this.appResBinPath = appResBinFile.getAbsolutePath();
            File appResBinZipFolderFile = new File(this.cacheFileDir,firstDir);
            this.appResZipFolder = appResBinZipFolderFile.getAbsolutePath();

            this.binStream = new SFFileWriter(this.appResBinPath);
            this.writeMagic();
            BinWriteResult firstResult = this.writeConfigFirst();
            BinWriteResult imageResult = this.writeImageData(ezipResult);
            BinWriteResult secondResult = this.writeConfigSecond(imageResult);
            if(firstResult.getAddress() != secondResult.getAddress() || firstResult.getDataLen() != secondResult.getDataLen()){
                SFException ex = new SFException(AppResErrorCode.DEBUG_ERROR,"两次写入数据长度，地址不一致");
                throw ex;
            }
            this.writeDataLen(secondResult.getDataLen());
            SFLog.i(TAG,"appResBinPath=%S",this.appResBinPath);
            return  this.appResBinPath;
        }finally {
            if(this.binStream != null){
                this.binStream.close();
                this.binStream = null;
            }
        }

    }

    private void writeMagic(){
        SFLog.i(TAG,"writeMagic");
        String hexMagic = "775dbb12";
        byte[] hexData = ByteUtil.parseHexStrToBytes(hexMagic);
        byte[] revertHexData = ByteUtil.revertBytes(hexData);
        binStream.writeBytes(revertHexData);

        long dataLen = 0;
        byte[] dataLenBytes = ByteUtil.uint32ToBytes(dataLen);
        binStream.writeBytes(dataLenBytes);
    }

    private BinWriteResult writeConfigFirst(){
        SFLog.i(TAG,"writeConfigFirst");
        BinWriteResult result = new BinWriteResult();
        result.setAddress(this.binStream.getCurrentPosition());

        String appId = this.resContext.getAppId();
        byte[] projectIdBytes = appId.getBytes(StandardCharsets.UTF_8);
        byte[] projectIdBytes16 = paddingBytesToLenWith0(projectIdBytes,16);
        binStream.writeBytes(projectIdBytes16);

        //protocol version
        String protocolVersion = "2.0.1";
        int protocol = convertStrVersionToInt(protocolVersion);
        byte[] protocolBytes = ByteUtil.intTo4Bytes(protocol);
        binStream.writeBytes(protocolBytes);

        byte[] hexUID = ByteUtil.parseHexStrToBytes(this.resContext.getHexUID());
        byte[] revertHexUID =ByteUtil.revertBytes(hexUID);
        binStream.writeBytes(revertHexUID);
        long lanType = 1;
        byte[] lanTypeBytes = ByteUtil.uint32ToBytes(lanType);
        binStream.writeBytes(lanTypeBytes);

        byte resourceType = 0;
        byte imageNumber = 1;
        long resFlg = FlagMaker.makeResourceFlag(resourceType,imageNumber);
        byte[] resFlgBytes = ByteUtil.uint32ToBytes(resFlg);
        binStream.writeBytes(resFlgBytes);

        long lanLen = 0;
        byte[] lanLenBytes = ByteUtil.uint32ToBytes(lanLen);
        binStream.writeBytes(lanLenBytes);

        long lanAddress = 0;
        byte[] lanAddressBytes = ByteUtil.uint32ToBytes(lanAddress);
        binStream.writeBytes(lanAddressBytes);

        long dataLen = this.binStream.getCurrentPosition() - result.getAddress();
        result.setDataLen(dataLen);
        return  result;
    }
    private BinWriteResult writeConfigSecond(BinWriteResult imageResult){
        SFLog.i(TAG,"writeConfigSecond");
        this.binStream.seekTo(HEADER_ADDRESS);
        BinWriteResult result = new BinWriteResult();
        result.setAddress(this.binStream.getCurrentPosition());

        String appId = this.resContext.getAppId();
        byte[] projectIdBytes = appId.getBytes(StandardCharsets.UTF_8);
        byte[] projectIdBytes16 = paddingBytesToLenWith0(projectIdBytes,16);
        binStream.writeBytes(projectIdBytes16);

        //protocol version
        String protocolVersion = "2.0.1";
        int protocol = convertStrVersionToInt(protocolVersion);
        byte[] protocolBytes = ByteUtil.intTo4Bytes(protocol);
        binStream.writeBytes(protocolBytes);

        byte[] hexUID = ByteUtil.parseHexStrToBytes(this.resContext.getHexUID());
        byte[] revertHexUID = ByteUtil.revertBytes(hexUID);
        binStream.writeBytes(revertHexUID);
        long lanType = 1;
        byte[] lanTypeBytes = ByteUtil.uint32ToBytes(lanType);
        binStream.writeBytes(lanTypeBytes);

        byte resourceType = 0;
        byte imageNumber = 1;
        long resFlg = FlagMaker.makeResourceFlag(resourceType,imageNumber);
        byte[] resFlgBytes = ByteUtil.uint32ToBytes(resFlg);
        binStream.writeBytes(resFlgBytes);

        long lanLen = imageResult.getDataLen();
        byte[] lanLenBytes = ByteUtil.uint32ToBytes(lanLen);
        binStream.writeBytes(lanLenBytes);

        long lanAddress = imageResult.getAddress();
        byte[] lanAddressBytes = ByteUtil.uint32ToBytes(lanAddress);
        binStream.writeBytes(lanAddressBytes);

        long dataLen = this.binStream.getCurrentPosition() - result.getAddress();
        result.setDataLen(dataLen);

        this.binStream.seekToEnd();
        return  result;
    }

    private BinWriteResult writeImageData(byte[] ezipResult){
        BinWriteResult result = new BinWriteResult();
        result.setAddress(this.binStream.getCurrentPosition());
        this.binStream.writeBytes(ezipResult);
        //无理由末尾加4个00
        long zero = 0;
        byte[] zeroBytes = ByteUtil.uint32ToBytes(zero);
        binStream.writeBytes(zeroBytes);
        long dataLen = this.binStream.getCurrentPosition() - result.getAddress();
        result.setDataLen(dataLen);
        return result;
    }

    private void writeDataLen(long dataLen){
        this.binStream.seekTo(DATA_LEAN_ADDRESS);
        byte[] dataLenBytes = ByteUtil.uint32ToBytes(dataLen);
        binStream.writeBytes(dataLenBytes);
        this.binStream.seekToEnd();
    }

    private static byte[] paddingBytesToLenWith0(byte[] src, int len) {
        byte[] result = new byte[len];
        for (int i = 0; i < result.length; i++) {
            result[i] = 0;
        }
        if (src == null) {
            return result;
        }
        for (int index = 0; index < src.length; index++) {
            if (index < len) {
                result[index] = src[index];
            }
        }
        return result;
    }

    public static int convertStrVersionToInt(String version) {
        if (version == null || version.isEmpty()) {
            return 0;
        }
        String[] parts = version.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("版本号格式不正确，应为x.x.x形式");
        }

        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int build = Integer.parseInt(parts[2]);

        return (major << 24) + (minor << 12) + build;
    }
}
