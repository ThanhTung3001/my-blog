package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.Entity.UserEntity;
import com.bezkoder.springjwt.payload.request.SignupRequest;

import javax.mail.MessagingException;

public interface UserService {
    public  boolean AuthenticationActive(String token);
    public int Save(SignupRequest signUpRequest) throws MessagingException;
    public boolean forgetPassword(String token);
    public boolean senderMail(String email) throws MessagingException;

    boolean changerPassword(UserEntity user);
}
