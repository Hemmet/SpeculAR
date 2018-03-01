package com.mate.specular;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mate.specular.model.Place;

import java.util.ArrayList;
import java.util.List;

public class PlaceListActivity extends AppCompatActivity {

    private RecyclerView placeView;
    private PlaceAdapter placeAdapter;
    private DatabaseReference placeList;
    private String userID;
    private Button addPlaceButton, logoutButton;
    Bundle extras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        placeList = FirebaseDatabase.getInstance().getReference("users/");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        placeView = findViewById(R.id.placeListView);

        extras = getIntent().getExtras();

        placeList.child(userID + "/places").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Place> data=new ArrayList<>();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Place plc_inf = new Place();
                    plc_inf.setName(ds.getValue(Place.class).getName());
                    data.add(plc_inf);
                }
                placeAdapter = new PlaceAdapter(PlaceListActivity.this, data, "users/" + userID + "/places");
                placeView.setAdapter(placeAdapter);
                placeView.setLayoutManager(new LinearLayoutManager(PlaceListActivity.this));
            }

            @Override
            public void onCancelled(DatabaseError de) {

            }
        });


        addPlaceButton = findViewById(R.id.addPlaceButton);
        addPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaceListActivity.this, AddPlace.class);
                startActivity(intent);
            }
        });

        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(PlaceListActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.place_list_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addNewContent:
                startActivity(new Intent(PlaceListActivity.this, NewContentCameraActivity.class));
                break;
            default:
                break;
        }

        return true;
    }
}
