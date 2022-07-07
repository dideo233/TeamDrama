package com.example.myapplication.model;


public class NotificationModel {
    public String to;
    public String priority ="high";
    public Notification notification = new Notification();
    public Data data = new Data();

    public static class Notification {
        public String title;
        public String text;
    }

    public static class Data {
        public String title;
        public String text;
    }

}