package com.example.teachdrone;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class DroneBridge extends Service {
    Socket[]socket=new Socket[1];
    PrintWriter[]output= new PrintWriter[1];
    BufferedReader[]input=new BufferedReader[1];
    InputStream input0;
    OutputStream output0;
    //char[]messageReceive;
    private final byte[]channel=new byte[16];
    IBinder binder = new DroneBridge.sBinder();
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    float[][]moduleData=new float[2][2]; //assuming 2 modules each having 2 data outputs in float..32bit floats
    private static final String ACTION_READ_CHANNEL = "READ_CHANNEL";
    private static final String ACTION_READ_MODULE1 = "READ_MODULE1";
    private static final String ACTION_READ_MODULE2 = "READ_MODULE2";
    private static final String ACTION_WRITE = "WRITE";
    private String currentAction = "none";
    private static final String EXTRA_PARAM1 = "PARAM1"; //command?
    private static final String EXTRA_PARAM2 = "PARAM2"; //what?
    String param1 = "flop";
    short[] param2=new short[]{0x1010,0x1010,0x1010,0x1010};
    public DroneBridge() {
        Thread thread=new Thread(runnable);
        thread.start();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }
    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            try {
                Log.i("try", "inside try");
                //socket.setSoTimeout(1000);
                socket[0] = new Socket("192.168.86.27", 8080);//change
                Log.i("socket", "passed socket");
                //output[0] = new PrintWriter(new OutputStreamWriter(socket[0].getOutputStream(), StandardCharsets.ISO_8859_1));
                output0 = new DataOutputStream(socket[0].getOutputStream());
                Log.i("output", "passed output");
                //input[0] = new BufferedReader(new InputStreamReader(socket[0].getInputStream()));
                input0 = new DataInputStream(socket[0].getInputStream());
                Log.i("input", "passed input");
                //listener.run();
            }catch(IOException ignore){
                Log.e("ahhhhhhhh","ahhhhhhhhhhhh");
            }
            while (true){
                Log.e("CASE","STATE");
                switch (currentAction) {
                    case ACTION_READ_CHANNEL: {
                        byte[]message =new byte[]{'$','M','<',0,105,0};
                        //char[]retMessage = new char[22];
                        byte checksum='$';
                        for(byte i=1; i<5;i++){
                            checksum^=message[i];
                        }
                        message[5]=checksum;
                        try {
                            Log.e("writing","writing message rr");
                            output0.write(message);
                            output0.flush();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        //throw new UnsupportedOperationException("Not yet implemented");
                        currentAction="none";
                        break;
                    }//read RC channel data (by sending a command)
                    case ACTION_READ_MODULE1:{
                        byte message[]=new byte[]{'$','M','<',0,(byte)254,0};
                        //char[]retMessage = new char[22];
                        byte checksum='$';
                        for(byte i=1; i<5;i++){
                            checksum^=message[i];
                        }
                        message[5]=checksum;
                        try {
                            Log.e("writing","writing message");
                            output0.write(message);
                            output0.flush();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        //throw new UnsupportedOperationException("Not yet implemented");
                        currentAction="none";
                        break;
                    }
                    case ACTION_READ_MODULE2:{
                        byte message[]=new byte[]{'$','M','<',0,(byte)255,0};
                        //char[]retMessage = new char[22];
                        byte checksum='$';
                        for(byte i=1; i<5;i++){
                            checksum^=message[i];
                        }
                        message[5]=checksum;
                        try {
                            Log.e("writing","writing message");
                            output0.write(message);
                            output0.flush();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        //throw new UnsupportedOperationException("Not yet implemented");
                        currentAction="none";
                        break;
                    }
                    case ACTION_WRITE: {
                        Log.i("action","begin");
                        byte[]message=new byte[22];

                        channel[0]=(byte)((param2[0]&0xFF00)>>8);
                        channel[2]=(byte)((param2[1]&0xFF00)>>8);
                        channel[4]=(byte)((param2[2]&0xFF00)>>8);
                        channel[6]=(byte)((param2[3]&0xFF00)>>8);
                        channel[1]=(byte)(param2[0]&0xFF);
                        channel[3]=(byte)(param2[1]&0xFF);
                        channel[5]=(byte)(param2[2]&0xFF);
                        channel[7]=(byte)(param2[3]&0xFF);
                        message[0]='$';
                        message[1]='M';
                        message[2]='<';
                        message[3]=16;
                        message[4]=(byte)200;
                        message[5]=channel[0];
                        message[6]=channel[1];
                        message[7]=channel[2];
                        message[8]=channel[3];
                        message[9]=channel[4];
                        message[10]=channel[5];
                        message[11]=channel[6];
                        message[12]=channel[7];
                        for(byte a=13;a<21;a++){
                            message[a]=channel[a-5];
                        }
                        byte checksum=message[0];
                        for(byte i=1;i<21;i++){
                            checksum^=message[i];
                        }
                        message[21]=checksum;
                        Log.e("channel0",String.format(Locale.ENGLISH,"%d", (int) (channel[0])));
                        Log.e("channel1",String.format(Locale.ENGLISH,"%d", (int) (channel[1])));
                        Log.e("channel1",String.format(Locale.ENGLISH,"%d", (int) ((int)message[4]+(int)((int)message[3]<<8))));
                        Log.e("param0",String.format(Locale.ENGLISH,"%d",param2[0]));
                        //set the message into correct format for protocol
                        //maybe handle the message and convert, e.g. forward 10 into the correct format
                        Log.wtf("message",new String(message));
                        //Log.e("number char",((char)255&0xFF)==0xFF?"f":"t");
                        try {
                            output0.write(message);
                            output0.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //throw new UnsupportedOperationException("Not yet implemented");
                        currentAction="none";
                        break;
                    }//write RC channel data
                    case "none":{//listener, i.e. when not sending a data packet. occurs after a read action.
                        try {
                            //Log.e("arrived","inside the listener");
                            //char[] message = new char[22];//starts with "$M>"
                            byte[]message=new byte[1024];
                           if (input0.read(message) != -1) {//never times out - blocks forever
                                //something on the port
                                //messageReceive = message;
                                switch (message[4]) {
                                    case 105: {//numbers for the command code
                                        onReceiveRcChannel(message);
                                        break;
                                    }
                                    case ((byte)254): {//whatever case for sensor1
                                        onReceiveSensorData1(message);
                                        break;
                                    }
                                    case ((byte)255): {//sensor2
                                        onReceiveSensorData2(message);
                                        break;
                                    }
                                    //more cases for future
                                }
                            }
                        } catch (Exception ignore) {
                        }
                        break;
                    }//listen to data received
                }
            }
        }
    };
    private void onReceiveRcChannel(byte[]message){
        for(byte i=5;i<21;i++){
            Log.e("receiving",String.format(Locale.ENGLISH,"channel data: %d",(short)((short)message[i])&0xFF));
            channel[i-5]=message[i];//sets RC channel stuff
        }
    }
    private void onReceiveSensorData1(byte[]message){
        //receive 2 sets of 32bit float
        //$M>[0x08][0xFE][8 bytes data][CRC] makes 14 bytes
        //float temp=ByteBuffer.wrap(new byte[]{}).getFloat();
        byte[]val1=new byte[4];
        byte[]val2=new byte[4];
        for(byte i=5;i<9;i++){
            val1[i-5]=message[i];
        }
        float temp1=ByteBuffer.wrap(val1).getFloat();
        for(byte i=9;i<13;i++){
            val2[i-9]=message[i];
        }
        float temp2=ByteBuffer.wrap(val2).getFloat();
        moduleData[0][0]=temp1;
        moduleData[0][1]=temp2;
    }
    private void onReceiveSensorData2(byte[]message){
        //receive 2 sets of 32bit float
        //$M>[0x08][0xFE][8 bytes data][CRC] makes 14 bytes
        //float temp=ByteBuffer.wrap(new byte[]{}).getFloat();
        byte[]val1=new byte[4];
        byte[]val2=new byte[4];
        for(byte i=5;i<9;i++){
            val1[i-5]=message[i];
        }
        float temp1=ByteBuffer.wrap(val1).getFloat();
        for(byte i=9;i<13;i++){
            val2[i-9]=message[i];
        }
        float temp2=ByteBuffer.wrap(val2).getFloat();
        moduleData[1][0]=temp1;
        moduleData[1][1]=temp2;
    }
    public class sBinder extends Binder {
        public DroneBridge getInstance(){
            return DroneBridge.this;
        }
    }
    public void write(short[]data){
        param2=(short[])data;
        currentAction=ACTION_WRITE;
    }
    public void write(){
        currentAction=ACTION_WRITE;
    }
    public void read(){
        currentAction=ACTION_READ_CHANNEL;
    }
    public void read(int cmd){
        switch(cmd) {
            case 1: {
                //channel
                currentAction = ACTION_READ_CHANNEL;
                break;
            } case 2:{
                //mod1
                currentAction=ACTION_READ_MODULE1;
                break;
            } case 3:{
                //mod2
                currentAction=ACTION_READ_MODULE2;
                break;
            }
        }
    }
    public float[]getSensorData(int moduleNumber){
        //byte[]dataToWrite=ByteBuffer.allocate(4).putFloat(moduleData[moduleNumber][0]).array();//4 byte floats for this data (32 bit)
        //float temp=ByteBuffer.wrap(new byte[]{}).getFloat();
        return moduleData[moduleNumber];//both floats returned
    }
    public byte[]getChannel(){
        return channel;
    }
}