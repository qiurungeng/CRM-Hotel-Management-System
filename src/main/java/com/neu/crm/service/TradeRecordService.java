package com.neu.crm.service;

import com.neu.crm.bean.TradeRecord;

import java.util.List;

/**
 * 交易记录服务
 * 交易记录=住宿记录+消费记录
 */
public interface TradeRecordService {
    List<TradeRecord> getClientTradeRecords(int clientId);
}
