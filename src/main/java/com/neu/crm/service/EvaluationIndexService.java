package com.neu.crm.service;

import com.neu.crm.bean.EvaluationIndex;

import java.util.List;

public interface EvaluationIndexService {
    List<EvaluationIndex> getEvaluationIndices(int categoryLevel);

    EvaluationIndex getEvaluationIndexByIndexField(String indexField);
}
