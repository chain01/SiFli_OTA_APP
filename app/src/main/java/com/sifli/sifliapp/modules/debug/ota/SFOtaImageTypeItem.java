package com.sifli.sifliapp.modules.debug.ota;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2023/9/15
 * description
 */
public class SFOtaImageTypeItem {
    private int type;
    private String name;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name + this.type;
    }
}
