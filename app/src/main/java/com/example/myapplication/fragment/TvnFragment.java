package com.example.myapplication.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.BroadListAdapter;
import com.example.myapplication.R;
import com.example.myapplication.model.TvScheduleData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TvnFragment extends Fragment {

    String broadcastStation = "tvN";
    String scheduleDate; //방송일자
    ArrayList<TvScheduleData> search_list = new ArrayList<>();
    EditText editText;
    BroadListAdapter broadListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewtvn = inflater.inflate(R.layout.fragment_tvn, container, false);
        RecyclerView rvchat = (RecyclerView) viewtvn.findViewById(R.id.rvchat);
        ImageButton searchDelte = viewtvn.findViewById(R.id.search_delete);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvchat.setLayoutManager(linearLayoutManager);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        scheduleDate = sdf.format(date);

        //검색
        editText = viewtvn.findViewById(R.id.editText);
        broadListAdapter = new BroadListAdapter(broadcastStation, scheduleDate);
        rvchat.setAdapter(broadListAdapter);

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchDelte.setVisibility(View.VISIBLE);
                searchDelte.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editText.setText("");
                    }
                });
                String searchText = editText.getText().toString();
                Log.d("searchText", searchText+"");
                FirebaseDatabase.getInstance().getReference().child("broadcast").child(broadcastStation).child(scheduleDate).orderByChild("title").startAt(searchText).endAt(searchText+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        search_list.clear();
                        for(DataSnapshot item : snapshot.getChildren()){
                            search_list.add(item.getValue(TvScheduleData.class));
                            broadListAdapter.setItems(search_list);

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        return viewtvn;
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        editText.setText("");
//    }
}