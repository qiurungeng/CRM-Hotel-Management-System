package com.neu.crm.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
public class ClientAccommodationInfo {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    private Integer id;
    private Integer clientId;
    private Integer roomId;
    private String date;
    private Integer dayCount;
    private Boolean state;
}
