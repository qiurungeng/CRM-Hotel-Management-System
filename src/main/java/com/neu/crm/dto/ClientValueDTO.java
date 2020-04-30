package com.neu.crm.dto;

import lombok.Data;

@Data
public class ClientValueDTO {
    private int clientId;
    private String name;
    private long phoneNumber;
    private double accommodationIncomeRate;
    private double consumeIncomeRate;
    private double tradeSuccessRate;
    private double clientValue;
}
