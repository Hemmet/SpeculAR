package com.mate.specular;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mate.specular.model.Place;

import java.util.HashMap;
import java.util.Map;

public class AddPlace extends AppCompatActivity {
    Button AddPlace;
    EditText Name;
    DatabaseReference plc_push;

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        extras = getIntent().getExtras();
        plc_push = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/places");
        AddPlace = findViewById(R.id.addPlaceButton);
        Name = findViewById(R.id.add_plc_name);

        AddPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Place plc = new Place();
                plc.setName(Name.getText().toString());

                plc_push.push().setValue(plc);

                plc_push.getParent().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Map<String, Object> taskMap = new HashMap<>();
                        taskMap.put("size", snapshot.getValue(Place.class).getFrames().size()+1);
                        plc_push.getParent().updateChildren(taskMap);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddPlace.this);
                builder.setMessage("Place added")
                        .setTitle("Success !!")
                        .setPositiveButton(android.R.string.ok, null);
                android.app.AlertDialog dialog = builder.create();
                dialog.show();

                Intent changeToTaskActivity = new Intent(AddPlace.this, PlaceListActivity.class);
                changeToTaskActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                changeToTaskActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(changeToTaskActivity);
            }
        });
    }
}
