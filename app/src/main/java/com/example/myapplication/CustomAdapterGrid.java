package com.example.myapplication;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;


import com.example.myapplication.Database.DBHandler;
import com.example.myapplication.Database.DBHandler2;
import com.example.myapplication.Database.DBHandler3;

import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;


// STAR LIST ADAPTER
public class CustomAdapterGrid extends ArrayAdapter<ContactsAllImg>{
    private final Context context;
    private final ArrayList<ContactsAllImg> array;
    TextView wordName,size,time;

    ImageView dots,pdfimage;

    // DATA
    final DBHandler3 dbHandler3;      //database for main list

    final DBHandler2 dbHandler2;      //database for main list
    final DBHandler dbHandler;      //database for main list
    WebView wv;
    public CustomAdapterGrid(Context context, ArrayList<ContactsAllImg> array2) {
        super(context, -1, array2);
        this.context = context;
        this.array = array2;
        dbHandler3  = new DBHandler3 (context);

        dbHandler2  = new DBHandler2 (context);
        dbHandler  = new DBHandler (context);
    }
    public AlertDialog.Builder cancelImageDialog( Context c, final ContactsAllImg cn1,File title){
        final File filename = title;
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("PDF Pro");
        builder.setMessage("Do you want to remove the pdf from recent. Note: The pdf will not be permanently deleted from the device..?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                array.remove(cn1);
                dbHandler.deleteContact(filename.getName(), filename.getPath());
                notifyDataSetChanged();
                Toast.makeText(context, "Removed from the Recent list.",Toast.LENGTH_SHORT).show();
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.grid_item_example, parent, false);
        final ContactsAllImg  cn = array.get(position);
        final File file = new File(cn.getPath());
        wordName = rowView.findViewById(R.id.filename);
        time = rowView.findViewById(R.id.time);
        size = rowView.findViewById(R.id.size);
        dots = rowView.findViewById(R.id.dots);
        pdfimage = rowView.findViewById(R.id.pdfimage);
        byte[] arr = dbHandler.getByteArray(cn.getFilename(), cn.getPath());

        if(arr != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length);
            pdfimage.setImageBitmap(bitmap);
        }

        // we have to pass the context of the getActivity() for it to work (important)
        final PopupMenu popup = new PopupMenu(context, dots);
        popup.inflate(R.menu.pop_menu_home);

        // FOR GETTING THE IMAGE FROM THE FILE CLASS  ( !!!!  IMPORTANT !!!!!!)
        /*Bitmap bitmap = getBitmap(file);
        pdfimage.setImageBitmap(bitmap);
*/
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


                        Toast.makeText(context, "Starred",Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.remove:
                        cancelImageDialog(context,cn,file).show();
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

        size.setText(size(cn.getSize())+"  .");

        time.setText(calculatetime(cn.getTime())+"");

        //SENDING THE FILE THROUGH INTENT FOR DISPLAY
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date= new Date();
                long time = date.getTime();
                Timestamp ts = new Timestamp(time);


                if(dbHandler.ifFileExists(cn.getFilename(), cn.getPath()) == 1){
                    byte[] byteArray = dbHandler.getByteArray(cn.getFilename(), cn.getPath());
                   // dbHandler.deleteContact(cn.getFilename(), cn.getPath());
                    dbHandler.updateRow(cn,ts+"");
                    array.remove(cn);

                   // dbHandler.addContact(new ContactsAllImg(SaveSharedPreference.getIntegerCount(context)+1,file.getName(), file.getPath(), cn.getSize(),ts+"",byteArray));

                    array.add(cn);
                    notifyDataSetChanged();
                }
                else{
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
                    dbHandler.addContact(new ContactsAllImg(SaveSharedPreference.getIntegerCount(context)+1,file.getName(), file.getPath(),
                            cn.getSize(),ts+"",byteArray));
                    notifyDataSetChanged();
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
    public String calculatetime(String timepara){
        String finaltime = "";
        Date date= new Date();
        //getTime() returns current time in milliseconds
        long time = date.getTime();
        //Passed the milliseconds to constructor of Timestamp class
        Timestamp ts = new Timestamp(time);

        String current = ts+"";
        String[] splitcurrent = current.split(" ");
        String[] timesplit = timepara.split(" ");

        if(splitcurrent[0].split("-")[2].equals(timesplit[0].split("-")[2])){
            if(Integer.parseInt(timesplit[1].split(":")[0]) > 12){
                int x = Integer.parseInt(timesplit[1].split(":")[0])-12;
                return x+":"+timesplit[1].split(":")[1]+" PM";
            }
            else{
                return timesplit[1].split(":")[0]+":"+timesplit[1].split(":")[1]+" AM";
            }
        }
        else if(Math.abs(Integer.parseInt(splitcurrent[0].split("-")[2]) - Integer.parseInt(timesplit[0].split("-")[2])) == 1){
            return "Yesterday";
        }
        else{
            String[] actualdate = timesplit[0].split("-");
            String ret = actualdate[0]+" ";
            HashMap<String, String> months = new HashMap<>();
            months.put("01","January"); months.put("02","January"); months.put("03","January"); months.put("04","January");
            months.put("05","January"); months.put("06","January"); months.put("07","January"); months.put("08","January");
            months.put("09","January"); months.put("10","January"); months.put("11","January"); months.put("12","January");

            ret =ret + months.get(actualdate[1])+" ";
            return ret + actualdate[2];
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
