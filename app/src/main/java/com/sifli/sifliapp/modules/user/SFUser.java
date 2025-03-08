package com.sifli.sifliapp.modules.user;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;


public class SFUser {
    private String TAG= "SFUser";
    private static SFUser mInstance;
    private SFLoginResult userEntity;
    private SharedPreferences sharedPreferences;
    private String KEY = "SFUserCache";
    public static SFUser getInstance(){
        if(mInstance == null){
            mInstance = new SFUser();
        }
        return mInstance;
    }

    public SFLoginResult getUserEntity() {
        return userEntity;
    }

    public String getMac(){
        if(userEntity == null){
            return  null;
        }
        return userEntity.getMac();
    }

    public void saveMac(String mac){
        if(userEntity == null){
            userEntity = new SFLoginResult();
        }
        userEntity.setMac(mac);
        this.save();
    }

    public void saveAppIdAndResUID(String appId,String resUID){
        if(userEntity == null){
            userEntity = new SFLoginResult();
        }
        userEntity.setAppId(appId);
        userEntity.setResUID(resUID);
        this.save();
    }

    public boolean isLogin(){
        return userEntity != null;
    }



    public void load(){
        String json =sharedPreferences.getString(KEY,null);
        if(json != null){
            Gson gson = new Gson();
            SFLoginResult setting=  gson.fromJson(json,SFLoginResult.class);
            this.userEntity = setting;
        }else{

        }
    }

    public void save(){

        Gson gson = new Gson();
        String jsonStr = gson.toJson(this.userEntity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY, jsonStr);
        editor.commit();
        Log.i(TAG,"save success");
    }


    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }
}
