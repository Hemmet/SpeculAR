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
import com.mate.specular.model.Place;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Place> data= Collections.emptyList();
    private PlaceHolder plcHolder;
    private String url;
    private DatabaseReference plc_del;

    public PlaceAdapter(Context context, List<Place> data, String url) {
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.data=data;
        this.url = url;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.place_elem, parent,false);
        plcHolder=new PlaceHolder(view);
        return plcHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        plcHolder = (PlaceHolder) holder;
        Place current=data.get(position);
        plcHolder.plcName.setText(current.getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class PlaceHolder extends RecyclerView.ViewHolder{
        TextView plcName;
        Button delete;

        void decrement(){
            plc_del.getParent().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Map<String, Object> taskMap = new HashMap<>();
                    //taskMap.put("size", snapshot.getValue(Place.class).getSize() - 1);
                    plc_del.getParent().updateChildren(taskMap);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        PlaceHolder(View itemView) {
            super(itemView);
            plcName = itemView.findViewById(R.id.plc_name);
            delete = itemView.findViewById(R.id.plc_del);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = data.get(getAdapterPosition()).getName();
                    plc_del = FirebaseDatabase.getInstance().getReference(url);

                    plc_del.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                plc_del.child(childSnapshot.getKey()).removeValue();
                                Intent changeToTaskActivity = new Intent(context, PlaceListActivity.class);
                                changeToTaskActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                changeToTaskActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(changeToTaskActivity);
                            }
                            decrement();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }
}
