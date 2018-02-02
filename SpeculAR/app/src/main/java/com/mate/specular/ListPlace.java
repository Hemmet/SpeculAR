package com.mate.specular;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListPlace extends AppCompatActivity {

    private RecyclerView placeView;
    private AdapterPlc placeAdapter;
    private DatabaseReference plc_list;
    private String userID;
    private Button AddPlc;
    Bundle extras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_place);

        plc_list = FirebaseDatabase.getInstance().getReference("users/");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        extras = getIntent().getExtras();

        plc_list.child(userID+"/avenues/" + extras.getString("ave_key")+ "/places").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<PlaceData> data=new ArrayList<>();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    PlaceData plc_inf = new PlaceData();

                    plc_inf.setName(ds.getValue(AvenueData.class).getName());

                    data.add(plc_inf);
                }
                placeView = findViewById(R.id.plc_list);
                placeAdapter = new AdapterPlc(ListPlace.this, data, "users/"+ userID+"/avenues/" + extras.getString("ave_key")+ "/places", extras.getString("ave_key") );
                placeView.setAdapter(placeAdapter);
                placeView.setLayoutManager(new LinearLayoutManager(ListPlace.this));
            }

            @Override
            public void onCancelled(DatabaseError de) {

            }
        });

        AddPlc = findViewById(R.id.add_plc);
        AddPlc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListPlace.this, AddPlace.class);
                intent.putExtra("ave_key",extras.getString("ave_key"));
                startActivity(intent);
            }
        });


    }
}
