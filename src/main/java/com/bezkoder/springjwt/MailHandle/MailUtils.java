package com.bezkoder.springjwt.MailHandle;

import com.bezkoder.springjwt.Model.MailModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.UUID;

@Component
public class MailUtils {
    @Autowired
    private JavaMailSender mailSender;

    public boolean SendMail(MailModel mail) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
      try{
             helper.setTo(mail.sendTo);
             helper.setSubject(mail.Subject);
             helper.setText(mail.text);
             mail.listFile.forEach(file->{
                 try {
                     helper.addAttachment(UUID.randomUUID().toString(), new ClassPathResource(file));
                 } catch (MessagingException e) {
                     e.printStackTrace();
                 }
             });
          mailSender.send(message);
          return  true;
      }catch (Exception ex){
          return  false;
      }
    }
}
