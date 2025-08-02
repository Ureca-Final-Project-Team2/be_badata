package com.TwoSeaU.BaData.global.email;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfig {

    private static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    private static final String MAIL_TRANSPORT_PROTOCOL_VALUE="smtp";

    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_SMTP_AUTH_VALUE="true";

    private static final String MAIL_SMTP_SOCKET_FACTORY = "mail.smtp.socketFactory.class";
    private static final String MAIL_SMTP_SOCKET_FACTORY_VALUE="javax.net.ssl.SSLSocketFactory";

    private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    private static final String MAIL_SMTP_STARTTLS_ENABLE_VALUE="true";

    private static final String MAIL_DEBUG = "mail.debug";
    private static final String MAIL_DEBUG_VALUE="true";

    private static final String MAIL_SMTP_SSL_TRUST = "mail.smtp.ssl.trust";
    private static final String MAIL_SMTP_SSL_TRUST_VALUE="smtp.gmail.com";

    private static final String MAIL_SMTP_SSL_PROTOCOLS = "mail.smtp.ssl.protocols";
    private static final String MAIL_SMTP_SSL_PROTOCOLS_VALUE="TLSv1.2";

    private static final int naverMailDefaultPortNumber = 587;

    @Value("${mail.google.id}")
    private String googleId;

    @Value("${mail.google.password}")
    private String googleAppPassword;


    @Bean
    public JavaMailSender mailSender() {

        JavaMailSenderImpl mailSender = makeJavaMailSenderImpl();

        Properties javaMailProperties = makeJavaMailProperties();

        mailSender.setJavaMailProperties(javaMailProperties);

        return mailSender;
    }

    private JavaMailSenderImpl makeJavaMailSenderImpl(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(MAIL_SMTP_SSL_TRUST_VALUE);
        mailSender.setPort(naverMailDefaultPortNumber);
        mailSender.setUsername(googleId);
        mailSender.setPassword(googleAppPassword);

        return mailSender;

    }

    private Properties makeJavaMailProperties(){
        Properties javaMailProperties = new Properties();
        javaMailProperties.put(MAIL_TRANSPORT_PROTOCOL, MAIL_TRANSPORT_PROTOCOL_VALUE);
        javaMailProperties.put(MAIL_SMTP_AUTH, MAIL_SMTP_AUTH_VALUE);
        javaMailProperties.put(MAIL_SMTP_SOCKET_FACTORY, MAIL_SMTP_SOCKET_FACTORY_VALUE);
        javaMailProperties.put(MAIL_SMTP_STARTTLS_ENABLE, MAIL_SMTP_STARTTLS_ENABLE_VALUE);
        //  javaMailProperties.put(MAIL_DEBUG, MAIL_DEBUG_VALUE);
        javaMailProperties.put(MAIL_SMTP_SSL_TRUST, MAIL_SMTP_SSL_TRUST_VALUE);
        javaMailProperties.put(MAIL_SMTP_SSL_PROTOCOLS, MAIL_SMTP_SSL_PROTOCOLS_VALUE);

        return javaMailProperties;
    }

}
