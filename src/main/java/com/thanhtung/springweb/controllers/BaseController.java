package com.thanhtung.springweb.controllers;

import com.thanhtung.springweb.Model.ResponseObject;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {
    public ResponseObject BuildSuccess(String message , int code, Object obj){
        return new ResponseObject(message,code,obj);
    }
    public ResponseObject BuildFail(String message,int code){
        return  new ResponseObject(message,code);
    }
}
