package com.neu.crm.bean;

import lombok.Data;

@Data
public class ClassificationDataSource {
    private Integer id;

    private Integer clientId;
    private Integer age;
    private Integer education;
    private Integer income;
    private Integer year;

    private Double satisfaction;

    private Double accommodationIncome;
    private Double consumeIncome;
    private Integer tradeTimes;
    private Integer orderTimes;
    private Double clientValue;
}
