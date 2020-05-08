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
    //用户状态：1代表已录入满意度评价，2代表已录入计算出的客户价值，3代表评价和价值都录入了
    private Integer state;

    public static final int CLIENT_SATISFACTION_HAS_BEEN_COLLECTED=1;
    public static final int CLIENT_VALUE_HAS_BEEN_COLLECTED=2;
    public static final int CLIENT_VALUE_AND_SATISFACTION_HAVE_BEEN_COLLECTED=3;
}
