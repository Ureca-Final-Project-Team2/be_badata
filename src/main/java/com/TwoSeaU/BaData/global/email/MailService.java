package com.TwoSeaU.BaData.global.email;


import com.TwoSeaU.BaData.global.exception.GlobalException;
import com.TwoSeaU.BaData.global.response.GeneralException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;
    private static final String encodingType = "utf-8";

    public void sendMail(final String email, final String title,final String contents){

        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            setMiMeMessageHelperForMailFormat(message,email,title,contents);
            javaMailSender.send(message);
        }
        catch (Exception e){
            log.info("이메일 예외 : {}",e.getMessage());
          //  throw new GeneralException(GlobalException.INTERNAL_MAIL_ERROR);
        }
    }

    private void setMiMeMessageHelperForMailFormat(final MimeMessage message,final String email,final String title,final String contents)
            throws  MessagingException {

        MimeMessageHelper helper = new MimeMessageHelper(message,true,encodingType);
        helper.setTo(email);
        helper.setSubject(title);
        helper.setText(contents,true);

    }



}

