package com.neu.crm.bean;

import lombok.Data;

@Data
public class TradeRecord {
    private String date;
    private String name;
    private Boolean state;
    private Double expense;
    private String type;
}
