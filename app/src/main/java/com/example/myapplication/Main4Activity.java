package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.Database.DBHandler3;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main4Activity extends AppCompatActivity implements RecognitionListener{
    DBHandler3 dbHandler3;
    Button savebutton;
    EditText content,pdfname;
    List<ContactsAll> array2;
    boolean flag = false;
    String contentEntered,filename;
    ImageView micimage;

    private SpeechRecognizer speech = null;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    //AdView adView;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO))
            {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();
                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
            }
            else
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            //Go ahead with recording audio now
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permissions Denied, Please select okay to ask questions.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        pdfname = findViewById(R.id.pdfname);
        content = findViewById(R.id.content);
/*
        MobileAds.initialize(this,"ca-app-pub-8206113478901010~9618015045");
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


 */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestAudioPermissions();
        }
        micimage = findViewById(R.id.mic);

        dbHandler3 = new DBHandler3(this);
        savebutton = findViewById(R.id.savebutton);
        array2 = new ArrayList<>();

        array2 = dbHandler3.getAllContacts();
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filename = pdfname.getText().toString();
                contentEntered = content.getText().toString();
                if(contentEntered.length() != 0 && filename.length() != 0){
                    for(int i =0;i<array2.size();i++){
                        if(array2.get(i).getFilename().equals(filename)){flag = true;break;}
                    }
                    if(!flag){
                        createDialog( filename,contentEntered, Main4Activity.this).show();
                       // createandDisplayPdf(contentEntered, filename);

                    }else{
                        Toast.makeText(getApplicationContext(), "File name already exists", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please enter the file name and content.", Toast.LENGTH_SHORT).show();

                }
            }
        });
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener((RecognitionListener) this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);

        micimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(Main4Activity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    requestAudioPermissions();
                }
                speech.startListening(recognizerIntent);
            }
        });
    }
    public AlertDialog.Builder createDialog(String filename, String content, final Context c){
        final String finalFileName = filename;
        final String finalContent = content;

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("PDF Pro");
        builder.setMessage("Do you want to create the pdf..?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createandDisplayPdf(finalContent, finalFileName);
                Toast.makeText(c, "PDF created succesfully..",Toast.LENGTH_SHORT).show();
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

    // Method for creating a pdf file from text, saving it then opening it for display
    public void createandDisplayPdf(String text, String filename) {
        Document doc = new Document();

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Dir";

            File dir = new File(path);
            if(!dir.exists())
                dir.mkdirs();
            if(filename.indexOf('.') == -1){
                filename = filename + ".pdf";
            }
            File file = new File(dir, filename);
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);
            doc.open();
            Paragraph p1 = new Paragraph(text);
            p1.setAlignment(Paragraph.ALIGN_LEFT);
            //add paragraph to document
            doc.add(p1);
        } catch (DocumentException de) {
        } catch (IOException e) {
        }
        finally {
            doc.close();
        }

        viewPdf(filename, "Dir");
    }

    // Method for opening a pdf file
    private void viewPdf(String file, String directory) {

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        double size = (double)(pdfFile.length()/(1024));


        dbHandler3.add(new ContactsAll(pdfFile.getName(), pdfFile.getPath(), size+""));

        Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
        try {
            intent.putExtra("file", pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(intent);

    }
    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }
    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }
    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording was incomplete";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Oops something went wrong.";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Please provide sufficient permissions from settings";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Some Network error.. Please try again";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server.. Please try again";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "Please speak something";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
    }
    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }
    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
    }
    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = matches.get(0).trim().toString();

        String full = content.getText().toString();
        full = full+text;
        content.setText(full);
    }
    @Override
    public void onPartialResults(Bundle bundle) {
    }
    @Override
    public void onEvent(int i, Bundle bundle) {

    }

}
