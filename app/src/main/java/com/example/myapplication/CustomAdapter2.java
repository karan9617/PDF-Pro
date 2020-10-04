package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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

import com.example.myapplication.Database.DBHandler;
import com.example.myapplication.Database.DBHandler2;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
// RECENT LIST ADAPTER



public class CustomAdapter2 extends ArrayAdapter<ContactsAll>{
    private final Context context;
    private final ArrayList<ContactsAll> array;
    TextView wordName,size;
    Switch s;
    ImageView dots;

    final DBHandler dbHandler;
    final DBHandler2 dbHandler2;

    public CustomAdapter2(Context context, ArrayList<ContactsAll> array2) {
        super(context, -1, array2);
        this.context = context;
        this.array = array2;
        dbHandler = new DBHandler(context);
        dbHandler2 = new DBHandler2(context);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.list_item_example, parent, false);
        final ContactsAll cn = array.get(position);
        final File file = new File(cn.getPath());
        wordName = (TextView) rowView.findViewById(R.id.filename);
        //pop menu
        dots = rowView.findViewById(R.id.dots);

        size = rowView.findViewById(R.id.size);
        size.setText("size: "+sizeCal(cn.getSize()));
        //ADDING THE ROW TO THE RECENT LIST
        final PopupMenu popup = new PopupMenu(context, dots);
        popup.inflate(R.menu.pop_upmenu_star);
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

                    case R.id.remove:
                        dbHandler2.deleteContact(file.getName(),file.getPath());
                        array.remove(cn);
                        notifyDataSetChanged();
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

        wordName.setText( processname(cn.getFilename()));


        //SENDING THE FILE THROUGH INTENT FOR DISPLAY
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Date object
                Date date= new Date();
                //getTime() returns current time in milliseconds
                long time = date.getTime();
                //Passed the milliseconds to constructor of Timestamp class
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
                    //dbHandler.deleteContact(cn.getFilename(), cn.getPath());
                    //dbHandler.addContact(new ContactsAllImg(SaveSharedPreference.getIntegerCount(context)+1,file.getName(), file.getPath(), cn.getSize(),ts+"",byteArray));
                    dbHandler.updateRowFromCustomAdapter3(cn,ts+"");
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
            Bitmap bitmap = Bitmap.createBitmap(width , height ,
                    Bitmap.Config.RGB_565);
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

    public String sizeCal(String size){
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


}