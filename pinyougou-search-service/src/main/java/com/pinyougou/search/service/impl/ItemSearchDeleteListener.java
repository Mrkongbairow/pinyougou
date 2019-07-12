package com.pinyougou.search.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.util.Arrays;

@Component
public class ItemSearchDeleteListener implements MessageListener {
    @Autowired
    private ItemSearchServiceImpl itemSearchService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage)message;

        try {
            Long[] ids = (Long[]) objectMessage.getObject();
            itemSearchService.deletById(Arrays.asList(ids));

            System.out.println("删除索引数据");

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
