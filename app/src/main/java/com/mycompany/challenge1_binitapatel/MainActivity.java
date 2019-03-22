package com.mycompany.challenge1_binitapatel;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Build;
import android.preference.DialogPreference;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.text.NumberFormat;
import java.text.ParsePosition;

public class MainActivity extends AppCompatActivity
{
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    //Define the button, imageview and editview
    ImageView imageView;
    ImageButton picBtn;
    EditText searchText;
    Button searchBtn;
    DataBaseHelper db;
    public void display(){
        this.db = new DataBaseHelper(getApplicationContext());
        this.searchBtn = (Button)findViewById(R.id.searchBtn);
        this.searchText = (EditText)findViewById(R.id.searchText);
        this.imageView = (ImageView)findViewById(R.id.imageView);
        this.picBtn = (ImageButton)findViewById(R.id.picBtn);
    }
    //permission to camera
    private static final int REQ_CAM_PERMISSION = 1;
    private static final int REQ_CODE_TAKE_PIC = 1;
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void picNotFind(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Does not exists").setMessage("Picture Cannot find").setNeutralButton(" ",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void invalidID(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Does not exists").setMessage("Picture ID unvalid").setNeutralButton(" ",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void camPermission(View view){
        if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
            alertToCamPermission(view);
        else
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_CAM_PERMISSION);
    }

    public void alertToCamPermission(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help").setMessage("Need permission for camera").setPositiveButton("Sucess",new DialogInterface.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Activity parent = getParent();
                parent.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_CAM_PERMISSION);
            }
        }).setNegativeButton("Unsucess", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Activity act = getParent();
                if(act != null)
                    act.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Take picture
    public void camOpen(){
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePic.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePic, REQ_CODE_TAKE_PIC);
        }
    }
    //when click on take picture icon
    public void takePicture(){
        camOpen();
    }
    public boolean isNumeric(String str){
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition os = new ParsePosition(0);
        formatter.parse(str,os);
        return str.length()==os.getIndex();
    }
    //when click on search button
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void searchPic(View view){
        String choice = this.searchText.getText().toString();
        if(isNumeric(choice)){
            try{
                String image = db.takePic(choice);
                if(image.equalsIgnoreCase("ERROR"))
                    picNotFind(view);
                else{
                    Bitmap bit = db.deBase(image);
                    this.imageView.setImageBitmap(bit);
                }

            }catch (SQLiteException e){
                System.out.println("Enable to read");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        else{
            invalidID(view);
        }
    }
    //output
    protected void onActivityResult(int reqCode, int resultCode, Intent intent){
        if(resultCode == RESULT_OK){
            try{
                Bitmap bit = (Bitmap)intent.getExtras().get("data");
                this.imageView.setImageBitmap(bit);
                db.addPic(db.enBase64(bit));

            }catch(SQLiteException e){
                System.out.println("Enable to insert");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}