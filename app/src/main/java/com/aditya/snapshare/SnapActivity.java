package com.aditya.snapshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class SnapActivity extends AppCompatActivity {
    ImageView imageView;
    EditText message;
    Button openGallery, selectUser;
    public static final int PICK_IMAGE = 1;
    Uri imageUri;
    StorageReference mStorage;
    StorageTask mUploadTask;
    UUID imageName;
    String downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap);

        imageView = findViewById(R.id.imageView);
        openGallery = findViewById(R.id.buttonOpenGallery);
        message = findViewById(R.id.messageEditText);
        selectUser = findViewById(R.id.buttonSelectUser);


        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        selectUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SnapActivity.this,ChooseUser.class);
                intent.putExtra("imageName", imageName);
                intent.putExtra("imageUrl",downloadUrl);
                intent.putExtra("message", message.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            uploadImage();
        }
    }

    private void uploadImage() {
        if(imageUri != null) {
            imageName = UUID.randomUUID();
            mStorage = FirebaseStorage.getInstance().getReference().child("image").child(imageName + ".jpg");
            mUploadTask = mStorage.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(SnapActivity.this, "Image Uploaded Successfully " + taskSnapshot.getBytesTransferred(), Toast.LENGTH_SHORT).show();
                    mStorage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            downloadUrl = task.getResult().toString();
                            Log.i("download",downloadUrl);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SnapActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}