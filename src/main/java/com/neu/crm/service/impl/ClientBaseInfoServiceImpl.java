package com.neu.crm.service.impl;

import com.neu.crm.bean.ClientBaseInfo;
import com.neu.crm.mapper.ClientBaseInfoMapper;
import com.neu.crm.service.ClientAccommodationService;
import com.neu.crm.service.ClientBaseInfoService;
import com.neu.crm.service.ClientConsumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class  ClientBaseInfoServiceImpl implements ClientBaseInfoService {

    @Autowired
    private ClientBaseInfoMapper clientBaseInfoMapper;
    @Autowired
    private ClientAccommodationService clientAccommodationService;
    @Autowired
    private ClientConsumeService clientConsumeService;

    @Override
    public ClientBaseInfo addClientBaseInfo(ClientBaseInfo clientBaseInfo) {
        clientBaseInfoMapper.insertSelective(clientBaseInfo);
        return clientBaseInfo;
    }

    @Override
    public List<ClientBaseInfo> getClientBaseInfos() {
        return clientBaseInfoMapper.selectAll();
    }

    @Override
    public ClientBaseInfo getClientBaseInfoById(Integer clientId) {
        return clientBaseInfoMapper.selectByPrimaryKey(clientId);
    }

    @Override
    public int getClientsSum() {
        return clientBaseInfoMapper.selectCount(new ClientBaseInfo());
    }

    @Override
    public ClientBaseInfo updateClientBaseInfo(ClientBaseInfo clientBaseInfo) {
        clientBaseInfoMapper.updateByPrimaryKeySelective(clientBaseInfo);
        return clientBaseInfo;
    }

    @Override
    public ClientBaseInfo deleteClientBaseInfo(Integer clientId) {
        ClientBaseInfo clientBaseInfo=new ClientBaseInfo();
        if (clientId!=null){
            clientBaseInfo.setClientId(clientId);
            clientBaseInfoMapper.delete(clientBaseInfo);
            clientAccommodationService.deleteClientAccommodationInfoByClientId(clientId);
            clientConsumeService.deleteClientConsumeInfoByClientId(clientId);
            return clientBaseInfo;
        }
        return null;
    }

}
