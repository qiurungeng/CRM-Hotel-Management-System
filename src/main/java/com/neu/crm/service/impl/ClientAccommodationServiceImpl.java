package com.neu.crm.service.impl;

import com.neu.crm.bean.ClientAccommodationInfo;
import com.neu.crm.mapper.ClientAccommodationInfoMapper;
import com.neu.crm.service.ClientAccommodationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ClientAccommodationServiceImpl implements ClientAccommodationService {

    @Autowired
    ClientAccommodationInfoMapper clientAccommodationInfoMapper;

    public List<ClientAccommodationInfo> getClientAccommodationInfos() {
        return clientAccommodationInfoMapper.selectAll();
    }

    @Override
    public void addClientAccommodation(ClientAccommodationInfo clientAccommodationInfo) {
        clientAccommodationInfoMapper.insertSelective(clientAccommodationInfo);
    }

    @Override
    public void updateClientAccommodationByClientId(ClientAccommodationInfo clientAccommodationInfo) {
        clientAccommodationInfoMapper.updateByPrimaryKeySelective(clientAccommodationInfo);
    }

    @Override
    public List<ClientAccommodationInfo> getClientAccommodationInfosByClientId(int clientId) {
        Example example=new Example(ClientAccommodationInfo.class);
        example.createCriteria().andEqualTo("clientId",clientId);
        return clientAccommodationInfoMapper.selectByExample(example);
    }

}
