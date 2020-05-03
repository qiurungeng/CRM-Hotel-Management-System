package com.neu.crm.service;

import com.neu.crm.bean.EvaluationIndex;

import java.util.List;

public interface EvaluationIndexService {
    List<EvaluationIndex> getEvaluationIndices(int categoryLevel);

    EvaluationIndex getEvaluationIndexByIndexField(String indexField);

    List<EvaluationIndex> getAllEvaluationIndices();

    void addEvaluationIndex(EvaluationIndex evaluationIndex);

    void modifyEvaluationIndex(EvaluationIndex evaluationIndex);

    EvaluationIndex getEvaluationIndexById(Integer parentIndex);

    List<EvaluationIndex> getEvaluationIndexByParentId(Integer evaluationId);

    void deleteEvaluationIndexById(Integer id);
}
