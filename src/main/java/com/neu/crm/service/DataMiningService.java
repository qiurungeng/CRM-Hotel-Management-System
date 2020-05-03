package com.neu.crm.service;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface DataMiningService {
    void prepareDataSetForKMeans(String year, String[] attrs) throws IOException, InvocationTargetException, IllegalAccessException;

    JSONObject doKMeansCluster(int K);
}
