package com.neu.crm.controller;

import com.neu.crm.bean.ClientAccommodationInfo;
import com.neu.crm.bean.ClientBaseInfo;
import com.neu.crm.bean.ClientConsumeInfo;
import com.neu.crm.bean.HotelService;
import com.neu.crm.dto.ClientAccommodationDto;
import com.neu.crm.dto.ClientConsumeDTO;
import com.neu.crm.service.ClientBaseInfoService;
import com.neu.crm.service.ClientConsumeService;
import com.neu.crm.service.HotelServiceService;
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
public class ClientConsumptionController {

    @Autowired
    ClientConsumeService clientConsumeService;

    @Autowired
    HotelServiceService hotelServiceService;

    @Autowired
    ClientBaseInfoService clientBaseInfoService;

    @RequestMapping("clientConsumption")
    public String clientConsumption(ModelMap modelMap){
        //前台显示对象
        List<ClientConsumeDTO> DTOs=new LinkedList<>();

        //拼装：DTOs = 客户消费信息+消费项目名称
        List<ClientConsumeInfo> clientConsumeInfos = clientConsumeService.getClientAccommodationInfos();
        for (ClientConsumeInfo info : clientConsumeInfos) {
            HotelService hotelService=hotelServiceService.getHotelServiceById(info.getServiceId());
            ClientBaseInfo clientBaseInfo = clientBaseInfoService.getClientBaseInfoById(info.getClientId());

            ClientConsumeDTO dto = new ClientConsumeDTO();
            BeanUtils.copyProperties(info,dto);

            dto.setServiceName(hotelService.getName());
            dto.setClientName(clientBaseInfo.getName());
            dto.setPrice(hotelService.getPrice());
            dto.setAmount(hotelService.getPrice()*info.getCount());

            DTOs.add(dto);
        }

        //返回到前台
        //客户消费记录
        modelMap.put("clientConsumeDTOs",DTOs);
        //所有酒店服务设施
        modelMap.put("hotelServices",hotelServiceService.getHotelServices());

        return "client_consumption";
    }

    /**
     * 增
     */
    @PostMapping("addClientConsumeInfo")
    @ResponseBody
    public boolean addClientConsumeInfo(ClientConsumeDTO dto){
        ClientBaseInfo client = clientBaseInfoService.getClientBaseInfoById(dto.getClientId());
        if (client!=null){
            ClientConsumeInfo clientConsumeInfo=new ClientConsumeInfo();
            BeanUtils.copyProperties(dto,clientConsumeInfo,"name");
            clientConsumeService.addClientConsumeInfo(clientConsumeInfo);
            return true;
        }
        return false;
    }

    /**
     * 改
     */
    @PostMapping("modifyClientConsumeInfo")
    @ResponseBody
    public boolean modifyClientConsumeInfo(ClientConsumeDTO dto){
        ClientBaseInfo client = clientBaseInfoService.getClientBaseInfoById(dto.getClientId());
        if (client!=null) {
            ClientConsumeInfo clientConsumeInfo = new ClientConsumeInfo();
            BeanUtils.copyProperties(dto, clientConsumeInfo, "name");
            clientConsumeService.modifyClientConsumeInfo(clientConsumeInfo);
            return true;
        }
        return false;
    }

    /**
     * 根据客户消费记录的id删除对应的记录
     * @param id ClientConsumeInfo: id
     * @return 操作成功或失败
     */
    @PostMapping("deleteClientConsumeInfo")
    @ResponseBody
    public boolean deleteClientConsumeInfo(Integer id){
        if (id!=null){
            clientConsumeService.deleteClientConsumeInfoById(id);
            return true;
        }
        return false;
    }

}
