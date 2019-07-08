package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

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
        String category = (String) mapSearch.get("category");
        if (!category.equals("")) {//如果分类不为空，则以当前分类查询规格和品牌
            map.putAll(searchBrandAndSpec(category));
        } else {//如果分类为空，则以分类集合中第一个分类查询
            if (list != null) {
                map.putAll(searchBrandAndSpec(list.get(0)));
            }
        }

        return map;
    }


    //主线的查询
    private Map searchMap(Map searchMap) {
        Map map = new HashMap<>();

        String keywords = (String) searchMap.get("keywords");

        searchMap.put("keywords",keywords.replace(" ",""));

         /* //添加查询条件
        Query query = new SimpleFacetQuery();
        //设置搜索条件
        Criteria criteria = new Criteria("item_keywords").is(mapSerch.get("keywords"));

        query.addCriteria(criteria);
        ScoredPage<TbItem> items = template.queryForPage(query, TbItem.class);*/
        //高亮查询 **********************高亮设置**********************
        HighlightQuery query = new SimpleHighlightQuery();
        //设置高亮域
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        //设置高亮前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //设置后缀高亮
        highlightOptions.setSimplePostfix("</em>");
        //设置高亮选项!!!!!
        query.setHighlightOptions(highlightOptions);
        //按照关键字查询
        //******************* 设置条件查询************************
        //1.1设置查询条件！！！！
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //1.2设置分类过滤查询
        if (!"".equals(searchMap.get("category"))) {//如果分类不等于null，则进行过滤
            Criteria criteria1 = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(criteria1);
            query.addFilterQuery(filterQuery);
        }
        //1.3设置品牌过滤查询
        if (!"".equals(searchMap.get("brand"))) {//如果品牌不等于null，则进行过滤
            Criteria criteria1 = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(criteria1);
            query.addFilterQuery(filterQuery);
        }
        //1.4设置规格过滤查询
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria criteria1 = new Criteria("item_spec_" + key).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }
        }
        //1.5设置根据价格区间查找
        if (!"".equals(searchMap.get("price"))) {
            String priceStr = (String) searchMap.get("price");//0-500
            String[] price = priceStr.split("-");//分割价格区间

            if (!"0".equals(price[0])){//查找价格需大于0
                Criteria criteria1 = new Criteria("item_price").greaterThan(price[0]);//价格不能小于等于0
                FilterQuery filterQuery = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }
            if(!"*".equals(price[1])){//查找价格需小于选择的最大价格
                Criteria criteria1 = new Criteria("item_price").lessThanEqual(price[1]);//价格不能大于最大价格
                FilterQuery filterQuery = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }
        }
        //1.6分页
        Integer pageNum = (Integer) searchMap.get("pageNum");//当前页码
        if (pageNum == null){
            pageNum = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");//每页显示条数
        if (pageSize == null){
            pageSize = 20;
        }
        query.setOffset(pageNum);//设置当前页码
        query.setRows(pageSize);//设置每页条数

        //1.7排序
        String sort = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");
        if (sort!= null && !sort.equals("")){
            if (sort.equals("ASC")){
                Sort asc = new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(asc);
            }
            if (sort.equals("DESC")){

                Sort desc = new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(desc);
            }

        }


        //**************获取结果集********************
        HighlightPage<TbItem> items = template.queryForHighlightPage(query, TbItem.class);

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
        map.put("totalPages",items.getTotalPages());//添加总页数
        map.put("total",items.getTotalElements());//添加总记录数

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

    /**
     * 保存更新后的商品列表
     * @param list
     */
    @Override
    public void importList(List list) {
        template.saveBeans(list);
        template.commit();
    }

    /**
     * 根据商品id删除商品
     * @param ids
     */
    @Override
    public void deletById(List ids) {

        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_goodsid").in(ids);
        query.addCriteria(criteria);
        template.delete(query);
        template.commit();

    }
}
