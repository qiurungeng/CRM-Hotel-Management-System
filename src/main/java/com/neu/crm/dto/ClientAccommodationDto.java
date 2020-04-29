package com.neu.crm.dto;

import lombok.Data;

@Data
public class ClientAccommodationDto {
    private Integer id;
    private Integer clientId;
    private Integer roomId;
    private String date;
    private Integer dayCount;
    private String name;
    private Boolean state;
}
