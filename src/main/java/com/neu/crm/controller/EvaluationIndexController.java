package com.neu.crm.controller;

import com.neu.crm.bean.EvaluationIndex;
import com.neu.crm.service.EvaluationIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class EvaluationIndexController {

    @Autowired
    EvaluationIndexService evaluationIndexService;

    @RequestMapping("evaluationIndexSetting")
    public String evaluationIndexSetting(ModelMap modelMap){
        List<EvaluationIndex> indices = evaluationIndexService.getAllEvaluationIndices();
        Map<Integer,EvaluationIndex> kvMap=new HashMap<>(indices.size()*2);
        for (EvaluationIndex index : indices) {
            kvMap.put(index.getId(),index);
        }
        modelMap.put("indices",indices);
        modelMap.put("kvMap",kvMap);
        return "evaluation_index";
    }

    @PostMapping("/addEvaluationIndex")
    @ResponseBody
    public boolean addEvaluationIndex(EvaluationIndex evaluationIndex){
        if (checkEvaluationIndex(evaluationIndex)){
            evaluationIndexService.addEvaluationIndex(evaluationIndex);
            return true;
        }
        return false;
    }

    @PostMapping("/modifyEvaluationIndex")
    @ResponseBody
    public boolean modifyEvaluationIndex(EvaluationIndex evaluationIndex){
        if (checkEvaluationIndex(evaluationIndex)){
            evaluationIndexService.modifyEvaluationIndex(evaluationIndex);
            return true;
        }
        return false;
    }


    @RequestMapping("deleteEvaluationIndex")
    @ResponseBody
    public boolean deleteEvaluationIndex(Integer id){
        if (id!=null){
            evaluationIndexService.deleteEvaluationIndexById(id);
            return true;
        }
        return false;
    }

    private boolean checkEvaluationIndex(EvaluationIndex index){
        //除了ID不能有空属性
        if (index.getIndexName()==null||
                index.getWeight()==null||
                index.getCategoryLevel()==null||
                index.getIndexField()==null||
                index.getParentIndex()==null
        )return false;
        //评价指标父层级只能为其上一级
        EvaluationIndex parent = evaluationIndexService.getEvaluationIndexById(index.getParentIndex());
        return (index.getCategoryLevel() == 3 && parent.getCategoryLevel() == 2) ||
                (index.getCategoryLevel() == 2 && parent.getCategoryLevel() == 1);
    }


}
