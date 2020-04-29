package com.neu.crm.controller;

import com.alibaba.fastjson.JSONObject;
import com.neu.crm.bean.ClientSatisfaction;
import com.neu.crm.bean.EvaluationIndex;
import com.neu.crm.service.ClientSatisfactionService;
import com.neu.crm.service.EvaluationIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class SatisfactionController {

    @Autowired
    ClientSatisfactionService clientSatisfactionService;

    @Autowired
    EvaluationIndexService evaluationIndexService;

    @RequestMapping("satisfactionCalculating")
    public String satisfactionCalculating(ModelMap modelMap){
        List<EvaluationIndex> indices = evaluationIndexService.getEvaluationIndices(2);
        String[] indexNames=new String[indices.size()];
        for (int i=0;i<indices.size();i++) {
            indexNames[i]=indices.get(i).getIndexName();
        }
        modelMap.put("indicesName",indexNames);
        modelMap.put("indices",indices);
        return "satisfaction_calculating";
    }

    @PostMapping("calculateClientSatisfaction")
    @ResponseBody
    public String calculateClientSatisfaction(@RequestBody JSONObject satisfactionJson){
        int total_score=0;
        double weighted_total_score=0;
        int clientId=satisfactionJson.getIntValue("clientId");
        satisfactionJson.remove("clientId");

        Map<String, Object> innerMap = satisfactionJson.getInnerMap();
        Set<Map.Entry<String, Object>> entries = innerMap.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            //由评价指标字段值找到评价指标
            EvaluationIndex ei = evaluationIndexService.getEvaluationIndexByIndexField(entry.getKey());
            //该评价指标所属层级
            //int categoryLevel = ei.getCategoryLevel();
            //保存用户对于该项指标的评价
            ClientSatisfaction clientSatisfaction=new ClientSatisfaction();
            clientSatisfaction.setEvaluationId(ei.getId());
            clientSatisfaction.setClientId(clientId);
            clientSatisfaction.setScore(Integer.valueOf((String)entry.getValue()));
            clientSatisfactionService.insertOrUpdateClientSatisfaction(clientSatisfaction);
            //计算整体满意度得分
            total_score += clientSatisfaction.getScore();
            weighted_total_score += clientSatisfaction.getScore() * ei.getWeight();
        }
        satisfactionJson.put("total_score",total_score);
        satisfactionJson.put("weighted_total_score",weighted_total_score);

        return satisfactionJson.toJSONString();
    }

    @RequestMapping("satisfactionAnalyzing")
    public String satisfactionAnalyzing(){

        return "satisfaction_analyse";
    }
}
