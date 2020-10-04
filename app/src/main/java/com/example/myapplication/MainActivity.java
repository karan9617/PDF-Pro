package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.myapplication.Database.DBHandler3;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView logo;
    ArrayList<String> l = new ArrayList<>();
    private static final int PERMISSION_REQUEST_CODE = 1;
    DBHandler3 dbHandler3 = null;
    Button getstarted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        @SuppressLint("WrongConstant") SharedPreferences sh = getSharedPreferences("MySharedPref",MODE_APPEND);

        String s = sh.getString("name","");

        if(s.length() != 2){
           SaveSharedPreferenceForMainList.setIntegerCount(getApplicationContext(),1);

            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();

            myEdit.putString("name","dd");
            myEdit.putString("addpage","1");

            myEdit.commit();

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                requestPermission();
            }
            dbHandler3  = new DBHandler3(this);
            logo = findViewById(R.id.logo);
            Animation startFadeOutAnimation5 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
            logo.startAnimation(startFadeOutAnimation5);
            getstarted = findViewById(R.id.getstarted);

            getstarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted
                        requestPermission();
                    }
                    new ParseURL().execute();
                    Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
                    startActivity(intent);
                }
            });
        }
        else{
            dbHandler3 = new DBHandler3(this);

            new ParseURL().execute();
            Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
            startActivity(intent);
        }
    }
    //SELECT * FROM Table ORDER BY dateColumn DESC Limit 10000000;
    public void Search_Dir(File dir) {
        String pdfPattern = ".pdf";
        File FileList[] = dir.listFiles();
        if (FileList != null) {
            for (int i = 0; i < FileList.length; i++) {
                if (FileList[i].isDirectory()) {
                    Search_Dir(FileList[i]);
                } else {
                    if (FileList[i].getName().endsWith(pdfPattern) && dbHandler3.ifexists(FileList[i].getName(),FileList[i].getPath()) == 0){
                        double size = (double)(FileList[i].length()/(1024));
                        dbHandler3.add(new ContactsAll(
                                FileList[i].getName(),FileList[i].getPath(), String.valueOf(size)));
                        SaveSharedPreferenceForMainList.setIntegerCount(getApplicationContext(), SaveSharedPreferenceForMainList.getIntegerCount(getApplicationContext())+1);

                        l.add(FileList[i].getName()+"?"+FileList[i].getPath()+"?"+(size)+"");
                    }
                }
            }
        }

    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
    class ParseURL extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            try {
                Search_Dir(Environment.getExternalStorageDirectory());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @TargetApi(24)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
 class SaveSharedPreference
{
    static final String PREF_USER_NAME= "initial";
    static final String INT_STRING = "initialInteger";
    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }
    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }
    public static void setIntegerCount(Context ctx,int c)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(INT_STRING, c);
        editor.commit();
    }
    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }
    public static int getIntegerCount(Context ctx)
    {
        return getSharedPreferences(ctx).getInt(INT_STRING,0);
    }
}

class SaveSharedPreferenceForMainList
{
    static final String PREF_USER_NAME= "initial";
    static final String INT_STRING = "initialInteger";
    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }
    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }
    public static void setIntegerCount(Context ctx,int c)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(INT_STRING, c);
        editor.commit();
    }
    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }
    public static int getIntegerCount(Context ctx)
    {
        return getSharedPreferences(ctx).getInt(INT_STRING,0);
    }



}

