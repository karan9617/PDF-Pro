package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.Database.DBHandler3;
import com.example.myapplication.Database.DBHandler4;
import com.google.android.material.snackbar.Snackbar;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;

public class Main10Activity extends AppCompatActivity implements BaseClassForPageInsert{
        EditText pageno;
        Button upload,addpage;
        EditText content;
        ProgressBar pgsBar;
    ImageView sad,clockface;
    TextView notfound;
    DBHandler3 dbHandler3;
        private Handler hdlr = new Handler();
        int i =0;
        DBHandler4 dbHandler4;
        CardView card_view4;
        RelativeLayout relativeLayout;
        String filename = "", dir = "Dir";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main10);
        addpage = findViewById(R.id.addpage);
        upload = findViewById(R.id.upload);
        relativeLayout = findViewById(R.id.relative);
        clockface = findViewById(R.id.clockface);
        notfound = findViewById(R.id.notfound);

        clockface.setVisibility(View.INVISIBLE);
        content = findViewById(R.id.content);
        card_view4 = findViewById(R.id.card_view4);
        pageno = findViewById(R.id.pageno);
        pgsBar = findViewById(R.id.pBar3);
        sad =findViewById(R.id.sad);

        pgsBar.setVisibility(View.INVISIBLE);
        pageno.setInputType(InputType.TYPE_CLASS_NUMBER);
        dbHandler3 = new DBHandler3(this);
        dbHandler4 = new DBHandler4(this);

        if(BaseClassForPageInsert.arrayList.size() == 0)
        {
            card_view4.setVisibility(View.INVISIBLE);
            upload.setVisibility(View.VISIBLE);
            addpage.setVisibility(View.INVISIBLE);
            sad.setVisibility(View.VISIBLE);
        }
        else{
            addpage.setVisibility(View.VISIBLE);
            sad.setVisibility(View.INVISIBLE);
            card_view4.setVisibility(View.VISIBLE);
            upload.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(),"One file uploaded",Toast.LENGTH_SHORT).show();
        }

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BaseClassForPageInsert.arrayList.clear();
                Intent intent = new Intent(Main10Activity.this, Main11Activity.class);
                startActivity(intent);
            }
        });
        addpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pageno.getText().toString().length()!= 0){
                    if(BaseClassForPageInsert.arrayList.size() == 1){
                        if(content.getText().toString().length() != 0){
                            try {
                                PDDocument document = PDDocument.load(BaseClassForPageInsert.arrayList.get(0));

                                int total = document.getNumberOfPages();
                                int pagenofinal = Integer.parseInt(pageno.getText().toString())-1;
                                if(total >= pagenofinal) {
                                    card_view4.setVisibility(View.INVISIBLE);
                                    clockface.setVisibility(View.VISIBLE);
                                    pgsBar.setVisibility(View.VISIBLE);

                                    notfound.setText("Your file is loading....");

                                    new ParseURL().execute(BaseClassForPageInsert.arrayList.get(0)); }
                                else
                                {
                                    Snackbar.make(relativeLayout, "The page number is greater than the total pages in the document.", Snackbar.LENGTH_LONG)
                                            .setAction("CLOSE", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                }
                                            })
                                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                            .show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //new ParseURL().execute(pageno.getText().toString(),content.getText().toString());
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Please enter some content to be displayed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Please upload only one file.",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please enter a valid page number to be changed.",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    class ParseURL extends AsyncTask<File, Void, Void> {

        String finalname="";
        @Override
        protected Void doInBackground(File... files) {
            try {
                addText(files[0],pageno.getText().toString(), content.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @TargetApi(24)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            filename = finalname;
        }
    }
    public void addText(File file, String pageno, String content){
        int pagenofinal = Integer.parseInt(pageno)-1;
        try {
            PDDocument document = PDDocument.load(file);

                PDPage page = document.getPage(pagenofinal);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                contentStream.newLineAtOffset(25, 500);
                contentStream.showText(content);
                contentStream.endText();
                contentStream.close();
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir";

                File dir = new File(path);
                if (!dir.exists())
                    dir.mkdirs();

                String filePath = path + "/" + file.getName() + "_added.pdf";

                File newfile = new File(filePath);
                document.save(newfile);

                //Closing the document
                document.close();
                viewPdf(newfile.getName(), "Dir");
            }
        catch (Exception e){
        }
    }
    private void viewPdf(String file, String directory) {
        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        double size = (double)(pdfFile.length()/(1024));

        dbHandler3.add(new ContactsAll(pdfFile.getName(),pdfFile.getPath(), String.valueOf(size)));

        dbHandler4.add(new ContactsAll( pdfFile.getName(),pdfFile.getPath(), String.valueOf(size)));

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

        Intent intent = new Intent(Main10Activity.this, Main2Activity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_encrypt, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.files:
                Intent intent = new Intent(Main10Activity.this, Main12Activity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
