package com.aditya.snapshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    ListView snapListView;
    ArrayList<String> snapsList;
    ArrayList<DataSnapshot> snapShotList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        snapListView = findViewById(R.id.snapListView);
        snapsList = new ArrayList<>();
        snapShotList = new ArrayList<>();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,snapsList);
        snapListView.setAdapter(arrayAdapter);
        FirebaseDatabase.getInstance().getReference()
                .child("user")
                .child(FirebaseAuth.getInstance().getUid())
                .child("snaps").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapsList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String email = postSnapshot.child("from").getValue().toString();
                    snapsList.add(email);
                    snapShotList.add(postSnapshot);
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        snapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DataSnapshot snapshot = snapShotList.get(i);
                Intent intent = new Intent(HomeActivity.this,SnapsDetailActivity.class);
                intent.putExtra("url",snapshot.child("url").getValue().toString());
                intent.putExtra("message",snapshot.child("message").getValue().toString());
                intent.putExtra("key",snapshot.getKey());
                Toast.makeText(HomeActivity.this, snapshot.getKey(), Toast.LENGTH_SHORT).show();
                startActivity(intent);

            }
        });
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.snap_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.takeSnap:
                Toast.makeText(this, "Take a Snap", Toast.LENGTH_SHORT).show();
                intent = new Intent(HomeActivity.this, SnapActivity.class);
                startActivity(intent);
                    break;
            case R.id.logout:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                finish();
                    break;
        }
        return super.onOptionsItemSelected(item);
    }


}