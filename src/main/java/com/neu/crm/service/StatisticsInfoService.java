package com.neu.crm.service;


public interface StatisticsInfoService {
    double getTotalClientAccommodationIncome();
    double getTotalClientConsumeIncome();

    void addClientAccommodationIncome(Double income);

    void addClientConsumeIncome(double income);
}
