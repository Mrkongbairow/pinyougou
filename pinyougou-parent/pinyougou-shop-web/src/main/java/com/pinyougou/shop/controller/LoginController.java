package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/login")
public class LoginController {

    /**
     * 页面显示用户名
     * @return
     */
    @RequestMapping("/loginName")
    public Map findName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        Map map = new HashMap();
        map.put("loginName",name);

        return map;
    }
}
