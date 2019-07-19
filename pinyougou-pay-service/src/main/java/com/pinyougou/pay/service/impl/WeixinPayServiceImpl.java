package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.util.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;

    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1.创建参数
        Map<String,String> map = new HashMap();

        map.put("appid",appid);//公众号
        map.put("mch_id",partner);//商户号
        map.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        map.put("body","品优购");//商品描述
        map.put("out_trade_no",out_trade_no);//商户订单
        map.put("total_fee",total_fee);//总金额(单位为分，避免小数)
        map.put("spbill_create_ip","127.0.0.1");//IP
        map.put("notify_url","http://www.jd.com");//回调地址
        map.put("trade_type","NATIVE");//交易类型（本地）

        try {//生成要发送的想xml字符串
            String xmlParam = WXPayUtil.generateSignedXml(map, partnerkey);
            System.out.println(xmlParam);

        //2.发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();
        //3.获取结果
            String content = httpClient.getContent();
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(content);

            //重新封装结果
            Map result = new HashMap();
            result.put("code_url",xmlToMap.get("code_url"));//获支付页地址
            result.put("total_fee",total_fee);//总金额
            result.put("out_trade_no",out_trade_no);//订单号

            System.out.println("支付结果："+content);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        //1.创建参数
        Map<String ,String> param = new HashMap<>();

        param.put("appid", appid);//公众账号 ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串

        try {
            String paramXml = WXPayUtil.generateSignedXml(param, partnerkey);//获取xml字符串
            //2.发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();

            //3.获取结果
            String result = httpClient.getContent();
            Map<String, String> toMap = WXPayUtil.xmlToMap(result);

            System.out.println("支付结果："+result);

            return toMap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
