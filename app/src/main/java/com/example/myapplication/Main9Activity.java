package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/*** FOR OPENENIG PDF FROM OUTSIDE THE APPLICATION **/
public class Main9Activity extends AppCompatActivity {
    PDFView pdfView;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main9);
        pdfView = findViewById(R.id.pdfview);

        Intent intent = getIntent();
        String type = intent.getType();

        if (intent != null) {
            InputStream inputStream;
            if (type.endsWith("pdf")) {
                Uri file_uri = intent.getData();
                try {
                    inputStream = getContentResolver().openInputStream(file_uri);
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Dir";
                    File dir = new File(path);
                    if(!dir.exists())
                        dir.mkdirs();

                    file = new File(dir, "saved.pdf");
                    OutputStream outputStream = new FileOutputStream(file);
                    IOUtils.copy(inputStream, outputStream);
                    pdfView.fromFile(file).enableSwipe(true).enableAnnotationRendering(true).
                            scrollHandle(new DefaultScrollHandle(this)).load();

                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }
            }
        }
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
                intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

                intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                        "Sharing File..");
                intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File..");

                startActivity(Intent.createChooser(intentShareFile, "Share File.."));

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
