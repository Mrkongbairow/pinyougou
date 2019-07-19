package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.util.IdWorker;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference(timeout = 100000)
    private WeixinPayService payService;
    @Reference
    private OrderService orderService;
    /**
     * 创建微信支付二维码
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //获取日志信息
        TbPayLog payLog = orderService.searchPayLogFromRedis(username);

        if (payLog!=null){
            return payService.createNative( payLog.getOutTradeNo(),payLog.getTotalFee()+"");
        }else {
            return new HashMap();
        }

    }

    /**
     * 查询支付结果
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result =null;

        int count = 0;//记录循环次数

        while(true){
            //调用查询方法
            Map<String,String> status = payService.queryPayStatus(out_trade_no);
            if (status == null){
                result = new Result(false,"支付发生错误");
                break;
            }
            if (status.get("trade_state").equals("SUCCESS")){//支付成功
                result = new Result(true,"支付成功");

                orderService.updateOrderStatus(out_trade_no,status.get("transaction_id"));
                break;
            }

            try {//每隔三秒查询一次
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //为了不让循环无休止地运行，我们定义一个循环变量，如果这个变量超过了这个值则退
            //出循环，设置时间为 5 分钟
            count++;
            if (count>=100){
                result = new Result(false,"二维码超时");
                break;
            }
        }
        return  result;
    }
 }
