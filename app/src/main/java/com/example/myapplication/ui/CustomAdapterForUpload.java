package com.example.myapplication.ui;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Main3Activity;
import com.example.myapplication.R;
import java.io.File;
import java.util.List;
public class CustomAdapterForUpload extends ArrayAdapter<File>{


    private final Context context;
    private final List<File> array;
    TextView wordName,size;
    Switch s;
    ImageView cancel;
    CustomAdapterForUpload customAdapterForUpload;

    public CustomAdapterForUpload(Context context, List<File> array2) {
        super(context, -1, array2);
        this.context = context;
        this.array = array2;
    }
    public CustomAdapterForUpload getObject(){
        return customAdapterForUpload;
    }
    public void setObject(CustomAdapterForUpload customAdapterForUpload){
        this.customAdapterForUpload = customAdapterForUpload;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.list_item_for_upload, parent, false);
        final File cn = array.get(position);
        final File file = new File(cn.getPath());
        cancel = rowView.findViewById(R.id.cancel);

        wordName =  rowView.findViewById(R.id.filename);
        //pop menu
        size =  rowView.findViewById(R.id.size);
        double filesize = (double)(cn.length()/(1024));
        size.setText("size: "+filesize+"KB");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                array.remove(cn);
                notifyDataSetChanged();
            }
        });
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = file.getName();
                if(name.contains("text")){
                    Intent intent = new Intent(context, Main3Activity.class);
                    try {
                        intent.putExtra("file", file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    context.startActivity(intent);
                }
            }
        });
        wordName.setText( processname(cn.getName()));
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
