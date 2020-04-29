package com.neu.crm.controller;

import com.neu.crm.bean.ClientAccommodationInfo;
import com.neu.crm.bean.ClientBaseInfo;
import com.neu.crm.dto.ClientAccommodationDto;
import com.neu.crm.service.ClientAccommodationService;
import com.neu.crm.service.ClientBaseInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedList;
import java.util.List;

@Controller
public class ClientAccommodationController {

    @Autowired
    private ClientAccommodationService clientAccommodationService;

    @Autowired
    private ClientBaseInfoService clientBaseInfoService;

    @RequestMapping("clientAccommodation")
    public String clientAccommodation(ModelMap modelMap){
        //前台显示数据
        List<ClientAccommodationDto> DTOs=new LinkedList<>();

        //拼装
        List<ClientAccommodationInfo> clientAccommodationInfos = clientAccommodationService.getClientAccommodationInfos();
        for (ClientAccommodationInfo info : clientAccommodationInfos) {
            ClientBaseInfo clientBaseInfo=clientBaseInfoService.getClientBaseInfoById(info.getClientId());
            ClientAccommodationDto dto = new ClientAccommodationDto();
            BeanUtils.copyProperties(info,dto);
            dto.setName(clientBaseInfo.getName());
            DTOs.add(dto);
        }

        modelMap.put("clientAccommodationDTOs",DTOs);
        return "client_accommodation";
    }

    @PostMapping("addClientAccommodation")
    @ResponseBody
    public String addClientAccommodation(ClientAccommodationDto dto){
        ClientAccommodationInfo clientAccommodationInfo=new ClientAccommodationInfo();
        BeanUtils.copyProperties(dto,clientAccommodationInfo,"name");
        clientAccommodationService.addClientAccommodation(clientAccommodationInfo);
        return "成功";
    }

    @PostMapping("modifyClientAccommodation")
    @ResponseBody
    public String modifyClientAccommodation(ClientAccommodationDto dto){
        ClientAccommodationInfo clientAccommodationInfo=new ClientAccommodationInfo();
        BeanUtils.copyProperties(dto,clientAccommodationInfo,"name");
        clientAccommodationService.updateClientAccommodationByClientId(clientAccommodationInfo);
        return "成功";
    }
}
