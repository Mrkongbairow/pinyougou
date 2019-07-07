package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate template;

    @Override
    public Map<String, Object> search(Map mapSearch) {
        Map map = new HashMap();
        //1.查询主线
        map.putAll(searchMap(mapSearch));
        //2.分组查询商品分类
        List<String> list = searchCategoryList(mapSearch);
        map.put("categoryList", list);
        //3.获取品牌和规格
        if (list != null) {
            map.putAll(searchBrandAndSpec(list.get(0)));
        }

        return map;
    }

    //主线的查询
    private Map searchMap(Map searchMap) {
        Map map = new HashMap<>();

         /* //添加查询条件
        Query query = new SimpleFacetQuery();
        //设置搜索条件
        Criteria criteria = new Criteria("item_keywords").is(mapSerch.get("keywords"));

        query.addCriteria(criteria);
        ScoredPage<TbItem> items = template.queryForPage(query, TbItem.class);*/
        //高亮查询
        HighlightQuery queryHighLight = new SimpleHighlightQuery();
        //设置高亮域
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        //设置高亮前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //设置后缀高亮
        highlightOptions.setSimplePostfix("</em>");
        //设置高亮选项!!!!!
        queryHighLight.setHighlightOptions(highlightOptions);
        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //设置查询条件！！！！
        queryHighLight.addCriteria(criteria);

        HighlightPage<TbItem> items = template.queryForHighlightPage(queryHighLight, TbItem.class);

        List<HighlightEntry<TbItem>> highlighted = items.getHighlighted();
        for (HighlightEntry<TbItem> hle : highlighted) {//获取高亮入口集合
            TbItem entity = hle.getEntity();//获取实体类
            List<HighlightEntry.Highlight> hl = hle.getHighlights();//获取高亮列表

            if (hl.size() > 0 && hl != null) {//如果高亮列表有值，则循环该列表
                for (HighlightEntry.Highlight h : hl) {
                    List<String> snipplets = h.getSnipplets();//每个域可存储多值
                    if (snipplets.size() > 0 && snipplets != null) {//如果高亮域存在值，则遍历该集合
                        for (String snipplet : snipplets) {
                            entity.setTitle(snipplet);
                            //System.out.println(snipplet);
                        }
                    }
                }
            }

            /*if (hle.getHighlights().size()>0 && hle.getHighlights().get(0).getSnipplets().size()>0){
                entity.setTitle(hle.getHighlights().get(0).getSnipplets().get(0));//设置高亮
            }*/
        }
        map.put("rows", items.getContent());
        return map;
    }

    //分组查询商品分类
    private List<String> searchCategoryList(Map map) {
        List<String> list = new ArrayList<>();

        Query query = new SimpleQuery();
        //添加查询条件
        Criteria criteria = new Criteria("item_keywords").is(map.get("keywords"));
        query.addCriteria(criteria);
        //设置分组查询条件group by
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");//后面可添加多个分组条件
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> pages = template.queryForGroupPage(query, TbItem.class);
        //获取分组结果
        GroupResult<TbItem> groupResult = pages.getGroupResult("item_category");//选择分组域
        //获取入口分组结果
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        for (GroupEntry<TbItem> entry : groupEntries) {
            list.add(entry.getGroupValue());//将分组名称存入数组
        }
        return list;
    }
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取缓存的品牌与规格
     *
     * @param category
     * @return
     */
    private Map searchBrandAndSpec(String category) {
        Map map = new HashMap();
        //根据分类名称或去模板ID
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        if (typeId != null) {
            //根据模板id获取品牌
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);

            //根据模板id获取规格
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
        return map;
    }
}
