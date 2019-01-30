package com.example.tathagat.arduino4;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;

import static android.support.constraint.Constraints.TAG;

public class MainActivity extends AppCompatActivity {
    ProgressDialog m;
    private  int STORAGE_PERMISSION_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stopService(new Intent(this,MyService.class));

        ToggleButton tb = (ToggleButton) findViewById(R.id.toggleButton2);

        startService(new Intent(this,MyService.class));

        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                   ischecked();
                } else {
                    // The toggle is disabled
                    isnotchecked();
                }
            }
        });

            requestpermission();
            switch1();

        ToggleButton tb2 = (ToggleButton) findViewById(R.id.tb2);
        tb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    ischecked2();
                } else {
                    // The toggle is disabled
                    isnotchecked2();
                }
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ping");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                //  Log.d(TAG, "Value is: " + value);
                  ping(value);
            }//  @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        }
    public void createNotification(){

        Intent intent = new Intent(this, MainActivity.class);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("New face Detected")
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

    }
    public  void ischecked(){
        Toast.makeText(this,"alert mode on",Toast.LENGTH_LONG).show();

        LinearLayout ll=(LinearLayout) findViewById(R.id.tt);
        ll.setBackgroundColor(Color.RED);
    }
    public  void isnotchecked(){

        Toast.makeText(this,"alert mode off",Toast.LENGTH_LONG).show();
        LinearLayout ll=(LinearLayout) findViewById(R.id.tt);
        ll.setBackgroundColor(Color.rgb(205,236,230));

    }
public void ping(String x){
 if(Integer.parseInt(x)==1){
     imageshow();

     createNotification();

     FirebaseDatabase database1 = FirebaseDatabase.getInstance();
     DatabaseReference myRef1 = database1.getReference("ping");
     myRef1.setValue("0");
 }


}
public  void imageshow(){

    final ImageView imageView=(ImageView)  findViewById(R.id.imageView);
    StorageReference mStorageRef;
    mStorageRef = FirebaseStorage.getInstance().getReference();
    showToast("Downloading.....");
    m=new ProgressDialog(MainActivity.this);
    m.setTitle("Downloading....");

    m.setMessage("please wait...");
    m.show();


    mStorageRef.child("images/intruder.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {


        @Override
        public void onSuccess(Uri uri) {
           // progressbar.setIndeterminate(true);


            Picasso.get().load(uri).into(imageView);
           // progressbar.setIndeterminate(false);
            m.dismiss();

        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d(TAG, "Value is: error");

        }
    });


}
public void showToast(String x){
        Toast.makeText(this,x,Toast.LENGTH_LONG).show();
    }

    public void saveimg(View view) {
        ImageView iv =  findViewById(R.id.imageView);
        BitmapDrawable draw = (BitmapDrawable) iv.getDrawable();
        Bitmap bitmap = draw.getBitmap();

        FileOutputStream outStream = null;
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() );
        dir.mkdirs();
        String fileName = String.format("%d.jpg", System.currentTimeMillis());
        File outFile = new File(dir, fileName);
        try {
            outStream = new FileOutputStream(outFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            showToast("Image saved");
        } catch (Exception e){
            showToast("error"+e);
        }


    }
    public  void requestpermission(){

        try {
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
            {
              ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void switch1(){
        final Intent intent = new Intent(this, Main2Activity.class);

        final Switch sw = (Switch) findViewById(R.id.sw);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                showToast((String) sw.getTextOn());



                    startActivity(intent);
                    sw.setChecked(false);
                }
            else{
                    showToast((String) sw.getTextOff());

                }}
        });
        sw.setChecked(false);

    }
    protected void onDestroy() {
        super.onDestroy();
        startService(new Intent(this,MyService.class));
    }

    public  void mode(View v){
        ToggleButton tb = findViewById(R.id.toggleButton2);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("mode");
        if(tb.isChecked()){
            myRef.setValue("1");
        }
        else{
            myRef.setValue("0");
        }
    }
    public void alarm(View v){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("alarm");
        myRef.setValue("1");

    }
    public void ischecked2(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Door");
        myRef.setValue("1");
    }
    public void isnotchecked2(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Door");
        myRef.setValue("0");
    }

}
