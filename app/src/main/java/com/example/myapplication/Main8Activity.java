package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;

import android.content.Intent;
import com.example.myapplication.Database.DBHandler3;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.tom_roush.pdfbox.multipdf.Splitter;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission;
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.Database.DBHandler2;
import com.example.myapplication.ui.CustomAdapterForUpload;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;


//************  FOR EXTRACTING TEXT FROM THE PDF  ***********/////


public class Main8Activity extends AppCompatActivity implements BaseClass {
    private ProgressBar pgsBar;
    Button button,merge;
    private Handler hdlr = new Handler();
    final ArrayList<File> arrayList = new ArrayList<>();
    TextView notfound;
    ImageView image;
    CustomAdapterForUpload customAdapterForUpload;
    ListView listview;
    ImageView clockface;
    ImageView sad;
    DBHandler2 dbHandler2;      //database for main list
    DBHandler3 dbHandler3;
    File list;
    int i =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main8);
        button = findViewById(R.id.button);
        merge = findViewById(R.id.merge);
        image = findViewById(R.id.image);
        sad =findViewById(R.id.sad);
        clockface =findViewById(R.id.clockface);
        dbHandler2 = new DBHandler2(this);
        notfound = findViewById(R.id.notfound);
        listview = findViewById(R.id.listview);
        pgsBar = findViewById(R.id.pBar3);
        pgsBar.setVisibility(View.INVISIBLE);
        dbHandler3 = new DBHandler3(this);

        clockface.setVisibility(View.INVISIBLE);
        if(BaseClass.arrayList.size() == 0){
            notfound.setVisibility(View.VISIBLE);
            listview.setVisibility(View.INVISIBLE);
            sad.setVisibility(View.VISIBLE);
        }
        else{
            sad.setVisibility(View.INVISIBLE);
            notfound.setVisibility(View.INVISIBLE);
            listview.setVisibility(View.VISIBLE);
            customAdapterForUpload = new CustomAdapterForUpload(getApplicationContext(), BaseClass.arrayList);
            customAdapterForUpload.setObject(customAdapterForUpload);
            listview.setAdapter(customAdapterForUpload);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Main8Activity.this, Main6Activity.class);
                startActivity(intent);
            }
        });
        merge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                  if(BaseClass.arrayList.size() == 1) {
                      i = pgsBar.getProgress();

                      pgsBar.setVisibility(View.VISIBLE);

                      notfound.setVisibility(View.VISIBLE);
                      new Thread(new Runnable() {
                          public void run() {
                              while (i < 100) {
                                  i += 1;
                                  // Update the progress bar and display the current value in text view
                                  hdlr.post(new Runnable() {
                                      public void run() {
                                          pgsBar.setProgress(i);
                                          notfound.setText(i+"/"+pgsBar.getMax());
                                      }
                                  });
                                  try {
                                      // Sleep for 100 milliseconds to show the progress slowly.
                                      Thread.sleep(20);
                                  } catch (InterruptedException e) {
                                      e.printStackTrace();
                                  }
                              }


                          }
                      }).start();

                      notfound.setText("Please wait for few seconds...");
                      clockface.setVisibility(View.VISIBLE);
                      new ParseURL().execute(BaseClass.arrayList.get(0));

                        // extracttext(BaseClass.arrayList.get(0));
                         // encry(BaseClass.arrayList.get(0),password.getText().toString());
                          Toast.makeText(getApplicationContext(), "Your file is loading....", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Upload one file at a time.",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){

                }
            }
        });
}

    class ParseURL extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... files) {
            try {
                extracttext(files[0]);

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

public void extracttext(File file){
    Document doc = new Document();

    try{
            PDDocument document = PDDocument.load(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();

            String originalname = file.getName();
            String text = pdfStripper.getText(document);
            //Toast.makeText(getApplicationContext(), text+"", Toast.LENGTH_SHORT).show();

            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Dir";
            File dir = new File(path);
            if(!dir.exists())
                dir.mkdirs();
            String finalpath = originalname.substring(0,originalname.indexOf('.'))+"_text.pdf";
            File newfile = new File(dir, finalpath);
            FileOutputStream fOut = new FileOutputStream(newfile);

            PdfWriter.getInstance(doc, fOut);
            doc.open();

            Paragraph p1 = new Paragraph(text);
            p1.setAlignment(Paragraph.ALIGN_LEFT);


            doc.add(p1);

             viewPdf(finalpath, "Dir");
            }catch (Exception e){

        }finally {
        doc.close();
    }

}
    private void viewPdf(String file, String directory) {

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        double size = (double)(pdfFile.length()/(1024));
        dbHandler3.add(new ContactsAll(pdfFile.getName(), pdfFile.getPath(), size+""));
        BaseClass.arrayList.remove(BaseClass.arrayList.get(0));
        BaseClassForPageInsert.arrayList.clear();

        Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
        try {
            intent.putExtra("file", pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(intent);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Main8Activity.this, Main2Activity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 7:

                if (resultCode == RESULT_OK) {

                    String PathHolder = data.getData().getPath();

                    File file = new File(PathHolder);
                    arrayList.add(file);
                    Toast.makeText(Main8Activity.this, file.getPath(), Toast.LENGTH_LONG).show();
                }
                break;

        }
    }
    public static ParcelFileDescriptor openFile(File file) {
        ParcelFileDescriptor descriptor;
        try {
            descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return descriptor;
    }


    public void encry(File file, String password){
        try{
            PDDocument document = PDDocument.load(file);
            AccessPermission ap = new AccessPermission();

            StandardProtectionPolicy spp = new StandardProtectionPolicy(password, password, ap);
            spp.setEncryptionKeyLength(128);
            spp.setPermissions(ap);
            document.protect(spp);
            Toast.makeText(getApplicationContext(), "Document encrypted",Toast.LENGTH_SHORT).show();

            Random rand = new Random();
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Dir";
            File dir = new File(path);
            if(!dir.exists()) dir.mkdirs();
            int rand_int1 = rand.nextInt(10000);
            String finalpath = path+"/encrypted"+rand_int1+".pdf";

            document.save(finalpath);

            File newfilename = new File(finalpath);
            double size = (double)(newfilename.length()/(1024));

            dbHandler3.add(new ContactsAll(
                    newfilename.getName(),newfilename.getPath(), String.valueOf(size)));

        }catch (Exception e){

        }
    }

}
