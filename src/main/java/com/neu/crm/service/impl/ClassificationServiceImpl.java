package com.neu.crm.service.impl;

import com.neu.crm.bean.Classification;
import com.neu.crm.mapper.ClassificationMapper;
import com.neu.crm.service.ClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Service
public class ClassificationServiceImpl implements ClassificationService {

    @Autowired
    ClassificationMapper classificationMapper;

    @Override
    public void createClasses(String dataset, List<String> classes) {
        //清空原有的分类信息
        classificationMapper.delete(new Classification());
        //添加新的分类信息
        for (String className : classes) {
            Classification classification = new Classification();
            classification.setName(className);
            classificationMapper.insert(classification);
        }
    }

    @Override
    public List<Classification> getAllClassification() {
        return classificationMapper.selectAll();
    }

    @Override
    public void updateClassification(Classification classification) {
        Example example=new Example(Classification.class);
        example.createCriteria().andEqualTo("name",classification.getName());

        classificationMapper.updateByExampleSelective(classification,example);
    }

    @Override
    public Classification getClassificationByName(String name) {
        Classification classification = new Classification();
        classification.setName(name);
        return classificationMapper.selectOne(classification);
    }
}
