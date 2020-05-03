package com.neu.crm.controller;

import com.alibaba.fastjson.JSONObject;
import com.neu.crm.service.DataMiningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


@Controller
public class ClassifyController {

    @Autowired
    DataMiningService dataMiningService;

    @GetMapping("classify")
    public String classify(){
        return "classification";
    }

    @PostMapping("classify/k_means")
    @ResponseBody
    public JSONObject KMeans(@RequestBody JSONObject argJson) throws IOException, InvocationTargetException, IllegalAccessException {
        int K=argJson.getIntValue("K");
        String dataSet=argJson.getString("data_set");
        String[] attrs= argJson.getObject("attrs",String[].class);

        //暂时只有2019年数据
        dataMiningService.prepareDataSetForKMeans("2019",attrs);

        return dataMiningService.doKMeansCluster(K);
    }
}
