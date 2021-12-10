package com.bezkoder.springjwt.security.services.Imp;

import com.bezkoder.springjwt.Constant.URL;
import com.bezkoder.springjwt.Entity.ERole;
import com.bezkoder.springjwt.Entity.RoleEntity;
import com.bezkoder.springjwt.Entity.UserEntity;
import com.bezkoder.springjwt.MailHandle.MailUtils;
import com.bezkoder.springjwt.Model.MailModel;
import com.bezkoder.springjwt.payload.request.LoginRequest;
import com.bezkoder.springjwt.payload.request.SignupRequest;
import com.bezkoder.springjwt.payload.response.MessageResponse;
import com.bezkoder.springjwt.repository.RoleRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    MailUtils mailHandle;
    @Override
    public int Save(SignupRequest signUpRequest) throws MessagingException {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return 0;
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return 1;
        }

        // Create new userEntity's account
        UserEntity userEntity = new UserEntity(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<RoleEntity> roles = new HashSet<>();
         UserEntity user= new UserEntity();
        if (strRoles == null) {
            RoleEntity userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: RoleEntity is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        RoleEntity adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: RoleEntity is not found."));
                          roles.add(adminRole);

                        break;
                    case "mod":
                        RoleEntity modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: RoleEntity is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        RoleEntity userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: RoleEntity is not found."));
                        roles.add(userRole);
                }
            });
        }

        userEntity.setRoles(roles);
        user=  userRepository.save(userEntity);

       String text ="<a href="+URL.URL_CURRENT+"/api/auth/Authentication?token="+user.uuidAuthen+">Authentication</a>";
        MailModel mailModel = new MailModel(user.getEmail(),"Authentication account",text);
        mailHandle.SendMail(mailModel);
        return  2;
    }

    @Override
    public boolean AuthenticationActive(String token) {
        boolean isExit = userRepository.existsByUuidAuthen(token);
        if(isExit){
            UserEntity user = userRepository.findAllByUuidAuthen(token);
            user.isActived=true;
            user.isBlock=true;
            user.setUuidAuthen(generateNewToken());
            userRepository.save(user);
        }
        return isExit;
    }
    @Override
   public boolean forgetPassword(String token){
        boolean isExit = userRepository.existsByForgetPassword(token);

        return isExit;
    }
    @Override
    public boolean senderMail(String email) throws MessagingException {
          UserEntity user = userRepository.findAllByEmail(email);
          if(user==null){
              return  false;
          }else{
              String text ="<a href="+URL.URL_CURRENT+"/api/auth/forget/password?token="+user.forgetPassword+">Forgot password</a>";
              MailModel mailModel = new MailModel(user.getEmail(),"Authentication account",text);
              mailHandle.SendMail(mailModel);
              return true;
          }
    }
    @Override
    public boolean changerPassword(UserEntity user){
        UserEntity userEntity = userRepository.findAllByForgetPassword(user.forgetPassword);
        try{
            userEntity.setPassword(encoder.encode(user.getPassword()));
            userEntity.setForgetPassword(generateNewToken());
            userRepository.save(userEntity);

            return true;
        }catch (Exception exception){
              return  false;
        }

    }
    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

    public  String generateNewToken() {
        byte[] randomBytes = new byte[100];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
