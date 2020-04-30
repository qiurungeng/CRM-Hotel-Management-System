package com.neu.crm.service.impl;

import com.neu.crm.bean.ClientConsumeInfo;
import com.neu.crm.mapper.ClientConsumeInfoMapper;
import com.neu.crm.service.ClientConsumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ClientConsumeServiceImpl implements ClientConsumeService {

    @Autowired
    ClientConsumeInfoMapper clientConsumeInfoMapper;

    @Override
    public List<ClientConsumeInfo> getClientAccommodationInfos() {
        return clientConsumeInfoMapper.selectAll();
    }

    @Override
    public void addClientConsumeInfo(ClientConsumeInfo clientConsumeInfo) {
        System.out.println("[insert] : "+clientConsumeInfo);
        clientConsumeInfoMapper.insertSelective(clientConsumeInfo);
    }

    @Override
    public void modifyClientConsumeInfo(ClientConsumeInfo clientConsumeInfo) {
        System.out.println("[modify] : "+clientConsumeInfo);
        clientConsumeInfoMapper.updateByPrimaryKeySelective(clientConsumeInfo);
    }

    @Override
    public List<ClientConsumeInfo> getClientConsumeInfosByClientId(int clientId) {
        ClientConsumeInfo clientConsumeInfo=new ClientConsumeInfo();
        clientConsumeInfo.setClientId(clientId);
        return clientConsumeInfoMapper.select(clientConsumeInfo);
    }

    @Override
    public void deleteClientConsumeInfoByClientId(Integer clientId) {
        if (clientId!=null){
            ClientConsumeInfo clientConsumeInfo=new ClientConsumeInfo();
            clientConsumeInfo.setClientId(clientId);
            clientConsumeInfoMapper.delete(clientConsumeInfo);
        }
    }
}
