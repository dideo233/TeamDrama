package com.example.myapplication.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.BroadListAdapter;
import com.example.myapplication.R;
import com.example.myapplication.VPAdapter;
import com.example.myapplication.model.TvScheduleData;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainFragment extends Fragment {
    Fragment Mbcfragment, Kbsfragment,Sbsfragment,Tvnfragment,Mbcevery1fragment;
    private List<TvScheduleData> tvScheduleDataList = new ArrayList<>();
    private List<String> keys = new ArrayList<>();


    String[] broadcastStations = {"KBS2","MBC","SBS","MBC Every1","tvN"};//방송사

    String scheduleDate; //방송일자

    String todayDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Mbcfragment = new MbcFragment(); // 뷰페이저 화면 1
        Kbsfragment = new KbsFragment(); // 뷰페이저 화면 2
        Sbsfragment = new SbsFragment(); // 뷰페이저 화면 3
        Tvnfragment = new TvnFragment(); // 뷰페이저 화면 4
        Mbcevery1fragment = new MbceveryFragment(); // 뷰페이저 화면 5

        View view = inflater.inflate(R.layout.fragment_main,container,false);
        TextView tvtoday = (TextView)view.findViewById(R.id.tvtoday);
        ViewPager2 viewpager2 = view.findViewById(R.id.viewpager);
        viewpager2.setAdapter(new VPAdapter(this)); // 여기서 this로 뷰페이저가 포함되어 있는 현재 프래그먼트(HomeFragment)를 인수로 넣어준다.
        viewpager2.setCurrentItem(0);

        TabLayout tabLayout = view.findViewById(R.id.tab);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewpager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("KBS2");
                        break;
                    case 1:
                        tab.setText("MBC");
                        break;
                    case 2:
                        tab.setText("SBS");
                        break;
                    case 3:
                        tab.setText("tvN");
                        break;
                    case 4:
                        tab.setText("MBC every1");
                        break;
                }
            }
        });
        tabLayoutMediator.attach();

        TextView[] names = {
                (TextView)view.findViewById(R.id.kpname),
                (TextView)view.findViewById(R.id.mpname),
                (TextView)view.findViewById(R.id.spname),
                (TextView)view.findViewById(R.id.tpname),
                (TextView)view.findViewById(R.id.mepname)
        };
        TextView[] starts = {
                (TextView)view.findViewById(R.id.startk),
                (TextView)view.findViewById(R.id.startm),
                (TextView)view.findViewById(R.id.starts),
                (TextView)view.findViewById(R.id.startt),
                (TextView)view.findViewById(R.id.startme)
        };

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM월 dd일");

        todayDate = sdf2.format(date);
        tvtoday.setText(todayDate);

        scheduleDate = sdf.format(date);
        Log.d("scheduleDate>>>",""+scheduleDate );
        for(int i=0; i <broadcastStations.length;i++){
            int pos = i;
            FirebaseDatabase.getInstance().getReference().child("broadcast").child(broadcastStations[i]).child(scheduleDate).orderByChild("onAir").equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for(DataSnapshot item : snapshot.getChildren()){
                        names[pos].setText(item.getValue(TvScheduleData.class).getTitle());
                        starts[pos].setText(item.getValue(TvScheduleData.class).getTime());

                        Log.d("11",item.getValue(TvScheduleData.class).getTitle());
                        Log.d("11",item.getValue(TvScheduleData.class).getTime());
                        Log.d("11",item.getValue(TvScheduleData.class).getCategory());
                        Log.d("11",item.getValue(TvScheduleData.class).isOnAir()+"");

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return view;
    }


}