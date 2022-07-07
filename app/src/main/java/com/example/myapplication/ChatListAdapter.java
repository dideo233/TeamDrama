package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.TvScheduleData;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    List<TvScheduleData> tvScheduleData;

    public ChatListAdapter(List<TvScheduleData> tvScheduleData) {
        this.tvScheduleData = tvScheduleData;
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
        TvScheduleData tvSchedule = tvScheduleData.get(position);

        holder.programname.setText(tvSchedule.getTitle());
        holder.programep.setText(tvSchedule.getTime());
    }

    @Override
    public int getItemCount() {
        return tvScheduleData==null?0:tvScheduleData.size();
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
