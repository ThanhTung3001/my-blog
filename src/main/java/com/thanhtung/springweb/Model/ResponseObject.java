package com.thanhtung.springweb.Model;

import java.util.Date;

public class ResponseObject {
    public String message;

    public int statusCode ;

    public Date timeAt = new Date();

    public  Object object;

    public ResponseObject(String message,int code, Object object){
        this.message =message;
         this.statusCode=code;
    }
    public ResponseObject(){

    }
    public ResponseObject(String message,int code){
             this.message=message;
             this.statusCode= code;
    }
}