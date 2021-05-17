package com.example.teachdrone.ui.main;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.teachdrone.ControlActivity;
import com.example.teachdrone.R;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SensorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
//TODO:make it show correct fragment, display sensor data/channel data
public class SensorFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "2";
    TextView[]textBox=new TextView[8];
    TextView[]sensorBox=new TextView[2];
    private PageViewModel pageViewModel;
    private byte[]data = new byte[16];
    ControlActivity activity;
    int index;

    public static SensorFragment newInstance(int index) {
        SensorFragment fragment = new SensorFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);

        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
        Log.e("index",String.format(Locale.ENGLISH,"%d",index));
        Activity sActivity=getActivity();
        if(sActivity instanceof ControlActivity){
            activity=(ControlActivity)sActivity;
        }else{
            Log.wtf("what a terrible failure","it's all broken");
            //kill
            System.exit(0);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sensor, container, false);
        final TextView textView = root.findViewById(R.id.fragment_label);
        LinearLayout linearLayout=root.findViewById(R.id.Text_Layout);//change name later and in control activity xml
        LinearLayout linearLayout1=root.findViewById(R.id.Sensor_Layout);
        for(int i=0;i<8;i++){//8 values, 1 linear layout (maybe make into 2?)
            textBox[i]=new TextView(getActivity());
            textBox[i].setId(i);
            textBox[i].setText(String.format(Locale.ENGLISH,"Channel Data %d: x",i));
            textBox[i].setHeight(60);
            textBox[i].setWidth(400);
            textBox[i].setY((float)(i*60+5));
            textBox[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(textBox[i]);
        }
        if((index==2)||(index==3)){

            for(int i=0;i<2;i++){
                sensorBox[i]=new TextView(getActivity());
                sensorBox[i].setId(i+18);
                sensorBox[i].setText(String.format(Locale.ENGLISH,"Sensor Data %d: x",i));
                sensorBox[i].setHeight(60);
                sensorBox[i].setWidth(400);
                sensorBox[i].setY((float)(i*60+5));
                sensorBox[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayout1.addView(sensorBox[i]);
            }
            //linearLayout1.setX(800);
        }
        textView.setX(800);
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                setDisplayData();
                /*if((index==2)||(index==3)){
                    setDisplayData("float");
                }*/
            }
        },1000,100);
        return root;
    }
    public void setDisplayData(){//live data effectively, updated each second. channel data only
        //change to the sensor data in control activity
        displayData();
        //display the correct sensor data
        //run on UI thread
    }
    private void displayData(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run(){
                data = activity.getChannelData();
                int[]dataInt=new int[8];
                for (int i=0;i<8;i++){
                    dataInt[i]=((int)((int)(data[2*i+1]))&0xFF)<<8;
                    dataInt[i]+=(int)((int)(data[2*i]))&0xFF;
                    textBox[i].setText(String.format(Locale.ENGLISH,"Channel %d data: %d",i,dataInt[i]));
                }
                if((index==2)||(index==3)) {
                    float[] modData = activity.getModuleData(0);
                    for (int i = 0; i < 2; i++) {
                        sensorBox[i].setText(String.format(Locale.ENGLISH, "Sensor data %d: %.02f", i, modData[i]));
                    }
                }
            }
        });
    }
}