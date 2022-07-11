package com.example.myapplication.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.R;

public class DetailsFragment extends Fragment {
    View view;

    ImageButton like;

    TextView tvtitle,tvcategory,tvbroadcastStation;
    String programname;
    String Programca;
    String broadcastStation;

    Button btncreatechat,btnchatclose,btnjoin1,btnjoin2,btnjoin3,btnjoin4,btnjoin5;
    TextView chattop1,chattop2,chattop3,chattop4,chattop5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_details, container, false);

        tvtitle = view.findViewById(R.id.title);
        tvcategory = view.findViewById(R.id.category);
        tvbroadcastStation = view.findViewById(R.id.broadcastStation);

        if (getArguments() != null)
        {
            programname = getArguments().getString("programname"); // 프래그먼트1에서 받아온 값 넣기
            Programca = getArguments().getString("Programca");
            broadcastStation = getArguments().getString("broadcastStation");

            tvtitle.setText(programname);
            tvcategory.setText(Programca);
            tvbroadcastStation.setText(broadcastStation);
        }

        btncreatechat = view.findViewById(R.id.btncreatechat);
        btnchatclose = view.findViewById(R.id.btnchatclose);
        btnjoin1 = view.findViewById(R.id.btnjoin1);
        btnjoin2 = view.findViewById(R.id.btnjoin2);
        btnjoin3 = view.findViewById(R.id.btnjoin3);
        btnjoin4 = view.findViewById(R.id.btnjoin4);
        btnjoin5 = view.findViewById(R.id.btnjoin5);
        chattop1 = view.findViewById(R.id.chattop1);
        chattop2 = view.findViewById(R.id.chattop2);
        chattop3 = view.findViewById(R.id.chattop3);
        chattop4 = view.findViewById(R.id.chattop4);
        chattop5 = view.findViewById(R.id.chattop5);



        return view;
    }


}