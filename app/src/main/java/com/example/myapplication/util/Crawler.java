package com.example.myapplication.util;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.ChatListAdapter;
import com.example.myapplication.model.TvData;
import com.example.myapplication.model.TvScheduleData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class Crawler {
    //네이버 TV 편성표
    //https://search.naver.com/search.naver?sm=tab_hty.top&where=nexearch&query=tv+%ED%8E%B8%EC%84%B1%ED%91%9C

    String url = "https://tv.kt.com/tv/channel/pSchedule.asp";
    Connection conn = Jsoup.connect(url);
    Document html;

    String broadcastStation; //방송사
    String scheduleDate; //방송일자

    Boolean check = false; //기존크롤링정보여부 체크
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public  void tvScheduleParse() {
        try {

            html = conn
                    .header("Accept-Encodin","gzip, deflate, br")
                    .data("view_type","1")
                    .data("service_ch_no","1")//MBC Every1 (다른 방송국 편성 정보 파싱하려면 채널 번호만 바꾸기. ex:52 spoTV2 )
                    .data("ch_type","3")
                    .ignoreContentType(true).post();

            Elements timeH = html.select(".time:eq(0)");  //시
            Elements timeM = html.select(".time:eq(1)");  //분
            Elements program = html.select(".program");   //방송명
            Elements category = html.select(".category"); //장르

            //방송사 구분
            broadcastStation = html.select("img").attr("alt");
            Log.d("방송사 ", broadcastStation); //방송사

            //날짜
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            scheduleDate = sdf.format(date);

            //이미 데이터베이스에 크롤링 정보가 존재하는 경우에는 생략

            for (int i = 0; i < timeH.size(); i++) {
                Log.d("time::: ", timeH.get(i).text() + ":" + timeM.get(i).text());
                Log.d("program::: ", program.get(i).text());
                Log.d("category::: ", category.get(i).text());

                //편성표 데이터
                TvScheduleData tvScheduleData = new TvScheduleData();
                tvScheduleData.setTitle(program.get(i).text());
                tvScheduleData.setCategory(category.get(i).text());
                tvScheduleData.setTime(timeH.get(i).text() + ":" + timeM.get(i).text());
                if (program.get(i).text().contains("방송중")) { //현재 방송 중인 프로그램
                    String title = program.get(i).text();
                    tvScheduleData.setTitle(title.substring(4, title.length()));
                    tvScheduleData.setOnAir(true);
                }
                //데이터 베이스에 방송 1개씩 저장 (채팅방 개설을 위한 키값부여)
                database.getReference().child("broadcast").child(broadcastStation).child(scheduleDate).push().setValue(tvScheduleData);
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
