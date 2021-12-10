package com.thanhtung.springweb.security.services;

import com.thanhtung.springweb.Entity.UserEntity;
import com.thanhtung.springweb.payload.request.SignupRequest;

import javax.mail.MessagingException;

public interface UserService {
    public  boolean AuthenticationActive(String token);
    public int Save(SignupRequest signUpRequest) throws MessagingException;
    public boolean forgetPassword(String token);
    public boolean senderMail(String email) throws MessagingException;

    boolean changerPassword(UserEntity user);
}
