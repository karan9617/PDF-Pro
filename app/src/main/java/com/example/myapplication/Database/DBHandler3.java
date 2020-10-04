package com.example.myapplication.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.myapplication.Contacts;
import com.example.myapplication.ContactsAll;

import java.util.ArrayList;
import java.util.List;

public class DBHandler3 extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactsManager2112";
    private static final String TABLE_CONTACTS = "contacts2112";
    private static final String KEY_ID = "id";
    private static final String KEY_FILE_NAME = "name";
    private static final String KEY_FILE_PATH = "path";
    private static final String KEY_SIZE = "size";
    private static final String KEY_IMAGE = "image";

    public DBHandler3(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_FILE_NAME + " TEXT,"
                + KEY_FILE_PATH + " TEXT,"+ KEY_SIZE+" TEXT,"+KEY_IMAGE+" BLOB"+")";

        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    public void onUpgrade(SQLiteDatabase db,int j,int i) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    public void add(ContactsAll contact){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FILE_NAME, contact.getFilename()); // Contact title
        values.put(KEY_FILE_PATH, contact.getPath()); // Contact note
        values.put(KEY_SIZE, contact.getSize()); // Contact date

        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }
    // code to add the new contact
    public void addContact(ContactsAll contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,contact.getId());
        values.put(KEY_FILE_NAME, contact.getFilename()); // Contact title
        values.put(KEY_FILE_PATH, contact.getPath()); // Contact note
        values.put(KEY_SIZE, contact.getSize()); // Contact date

        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }


    // code to get all contacts in a list view
    public List<ContactsAll> getAllContacts() {
        List<ContactsAll> contactList = new ArrayList<ContactsAll>();
        // Select All Query
        String selectQuery = "select  * from " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ContactsAll contact = new ContactsAll();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setFilename(cursor.getString(1));
                contact.setPath(cursor.getString(2));
                contact.setSize(cursor.getString(3));

                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // code to update the single contact
    public int updateContact(ContactsAll contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FILE_NAME, contact.getFilename());
        values.put(KEY_FILE_PATH, contact.getPath());
        values.put(KEY_SIZE, contact.getSize());
        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?", new String[] { String.valueOf(contact.getId()) });
    }

    // Deleting single contact
    public void deleteContact(ContactsAll contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "DELETE FROM "+TABLE_CONTACTS+ " WHERE "+KEY_FILE_NAME+" = "+'"'+contact.getFilename()+'"';
        db.execSQL(selectQuery);

        db.close();
        /*db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();*/
    }
    public void deleteContact(String filename, String pathname) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "DELETE FROM "+TABLE_CONTACTS+ " WHERE "+KEY_FILE_NAME+" = "+'"'+filename+'"'+" AND "+KEY_FILE_PATH+" = "+'"'+pathname
                +'"';
        db.execSQL(selectQuery);
        db.close();
    }
    public void droptable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_CONTACTS);

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_FILE_NAME + " TEXT,"
                + KEY_FILE_PATH + " TEXT,"+ KEY_SIZE+" TEXT"+")";

        db.execSQL(CREATE_CONTACTS_TABLE);
    }


    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // functon to get all voice list

    public String getT(String wordName){
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS +" WHERE "+ KEY_FILE_NAME+" = "+'"'+wordName+'"';

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int x = cursor.getCount();
        if(x==0){
            return "1";
        }else {
            String s = cursor.getString(0)+":" + cursor.getString(1)+":" + cursor.getString(2)+":" + cursor.getString(3)+":" +
                    cursor.getString(4)+":"+cursor.getString(5);
            return s;
        }

    }
    public int ifexists(String filename, String path){
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS +" WHERE "+ KEY_FILE_NAME+" = "+'"'+filename+'"'+" AND "+KEY_FILE_PATH+" = "+'"'+
                path+'"';

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int x = cursor.getCount();
        if(x==0){
            return 0;
        }else {

            return 1;
        }
    }


}
