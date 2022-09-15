package com.aditya.snapshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class SnapsDetailActivity extends AppCompatActivity {
    ImageView snap;
    TextView snapMessage;
    ProgressBar progressBar;
    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaps_detail);

        snap = findViewById(R.id.snap);
        snapMessage = findViewById(R.id.snapMessage);
        progressBar = findViewById(R.id.progressBar);
        Intent intent = getIntent();
        String imageUrl = intent.getExtras().getString("url");
        String message = intent.getExtras().getString("message");
        key = intent.getExtras().getString("key");
        snapMessage.setText(message);

        Picasso.get().load(imageUrl).into(snap, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(SnapsDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseDatabase.getInstance().getReference()
                .child("user")
                .child(FirebaseAuth.getInstance().getUid())
                .child("snaps")
                .child(key)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(SnapsDetailActivity.this, "Snap deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}