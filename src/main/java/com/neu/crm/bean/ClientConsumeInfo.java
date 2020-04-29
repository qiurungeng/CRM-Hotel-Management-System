package com.neu.crm.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
public class ClientConsumeInfo {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    private Integer id;
    private Integer clientId;
    private String date;
    private Integer serviceId;
    private Integer count;
    private Boolean state;
}
