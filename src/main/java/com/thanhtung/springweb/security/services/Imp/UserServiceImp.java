package com.thanhtung.springweb.security.services.Imp;

import com.thanhtung.springweb.Constant.URL;
import com.thanhtung.springweb.Entity.ERole;
import com.thanhtung.springweb.Entity.RoleEntity;
import com.thanhtung.springweb.Entity.UserEntity;
import com.thanhtung.springweb.MailHandle.MailUtils;
import com.thanhtung.springweb.Model.MailModel;
import com.thanhtung.springweb.payload.request.SignupRequest;
import com.thanhtung.springweb.repository.RoleRepository;
import com.thanhtung.springweb.repository.UserRepository;
import com.thanhtung.springweb.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

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

       String text ="<a href="+ URL.URL_CURRENT+"/api/auth/Authentication?token="+user.uuidAuthen+">Authentication</a>";
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
