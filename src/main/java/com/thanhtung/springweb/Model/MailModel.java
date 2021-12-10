package com.thanhtung.springweb.Model;

import java.util.ArrayList;
import java.util.List;

public class MailModel {
    public  String sendTo;
    public String Subject;
    public  String text;
   public List<String>listFile =new ArrayList<>();

    public MailModel(String sendTo,String subject,String text){
        this.sendTo =sendTo;
        this.Subject =subject;
        this.text=text;

    }
    public  MailModel(){

    }
}
