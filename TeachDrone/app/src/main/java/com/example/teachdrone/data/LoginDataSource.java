package com.example.teachdrone.data;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.teachdrone.MainActivity;
import com.example.teachdrone.data.model.LoggedInUser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    public Result<LoggedInUser> login(String username, String password, String location) {//rem location
        try {
            // TODO: handle loggedInUser authentication
            String currentUsername, currentPassword;
            boolean loggedIn=true;
            /*try {
                //to make a new file for password and username, set some fileoutput streams similar to below to write them in.
                FileInputStream fileInputStream=new FileInputStream(location+"/username"); //this fix can't actually be done. For whatever reason it can't access the SD card
                //could fix pass passing context over extra (poor)
                ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
                currentUsername=objectInputStream.readObject().toString();
                //better ways of checking username and password using maybe a hash table?
                FileInputStream fileInputStream2=new FileInputStream(location+"/password");
                ObjectInputStream objectInputStream2=new ObjectInputStream(fileInputStream2);
                currentPassword=objectInputStream2.readObject().toString();
                if(currentUsername.equals(username)&&currentPassword.equals(password)){
                    loggedIn=true;
                }else{
                    loggedIn=false;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("e3 Not found", "couldn't find username file");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("e4 IOE", "failed to read username");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Log.e("e5 class not found", "the username object couldn't be located");
            }*/
            if(loggedIn) {
                LoggedInUser fakeUser =
                        new LoggedInUser(
                                java.util.UUID.randomUUID().toString(),
                                "Jane Doe");//using tables, e.g. hashtables for all this, much better
                return new Result.Success<>(fakeUser);
            }else{
                return new Result.Error(null);
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}