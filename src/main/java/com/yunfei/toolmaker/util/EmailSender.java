package com.yunfei.toolmaker.util;

import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Properties;

@ConfigurationProperties(prefix = "email-sender")
@Component
public class EmailSender {
    private String senderEmail;
    private String password;
    private static final String host = "smtp.qq.com";

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void sendEmail(String email, String subject, String content) throws GeneralSecurityException, MessagingException {
        Properties props=new Properties();
        props.setProperty("mail.debug", "true");
        //发送服务器需要身份验证
        props.setProperty("mail.smtp.auth", "true");
        //发送邮件服务器的主机名
        props.setProperty("mail.smtp.host", "smtp.qq.com");
        //端口号
        props.setProperty("mail.smtp.port", "465");
        //发送邮件协议
        props.setProperty("mail.transport.protocol", "smtp");
        //开启ssl加密（并不是所有的邮箱服务器都需要，但是qq邮箱服务器是必须的）
        MailSSLSocketFactory msf= new MailSSLSocketFactory();
        msf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory",msf);
        //获取Session会话实例（javamail Session与HttpSession的区别是Javamail的Session只是配置信息的集合）
        Session session=Session.getInstance(props,new javax.mail.Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication(){
                //用户名密码验证（取得的授权吗）
                return new PasswordAuthentication (senderEmail,password);
            }
        });

        //抽象类MimeMessage为实现类 消息载体封装了邮件的所有消息
        Message message=new MimeMessage(session);
        //设置邮件主题
        message.setSubject(subject);
        //封装需要发送电子邮件的信息
        message.setText(content);
        //设置发件人地址
        message.setFrom(new InternetAddress(senderEmail));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        //此类的功能是发送邮件 又会话获得实例
        Transport transport=session.getTransport();
        //开启连接
        transport.connect();
        //设置收件人地址邮件信息

        transport.sendMessage(message,new Address[]{new InternetAddress(email)});
        //邮件发送后关闭信息
        transport.close();


    }
}
