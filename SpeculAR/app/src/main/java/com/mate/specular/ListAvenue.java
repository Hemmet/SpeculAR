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

public class ListAvenue extends AppCompatActivity {

    private RecyclerView avenueView;
    private AdapterAve avenueAdapter;
    private DatabaseReference ave_list;
    private String userID;
    private Button AddAve, Login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_avenue);

        ave_list = FirebaseDatabase.getInstance().getReference("users/");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        ave_list.child(userID+"/avenues").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<AvenueData> data=new ArrayList<>();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                   AvenueData ave_inf = new AvenueData();

                   ave_inf.setName(ds.getValue(AvenueData.class).getName());
                   ave_inf.setOwner_name(ds.getValue(AvenueData.class).getOwner_name());
                   ave_inf.setSize(ds.getValue(AvenueData.class).getSize());
                   ave_inf.setType(ds.getValue(AvenueData.class).getType());

                   data.add(ave_inf);
                }
                avenueView = findViewById(R.id.ave_list);
                avenueAdapter = new AdapterAve(ListAvenue.this, data, "users/" + userID + "/avenues");
                avenueView.setAdapter(avenueAdapter);
                avenueView.setLayoutManager(new LinearLayoutManager(ListAvenue.this));
            }

            @Override
            public void onCancelled(DatabaseError de) {

            }
        });

        AddAve = findViewById(R.id.ave_add);
        AddAve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListAvenue.this, AddAvenue.class);
                startActivity(intent);
            }
        });

        Login = findViewById(R.id.logout);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ListAvenue.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
