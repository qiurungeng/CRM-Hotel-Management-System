package com.neu.crm.mapper;

import com.neu.crm.bean.StatisticsInfo;
import tk.mybatis.mapper.common.Mapper;

public interface StatisticsInfoMapper extends Mapper<StatisticsInfo> {
    double getTotalClientAccommodationIncome();
    double getTotalClientConsumeIncome();
}
