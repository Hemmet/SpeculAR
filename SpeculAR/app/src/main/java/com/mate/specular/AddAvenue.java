package com.mate.specular;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddAvenue extends AppCompatActivity {
    Button AddAvenue;
    EditText Name, Owner, Type;
    DatabaseReference ave_push;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_avenue);

        ave_push = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/avenues");

        AddAvenue = findViewById(R.id.add_ave);

        Name = findViewById(R.id.add_ave_name);
        Owner = findViewById(R.id.add_owner_name);
        Type = findViewById(R.id.add_type);

        AddAvenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AvenueData ave = new AvenueData();
                ave.setName(Name.getText().toString());
                ave.setOwner_name(Owner.getText().toString());
                ave.setType(Type.getText().toString());
                ave.setSize(0);

                ave_push.push().setValue(ave);
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddAvenue.this);
                builder.setMessage("Avenue added")
                        .setTitle("Success !!")
                        .setPositiveButton(android.R.string.ok, null);
                android.app.AlertDialog dialog = builder.create();
                dialog.show();

                Intent changeToTaskActivity = new Intent(AddAvenue.this, ListAvenue.class);
                startActivity(changeToTaskActivity);
            }
        });
    }
}
