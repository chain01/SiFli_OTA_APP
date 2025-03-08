package com.sifli.sifliapp.modules.debug.ota;

import android.view.View;

/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2024/1/6
 * description
 */
public interface ISFOtaImageAdapterCallback {
    void onSelectTypeBtnTouch(SFOtaImageItem item, View view);
    void onRemoveBtnTouch(SFOtaImageItem item, View view);
}
