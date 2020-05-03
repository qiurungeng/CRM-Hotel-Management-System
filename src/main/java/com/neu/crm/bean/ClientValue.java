package com.neu.crm.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
public class ClientValue {
    @Id
    @Column(name = "client_id")
    private Integer clientId;
    private Double accommodationIncomeRate;
    private Double consumeIncomeRate;
    private Double tradeSuccessRate;
    private Double clientValue;
    private Double accommodationIncome;
    private Double consumeIncome;
    private Integer tradeTimes;
    private Integer orderTimes;
}
