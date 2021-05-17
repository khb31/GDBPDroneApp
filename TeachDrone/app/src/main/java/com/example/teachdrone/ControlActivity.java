package com.example.teachdrone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.os.Bundle;

import com.example.teachdrone.ui.main.SensorFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.IBinder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.teachdrone.ui.main.SectionsPagerAdapter;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
//TODO: make it display correct fragment, passing to sensor fragment if needed. send commands to read sensor data.
public class ControlActivity extends AppCompatActivity {
        Button buttonStop;
        DroneBridge droneBridge=new DroneBridge();
        Timer timer=new Timer();
        static boolean STOP=false;
        byte[]channel=new byte[16];
        float[][]modData=new float[2][2];
        //SensorFragment sensorFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("creation","created");
        setContentView(R.layout.activity_control);
        buttonStop=(Button)(findViewById(R.id.buttonStop));
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                STOP=true;
            }
        });
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        LockableViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setSwipeable(false);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        Intent intent=new Intent(this,DroneBridge.class);
        bindService(intent,serviceConnection,0);
        /*timer.schedule(new TimerTask() { //runs every second, will update the channels in the service
            @Override
            public void run() {//not needed, instead write on receive of joystick data.
                droneBridge.Write();//write the channel data which has been set - need to set it here too (currently object data)
            }
        },1000,200);*/
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                channel=droneBridge.getChannel();//needs a block at some point - I don't think it's automatic
                //gets the current channel data - this could potentially be replaced with a method that gets sensor data
                droneBridge.read(1); //organises the next read for the channel data, by sending the required message to drone.

                //maybe pass an int to select which code to send, to read channels or to read mod1/mod2
                //sectionsPagerAdapter.getItem(1).setData(channel);
                Log.e("running","running true");
                //do something with channels - maybe write them into the UI in text boxes.
                //needs the rpi connection to be listening on port 8080 and to send back the data in format
                //$M>[0x10][0xZZ][DATA (16 bytes)][CRC]
            }
        },1100,200);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //channel=droneBridge.getChannel();//needs a block at some point - I don't think it's automatic
                //gets the current channel data - this could potentially be replaced with a method that gets sensor data
                modData[0]=droneBridge.getSensorData(0);
                droneBridge.read(2); //organises the next read for the channel data, by sending the required message to drone.

                //maybe pass an int to select which code to send, to read channels or to read mod1/mod2
                //sectionsPagerAdapter.getItem(1).setData(channel);
                Log.e("running","running true");
                //do something with channels - maybe write them into the UI in text boxes.
                //needs the rpi connection to be listening on port 8080 and to send back the data in format
                //$M>[0x10][0xZZ][DATA (16 bytes)][CRC]
            }
        },1150,200);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                modData[1]=droneBridge.getSensorData(1);//needs a block at some point - I don't think it's automatic
                //gets the current channel data - this could potentially be replaced with a method that gets sensor data
                droneBridge.read(3); //organises the next read for the channel data, by sending the required message to drone.

                //maybe pass an int to select which code to send, to read channels or to read mod1/mod2
                //sectionsPagerAdapter.getItem(1).setData(channel);
                Log.e("running","running true");
                //do something with channels - maybe write them into the UI in text boxes.
                //needs the rpi connection to be listening on port 8080 and to send back the data in format
                //$M>[0x10][0xZZ][DATA (16 bytes)][CRC]
            }
        },1250,200);
        Log.e("arrived","yes arrived");
    }
    ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DroneBridge.sBinder binder = (DroneBridge.sBinder)service;
            droneBridge=binder.getInstance();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    void tabSelect(int currentTab){
        //the tab view is already set, this
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        unbindService(serviceConnection);
    }
    public byte[]getChannelData(){
        return channel;
    }
    public float[]getModuleData(int sel){return modData[sel];}
    public void sendJoystickData(short[]data){
        droneBridge.write(data);
    }
}