package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.*;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;

public class Main3Activity extends AppCompatActivity {

    PDFView pdfView;
    File u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        pdfView = findViewById(R.id.pdfview);

        u = (File) getIntent().getExtras().get("file");
        RelativeLayout relativeLayout= findViewById(R.id.relative);
        Snackbar.make(relativeLayout, "The file name is:"+u.getName(), Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();

        pdfView = findViewById(R.id.pdfview);
        pdfView.fromFile(u).enableSwipe(true).enableAnnotationRendering(true).scrollHandle(new DefaultScrollHandle(this)).load();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_side, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.share:
                Intent intentShareFile = new Intent(Intent.ACTION_SEND);

                intentShareFile.setType("application/pdf");
                intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(u));

                intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File..");
                intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File..");

                startActivity(Intent.createChooser(intentShareFile, "Share File.."));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(u.getName().contains("text")){
            Intent intent = new Intent(Main3Activity.this, Main2Activity.class);
            startActivity(intent);
        }
        if(u.getName().contains("added")){
            Intent intent = new Intent(Main3Activity.this, Main2Activity.class);
            startActivity(intent);
        }

    }
}
