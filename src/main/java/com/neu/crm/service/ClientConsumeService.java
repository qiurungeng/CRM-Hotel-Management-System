package com.neu.crm.service;

import com.neu.crm.bean.ClientConsumeInfo;

import java.util.List;

public interface ClientConsumeService {
    List<ClientConsumeInfo> getClientAccommodationInfos();

    void addClientConsumeInfo(ClientConsumeInfo clientConsumeInfo);

    void modifyClientConsumeInfo(ClientConsumeInfo clientConsumeInfo);

    List<ClientConsumeInfo> getClientConsumeInfosByClientId(int clientId);
}
