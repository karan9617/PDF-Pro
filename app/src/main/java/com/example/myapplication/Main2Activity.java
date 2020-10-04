package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.example.myapplication.Database.DBHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class Main2Activity extends AppCompatActivity {
    private FragmentRefreshListener fragmentRefreshListener;
    DBHandler dbHandler;      //database for main list
    private static final int PERMISSION_REQUEST_CODE = 1;

    public AlertDialog.Builder cancelImageDialog(Context c){

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("PDF Pro");
        builder.setMessage("Do you want to clear all files from Recent..?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHandler.droptable();
                Toast.makeText(getApplicationContext(), "Recent files cleared.",Toast.LENGTH_SHORT).show();
                getFragmentRefreshListener().onRefresh();

                dialog.dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setIcon(R.drawable.pdfimage);
        return builder;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        dbHandler = new DBHandler(this);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        if (ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            requestPermission();

        }
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();

        NavController navController = Navigation.findNavController(Main2Activity.this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(Main2Activity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Toast.makeText(Main2Activity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Main2Activity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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
    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
        this.finishAffinity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_side_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(Main2Activity.this, Main10ActivityAbout.class);
                startActivity(intent);
                return true;
            case R.id.recent:
                clearall();

            case R.id.grid:
                SaveSharedPreferenceForMainList2.setIntegerCount(getApplicationContext(),0);
                getFragmentRefreshListener().onRefresh();
                return true;

            case R.id.list:
                SaveSharedPreferenceForMainList2.setIntegerCount(getApplicationContext(),1);
                getFragmentRefreshListener().onRefresh();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }
    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }
    public interface FragmentRefreshListener{
        void onRefresh();
    }
    public void clearall(){
        dbHandler.droptable();
    }
}
