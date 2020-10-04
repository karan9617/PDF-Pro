package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import com.example.myapplication.Database.DBHandler3;
import java.util.List;

public class Main6Activity extends AppCompatActivity {
    ListView listview;

    List<ContactsAll> allfiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        listview = findViewById(R.id.listview);
        final DBHandler3 dbHandler3  = new DBHandler3(this);
        allfiles = dbHandler3.getAllContacts();
        CustomAdapter5 customAdapter5 = new CustomAdapter5(this,allfiles);
        listview.setAdapter(customAdapter5);

    }

}




