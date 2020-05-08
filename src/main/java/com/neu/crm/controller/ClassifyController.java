package com.neu.crm.controller;

import com.alibaba.fastjson.JSONObject;
import com.neu.crm.bean.Classification;
import com.neu.crm.bean.ClassificationDataSource;
import com.neu.crm.bean.ClientBaseInfo;
import com.neu.crm.bean.ClientValue;
import com.neu.crm.service.*;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.util.StringUtil;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Controller
public class ClassifyController {

    @Autowired
    DataMiningService dataMiningService;

    @Autowired
    ClassificationService classificationService;

    @Autowired
    ClientBaseInfoService clientBaseInfoService;

    @Autowired
    ClassificationDataSourceService classificationDataSourceService;

    @Autowired
    ClientValueService clientValueService;

    @Value("${data_set_path}")
    String data_set_path;

    /**
     * 数据预处理页面
     */
    @GetMapping("classify")
    public String dataPreprocessPage(){
        return "classification";
    }

    /**
     * 对于选定的数据源进行KMeans聚类分析
     * @param argJson 参数JSON：包含1.指定年份的数据 2.K值选取
     * @return KMeans聚类得到的簇心
     */
    @PostMapping("classify/k_means")
    @ResponseBody
    public JSONObject KMeans(@RequestBody JSONObject argJson) throws IOException, InvocationTargetException, IllegalAccessException {
        int K=argJson.getIntValue("K");
        String dataSet=argJson.getString("data_set");
        String[] attrs= argJson.getObject("attrs",String[].class);

        dataMiningService.prepareDataSetForKMeans(dataSet,attrs);
        return dataMiningService.doKMeansCluster(K,dataSet);
    }

    @GetMapping("preprocess_frag")
    public String frag(){
        return "/common/preprocess_frag1";
    }

    /**
     * 前台管理员对KMeans聚类结果进行分析，并为其设置的分类信息
     * 这些分类信息将被应用到KMeans聚类所使用的的源数据集上，对数据集中所有数据设定分类信息，
     * 并按管理员设定的比例将其划分为决策树构建所需的训练集与测试集。
     * @param jsonObject 数据集分类信息
     * @return boolean 操作成功或失败
     */
    @PostMapping("classify/c45")
    @ResponseBody
    public boolean c45DecisionTree(@RequestBody JSONObject jsonObject) throws Exception {
        dataMiningService.saveKMeansClusteringResultForC45(jsonObject);
        return true;
    }





    /**
     * 决策树模型设置页面
     */
    @GetMapping("decisionTreeModelSetting")
    public String decisionTreeModelSettingPage(ModelMap modelMap) throws FileNotFoundException {
        List<String> dataSetNames=new ArrayList<>();
        File file = ResourceUtils.getFile(data_set_path);
        File[] dataSets = file.listFiles();
        assert dataSets != null;
        for (File dataSet : dataSets) {
            if (dataSet.getName().contains("_train")){
                dataSetNames.add(dataSet.getName().substring(0,dataSet.getName().length()-11));
            }
        }
        modelMap.put("dataSets",dataSetNames);
        return "decision_tree";
    }

    /**
     * 根据前端选定的训练集与测试集构建决策树，并测试决策树分类的正确率
     * @param dataset 训练集与测试集名称
     * @return 由训练集训练的决策树模型的测试结果
     */
    @PostMapping(value = "testDecisionTreeModel")
    @ResponseBody
    public JSONObject testModel(String dataset) throws Exception {
        File dataset_train = ResourceUtils.getFile(data_set_path+dataset+"_train.arff");
        File dataset_test = ResourceUtils.getFile(data_set_path+dataset+"_test.arff");
        JSONObject result=new JSONObject();
        result.put("modelTestResult",dataMiningService.c45(dataset_train, dataset_test));
        return result;
    }

    /**
     * 序列化保存该决策树模型，作为系统的分类默认决策树模型
     */
    @RequestMapping("saveDecisionTree")
    @ResponseBody
    public String saveModel(String dataset) throws IOException, ClassNotFoundException {
        // 该决策树对应的训练集
        File trainSet = ResourceUtils.getFile(data_set_path+dataset+"_train.arff");
        ArffLoader loader=new ArffLoader();
        loader.setSource(trainSet);
        // 将训练集中管理员定义的分类名称保存到数据库
        Instances instances=loader.getDataSet();
        Enumeration<Object> classes = instances.attribute("class").enumerateValues();
        List<String> classList=new ArrayList<>();
        while (classes.hasMoreElements()){
            classList.add(classes.nextElement().toString());
        }
        classificationService.createClasses(dataset,classList);

        // 决策树模型源文件路径
        File model = ResourceUtils.getFile(data_set_path+"tempDecisionTree.ser");
        ObjectInputStream ois=new ObjectInputStream(new FileInputStream(model));
        J48 classifier = (J48) ois.readObject();
        ois.close();
        // 目标文件路径
        File endDirection=ResourceUtils.getFile(data_set_path+"permanent");
        if(!endDirection.exists()) {
            endDirection.mkdirs();
        }

        // 保留该训练集的Instance样式
        Instances attrsInfo = new Instances(instances,0,0);
        FileWriter writer=new FileWriter(new File(endDirection+ File.separator+ "defaultInstanceForm.arff"));
        writer.write(attrsInfo.toString());
        writer.close();
        // 保存该决策树对象
        File endFile=new File(endDirection+ File.separator+ "defaultDecisionTree.ser");
        ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(endFile));
        oos.writeObject(classifier);
//        oos.close();
//        try {
//            if (model.renameTo(endFile)) {
//                return "保存成功";
//            }
//        }catch(Exception e) {
//            System.out.println("文件移动出现异常！起始路径：{"+model.getAbsolutePath()+"}");
//        }
        return "保存成功";
    }


    /**
     * 分类管理页面
     */
    @GetMapping("classificationManagement")
    public String classificationManagementPage(ModelMap modelMap){
        List<Classification> classificationList=classificationService.getAllClassification();
        modelMap.put("classificationList",classificationList);
        modelMap.put("clientInfos",clientBaseInfoService.getClientBaseInfos());
        return "classification_management";
    }

    @PostMapping("updateClassification")
    @ResponseBody
    public boolean updateClassification(Classification classification){
        if (classification.getName()!=null){
            classificationService.updateClassification(classification);
            return true;
        }
        return false;
    }

    /**
     * 利用现有的决策树模型为用户分类
     * @param clientId 用户档案号
     */
    @PostMapping("getClientClassification")
    @ResponseBody
    public JSONObject getClientClassification(int clientId) throws Exception {
        JSONObject jsonObject=new JSONObject();

        //根据clientId查找此用户用于分类的数据
        ClassificationDataSource dataSource = classificationDataSourceService.getClassificationDataSource(clientId);
        if (dataSource==null){
            return null;
        }
        //获取存有Instances格式的文件，得到其中规定的属性值，应用于dataSource构造决策树可用的数据
        ArffLoader loader = new ArffLoader();
        loader.setSource(ResourceUtils.getFile(data_set_path+"/permanent/defaultInstanceForm.arff"));
        Instances instances = loader.getDataSet();
        StringBuilder sb=new StringBuilder();
        sb.append(instances.toString());
        //由下划线属性名获得对应的驼峰命名的getter方法名
        String[] getterNames=new String[instances.numAttributes()-1];
        for (int i = 0; i < getterNames.length; i++) {
            Attribute attribute = instances.attribute(i);
            getterNames[i] = "get_"+attribute.name();
            getterNames[i] = StringUtil.underlineToCamelhump(getterNames[i]);
        }
        //方法名通过反射调用getter，获得 数据源 对应属性的值
        for (String getterName : getterNames) {
            Method getter = dataSource.getClass().getMethod(getterName);
            sb.append(getter.invoke(dataSource)).append(",");
        }
        sb.append(instances.attribute("class").value(0)); //分类属性未知

        //用于决策树分类的Instance
        ArffLoader loader2=new ArffLoader();
        loader2.setSource(new ByteArrayInputStream(sb.toString().getBytes()));
        Instances clientInstances = loader2.getDataSet();
        clientInstances.setRelationName("client_classification");
        clientInstances.setClassIndex(clientInstances.numAttributes()-1);

        //加载决策树
        ObjectInputStream ois= new ObjectInputStream(
                new FileInputStream(
                        ResourceUtils.getFile(data_set_path+"/permanent/defaultDecisionTree.ser")));
        Classifier classifier = (J48) ois.readObject();
        if (classifier==null){
            return null;
        }

        //对Instance执行分类，得到分类结果
        double classificationResult = classifier.classifyInstance(clientInstances.instance(0));
        String cName = clientInstances.attribute(clientInstances.numAttributes() - 1).value((int) classificationResult);
        ois.close();

        //返回JSON结果到前端
        Classification classification = classificationService.getClassificationByName(cName);
        ClientBaseInfo clientInfo = clientBaseInfoService.getClientBaseInfoById(clientId);
        ClientValue clientValue = clientValueService.getClientValueById(clientId);
        jsonObject.put("classification",classification);
        jsonObject.put("clientInfo",clientInfo);
        jsonObject.put("clientValue",clientValue);

        return jsonObject;
    }

}
