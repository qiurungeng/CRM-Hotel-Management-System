package com.neu.crm.service.impl;

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
}
