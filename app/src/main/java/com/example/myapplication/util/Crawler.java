package com.example.myapplication.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class Crawler {
    String url = "https://tv.kt.com/tv/channel/pChInfo.asp";
    Connection conn = Jsoup.connect(url);
    Document html;

    public void tvSchedule() {
        try {
            html = conn.get();
            Element channel = html.getElementById("linkChannel43");

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
