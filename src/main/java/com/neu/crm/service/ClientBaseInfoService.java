package com.neu.crm.service;

import com.neu.crm.bean.ClientBaseInfo;

import java.util.List;

public interface ClientBaseInfoService {
    ClientBaseInfo addClientBaseInfo(ClientBaseInfo clientBaseInfo);

    List<ClientBaseInfo> getClientBaseInfos();

    ClientBaseInfo getClientBaseInfoById(Integer clientId);

    int getClientsSum();
}
