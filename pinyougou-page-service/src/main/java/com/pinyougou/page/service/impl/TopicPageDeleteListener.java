package com.pinyougou.page.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class TopicPageDeleteListener implements MessageListener {
    @Autowired
    private ItemPageServiceImpl itemPageService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage)message;

        try {
            Long[] ids = (Long[]) objectMessage.getObject();

            System.out.println("接收到"+ids);
            itemPageService.deleteItemHtml(ids);

            System.out.println("删除页面成功");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
