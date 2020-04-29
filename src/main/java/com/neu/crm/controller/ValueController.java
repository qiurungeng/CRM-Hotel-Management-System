package com.neu.crm.controller;

import com.neu.crm.bean.ClientBaseInfo;
import com.neu.crm.bean.TradeRecord;
import com.neu.crm.dto.ClientValueDTO;
import com.neu.crm.dto.ClientValuePageInfoDTO;
import com.neu.crm.service.ClientBaseInfoService;
import com.neu.crm.service.StatisticsInfoService;
import com.neu.crm.service.TradeRecordService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class ValueController {

    @Autowired
    ClientBaseInfoService clientBaseInfoService;
    @Autowired
    TradeRecordService tradeRecordService;
    @Autowired
    StatisticsInfoService statisticsInfoService;

    @RequestMapping("valueCalculating")
    public String clientValuePage(ModelMap modelMap){
        ClientValuePageInfoDTO clientValuePageInfoDTO = new ClientValuePageInfoDTO();
        clientValuePageInfoDTO.setName("?");
        clientValuePageInfoDTO.setSex(true);
        clientValuePageInfoDTO.setAccommodationRevenue(BigDecimal.ZERO);
        clientValuePageInfoDTO.setSalesRevenue(BigDecimal.ZERO);
        modelMap.put("clientValuePageInfo",clientValuePageInfoDTO);
        return "value_calculating";
    }

    @RequestMapping("valueCalculating/{client_id}")
    public String getClientValuePageInfo(@PathVariable Integer client_id, ModelMap modelMap){
        ClientValuePageInfoDTO dto=new ClientValuePageInfoDTO();

        ClientBaseInfo clientBaseInfo = clientBaseInfoService.getClientBaseInfoById(client_id);
        List<TradeRecord> clientTradeRecords = tradeRecordService.getClientTradeRecords(client_id);

        BigDecimal accommodationRevenue=new BigDecimal(0);
        BigDecimal salesRevenue=new BigDecimal(0);
        int numberOfTrades=0;
        for (TradeRecord clientTradeRecord : clientTradeRecords) {
            //各类型交易对应的总收入
            if (clientTradeRecord.getType().equals("住宿")){
                accommodationRevenue=accommodationRevenue.add(BigDecimal.valueOf(clientTradeRecord.getExpense()));
            }else {
                salesRevenue=salesRevenue.add(BigDecimal.valueOf(clientTradeRecord.getExpense()));
            }
            // 实际已付款交易的数量
            if (clientTradeRecord.getState())numberOfTrades++;
        }

        dto.setName(clientBaseInfo.getName());
        dto.setClientId(client_id);
        dto.setSex(clientBaseInfo.getSex());
        dto.setAccommodationRevenue(accommodationRevenue);
        dto.setSalesRevenue(salesRevenue);
        dto.setNumberOfTrades(numberOfTrades);
        dto.setExpectedNumberOfTrades(clientTradeRecords.size());

        dto.setTradeRecords(clientTradeRecords);

        modelMap.put("clientValuePageInfo",dto);
        System.out.println("[ValueController]: dto: "+dto);

        return "value_calculating";
    }

    @RequestMapping("getClientValue")
    @ResponseBody
    public ClientValueDTO getClientValue(Double accommodationIncome,
                                         Double consumeIncome,
                                         Integer numberOfTrades,
                                         Integer expectedNumberOfTrades){
        ClientValueDTO dto=new ClientValueDTO();

        int clientSum = clientBaseInfoService.getClientsSum();
        double totalClientAccommodationIncome = statisticsInfoService.getTotalClientAccommodationIncome();
        double totalClientConsumeIncome = statisticsInfoService.getTotalClientConsumeIncome();

        //房费收入率=某客户的房费收入额/所有客户的平均房费收入额
        dto.setAccommodationIncomeRate((accommodationIncome*clientSum)/totalClientAccommodationIncome);
        //消费收入率=某客户的消费收入额/所有客户的平均消费收入额
        dto.setConsumeIncomeRate((consumeIncome*clientSum)/totalClientConsumeIncome);
        //成功交易率=某客户的实际交易次数/某客户的预订次数
        dto.setTradeSuccessRate((double)numberOfTrades/expectedNumberOfTrades);
        //客户价值=0.35x房费收入率+0.35x消费收入率+0.3成功交易率
        dto.setClientValue(0.35*dto.getAccommodationIncomeRate()
                + 0.35*dto.getConsumeIncomeRate()
                + 0.3*dto.getTradeSuccessRate());
        return dto;
    }

}
