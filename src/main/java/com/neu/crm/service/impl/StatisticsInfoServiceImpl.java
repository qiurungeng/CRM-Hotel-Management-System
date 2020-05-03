package com.neu.crm.service.impl;

import com.neu.crm.bean.StatisticsInfo;
import com.neu.crm.mapper.StatisticsInfoMapper;
import com.neu.crm.service.StatisticsInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsInfoServiceImpl implements StatisticsInfoService {

    @Autowired
    StatisticsInfoMapper statisticsInfoMapper;

    @Override
    public double getTotalClientAccommodationIncome() {
        return statisticsInfoMapper.getTotalClientAccommodationIncome();
    }

    @Override
    public double getTotalClientConsumeIncome() {
        return statisticsInfoMapper.getTotalClientConsumeIncome();
    }

    @Override
    public void addClientAccommodationIncome(Double income) {
        StatisticsInfo query=new StatisticsInfo();
        query.setNotes("酒店历史累计销售额");
        StatisticsInfo statisticsInfo = statisticsInfoMapper.selectOne(query);
        statisticsInfo.setClient_accommodation_income(statisticsInfo.getClient_accommodation_income()+income);
        statisticsInfoMapper.updateByPrimaryKeySelective(statisticsInfo);
    }

    @Override
    public void addClientConsumeIncome(double income) {
        StatisticsInfo query=new StatisticsInfo();
        query.setNotes("酒店历史累计销售额");
        StatisticsInfo statisticsInfo = statisticsInfoMapper.selectOne(query);
        statisticsInfo.setClient_consume_income(statisticsInfo.getClient_consume_income()+income);
        statisticsInfoMapper.updateByPrimaryKeySelective(statisticsInfo);
    }
}
