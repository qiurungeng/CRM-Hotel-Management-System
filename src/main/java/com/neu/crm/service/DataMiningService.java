package com.neu.crm.service;

import com.alibaba.fastjson.JSONObject;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface DataMiningService {
    void prepareDataSetForKMeans(String year, String[] attrs) throws IOException, InvocationTargetException, IllegalAccessException;

    JSONObject doKMeansCluster(int K,String year);

    void saveKMeansClusteringResultForC45(JSONObject jsonObject) throws Exception;

    String c45(File trainSetFile,File testSetFile) throws Exception;
}
