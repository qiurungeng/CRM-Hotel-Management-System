package com.neu.crm.service.impl;

import com.neu.crm.bean.ClientBaseInfo;
import com.neu.crm.mapper.ClientBaseInfoMapper;
import com.neu.crm.service.ClientBaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class  ClientBaseInfoServiceImpl implements ClientBaseInfoService {

    @Autowired
    private ClientBaseInfoMapper clientBaseInfoMapper;

    @Override
    public ClientBaseInfo addClientBaseInfo(ClientBaseInfo clientBaseInfo) {
        clientBaseInfoMapper.insertSelective(clientBaseInfo);
        System.out.println(clientBaseInfo);
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

}
