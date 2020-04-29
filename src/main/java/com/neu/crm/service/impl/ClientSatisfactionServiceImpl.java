package com.neu.crm.service.impl;

import com.neu.crm.bean.ClientSatisfaction;
import com.neu.crm.mapper.ClientSatisfactionMapper;
import com.neu.crm.service.ClientSatisfactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
public class ClientSatisfactionServiceImpl implements ClientSatisfactionService {
    @Autowired
    ClientSatisfactionMapper clientSatisfactionMapper;

    @Override
    public void insertOrUpdateClientSatisfaction(ClientSatisfaction clientSatisfaction) {
        ClientSatisfaction query=new ClientSatisfaction();
        query.setClientId(clientSatisfaction.getClientId());
        query.setEvaluationId(clientSatisfaction.getEvaluationId());
        ClientSatisfaction dbClientSatisfaction = clientSatisfactionMapper.selectOne(query);
        if (dbClientSatisfaction!=null){
            Example example=new Example(ClientSatisfaction.class);
            example.createCriteria().
                    andEqualTo("clientId",clientSatisfaction.getClientId()).
                    andEqualTo("evaluationId",clientSatisfaction.getEvaluationId());
            clientSatisfactionMapper.updateByExample(clientSatisfaction,example);
        }else {
            clientSatisfactionMapper.insertSelective(clientSatisfaction);
        }
    }

}
