package com.example.imagepicker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class SelectUploadImageActivity extends AppCompatActivity
{

    Button btnSelect, btnUpload;
    ImageView imageShow;
    private Uri filePath;

    int PICK_IMAGE_REQUEST = 10;

    FirebaseStorage mStorage;
    StorageReference rootReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_image_activity_layout);

        //get runtime permission from user
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        //storage initialization
        mStorage = FirebaseStorage.getInstance();
        rootReference = mStorage.getReference();

        btnSelect = findViewById(R.id.btnSelectImage);
        btnUpload = findViewById(R.id.btnUploadImage);

        imageShow = findViewById(R.id.imgShow);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

        private void selectImage(){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select image from here..."),PICK_IMAGE_REQUEST);
        }

        private void uploadImage()
        {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            StorageReference fileRef = rootReference.child(UUID.randomUUID().toString() + ".jpg");

            fileRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();

                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("DOWNLOAD URL",uri.toString());
                        }
                    });

                    Toast.makeText(SelectUploadImageActivity.this,"Upload Successfully...",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(SelectUploadImageActivity.this,"Uploading Fail...",Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded" + " " +(int)progress + "%");
                }
            });
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
        {
            super.onActivityResult(requestCode,resultCode,data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
            {
                //get the Uri of data
                filePath = data.getData();
                try {
                    //setting image on image view using Bitmap
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                    imageShow.setImageBitmap(bitmap);
                }
                catch (IOException e){
                    //log the exception
                    e.printStackTrace();
                }
            }
        }
}
