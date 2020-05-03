package com.neu.crm.service.impl;

import com.neu.crm.bean.EvaluationIndex;
import com.neu.crm.mapper.EvaluationIndexMapper;
import com.neu.crm.service.EvaluationIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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

    @Override
    public List<EvaluationIndex> getAllEvaluationIndices() {
        return evaluationIndexMapper.selectAll();
    }

    @Override
    public void addEvaluationIndex(EvaluationIndex evaluationIndex) {
        evaluationIndexMapper.insertSelective(evaluationIndex);
    }

    @Override
    public void modifyEvaluationIndex(EvaluationIndex evaluationIndex) {
        evaluationIndexMapper.updateByPrimaryKeySelective(evaluationIndex);
    }

    @Override
    public EvaluationIndex getEvaluationIndexById(Integer parentIndex) {
        return evaluationIndexMapper.selectByPrimaryKey(parentIndex);
    }

    @Override
    public List<EvaluationIndex> getEvaluationIndexByParentId(Integer evaluationId) {
        EvaluationIndex query=new EvaluationIndex();
        query.setParentIndex(evaluationId);
        return evaluationIndexMapper.select(query);
    }

    @Override
    public void deleteEvaluationIndexById(Integer id) {
        evaluationIndexMapper.deleteByPrimaryKey(id);
    }
}
