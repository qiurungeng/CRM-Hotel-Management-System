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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
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
    public String satisfactionCalculating(Integer categoryLevel,ModelMap modelMap){
        //默认采用二级评价指标
        if (categoryLevel==null){
            categoryLevel=2;
        }
        List<EvaluationIndex> indices = evaluationIndexService.getEvaluationIndices(categoryLevel);
        String[] indexNames=new String[indices.size()];
        for (int i=0;i<indices.size();i++) {
            indexNames[i]=indices.get(i).getIndexName();
        }
        modelMap.put("indicesName",indexNames);
        modelMap.put("indices",indices);
        modelMap.put("categoryLevel",categoryLevel);
        return "satisfaction_calculating";
    }

    @PostMapping("calculateClientSatisfaction")
    @ResponseBody
    public String calculateClientSatisfaction(@RequestBody JSONObject json){
        //各评价指标
        HashMap<Integer,EvaluationIndex> EIMap=new HashMap<>();
        //各评价指标对应的满意度得分
        HashMap<Integer, BigDecimal> CSMap=new HashMap<>();
        //初始化
        for (EvaluationIndex index : evaluationIndexService.getAllEvaluationIndices()) {
            EIMap.put(index.getId(),index);
            CSMap.put(index.getId(),BigDecimal.valueOf(0));
        }

        int clientId=json.getIntValue("clientId");
        json.remove("clientId");
        int categoryLevel = json.getIntValue("categoryLevel");
        json.remove("categoryLevel");
        for (Map.Entry<String, Object> entry : json.getInnerMap().entrySet()) {
            //由评价指标字段值找到评价指标
            EvaluationIndex ei = evaluationIndexService.getEvaluationIndexByIndexField(entry.getKey());
            //保存用户对于该项指标的评价
            ClientSatisfaction cs=new ClientSatisfaction();
            cs.setEvaluationId(ei.getId());
            cs.setClientId(clientId);
            cs.setScore(Double.valueOf((String)entry.getValue()));
            CSMap.put(ei.getId(),BigDecimal.valueOf(cs.getScore()));
            //若该层级为1、2，将该评价延伸到下一层级，予以保存
            switch (categoryLevel){
                //如果当前满意度评价属于对一级评价指标的评价
                case 1:
                    //更新该指标对应的所有二级评价
                    for (EvaluationIndex index2 : evaluationIndexService.getEvaluationIndexByParentId(cs.getEvaluationId())) {
                        CSMap.put(index2.getId(),BigDecimal.valueOf(cs.getScore()));
                        //更新该指标对应的所有三级评价
                        List<EvaluationIndex> indices3=evaluationIndexService.getEvaluationIndexByParentId(index2.getId());
                        for (EvaluationIndex index3 : indices3) {
                            CSMap.put(index3.getId(),BigDecimal.valueOf(cs.getScore()));
                        }
                    }
                    break;
                //如果当前满意度评价属于对二级评价指标的评价
                case 2:
                    //更新该指标对应的所有三级评价
                    for (EvaluationIndex index3 : evaluationIndexService.getEvaluationIndexByParentId(cs.getEvaluationId())) {
                        CSMap.put(index3.getId(),BigDecimal.valueOf(cs.getScore()));
                    }
                    break;
                default:
                    break;
            }
            //若该层级为2、3，将该层评价累加到上一层级
            switch (categoryLevel){
                case 2:
                    CSMap.put(ei.getParentIndex(),CSMap.get(ei.getParentIndex()).add(BigDecimal.valueOf(cs.getScore()*ei.getWeight())));
                    break;
                case 3:
                    EvaluationIndex parent=EIMap.get(ei.getParentIndex());
                    //对应的二级指标值+=得分*权重
                    CSMap.put(parent.getId(),CSMap.get(parent.getId()).add(BigDecimal.valueOf(cs.getScore()*ei.getWeight())));
                    //对应的一级指标值+=得分*权重*二级指标权重
                    CSMap.put(parent.getParentIndex(),CSMap.get(parent.getParentIndex()).add(BigDecimal.valueOf(cs.getScore()*ei.getWeight()*parent.getWeight())));
                    break;
                default:
                    break;
            }
        }
        //保存该用户对各项指标的满意度评分
        for (Integer evaluationIndexId : CSMap.keySet()) {
            insertOrUpdateClientSatisfaction(clientId,evaluationIndexId,CSMap.get(evaluationIndexId).doubleValue());
        }

        //返回计算结果到前端
        JSONObject result=new JSONObject();
        for (String attr : new String[]{
                "environment_aware",
                "service_aware",
                "facility_aware",
                "staff_aware",
                "shopping_aware",
                "total_aware"
        }) result.put(attr,CSMap.get(evaluationIndexService.getEvaluationIndexByIndexField(attr).getId()));


        return result.toJSONString();
    }

//    @RequestMapping("satisfactionAnalyzing")
//    public String satisfactionAnalyzing(){
//        return "satisfaction_analyse";
//    }

    /**
     * 插入或更新客户满意度
     * @param clientId 客户档案号
     * @param evaluationId 评价指标ID
     * @param score 评价得分
     */
    private void insertOrUpdateClientSatisfaction(int clientId,int evaluationId,double score){
        ClientSatisfaction cs = new ClientSatisfaction();
        cs.setEvaluationId(evaluationId);
        cs.setClientId(clientId);
        cs.setScore(score);
        clientSatisfactionService.insertOrUpdateClientSatisfaction(cs);
    }

}
