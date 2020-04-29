package com.neu.crm.service.impl;

import com.neu.crm.bean.EvaluationIndex;
import com.neu.crm.mapper.EvaluationIndexMapper;
import com.neu.crm.service.EvaluationIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluationIndexServiceImpl implements EvaluationIndexService {
    @Autowired
    EvaluationIndexMapper evaluationIndexMapper;

    @Override
    public List<EvaluationIndex> getEvaluationIndices(int categoryLevel) {
        EvaluationIndex evaluationIndex=new EvaluationIndex();
        evaluationIndex.setCategoryLevel(categoryLevel);
        return evaluationIndexMapper.select(evaluationIndex);
    }

    @Override
    public EvaluationIndex getEvaluationIndexByIndexField(String indexField) {
        EvaluationIndex query=new EvaluationIndex();
        query.setIndexField(indexField);
        return evaluationIndexMapper.selectOne(query);
    }
}
