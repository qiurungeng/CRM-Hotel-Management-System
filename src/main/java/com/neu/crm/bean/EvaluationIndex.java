package com.neu.crm.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
public class EvaluationIndex {
    @Id
    @Column(name="id")
    @GeneratedValue(generator = "JDBC")
    private Integer id;
    private String indexName;
    private String indexField;
    private Double weight;
    private Integer categoryLevel;
    private Integer parentIndex;
}
