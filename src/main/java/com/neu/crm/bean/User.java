package com.neu.crm.bean;

import lombok.Data;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String profession;
    private String phoneNumber;
    private String email;
    private String address;
    private String notes;
    private Boolean isApproved;
    private Boolean isAuthorized;
    private Integer type;
}
