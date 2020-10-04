package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.example.myapplication.Database.DBHandler3;
import com.example.myapplication.Database.DBHandler4;


import java.util.ArrayList;
import java.util.List;

public class Main12Activity extends AppCompatActivity {

    List<ContactsAll> allfiles = new ArrayList<>();
    ListView listView;
    //AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main12);
/*
        MobileAds.initialize(this,"ca-app-pub-8206113478901010~9618015045");
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


 */
        listView = findViewById(R.id.listview);
        final DBHandler4 dbHandler4  = new DBHandler4(this);
        allfiles = dbHandler4.getAllContacts();
        ArrayList<ContactsAll> list = (ArrayList<ContactsAll>) allfiles;
        CustomAdapter2 customAdapter2 = new CustomAdapter2(this,list);
        listView.setAdapter(customAdapter2);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(Main12Activity.this, Main10Activity.class);
        startActivity(intent);
    }
}
