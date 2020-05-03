package com.neu.crm.service.impl;

import com.neu.crm.bean.ClientValue;
import com.neu.crm.mapper.ClientValueMapper;
import com.neu.crm.service.ClientValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientValueServiceImpl implements ClientValueService {
    @Autowired
    ClientValueMapper clientValueMapper;

    @Override
    public void addOrUpdateClientValue(ClientValue clientValue) {
        ClientValue query = clientValueMapper.selectByPrimaryKey(clientValue.getClientId());
        if (query==null){
            clientValueMapper.insertSelective(clientValue);
        }else {
            clientValueMapper.updateByPrimaryKey(clientValue);
        }

    }

    @Override
    public List<ClientValue> getClientValues() {
        return clientValueMapper.selectAll();
    }
}
