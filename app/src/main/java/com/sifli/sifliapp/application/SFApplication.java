package com.sifli.sifliapp.application;

import android.app.Application;
import android.content.Context;


import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome;
import com.sifli.siflicore.log.SFLog;


/**
 * @author hecq
 * @email 33912760@qq.com
 * create at 2023/12/19
 * description
 */
public class SFApplication extends Application {
    private final String TAG = "SFApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        SFLog.i(TAG,"onCreate");
        Iconics.init(getApplicationContext());
        Iconics.registerFont(FontAwesome.INSTANCE);
    }
}
