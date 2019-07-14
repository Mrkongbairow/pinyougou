package com.sms;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SmsListener {
    @Autowired
    private SmsUtil smsUtil;

    @JmsListener(destination = "sms")
    public void sendSms(Map<String,String> map){
        String phoneNum = map.get("phoneNum");
        String code = map.get("code");
        String status = smsUtil.sendSms(phoneNum, code);

        System.out.println(status);

    }

}
