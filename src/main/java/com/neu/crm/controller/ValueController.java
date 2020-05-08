package com.neu.crm.controller;

import com.neu.crm.bean.ClientBaseInfo;
import com.neu.crm.bean.ClientValue;
import com.neu.crm.bean.TradeRecord;
import com.neu.crm.dto.ClientValueDTO;
import com.neu.crm.dto.ClientValuePageInfoDTO;
import com.neu.crm.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

@Controller
public class ValueController {

    @Autowired
    ClientBaseInfoService clientBaseInfoService;
    @Autowired
    TradeRecordService tradeRecordService;
    @Autowired
    StatisticsInfoService statisticsInfoService;
    @Autowired
    ClientValueService clientValueService;
    @Autowired
    ClassificationDataSourceService classificationDataSourceService;

    @RequestMapping("valueCalculating")
    public String clientValuePage(ModelMap modelMap){
        ClientValuePageInfoDTO clientValuePageInfoDTO = new ClientValuePageInfoDTO();
        clientValuePageInfoDTO.setName("?");
        clientValuePageInfoDTO.setSex(true);
        clientValuePageInfoDTO.setAccommodationRevenue(BigDecimal.ZERO);
        clientValuePageInfoDTO.setSalesRevenue(BigDecimal.ZERO);
        modelMap.put("clientValuePageInfo",clientValuePageInfoDTO);
        modelMap.put("clientInfos",clientBaseInfoService.getClientBaseInfos());
//        modelMap.put("clientValueDTO",new ClientValueDTO());
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

        if (clientBaseInfo!=null){
            dto.setName(clientBaseInfo.getName());
            dto.setClientId(client_id);
            dto.setSex(clientBaseInfo.getSex());
            dto.setAccommodationRevenue(accommodationRevenue);
            dto.setSalesRevenue(salesRevenue);
            dto.setNumberOfTrades(numberOfTrades);
            dto.setExpectedNumberOfTrades(clientTradeRecords.size());
            dto.setTradeRecords(clientTradeRecords);
        }else {
            dto.setName("不存在的用户");
        }

        modelMap.put("clientValuePageInfo",dto);
        modelMap.put("clientInfos",clientBaseInfoService.getClientBaseInfos());
//        //直接计算客户价值
//        modelMap.put("clientValue",getClientValue(
//                accommodationRevenue.doubleValue(),
//                salesRevenue.doubleValue(),
//                numberOfTrades,
//                clientTradeRecords.size(),
//                client_id));
        return "value_calculating";
    }

    @RequestMapping("getClientValue")
    @ResponseBody
    public ClientValueDTO getClientValue(Double accommodationIncome,
                                         Double consumeIncome,
                                         Integer numberOfTrades,
                                         Integer expectedNumberOfTrades,
                                         Integer clientId) throws ParseException {
        ClientValueDTO dto=new ClientValueDTO();

        int clientSum = clientBaseInfoService.getClientsSum();
        double totalClientAccommodationIncome = statisticsInfoService.getTotalClientAccommodationIncome();
        double totalClientConsumeIncome = statisticsInfoService.getTotalClientConsumeIncome();

        //房费收入率=某客户的房费收入额/所有客户的平均房费收入额
        dto.setAccommodationIncomeRate((accommodationIncome*clientSum)/totalClientAccommodationIncome);
        //消费收入率=某客户的消费收入额/所有客户的平均消费收入额
        dto.setConsumeIncomeRate((consumeIncome*clientSum)/totalClientConsumeIncome);
        //成功交易率=某客户的实际交易次数/某客户的预订次数
        double tradeSuccessRate=numberOfTrades==0?0:(double)numberOfTrades/expectedNumberOfTrades;
        dto.setTradeSuccessRate(tradeSuccessRate);
        //客户价值=0.35x房费收入率+0.35x消费收入率+0.3成功交易率
        dto.setClientValue(0.35*dto.getAccommodationIncomeRate()
                + 0.35*dto.getConsumeIncomeRate()
                + 0.3*dto.getTradeSuccessRate());

        //保存此次计算结果到数据库
        ClientValue clientValue=new ClientValue();
        BeanUtils.copyProperties(dto,clientValue);
        clientValue.setClientId(clientId);
        clientValue.setAccommodationIncome(accommodationIncome);
        clientValue.setConsumeIncome(consumeIncome);
        clientValue.setOrderTimes(expectedNumberOfTrades);
        clientValue.setTradeTimes(numberOfTrades);
        clientValueService.addOrUpdateClientValue(clientValue);

        //更新用户状态
        ClientBaseInfo client = clientBaseInfoService.getClientBaseInfoById(clientId);
        client.setState(client.getState()|ClientBaseInfo.CLIENT_VALUE_HAS_BEEN_COLLECTED);
        clientBaseInfoService.updateClientBaseInfo(client);
        //若用户已完成满意度录入和价值计算，将其信息收入分类模型数据源中
        if (client.getState()==ClientBaseInfo.CLIENT_VALUE_AND_SATISFACTION_HAVE_BEEN_COLLECTED){
            classificationDataSourceService.createOrUpdateClassificationDataSource(client,clientValue);
        }

        return dto;
    }

    @GetMapping("valueAnalyse")
    public String valueAnalyse(ModelMap modelMap){
        List<ClientValueDTO> DTOs=new LinkedList<>();

        List<ClientValue> clientValues = clientValueService.getClientValues();

        for (ClientValue clientValue : clientValues) {
            ClientBaseInfo clientInfo = clientBaseInfoService.getClientBaseInfoById(clientValue.getClientId());
            if (clientInfo!=null){
                ClientValueDTO dto=new ClientValueDTO();
                BeanUtils.copyProperties(clientInfo,dto);
                BeanUtils.copyProperties(clientValue,dto,"clientId");
                DTOs.add(dto);
            }
        }

        modelMap.put("clientValues",DTOs);
        return "value_analyse";
    }

}
