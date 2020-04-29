package com.neu.crm.bean;

import lombok.Data;

@Data
public class EvaluationIndex {
    private Integer id;
    private String indexName;
    private String indexField;
    private Double weight;
    private Integer categoryLevel;
    private Integer parentIndex;
}
