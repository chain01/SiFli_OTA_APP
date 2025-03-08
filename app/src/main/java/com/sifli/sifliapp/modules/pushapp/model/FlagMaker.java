package com.sifli.sifliapp.modules.pushapp.model;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2024/11/22
 * description
 */
public class FlagMaker {
    public static long makeResourceFlag(byte resourceType, byte imageNumber) {
        // 对 resourceType 进行掩码处理，只保留 0 - 3 bit
        resourceType &= 0x0F;
        long result = 0;
        result |= (long) resourceType;
        result |= ((long) imageNumber << 24);
        return result;
    }
}
