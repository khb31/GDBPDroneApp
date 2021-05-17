package com.example.teachdrone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.teachdrone.ui.login.LoginActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_1=1;
    private String Username;
    private String Password;
    boolean loggedIn = false;
    Button button;
    SSHService sshService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Intent intent = new Intent(this, SSHService.class);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("c",getFilesDir().getPath());
        startActivityForResult(intent,0);
        //bindService(intent,serviceConnection,0);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        //potential to add other commands on close
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        loggedIn=intent.getBooleanExtra("loggedIn",false);
        Log.i("made","made it here");
        if((resultCode==RESULT_OK)&&(loggedIn)){
            nextStage();
        }else{
            startActivityForResult(new Intent(this,LoginActivity.class),2);
        }
    }
    private void nextStage(){
        Log.i("loggedIn status",loggedIn?"Success":"Failure");
        Intent intent=new Intent(this,ControlActivity.class);
        startActivity(intent);
    }
    String connectWifi(String ssid, String password){
        WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", password);
        //remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        WifiInfo wifiInfo=wifiManager.getConnectionInfo();

        boolean isConnectionSuccessful=(wifiInfo.getNetworkId()!=-1);

        if(isConnectionSuccessful){
            return "connection successful";
        }else{
            return "invalid credential";
        }
    }
}