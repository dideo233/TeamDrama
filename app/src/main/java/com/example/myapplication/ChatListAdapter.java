package com.example.myapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.ChatModel;
import com.example.myapplication.model.TvScheduleData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private List<TvScheduleData> tvScheduleDataList = new ArrayList<>();
    private List<String> keys = new ArrayList<>(); //방에 대한 키
    String broadcastStation; //방송사
    String scheduleDate; //방송일자

    public ChatListAdapter(String broadcastStation, String scheduleDate) {
        this.broadcastStation =broadcastStation;
        this.scheduleDate = scheduleDate;

        FirebaseDatabase.getInstance().getReference().child("broadcast").child(broadcastStation).child(scheduleDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvScheduleDataList.clear();

                for(DataSnapshot item : snapshot.getChildren()){
                    tvScheduleDataList.add(item.getValue(TvScheduleData.class));
                    keys.add(item.getKey()); //방에 대한 키
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mainchatlist, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TvScheduleData tvScheduleData = tvScheduleDataList.get(position);

        holder.programname.setText(tvScheduleData.getTitle());
        holder.programep.setText(tvScheduleData.getTime());
    }

    @Override
    public int getItemCount() {
        Log.d("size:::::", tvScheduleDataList.size()+"");
        return tvScheduleDataList ==null?0: tvScheduleDataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView programname, programep;
        ImageButton joinchat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            programname = itemView.findViewById(R.id.programname);
            programep = itemView.findViewById(R.id.programep);
            joinchat = itemView.findViewById(R.id.joinchat);
        }
    }
}
