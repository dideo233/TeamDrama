package com.example.myapplication.util;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ChatListAdapter;
import com.example.myapplication.model.TvScheduleData;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class Crawler {
    //네이버 TV 편성표
    //https://search.naver.com/search.naver?sm=tab_hty.top&where=nexearch&query=tv+%ED%8E%B8%EC%84%B1%ED%91%9C

    String url = "https://tv.kt.com/tv/channel/pSchedule.asp";
    Connection conn = Jsoup.connect(url);
    Document html;
    ArrayList<TvScheduleData> tvScheduleData = new ArrayList<>();
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

            Log.d("방송사 ", html.select("img").attr("alt")); //방송사


            for(int i = 0; i < timeH.size(); i++){
                if(program.get(i).text().contains("방송중")){ //현재 방송 중인 프로그램
                    Log.d("time::: " , timeH.get(i).text()+":"+timeM.get(i).text());
                    Log.d("program::: " , program.get(i).text());
                    Log.d("category::: " , category.get(i).text());
                    TvScheduleData tvSchedule = new TvScheduleData();
                    tvSchedule.setCategory(category.get(i).text());
                    tvSchedule.setTitle(program.get(i).text());
                    tvSchedule.setTime(category.get(i).text());
                    tvScheduleData.add(tvSchedule);
                    continue;
                }

                Log.d("time " , timeH.get(i).text()+":"+timeM.get(i).text());
                Log.d("program " , program.get(i).text());
                Log.d("category " , category.get(i).text());

            }
             chatListAdapter = new ChatListAdapter(tvScheduleData);
            Log.d("tvScheduleData size()", ""+tvScheduleData.size());

            /*
            //네이버 TV 편성표
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
            */
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
