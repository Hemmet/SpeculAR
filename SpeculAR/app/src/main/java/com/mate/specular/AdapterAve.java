package com.mate.specular;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;


public class AdapterAve extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<AvenueData> data= Collections.emptyList();
    private AvenueHolder aveHolder;
    private String url;
    private DatabaseReference ave_del;

    public AdapterAve(Context context, List<AvenueData> data, String url) {
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.data=data;
        this.url = url;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.avenue_elem, parent,false);
        aveHolder=new AvenueHolder(view);
        return aveHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        aveHolder = (AvenueHolder) holder;
        AvenueData current=data.get(position);
        aveHolder.aveName.setText(current.getName());
        aveHolder.owner.setText(current.getOwner_name());
        aveHolder.size.setText(current.getSize()+"");
        aveHolder.type.setText(current.getType());
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    class AvenueHolder extends RecyclerView.ViewHolder{
        TextView aveName;
        TextView owner;
        TextView size;
        TextView type;
        Button delete;

            AvenueHolder(View itemView) {
            super(itemView);
            aveName= itemView.findViewById(R.id.ave_name);
            owner = itemView.findViewById(R.id.owner_name);
            size = itemView.findViewById(R.id.size);
            type = itemView.findViewById(R.id.type);
            delete = itemView.findViewById(R.id.ave_del);

            aveName.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    String name = data.get(getAdapterPosition()).getName();
                    FirebaseDatabase.getInstance().getReference(url).orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                                Intent changeToTaskActivity = new Intent(context, ListPlace.class);
                                changeToTaskActivity.putExtra("ave_key", childSnapshot.getKey());
                                context.startActivity(changeToTaskActivity);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   String name = data.get(getAdapterPosition()).getName();
                   ave_del = FirebaseDatabase.getInstance().getReference(url);
                   delete(name);

                }
            });
        }
        void delete( String s) {
            ave_del.orderByChild("name").equalTo(s).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        ave_del.child(childSnapshot.getKey()).removeValue();
                        Intent changeToTaskActivity = new Intent(context, ListAvenue.class);
                        context.startActivity(changeToTaskActivity);
                    }
                }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
    }
}
