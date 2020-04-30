package com.neu.crm.service.impl;

import com.neu.crm.bean.Education;
import com.neu.crm.mapper.EducationMapper;
import com.neu.crm.service.EducationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EducationServiceImpl implements EducationService {
    @Autowired
    EducationMapper educationMapper;

    @Override
    public List<Education> getEducationNames() {
        return educationMapper.selectAll();
    }
}
