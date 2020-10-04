package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.example.myapplication.Database.DBHandler3;


import java.util.ArrayList;
import java.util.List;

public class Main7Activity extends AppCompatActivity {
    List<ContactsAll> allfiles = new ArrayList<>();
    ListView listView;
  //  AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
/*
        MobileAds.initialize(this,"ca-app-pub-8206113478901010~9618015045");
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


 */
        listView = findViewById(R.id.listview);
        final DBHandler3 dbHandler3  = new DBHandler3(this);
        allfiles = dbHandler3.getAllContacts();
        CustomAdapter3 customAdapter3 = new CustomAdapter3(this,allfiles);
        listView.setAdapter(customAdapter3);

    }
}
