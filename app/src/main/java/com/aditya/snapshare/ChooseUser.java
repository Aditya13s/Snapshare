package com.aditya.snapshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChooseUser extends AppCompatActivity {
    ListView userList;
    ArrayList<String> emailList;
    ArrayList<String> uidList;
    ArrayAdapter arrayAdapter;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        userList = findViewById(R.id.userList);
        emailList = new ArrayList<>();
        uidList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, emailList);
        userList.setAdapter(arrayAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                emailList.clear();
                uidList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    String email = postSnapshot.child("Email").getValue().toString();
                    if(!email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        String uid = postSnapshot.getKey();
                        uidList.add(uid);
                        emailList.add(email);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = getIntent();
                String url = intent.getExtras().getString("imageUrl");
                String name = intent.getExtras().getString("imageName");
                String message = intent.getExtras().getString("message");
                String from = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                HashMap<String,String> map = new HashMap<>();
                map.put("url",url);
                map.put("imageName", name);
                map.put("message", message);
                map.put("from",from);

                //The push is used to generate a random key (child of snaps)
                FirebaseDatabase.getInstance().getReference().child("user").child(uidList.get(i)).child("snaps").push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ChooseUser.this, "Snap successfully send", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}