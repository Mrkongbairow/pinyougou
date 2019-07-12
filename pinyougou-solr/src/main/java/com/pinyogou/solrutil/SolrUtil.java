package com.pinyogou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    public void importDate(){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//状态为1的可以导入
        List<TbItem> tbItems = itemMapper.selectByExample(example);

        System.out.println("==================================");
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem.getBrand()+"  "+tbItem.getTitle()+"   "+tbItem.getSpec());

            Map map = JSON.parseObject(tbItem.getSpec(), Map.class);
            tbItem.setSpecMap(map);
        }
        System.out.println("==================================");

       SimpleQuery query = new SimpleQuery("*:*");
       solrTemplate.delete(query);

        //  solrTemplate.saveBeans(tbItems);

        solrTemplate.commit();
    }

    public static void main(String[] args) {
        ApplicationContext context =new  ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");

        solrUtil.importDate();
    }
}
