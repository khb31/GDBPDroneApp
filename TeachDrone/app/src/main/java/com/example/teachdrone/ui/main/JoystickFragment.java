package com.example.teachdrone.ui.main;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.teachdrone.ControlActivity;
import com.example.teachdrone.R;

import java.util.Locale;

public class JoystickFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    short thrust=0;
    short yaw=1500;
    short roll=1500;
    short pitch=1500;
    short[]data=new short[8];
    private PageViewModel pageViewModel;
    ControlActivity activity;
    JoystickView[]joystickView=new JoystickView[2];

    public static JoystickFragment newInstance(int index) {
        JoystickFragment fragment = new JoystickFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }
    JoystickView.JoystickListener listener=new JoystickView.JoystickListener() {
        @Override
        public void onJoystickMoved(float xPercent, float yPercent, int id) {
            switch (id)//thrust:yaw:roll:pitch
            {
                case R.id.joystickRight:
                    Log.d("Right com.example.teachdrone.Joystick", "X percent: " + xPercent + " Y percent: " + yPercent);//not percentages
                    //pitch on y, roll on x
                    pitch=(short)(1500-(float)500*yPercent);
                    roll=(short)(1500+(float)500*xPercent);
                    break;
                case R.id.joystickLeft:
                    Log.d("Left com.example.teachdrone.Joystick", "X percent: " + xPercent + " Y percent: " + yPercent);//not percentages
                    //throttle on y, yaw on x
                    thrust=(short)((float)1000*(float)(1-yPercent));
                    yaw=(short)(1500+(float)500*xPercent);
                    break;
            }
            data[0]=thrust;
            data[1]=yaw;
            data[2]=roll;
            data[3]=pitch;
            for(int i=4;i<8;i++){
                data[i]=(short)1000;
            }
            activity.sendJoystickData(data);
            Log.wtf("joy data",String.format(Locale.ENGLISH,"Thrust: %d; Yaw: %d; Roll: %d; Pitch: %d",data[0],data[1],data[2],data[3]));
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;//0?
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);//must be first?
        }
        pageViewModel.setIndex(index);
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
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_joystick, container, false);
        final TextView textView = root.findViewById(R.id.section_label);
        joystickView[0]=root.findViewById(R.id.joystickLeft);
        joystickView[1]=root.findViewById(R.id.joystickRight);
        joystickView[0].setListener(listener);
        joystickView[1].setListener(listener);
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
    public short[]getChannelData(){//alternative method, not used here
        return data;
    }

}
