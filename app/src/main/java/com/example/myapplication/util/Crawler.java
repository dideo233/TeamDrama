package com.example.myapplication.util;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.ChatListAdapter;
import com.example.myapplication.model.TvData;
import com.example.myapplication.model.TvScheduleData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

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
    TvData tvData = new TvData();
    List<TvScheduleData> tvScheduleDataList = new ArrayList<>();


    ChatListAdapter chatListAdapter;

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

            String broadcast = html.select("img").attr("alt");
            Log.d("방송사 ", broadcast); //방송사

            for(int i = 0; i < timeH.size(); i++){

                    Log.d("time::: " , timeH.get(i).text()+":"+timeM.get(i).text());
                    Log.d("program::: " , program.get(i).text());
                    Log.d("category::: " , category.get(i).text());
                    TvScheduleData tvSchedule = new TvScheduleData();
                    tvSchedule.setCategory(category.get(i).text());
                    tvSchedule.setTitle(program.get(i).text());
                    tvSchedule.setTime(timeH.get(i).text()+":"+timeM.get(i).text());
                if(program.get(i).text().contains("방송중")) { //현재 방송 중인 프로그램
                    String title = program.get(i).text();
                    tvSchedule.setTitle(title.substring(4,title.length()));
                    tvSchedule.setOnAir(true);
                }
                    tvScheduleDataList.add(tvSchedule);
            }
            long now = System.currentTimeMillis();
            Date date = new Date(now);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String getTime = sdf.format(date);

            tvData.setScheduleDate(getTime);
            tvData.setTvScheduleData(tvScheduleDataList);
            tvData.setBroadcastStation(broadcast);
            dramaSave(tvData);
//                }
//
//
//
//                Log.d("time " , timeH.get(i).text()+":"+timeM.get(i).text());
//                Log.d("program " , program.get(i).text());
//                Log.d("category " , category.get(i).text());
//
//            chatListAdapter = new ChatListAdapter(tvScheduleData);
//            Log.d("tvScheduleData size()", ""+tvScheduleData.size());

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    void dramaSave(TvData tvData){

        FirebaseDatabase.getInstance().getReference().child("broadcast").push().setValue(tvData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("아아아아아 성공", "성공");
                    }
                });


    }

}
