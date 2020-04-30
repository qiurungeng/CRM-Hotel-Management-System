package com.neu.crm.controller;

import com.neu.crm.bean.ClientBaseInfo;
import com.neu.crm.bean.User;
import com.neu.crm.service.ClientBaseInfoService;
import com.neu.crm.service.EducationService;
import com.neu.crm.service.IncomeTypeService;
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
    @Autowired
    private EducationService educationService;
    @Autowired
    private IncomeTypeService incomeTypeService;

    @RequestMapping({"index","/"})
    public String index(User user, ModelMap modelMap){
        if (user!=null){
            modelMap.put("loginUser",user);
        }
        //显示所有客户信息
        modelMap.put("clientBaseInfos",clientBaseInfoService.getClientBaseInfos());
        modelMap.put("educations",educationService.getEducationNames());
        modelMap.put("incomeTypes",incomeTypeService.getIncomeTypes());
        return "index";
    }

    @RequestMapping("clientInfoManage")
    public String clientInfoManage(){
        return "client";
    }

    @ResponseBody
    @RequestMapping("addClientBaseInfo")
    public ClientBaseInfo addClientBaseInfo(ClientBaseInfo clientBaseInfo){
        //若添加成功，返回携带有客户档案号的客户基本消息。
        return clientBaseInfoService.addClientBaseInfo(clientBaseInfo);
    }

    @ResponseBody
    @RequestMapping("modifyClientBaseInfo")
    public ClientBaseInfo modifyClientBaseInfo(ClientBaseInfo clientBaseInfo){
        //若添加成功，返回携带有客户档案号的客户基本消息
        return clientBaseInfoService.updateClientBaseInfo(clientBaseInfo);
    }

    @RequestMapping("deleteClientBaseInfo")
    @ResponseBody
    public ClientBaseInfo deleteClientBaseInfo(Integer clientId){
        return clientBaseInfoService.deleteClientBaseInfo(clientId);
    }



}
