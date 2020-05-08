package com.neu.crm.service.impl;

import com.neu.crm.bean.ClassificationDataSource;
import com.neu.crm.bean.ClientBaseInfo;
import com.neu.crm.bean.ClientSatisfaction;
import com.neu.crm.bean.ClientValue;
import com.neu.crm.mapper.ClassificationDataSourceMapper;
import com.neu.crm.mapper.ClientSatisfactionMapper;
import com.neu.crm.mapper.ClientValueMapper;
import com.neu.crm.service.ClassificationDataSourceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@Service
public class ClassificationDataSourceServiceImpl implements ClassificationDataSourceService {

    @Autowired
    ClientValueMapper clientValueMapper;

    @Autowired
    ClassificationDataSourceMapper classificationDataSourceMapper;

    @Autowired
    ClientSatisfactionMapper clientSatisfactionMapper;

    @Override
    public void createOrUpdateClassificationDataSource(ClientBaseInfo client, double satisfactionScore) throws ParseException {
        ClientValue clientValue = clientValueMapper.selectByPrimaryKey(client.getClientId());
        if (clientValue==null) return;
        createOrUpdateClassificationDataSource(client,satisfactionScore,clientValue);
    }

    @Override
    public void createOrUpdateClassificationDataSource(ClientBaseInfo client, ClientValue clientValue) throws ParseException {
        Example example=new Example(ClientSatisfaction.class);
        example.createCriteria()
                .andEqualTo("clientId",client.getClientId())
                .andEqualTo("evaluationId",1);
        ClientSatisfaction clientSatisfaction = clientSatisfactionMapper.selectOneByExample(example);
        createOrUpdateClassificationDataSource(client,clientSatisfaction.getScore(),clientValue);
    }

    @Override
    public ClassificationDataSource getClassificationDataSource(int clientId) {
        ClassificationDataSource classificationDataSource = new ClassificationDataSource();
        classificationDataSource.setClientId(clientId);
        return classificationDataSourceMapper.selectOne(classificationDataSource);
    }

    private void createOrUpdateClassificationDataSource(ClientBaseInfo client,double satisfactionScore,ClientValue clientValue) throws ParseException {
        ClassificationDataSource dataSource = new ClassificationDataSource();
        //clientId
        dataSource.setClientId(client.getClientId());
        //age
        Calendar calendar=Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(client.getBirthday()));
        int birthYear=calendar.get(Calendar.YEAR);

        dataSource.setAge(ageMap(currentYear-birthYear));
        //year
        dataSource.setYear(currentYear);
        //income per year
        dataSource.setIncome(client.getIncomeType());
        //education
        dataSource.setEducation(client.getEducation());
        //satisfaction
        dataSource.setSatisfaction(satisfactionScore);
        //client value
        BeanUtils.copyProperties(clientValue,dataSource);

        Example example=new Example(ClassificationDataSource.class);
        example.createCriteria().andEqualTo("clientId",client.getClientId());
        ClassificationDataSource db=classificationDataSourceMapper.selectOneByExample(example);
        if (db==null){
            classificationDataSourceMapper.insert(dataSource);
        }else {
            classificationDataSourceMapper.updateByExampleSelective(dataSource,example);
        }
    }


    private int ageMap(int age){
        if (age<20){
            return 1;
        }else if (age<30){
            return 2;
        }else if (age<40){
            return 3;
        }else if (age<50){
            return 4;
        }else return 5;
    }
}
