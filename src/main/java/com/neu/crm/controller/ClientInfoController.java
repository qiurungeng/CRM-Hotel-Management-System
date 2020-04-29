package com.neu.crm.controller;

import com.neu.crm.bean.ClientBaseInfo;
import com.neu.crm.bean.User;
import com.neu.crm.service.ClientBaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ClientInfoController {

    @Autowired
    private ClientBaseInfoService clientBaseInfoService;

    @RequestMapping("index")
    public String index(User user, ModelMap modelMap){
        if (user!=null){
            modelMap.put("loginUser",user);
        }
        //显示所有客户信息
        modelMap.put("clientBaseInfos",clientBaseInfoService.getClientBaseInfos());
        return "index";
    }

    @RequestMapping("clientInfoManage")
    public String clientInfoManage(){
        return "client";
    }

    @ResponseBody
    @RequestMapping("addClientBaseInfo")
    public ClientBaseInfo addClientBaseInfo(ClientBaseInfo clientBaseInfo){
        System.out.println("addClientBaseInfo:Controller:"+clientBaseInfo);
        //若添加成功，返回携带有客户档案号的客户基本消息。
        return clientBaseInfoService.addClientBaseInfo(clientBaseInfo);
    }



}
