package com.xupt.service;

import android.os.AsyncTask;
import com.xupt.utils.EmailSender;
import javax.mail.MessagingException;

public class  SendEmailServer extends AsyncTask<String, Integer, String> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        EmailSender sender = new EmailSender();
        //设置服务器地址和端口，网上搜的到
        sender.setProperties("smtp.qq.com", "465");
        try {
            //分别设置发件人，邮件标题和文本内容
            sender.setMessage("1991987275@qq.com", strings[1], strings[2]);
            //设置收件人
            sender.setReceiver(new String[]{strings[0]});
            sender.sendEmail("smtp.qq.com", "1991987275@qq.com", "slzbinetxsxyeddi");
        } catch (MessagingException e) {
        }
        return "";
    }
}

