package com.neu.crm.dto;

import lombok.Data;

@Data
public class ClientConsumeDTO {
    private Integer id;
    private Integer clientId;
    private String clientName;
    private Integer serviceId;
    private String serviceName;
    private Double price;
    private Integer count;
    private String date;
    private Double amount;
    private Boolean state;
}
