package com.neu.crm.service;

import com.neu.crm.bean.ClientAccommodationInfo;

import java.util.List;

public interface ClientAccommodationService {
    List<ClientAccommodationInfo> getClientAccommodationInfos();

    void addClientAccommodation(ClientAccommodationInfo clientAccommodationInfo);

    void updateClientAccommodationByClientId(ClientAccommodationInfo clientAccommodationInfo);

    List<ClientAccommodationInfo> getClientAccommodationInfosByClientId(int clientId);

    void deleteClientAccommodationInfoById(Integer id);

    void deleteClientAccommodationInfoByClientId(Integer clientId);
}
