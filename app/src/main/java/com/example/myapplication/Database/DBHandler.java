package com.example.myapplication.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.Contacts;
import com.example.myapplication.ContactsAll;
import com.example.myapplication.ContactsAllImg;

import java.util.ArrayList;
import java.util.List;

// MAIN DATABASE FOR THE recent WORD LIST
//
//
public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactsManager245";
    private static final String TABLE_CONTACTS = "contacts245";
    private static final String KEY_ID = "id";
    private static final String KEY_FILE_NAME = "name";
    private static final String KEY_FILE_PATH = "path";
    private static final String KEY_SIZE = "size";
    private static final String KEY_TIMESTAMP = "time";
    private static final String KEY_IMAGE = "image";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_FILE_NAME + " TEXT,"
                + KEY_FILE_PATH + " TEXT,"+ KEY_SIZE+" TEXT,"+KEY_TIMESTAMP+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+KEY_IMAGE+" BLOB"+")";

        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    public void onUpgrade(SQLiteDatabase db,int j,int i) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    public void addContact(ContactsAllImg contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_ID,contact.getId());
        values.put(KEY_FILE_NAME, contact.getFilename()); // Contact title
        values.put(KEY_FILE_PATH, contact.getPath()); // Contact note
        values.put(KEY_SIZE, contact.getSize()); // Contact date
        values.put(KEY_TIMESTAMP, contact.getTime()); // Contact date
        values.put(KEY_IMAGE, contact.getImg()); // Contact date

        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }
//CALLED FROM GRIDLAYOUT AND LISTLAYOUT
    public void updateRow(ContactsAllImg cn, String time){
        String filename = cn.getFilename();
        String path = cn.getPath();

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "UPDATE "+TABLE_CONTACTS +" SET " + KEY_TIMESTAMP+ " = "+'"'+time+'"'+ " WHERE "+KEY_FILE_PATH+ " = "+'"'+path+'"'+" AND "+KEY_FILE_NAME +" = "+
                '"' + filename+'"';
        db.execSQL(selectQuery);
        db.close();

    }
//CALLED FROM CUSTOMADAPTER3 LAYOUT

    public void updateRowFromCustomAdapter3(ContactsAll cn, String time){
        String filename = cn.getFilename();
        String path = cn.getPath();

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "UPDATE "+TABLE_CONTACTS +" SET " + KEY_TIMESTAMP+ " = "+'"'+time+'"'+ " WHERE "+KEY_FILE_PATH+ " = "+'"'+path+'"'+" AND "+KEY_FILE_NAME +" = "+
                '"' + filename+'"';
        db.execSQL(selectQuery);
        db.close();

    }
    // code to get the single contact
    Contacts getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_ID,
                        KEY_FILE_NAME, KEY_FILE_PATH,KEY_SIZE,KEY_TIMESTAMP}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Contacts contact = new Contacts(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        return contact;
    }
    // code to get all contacts in a list view
    public List<ContactsAllImg> getAllContacts() {
        List<ContactsAllImg> contactList = new ArrayList<ContactsAllImg>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS+" ORDER BY "+KEY_TIMESTAMP +" DESC LIMIT 15";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ContactsAllImg contact = new ContactsAllImg();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setFilename(cursor.getString(1));
                contact.setPath(cursor.getString(2));
                contact.setSize(cursor.getString(3));
                contact.setTime(cursor.getString(4));
                contact.setImg(cursor.getBlob(5));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    public int ifFileExists(String filename, String path){
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS +" WHERE "+ KEY_FILE_NAME+" = "+'"'+filename+'"'+" AND "+KEY_FILE_PATH+" = "+'"'
                +path+'"';
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int x = cursor.getCount();
        if(x==1){
            return 1;
        }
        else{
            return 0;
        }
    }
    // code to update the single contact
    public int updateContact(ContactsAllImg contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FILE_NAME, contact.getFilename());
        values.put(KEY_FILE_PATH, contact.getPath());
        values.put(KEY_SIZE, contact.getSize());

        values.put(KEY_TIMESTAMP," time('now') ");
        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?", new String[] { String.valueOf(contact.getId()) });
    }
    public void deleteFirstRow()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, null, null, null, null, null, null);

        if(cursor.moveToFirst()) {
            String rowId = cursor.getString(cursor.getColumnIndex(KEY_ID));

            db.delete(TABLE_CONTACTS, KEY_ID + "=?",  new String[]{rowId});
        }
        db.close();
    }
    /* Deleting single contact
    public void deleteContact(Contacts contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "DELETE FROM "+TABLE_CONTACTS+ " WHERE "+KEY_FILE_NAME+" = "+'"'+contact.getFilename()+'"';
        db.execSQL(selectQuery);

        db.close();

    }*/
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
                + KEY_FILE_PATH + " TEXT,"+ KEY_SIZE+" TEXT,"+KEY_TIMESTAMP+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+KEY_IMAGE+" BLOB"+")";

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
    public byte[] getByteArray(String filename, String path){
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS +" WHERE "+ KEY_FILE_NAME+" = "+'"'+filename+'"'+ " AND "+KEY_FILE_PATH+" = "
                +'"' + path+'"';
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        return cursor.getBlob(5);

    }


}
