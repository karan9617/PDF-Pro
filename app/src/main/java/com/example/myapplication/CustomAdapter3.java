package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.adobe.acrobat.pdf.Document;
import com.example.myapplication.Database.DBHandler;
import com.example.myapplication.Database.DBHandler2;
import com.example.myapplication.Database.DBHandler3;
import com.example.myapplication.Database.DBHandler4;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;

import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;

public class CustomAdapter3 extends ArrayAdapter<ContactsAll> {


    private final Context context;
    private final List<ContactsAll> array;
    TextView wordName,size,time;
    Switch s;
    ImageView dots,pdfimage;
    final DBHandler2 dbHandler2;      //database for main list

    final DBHandler3 dbHandler3;
    final DBHandler4 dbHandler4;
    final DBHandler dbHandler;

    public CustomAdapter3(Context context, List<ContactsAll> array2) {
        super(context, -1, array2);
        this.context = context;
        this.array = array2;
        dbHandler2  = new DBHandler2 (context);
        dbHandler = new DBHandler(context);
        dbHandler3 = new DBHandler3(context);
        dbHandler4 = new DBHandler4(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.list_item_example3, parent, false);
        final ContactsAll cn = array.get(position);
        final File file = new File(cn.getPath());
        wordName = (TextView) rowView.findViewById(R.id.filename);
        time = (TextView) rowView.findViewById(R.id.time);
        //pop menu
        dots = rowView.findViewById(R.id.dots);
        size = (TextView) rowView.findViewById(R.id.size);
        pdfimage = rowView.findViewById(R.id.pdfimage);

        size.setText("size: "+size(cn.getSize()));
        //ADDING THE ROW TO THE RECENT LIST


        final PopupMenu popup = new PopupMenu(context, dots);
        popup.inflate(R.menu.popup_menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.share:

                        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

                        intentShareFile.setType("application/pdf");
                        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                "Sharing File..");
                        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File..");

                        context.startActivity(Intent.createChooser(intentShareFile, "Share File.."));
                        return true;

                    case R.id.star:
                        if(dbHandler2.ifFileExists(cn.getFilename(), cn.getPath()) == 1){
                            Toast.makeText(context, "File already in favourites",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            dbHandler2.addContact(new ContactsAll(SaveSharedPreference.getIntegerCount(context)+1,cn.getFilename(), cn.getPath(),
                                    cn.getSize()));
                            SaveSharedPreference.setIntegerCount(context, SaveSharedPreference.getIntegerCount(context)+1);
                        }
                        Toast.makeText(context, "Added to star list.",Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.delete:

                        cancelImageDialog(file, context,cn).show();

                        return true;

                    default:
                        return false;
                }

            }

        });
        dots.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public final void onClick(View v) {
                                        popup.show();
                                    }
                                }
        );
        dots.setOnTouchListener(popup.getDragToOpenListener());

        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                return false;
            }
        });
        wordName.setText( processname(cn.getFilename()));
        //SENDING THE FILE THROUGH INTENT FOR DISPLAY
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Date object
                Date date= new Date();
                //getTime() returns current time in milliseconds
                long time = date.getTime();
                Timestamp ts = new Timestamp(time);

                Bitmap bitmap = getBitmap(file);

                byte[] byteArray;
                if(bitmap != null){
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 15, stream);
                    byteArray = stream.toByteArray();
                }
                else{
                    byteArray = null;
                }

                if(dbHandler.ifFileExists(cn.getFilename(), cn.getPath()) == 1){
                  //  dbHandler.deleteContact(cn.getFilename(), cn.getPath());
                   // dbHandler.addContact(new ContactsAllImg(SaveSharedPreference.getIntegerCount(context)+1,file.getName(), file.getPath(), cn.getSize(),ts+"", byteArray ));

                    dbHandler.updateRowFromCustomAdapter3(cn,ts+"");
                   // SaveSharedPreference.setIntegerCount(context, SaveSharedPreference.getIntegerCount(context)+1);
                }
                else{
                    dbHandler.addContact(new ContactsAllImg(SaveSharedPreference.getIntegerCount(context)+1,file.getName(), file.getPath(),
                            cn.getSize(),ts+"",byteArray));

                    SaveSharedPreference.setIntegerCount(context, SaveSharedPreference.getIntegerCount(context)+1);
                }


                Intent intent = new Intent(context, Main3Activity.class);
                try {
                    intent.putExtra("file", file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                context.startActivity(intent);
            }
        });
        return rowView;
    }

    public Bitmap getBitmap(File file){
        int pageNum = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(context);
        try {
            PdfDocument pdfDocument = pdfiumCore.newDocument(openFile(file));
            pdfiumCore.openPage(pdfDocument, pageNum);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNum);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNum);

            // ARGB_8888 - best quality, high memory usage, higher possibility of OutOfMemoryError
            // RGB_565 - little worse quality, twice less memory usage
            Bitmap bitmap = Bitmap.createBitmap(width , height , Bitmap.Config.RGB_565);
            pdfiumCore.renderPageBitmap(pdfDocument, bitmap, pageNum, 0, 0,
                    width, height);
            pdfiumCore.closeDocument(pdfDocument); // important!
            return bitmap;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
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
    public AlertDialog.Builder cancelImageDialog(File title, Context c, final ContactsAll cn){
        final File filename = title;
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("PDF Pro");
        builder.setMessage("Do you want to delete the PDF from the device..?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (dbHandler.ifFileExists(filename.getName(), filename.getPath()) ==1){
                    dbHandler.deleteContact(filename.getName(), filename.getPath());
                }
                if (dbHandler2.ifFileExists(filename.getName(), filename.getPath()) ==1){
                    dbHandler2.deleteContact(filename.getName(),filename.getPath());
                }
                if (dbHandler4.ifFileExists(filename.getName(), filename.getPath()) ==1){
                    dbHandler4.deleteContact(filename.getName(),filename.getPath());
                }
                dbHandler3.deleteContact(filename.getName(),filename.getPath());

                array.remove(cn);
                notifyDataSetChanged();

                delete(filename);
                dialog.dismiss();
            }

        }).setNegativeButton("Discard", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setIcon(R.drawable.pdfimage);
        return builder;
    }

    public String size(String size){
        int i =0;String finalvalue = "";
        while(size.charAt(i) != '.'){
            finalvalue = finalvalue+size.charAt(i);
            i++;
        }

        int s = Integer.parseInt(finalvalue);
        if(s > 1024){
            s = s/1024;
            return s+" Mb";
        }
        else{
            return s+" Kb";
        }
    }
    public void delete(File filename){
        File fdelete = new File(filename.getPath());
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Toast.makeText(context, "deleted",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "NOT DELETED",Toast.LENGTH_SHORT).show();

            }
        }
    }
    public String processname(String filename){
        if(filename.length() > 30){

            char[] str = new char[30];
            int i = 0, j = 29,t=0;
            while( i <= j){
                str[i] = filename.charAt(i);
                str[j] = filename.charAt(filename.length()-1-t);
                j--;
                t++;
                i++;
            }
            for(int p = 17;p<=22;p++){
                str[p] = '.';
            }
            String string = String.valueOf(str);
            return string;
        }
        else{
            return filename;
        }

    }
}
