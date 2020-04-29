package com.neu.crm;

import com.neu.crm.controller.ValueController;
import com.neu.crm.dto.ClientValueDTO;
import com.neu.crm.mapper.StatisticsInfoMapper;
import com.neu.crm.service.ClientBaseInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class CrmApplicationTests {

    @Autowired
    ClientBaseInfoService clientBaseInfoService;
    @Autowired
    StatisticsInfoMapper statisticsInfoMapper;
    @Autowired
    ValueController valueController;

    @Test
    void contextLoads() {
        //获得当前用户数量
        System.out.println(clientBaseInfoService.getClientsSum());
    }

    @Test
    void testStatisticsMapper(){
        System.out.println(statisticsInfoMapper.getTotalClientAccommodationIncome());
        System.out.println(statisticsInfoMapper.getTotalClientConsumeIncome());
    }

    @Test
    public void testClientValueCalculating(){
        ClientValueDTO dto = valueController.getClientValue(2450.0,
                530.0,
                3,
                6);
        System.out.println(dto);
    }

}
