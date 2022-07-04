package com.example.myapplication.util;

import android.util.Log;

import com.example.myapplication.model.TvScheduleData;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Crawler {
    List<TvScheduleData> onAirList = new ArrayList<>();

    //Jsoup (네이버 편성표 파싱)
    String url = "https://search.naver.com/search.naver?sm=tab_hty.top&where=nexearch&query=tv+%ED%8E%B8%EC%84%B1%ED%91%9C";
    Connection conn = Jsoup.connect(url);
    Document html;

    public void tvScheduleParse() {
        try {
            html = conn.get();
            //Elements all = html.select(".timeline_box .ind_program");
            Elements onAirTitle = html.select("div[class='ind_program on']").select("div[class='scm_ellipsis _ellipsis']");
            Elements onAirTime = html.select("div[class='ind_program on']").select(".sub_info");
            Elements offAir = html.select("div[class='ind_program']");

            for(int i=0; i < onAirTitle.size(); i++){
                TvScheduleData schedule = new TvScheduleData();
                schedule.setTitle(onAirTitle.get(i).text());
                schedule.setTime(onAirTime.get(i).text());
                onAirList.add(schedule);
            }

            //파싱한 데이터 확인
            //onAir
            Log.d("onAir list ", onAirList.size() + " ----------------");
            for(int i = 0; i < onAirList.size(); i++){
                Log.d("onAir title : ", onAirList.get(i).getTime()+"");
                Log.d("onAir time : ", onAirList.get(i).getTitle()+"");
            }
            //onAir 이전 및 이후 방송
            Log.d("offAir Size : ", offAir.size() + " ----------------");
            for(int i=0; i < offAir.size(); i++){
                Log.d("data :  ", offAir.get(i).text());
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
