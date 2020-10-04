package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.example.myapplication.Database.DBHandler3;

import java.util.List;

public class Main11Activity extends AppCompatActivity {
    ListView listview;
    List<ContactsAll> allfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main11);
        listview = findViewById(R.id.listview);
        final DBHandler3 dbHandler3  = new DBHandler3(this);
        allfiles = dbHandler3.getAllContacts();
        CustomAdapterForPage customAdapter5 = new CustomAdapterForPage(this,allfiles);
        listview.setAdapter(customAdapter5);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Main11Activity.this, Main2Activity.class);
        startActivity(intent);
    }
}
