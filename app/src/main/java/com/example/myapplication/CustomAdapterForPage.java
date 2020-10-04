package com.example.myapplication;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.myapplication.Database.DBHandler;
import com.example.myapplication.Database.DBHandler2;
import com.example.myapplication.Database.DBHandler3;

import java.io.File;

import java.util.List;


public class CustomAdapterForPage extends ArrayAdapter<ContactsAll> implements BaseClassForPageInsert{


    private final Context context;
    private final List<ContactsAll> array;
    TextView wordName,size,time;
    Switch s;
    ImageView dots,pdfimage;
    final DBHandler2 dbHandler2;      //database for main list

    final DBHandler3 dbHandler3;
    final DBHandler dbHandler;

    public CustomAdapterForPage(Context context, List<ContactsAll> array2) {
        super(context, -1, array2);
        this.context = context;
        this.array = array2;
        dbHandler2  = new DBHandler2 (context);
        dbHandler = new DBHandler(context);
        dbHandler3 = new DBHandler3(context);
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


        wordName.setText( processname(cn.getFilename()));
        //SENDING THE FILE THROUGH INTENT FOR DISPLAY
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BaseClassForPageInsert.arrayList.add(file);

                Intent intent = new Intent(context, Main10Activity.class);
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
