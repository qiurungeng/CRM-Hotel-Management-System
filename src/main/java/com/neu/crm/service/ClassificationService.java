package com.neu.crm.service;

import com.neu.crm.bean.Classification;

import java.util.List;

public interface ClassificationService {
    void createClasses(String dataset, List<String> classes);

    List<Classification> getAllClassification();

    void updateClassification(Classification classification);

    Classification getClassificationByName(String name);
}
