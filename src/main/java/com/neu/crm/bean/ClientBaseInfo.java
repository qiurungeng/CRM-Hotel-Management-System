package com.neu.crm.bean;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name="client_base_info")
@Data
public class ClientBaseInfo {
    @Id
    @Column(name = "client_id")
    @GeneratedValue(generator = "JDBC")
    private Integer clientId;
    private String name;
    private Boolean sex;
    private String birthday;
    private String profession;
    private String birthPlace;
    private String address;
    private Long phoneNumber;
    private String idCardNumber;
    private String idCardType;
    private String extras;
    private String hobbies;
    private Integer education;
    private Integer incomeType;
}
