package com.neu.crm.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
public class ClientSatisfaction {
    private Integer clientId;
    private Integer evaluationId;
    private Integer score;
}
