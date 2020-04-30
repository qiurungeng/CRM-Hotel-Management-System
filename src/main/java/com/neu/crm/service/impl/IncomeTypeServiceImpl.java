package com.neu.crm.service.impl;

import com.neu.crm.bean.IncomeType;
import com.neu.crm.mapper.IncomeTypeMapper;
import com.neu.crm.service.IncomeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncomeTypeServiceImpl implements IncomeTypeService {
    @Autowired
    IncomeTypeMapper incomeTypeMapper;
    @Override
    public List<IncomeType> getIncomeTypes() {
        return incomeTypeMapper.selectAll();
    }
}
