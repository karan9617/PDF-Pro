package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.Database.DBHandler2;

/*import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;


 */
import java.util.ArrayList;
import java.util.List;

public class Main5Activity extends AppCompatActivity {

    ListView starlistview;
    //AdView adView;
    ImageView startimage;
    TextView nostartext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
/*
        MobileAds.initialize(this,"ca-app-pub-8206113478901010~9618015045");
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

 */
        starlistview = findViewById(R.id.starlistview);
        List<ContactsAll> list;



        ArrayList<ContactsAll> finalarr  = new ArrayList<>();
        startimage = findViewById(R.id.startimage);
        nostartext = findViewById(R.id.nostartext);
        startimage.setVisibility(View.INVISIBLE);
        nostartext.setVisibility(View.INVISIBLE);
        final DBHandler2 dbHandler2 = new DBHandler2(this);    //database for main list
        list = dbHandler2.getAllContacts();

        for(int i=0;i<list.size();i++){

            finalarr.add(list.get(i));
        }
        if(finalarr.size() > 0) {
            startimage.setVisibility(View.INVISIBLE);
            nostartext.setVisibility(View.INVISIBLE);
        }
        else{
            startimage.setVisibility(View.VISIBLE);
            nostartext.setVisibility(View.VISIBLE);
        }
        CustomAdapter2 customAdapter2 = new CustomAdapter2(this, finalarr);
        starlistview.setAdapter(customAdapter2);


    }
}
