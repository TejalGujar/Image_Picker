package com.example.imagepicker;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;

public class SelectUploadImageActivity extends AppCompatActivity
{

    Button btnSelect, btnUpload;
    ImageView imageShow;
    private Uri filePath;

    int PICK_IMAGE_REQUEST = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_image_activity_layout);

        //get runtime permission from user
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        btnSelect = findViewById(R.id.btnSelectImage);
        btnUpload = findViewById(R.id.btnUploadImage);

        imageShow = findViewById(R.id.imgShow);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }
        private void selectImage(){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select image from here..."),PICK_IMAGE_REQUEST);
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
