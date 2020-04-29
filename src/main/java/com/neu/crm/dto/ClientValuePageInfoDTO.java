package com.neu.crm.dto;

import com.neu.crm.bean.TradeRecord;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 客户价值页面信息
 * 包含客户信息+客户消费记录
 */
@Data
public class ClientValuePageInfoDTO {
    private String name;
    private int clientId;
    private int id;
    private boolean sex;
    private BigDecimal accommodationRevenue;
    private BigDecimal salesRevenue;
    private int numberOfTrades;
    private int expectedNumberOfTrades;
    private List<TradeRecord> tradeRecords;
}
