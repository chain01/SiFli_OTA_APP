package com.example.sifliotademo;

import static com.sifli.siflidfu.Protocol.IMAGE_ID_CTRL;
import static com.sifli.siflidfu.Protocol.IMAGE_ID_DYN;
import static com.sifli.siflidfu.Protocol.IMAGE_ID_HCPU;
import static com.sifli.siflidfu.Protocol.IMAGE_ID_LCPU;
import static com.sifli.siflidfu.Protocol.IMAGE_ID_MUSIC;
import static com.sifli.siflidfu.Protocol.IMAGE_ID_NAND_LCPU_PATCH;
import static com.sifli.siflidfu.Protocol.IMAGE_ID_NAND_RES;
import static com.sifli.siflidfu.Protocol.IMAGE_ID_RES;
import static com.sifli.siflidfu.SifliDFUService.BROADCAST_DFU_LOG;
import static com.sifli.siflidfu.SifliDFUService.BROADCAST_DFU_PROGRESS;
import static com.sifli.siflidfu.SifliDFUService.BROADCAST_DFU_STATE;
import static com.sifli.siflidfu.SifliDFUService.EXTRA_DFU_PROGRESS;
import static com.sifli.siflidfu.SifliDFUService.EXTRA_DFU_PROGRESS_TYPE;
import static com.sifli.siflidfu.SifliDFUService.EXTRA_DFU_STATE;
import static com.sifli.siflidfu.SifliDFUService.EXTRA_DFU_STATE_RESULT;
import static com.sifli.siflidfu.SifliDFUService.EXTRA_LOG_MESSAGE;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import com.sifli.siflidfu.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static  final String TAG = "MainActivity";
    private BroadcastReceiver localBroadcastReceiver;
    private EditText macEt;
    private TextView otaNorV1Tv;
    private TextView otaNorV1ForceTv;
    private TextView otaNorV1StopTv;
    private TextView otaNorV1ResumeTv;

    private TextView otaNorV2Tv;

    private TextView otaNorV2StopTv;
    private TextView otaNorV2ResumeTv;

    private TextView otaNorOfflineStartTv;
    private TextView otaNorOfflineStopTv;

    private TextView otaNandTv;
    private TextView otaNandStopTv;
    private TextView otaNandResumeTv;

    private TextView progressTv;


    private ProgressBar progressBar;

    public static  String TEMP_FILE_PATH;

    private  String nandCtrlFile;
    private  String nandHcpuFile;
    private  String nandResFile;
    private  String nandResDir;

    private  String norV1CtrlFile;
    private  String norV1HcpuFile;
    private  AssetCopyer assetCopyer;
    private  boolean copyFileSuccess;

    private SifliDFUService.SifliDFUBinder mBinder;
    private ISifliDFUService sifliDFUService;
    // 定义一个布尔值，用来标记服务是否绑定
    private boolean isBound = false;
    private final String[] PERMISSIONS_CALL = {
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
    };
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (SifliDFUService.SifliDFUBinder) service;
            sifliDFUService = mBinder.getDfuService();
            isBound = true;
            Log.i(TAG,"onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG,"onServiceDisconnected");
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setupView();
        init();
        copyOtaFiles();

        localBroadcastReceiver = new LocalBroadcastReceiver();
        registerDfuLocalBroadcast();

        // 创建一个 Intent 对象，指定要绑定的服务
        Intent serviceIntent = new Intent(this, SifliDFUService.class);
        // 调用 bindService() 方法，传入 Intent 对象，ServiceConnection 对象，和绑定模式
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
        this.checkCallPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(localBroadcastReceiver);
        if(isBound){
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    private void checkCallPermission () {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "checkCallPermission...");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_CALL, 123);
            }
        }
    }

    private void setupView(){
        this.macEt = findViewById(R.id.sf_main_ota_mac_et);
        this.macEt.setText("FF:FF:15:66:07:2B");
//        this.macEt.setText("11:22:33:44:56:DF");

        this.otaNorV1Tv = findViewById(R.id.sf_main_ota_nor_v1_tv);
        this.otaNorV1ForceTv = findViewById(R.id.sf_main_ota_nor_v1_force_tv);
        this.otaNorV1StopTv = findViewById(R.id.sf_main_ota_nor_v1_stop_tv);
        this.otaNorV1ResumeTv = findViewById(R.id.sf_main_ota_nor_v1_resume_tv);

        this.otaNorV2Tv = findViewById(R.id.sf_main_ota_nor_v2_tv);
        this.otaNorV2StopTv = findViewById(R.id.sf_main_ota_nor_v2_stop_tv);
        this.otaNorV2ResumeTv = findViewById(R.id.sf_main_ota_nor_v2_resume_tv);

        this.otaNorOfflineStartTv = findViewById(R.id.sf_main_ota_nor_offline_start_tv);
        this.otaNorOfflineStopTv = findViewById(R.id.sf_main_ota_nor_offline_stop_tv);

        this.otaNandTv = findViewById(R.id.sf_main_ota_nand_tv);
        this.otaNandStopTv = findViewById(R.id.sf_main_ota_nand_stop_tv);
        this.otaNandResumeTv = findViewById(R.id.sf_main_ota_nand_resume_tv);

        this.progressBar = findViewById(R.id.sf_main_ota_progress_pb);
        this.progressTv = findViewById(R.id.sf_main_ota_progress_tv);

        this.otaNorV1Tv.setOnClickListener(this);
        this.otaNorV1ForceTv.setOnClickListener(this);
        this.otaNorV1StopTv.setOnClickListener(this);
        this.otaNorV1ResumeTv.setOnClickListener(this);

        this.otaNorV2Tv.setOnClickListener(this);
        this.otaNorV2StopTv.setOnClickListener(this);
        this.otaNorV2ResumeTv.setOnClickListener(this);

        this.otaNorOfflineStartTv.setOnClickListener(this);
        this.otaNorOfflineStopTv.setOnClickListener(this);

        this.otaNandTv.setOnClickListener(this);
        this.otaNandStopTv.setOnClickListener(this);
        this.otaNandResumeTv.setOnClickListener(this);
    }

    private void init(){

        TEMP_FILE_PATH = this.getExternalFilesDir(null)+"";
        File file = new File(TEMP_FILE_PATH);
        assetCopyer = new AssetCopyer(this,file);
    }

    private void copyOtaFiles() {
        nandCtrlFile = TEMP_FILE_PATH + "/nand/ctrl_packet.bin";
        nandHcpuFile = TEMP_FILE_PATH + "/nand/outER_IROM1.bin";
        nandResFile = TEMP_FILE_PATH + "/nand/diff.zip";
        nandResDir = TEMP_FILE_PATH + "/nand/diff_dir";

        norV1CtrlFile = TEMP_FILE_PATH + "/norv1/ctrl_packet.bin";
        norV1HcpuFile = TEMP_FILE_PATH + "/norv1/outapp.bin";
//        copyAssetFile2SD(this, "nand_ctrl.bin", nandCtrlFile);
//        copyAssetFile2SD(this, "/nand/nand_hcpu.bin", nandHcpuFile);
        try{
            assetCopyer.copy();
            Log.i(TAG,"copyOtaFiles success");
            ZipUtil.unzipFolder(nandResFile,nandResDir);
            this.copyFileSuccess = true;
        }catch ( Exception ex){
            Log.e(TAG,"copyOtaFiles fail.ex=" + ex.toString());
            this.copyFileSuccess = false;
        }

    }

    @Override
    public void onClick(View v) {
        long viewId = v.getId();
        if(viewId == R.id.sf_main_ota_nor_v1_tv){
            this.otaNorV1(Protocol.DFU_MODE_NORMAL);
        }else if(viewId == R.id.sf_main_ota_nor_v1_force_tv){
            this.otaNorV1(Protocol.DFU_MODE_FORCE);
        }else if(viewId == R.id.sf_main_ota_nor_v2_tv){
            this.otaNorV2(false);
        }else if(viewId == R.id.sf_main_ota_nand_tv){
            this.otaNand(false);
        }else if(viewId == R.id.sf_main_ota_nor_v1_stop_tv){
            this.stop();
        }else if(viewId == R.id.sf_main_ota_nor_v2_stop_tv){
            this.stop();
        }else if(viewId == R.id.sf_main_ota_nand_stop_tv){
            this.stop();
        }else if(viewId == R.id.sf_main_ota_nor_v1_resume_tv){
            this.otaNorV1(Protocol.DFU_MODE_RESUME);
        }else if(viewId == R.id.sf_main_ota_nor_v2_resume_tv){
            this.otaNorV2(true);
        }else if(viewId == R.id.sf_main_ota_nand_resume_tv){
            this.otaNand(true);
        }else if(viewId == R.id.sf_main_ota_nor_offline_start_tv){
            this.otaNorOffline();
        }else if(viewId == R.id.sf_main_ota_nor_offline_stop_tv){
            this.stop();
        }
    }

    private void otaNand(boolean resume){
        Log.i(TAG,"otaNand");
        String bluetoothAddress = macEt.getText().toString();
        ArrayList<DFUImagePath> imagePaths = getNandImagePaths();
        if(imagePaths.size() == 0){
            Log.e(TAG,"ctrl image file not exist");
            return;
        }
        if(resume){
            this.sifliDFUService.startActionDFUNand(this, bluetoothAddress, imagePaths, 1, 0);
        }else{
            sifliDFUService.startActionDFUNand(this, bluetoothAddress, imagePaths, 0, 0);
        }

    }

    private  void stop(){
        Intent intent = new Intent(this, SifliDFUService.class);
        this.stopService(intent);
    }

    private void otaNorV1(int mode){
        String bluetoothAddress = macEt.getText().toString();
        ArrayList<DFUImagePath> imagePaths = getNorV1ImagePaths();
        if(imagePaths.size() == 0){
            Log.e(TAG,"ctrl image file not exist");
            return;
        }
        sifliDFUService.startActionDFUNor(this,bluetoothAddress,imagePaths,mode,0);
    }

    private  void otaNorV2(boolean resume){
        String bluetoothAddress = macEt.getText().toString();
        ArrayList<DFUImagePath> imagePaths = getNorV1ImagePaths();
        if(imagePaths.size() == 0){
            Log.e(TAG,"ctrl image file not exist");
            return;
        }

        if(resume){
            sifliDFUService.startActionDFUNorExt(this,bluetoothAddress,imagePaths,1,0);
        }else{
            sifliDFUService.startActionDFUNorExt(this,bluetoothAddress,imagePaths,0,0);
        }

    }

    private  void otaNorOffline(){
        Log.i(TAG,"otaNorOffline");
        String bluetoothAddress = macEt.getText().toString();
        ArrayList<DFUImagePath> imagePaths = getNorV1ImagePaths();
        if(imagePaths.size() == 0){
            Log.e(TAG,"ctrl image file not exist");
            return;
        }
        DFUImagePath image = imagePaths.get(0);

        sifliDFUService.startActionDFUNorOffline(this,bluetoothAddress,image.getImageUri(),null);
    }

    private ArrayList<DFUImagePath> getNandImagePaths() {
        ArrayList<DFUImagePath> paths = new ArrayList<>();

        File nandCtrlFileO = new File(nandCtrlFile);
        Uri nandCtrlFileUri = Uri.fromFile(nandCtrlFileO);
        Boolean isExist = this.isFileExists(nandCtrlFileUri);
        Log.i(TAG,"nandExist =" + isExist + ",path=" + nandCtrlFileUri.getPath() + ",scheme=" + nandCtrlFileUri.getScheme());
        DFUImagePath ctrlPath = new DFUImagePath(null, nandCtrlFileUri, IMAGE_ID_CTRL);
        if(isExist){
            paths.add(ctrlPath);
        }


        Uri nandHcpuFileUri = Uri.parse(nandHcpuFile);
        isExist = this.isFileExists(nandHcpuFileUri);
        Log.i(TAG,"hcpu exist =" + isExist + ",path=" + nandHcpuFileUri.getPath());
        DFUImagePath hcpuPath = new DFUImagePath(null, nandHcpuFileUri, IMAGE_ID_HCPU);
        if(isExist){
            paths.add(hcpuPath);
        }

        Uri nandResUri = Uri.parse(nandResDir);
        isExist = this.isFileExists(nandResUri);
        Log.i(TAG,"nand res exist =" + isExist + ",path=" + nandResUri.getPath());
        DFUImagePath resPath = new DFUImagePath(null, nandResUri, IMAGE_ID_NAND_RES);
        if(isExist){
            paths.add(resPath);
        }

        Log.d(TAG, "get hcpu");

        return paths;
    }

    private ArrayList<DFUImagePath> getNorV1ImagePaths() {
        ArrayList<DFUImagePath> paths = new ArrayList<>();
        File novv1CtrolFile0 = new File(norV1CtrlFile);
        Uri norv1CtrlFileUri = Uri.fromFile(novv1CtrolFile0);
        Boolean isExist = this.isFileExists(norv1CtrlFileUri);
        Log.i(TAG,"norv1 Exist =" + isExist + ",path=" + norv1CtrlFileUri.getPath() + ",scheme=" + norv1CtrlFileUri.getScheme());
        DFUImagePath ctrlPath = new DFUImagePath(null, norv1CtrlFileUri, IMAGE_ID_CTRL);
        if(isExist){
            paths.add(ctrlPath);
        }

        Uri norv1HcpuFileUri = Uri.parse(norV1HcpuFile);
        isExist = this.isFileExists(norv1HcpuFileUri);
        Log.i(TAG,"hcpu exist =" + isExist + ",path=" + norv1HcpuFileUri.getPath());
        DFUImagePath hcpuPath = new DFUImagePath(null, norv1HcpuFileUri, IMAGE_ID_HCPU);
        if(isExist){
            paths.add(hcpuPath);
        }

        return  paths;
    }

    public boolean isFileExists(Uri uri) {
//        ContentResolver resolver = getContentResolver();
//        Cursor cursor = resolver.query(uri, null, null, null, null);
//        boolean exists = (cursor != null && cursor.getCount() > 0);
//        if (cursor != null) {
//            cursor.close();
//        }
        File file = new File(uri.getPath());
        return file.exists();
    }

    private void updateProgress(int pro) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(pro);
                progressTv.setText(pro + "%");
            }
        });
    }



    class LocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BROADCAST_DFU_PROGRESS:
                    int progress = intent.getIntExtra(EXTRA_DFU_PROGRESS, 0);
                    int type = intent.getIntExtra(EXTRA_DFU_PROGRESS_TYPE, 0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progress);
                            progressTv.setText("pro" + progress);
                        }
                    });
                    //Log.i(TAG, "dfu progress " + progress);
                    break;
                case BROADCAST_DFU_LOG:
                    String DFULog = intent.getStringExtra(EXTRA_LOG_MESSAGE);

                    //Log.d(TAG, "DFU LOG - " + DFULog);
                    updateLogText(DFULog);
                    break;
                case BROADCAST_DFU_STATE:
                    int dfuState = intent.getIntExtra(EXTRA_DFU_STATE, 0);
                    int dfuStateResult = intent.getIntExtra(EXTRA_DFU_STATE_RESULT, 0);
                    Log.i(TAG,"dfuState=" + dfuState + ",dfuStateResult=" + dfuStateResult);
                    break;
            }
        }
    }

    private void registerDfuLocalBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_DFU_LOG);
        intentFilter.addAction(BROADCAST_DFU_STATE);
        intentFilter.addAction(BROADCAST_DFU_PROGRESS);
        // more action

        registerLocalReceiver(localBroadcastReceiver, intentFilter);
    }

    public void registerLocalReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(receiver, filter);
    }

    private void updateLogText(String string) {
        runOnUiThread(() -> {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            int second = c.get(Calendar.SECOND);

//            @SuppressLint("DefaultLocale") String newStr = String.format("%s\n", DFUInfoTextView.getText().toString()) +
//                    String.format("%02d", hour) + ":" +
//                    String.format("%02d", minute) + ":" +
//                    String.format("%02d", second) + "  " +
//                    String.format("%s", string);
//            DFUInfoTextView.setText(newStr);
        });
    }
}