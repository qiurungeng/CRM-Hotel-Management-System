package com.neu.crm.service;

import com.neu.crm.bean.ClassificationDataSource;
import com.neu.crm.bean.ClientBaseInfo;
import com.neu.crm.bean.ClientValue;

import java.math.BigDecimal;
import java.text.ParseException;

public interface ClassificationDataSourceService {
    void createOrUpdateClassificationDataSource(ClientBaseInfo client, double satisfactionScore) throws ParseException;
    void createOrUpdateClassificationDataSource(ClientBaseInfo client, ClientValue clientValue) throws ParseException;
    ClassificationDataSource getClassificationDataSource(int clientId);
}
