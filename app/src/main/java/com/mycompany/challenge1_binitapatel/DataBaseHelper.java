package com.mycompany.challenge1_binitapatel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Binita on 3/21/2019.
 */

public class DataBaseHelper {
    private final String dbName = "Photos";
    //create table
    private final String CREATE_PICS_TABLE = "CREATE TABLE" + this.dbName + " (" + "INTEGER PRIMARY KEY," + " image TEXT)";
    //drop older stuff
    private final String DROP_PICS_TABLE = "DROP TABLE IF EXISTS " + this.dbName;
    //constructor
    private SQLiteDatabase db;
    public DataBaseHelper(){}
    private void onCreate(){
        try{
            this.db.execSQL(this.DROP_PICS_TABLE);
            this.db.execSQL(this.CREATE_PICS_TABLE);
        }
        catch (SQLiteException e){
            System.out.println("Unable to create table");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    //Add new images
    public void addPic(String base64Img) throws SQLiteException{
        String add = "INSERT INTO " + this.dbName + "(image) VALUES ('"+ base64Img+"')";
        db.execSQL(add);
    }
    private Context context;
    public DataBaseHelper(Context context){
        this.context = context;
        this.db = context.openOrCreateDatabase(this.dbName, Context.MODE_PRIVATE,null);
        onCreate();
    }
    //collection of pics
    public String takePic(String ID) throws SQLiteException{
        String select = "SELECT image FROM " + this.dbName+ " WHERE ID="+ID;
        Cursor cursor = db.rawQuery(select, null);
        String img = "ERROR";
        if(cursor.moveToFirst()){
            do{
                img = cursor.getString(cursor.getColumnIndex("Image"));
            }while(cursor.moveToNext());
            cursor.close();
        }
        return img;
    }
    //Exist the database
    private boolean existData(){
        SQLiteDatabase check = null;
        try{
            check = SQLiteDatabase.openDatabase(this.dbName, null, SQLiteDatabase.OPEN_READONLY);
            check.close();
        }
        catch (SQLiteException e){

        }
        return check != null;
    }
    //Encode into a base64
    public String enBase64(Bitmap bitmap){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        byte[] bit = os.toByteArray();
        String image = Base64.encodeToString(bit, Base64.DEFAULT);
        return image;
    }
    //Decode into a Base64
    public Bitmap deBase(String image){
        byte[] data = Base64.decode(image, Base64.DEFAULT);
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inMutable = true;
        Bitmap bit = BitmapFactory.decodeByteArray(data, 0 , data.length, op);
        return bit;
    }
}
