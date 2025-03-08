package com.sifli.sifliapp.modules.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sifli.sifliapp.MainActivity;
import com.sifli.sifliapp.R;
import com.sifli.sifliapp.modules.debug.DebugHomeActivity;
import com.sifli.siflicore.log.SFLog;

public class PushAppSettingActivity extends AppCompatActivity implements View.OnClickListener{
    private final static String TAG = "PushAppSettingActivity";
    private RelativeLayout debugHomeLl;
    private TextView appVersionTv;
    private View backView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_app_setting);
        this.setupView();
        this.bindEvent();
        this.appVersionTv.setText(this.getVersionName());
    }

    private  void setupView(){
        this.debugHomeLl = findViewById(R.id.sf_setting_debug_rl);
        this.appVersionTv = findViewById(R.id.sf_setting_app_version_tv);
        this.backView = findViewById(R.id.sf_setting_back_iv);
    }

    private void bindEvent(){
        this.debugHomeLl.setOnClickListener(this);
        this.backView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.sf_setting_debug_rl){
            onDebugHomeLlTouch();
        }else if(viewId == R.id.sf_setting_back_iv ){
            this.goBack();
        }
    }

    private  void goBack(){
        SFLog.i(TAG,"go back");
        this.finish();
    }

    private void onDebugHomeLlTouch() {
        Intent intent = new Intent(PushAppSettingActivity.this, DebugHomeActivity.class);
//        intent.putExtra(SFRpcTestActivity.EXTRA_BLE_DEVICE, this.targetMac);
        startActivity(intent);
    }

    private String getVersionName() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }
}