package com.neu.crm.service;

import com.neu.crm.bean.ClientValue;

import java.util.List;

public interface ClientValueService {

    void addOrUpdateClientValue(ClientValue clientValue);

    List<ClientValue> getClientValues();
}
