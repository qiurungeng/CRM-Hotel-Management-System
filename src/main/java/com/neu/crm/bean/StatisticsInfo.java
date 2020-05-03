package com.neu.crm.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
public class StatisticsInfo {
    @Id
    @Column(name = "id")
    private Integer id;
    private Double client_accommodation_income;
    private Double client_consume_income;
    private Integer year;
    private Integer season;
    private Integer month;
    private String notes;
}
