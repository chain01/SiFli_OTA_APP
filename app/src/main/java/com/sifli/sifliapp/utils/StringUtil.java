package com.sifli.sifliapp.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2023/9/14
 * description
 */
public class StringUtil {
    public static boolean isNullOrEmpty(String txt){
        if(txt == null || txt.isEmpty()){
            return true;
        }
        return false;
    }

    public static String getTimeStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        return sdf.format(new Date());
    }

    public static String getExt(String url) {
        if (url == null || url.lastIndexOf('.') == -1) {
            return "";
        }
        return url.substring(url.lastIndexOf('.') + 1);
    }

    public static String md5(String url) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(url.getBytes());
            byte[] messageDigest = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static  String md5FileName(String url){
        String md5 = md5(url);
        String ext = getExt(url);
        return md5 + ext;
    }

    public static boolean isHexStr(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            return false;
        }
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            if (!((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f'))) {
                return false;
            }
        }
        return true;
    }
}
