package com.pinyougou.page.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 使用消息订阅生成静态页面
 */
@Component
public class TopicPageListener implements MessageListener {
    @Autowired
    private ItemPageServiceImpl pageService;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage)message;
        try {
            String id = textMessage.getText();
            System.out.println("接收到消息："+id);

            pageService.genItemHtml(Long.valueOf(id));//生成静态页面
            System.out.println("页面生成");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
