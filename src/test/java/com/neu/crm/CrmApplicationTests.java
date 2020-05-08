package com.neu.crm;

import com.alibaba.fastjson.JSONObject;
import com.neu.crm.controller.ValueController;
import com.neu.crm.dto.ClientValueDTO;
import com.neu.crm.mapper.StatisticsInfoMapper;
import com.neu.crm.service.ClientBaseInfoService;
import com.neu.crm.service.DataMiningService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.clusterers.SimpleKMeans;
import weka.core.*;
import weka.core.converters.ArffLoader;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;


@SpringBootTest
class CrmApplicationTests {

    @Autowired
    ClientBaseInfoService clientBaseInfoService;
    @Autowired
    StatisticsInfoMapper statisticsInfoMapper;
    @Autowired
    ValueController valueController;
    @Autowired
    DataMiningService dataMiningService;

    @Test
    void contextLoads() {
        //获得当前用户数量
        System.out.println(clientBaseInfoService.getClientsSum());
    }

    @Test
    void testStatisticsMapper(){
        System.out.println(statisticsInfoMapper.getTotalClientAccommodationIncome());
        System.out.println(statisticsInfoMapper.getTotalClientConsumeIncome());
    }

    @Test
    public void testClientValueCalculating() throws ParseException {
        ClientValueDTO dto = valueController.getClientValue(2450.0,
                530.0,
                3,
                6,1);
        System.out.println(dto);
    }

    @Test
    public void testKMeans(){
        Instances ins = null;

        SimpleKMeans KM = null;
        DistanceFunction disFun = null;

        try {
            // 读入样本数据
            File file = new File("static/data1.txt");
            System.out.println(file.exists());
            ArffLoader loader = new ArffLoader();
            loader.setFile(file);
            ins = loader.getDataSet();

            // 初始化聚类器 （加载算法）
            KM = new SimpleKMeans();
            KM.setNumClusters(4);       //设置聚类要得到的类别数量
            KM.buildClusterer(ins);     //开始进行聚类

            System.out.println(KM.preserveInstancesOrderTipText());
            System.out.println("-----------------@@@---------------");
            // 打印聚类结果
            System.out.println(KM.toString());

            System.out.println("-----------------@@@---------------");

            // 4.打印聚类结果
            Instances tempIns = null;
            tempIns = KM.getClusterCentroids();
            System.out.println("CentroIds: " + tempIns);
            System.out.println("-------------------/n");
            for (Instance temp : tempIns) {
                System.out.println(temp.numAttributes());
                for (int j = 0; j < temp.numAttributes(); j++) {
                    System.out.print(temp.value(j) + ",");
                }
                System.out.println("");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void testMyKMeans() throws IOException {
//        //读取数据
//        MyKMeans.readF1();
//        //第一次迭代
//        MyKMeans.Kluster();
//        //第一次迭代后计算簇心
//        MyKMeans.CalCentroid();
//        //不断迭代，直到收敛
//        MyKMeans.RecursionKluster();
//    }

    @Test
    public void testKMeansCSV(){
        Instances ins = null;

        SimpleKMeans KM = null;
        DistanceFunction disFun = null;

        try {
            // 读入样本数据
            File file = new File("data_set/2019.csv");
            ArffLoader loader = new ArffLoader();
            loader.setFile(file);
            ins = loader.getDataSet();

            // 初始化聚类器 （加载算法）
            KM = new SimpleKMeans();
            KM.setNumClusters(8);       //设置聚类要得到的类别数量
            KM.buildClusterer(ins);     //开始进行聚类

            System.out.println(KM.preserveInstancesOrderTipText());
            System.out.println("-----------------@@@---------------");
            // 打印聚类结果
            System.out.println(KM.toString());

            System.out.println("-----------------@@@---------------");

            // 4.打印聚类结果
            Instances tempIns = null;
            tempIns = KM.getClusterCentroids(); //得到簇心

            System.out.println("矩心: \n" + tempIns);
            System.out.println("------------------\n");
            for (Instance temp : tempIns) {
                System.out.println("属性数目："+temp.numAttributes()+"\t簇心点：");
                for (int j = 0; j < temp.numAttributes(); j++) {
                    System.out.println(temp.attribute(j).name() + ":" +temp.value(j));
                }
                System.out.println("\n");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPreparingData() throws IOException, InvocationTargetException, IllegalAccessException {
        dataMiningService.prepareDataSetForKMeans("2019",new String[]{"education","satisfaction","client_value"});
    }

    @Test
    public void testStringBuilder(){
        StringBuilder sb=new StringBuilder();
        sb.append("asdfg");
        sb.replace(sb.length()-1,sb.length(),"\n");
        sb.append("haha");
        System.out.println(sb);
    }

    @Test
    public void testDoKMeansCluster(){
        JSONObject jsonObject = dataMiningService.doKMeansCluster(4,"2019");
        System.out.println(jsonObject);
    }

    @Test
    public void testJ48() throws Exception {
        File inputfile = new File("data_set/j48.arff");
        ArffLoader loader = new ArffLoader();
        loader.setFile(inputfile);

        Instances insTrain = loader.getDataSet();
        insTrain.setClassIndex(insTrain.numAttributes()-1);

        inputfile = new File("data_set/j48.arff");
        loader.setFile(inputfile);
        Instances insTest = loader.getDataSet();
        insTest.setClassIndex(insTest.numAttributes()-1);

        double sum = insTest.numInstances();
        int right = 0;
        Classifier classifier = new J48();
        classifier.buildClassifier(insTrain);

        for(int i = 0; i < sum; i++) {
            if(classifier.classifyInstance(insTest.instance(i)) == insTest.instance(i).classValue()) {
                right++;
            }
            System.out.println(classifier.classifyInstance(insTest.instance(i))+" :　"+insTest.instance(i).classValue());
            System.out.println(insTest.instance(i));
        }
        System.out.println("分类准确率："+right/sum);
    }


    @Test
    public void test() throws IOException {
        //从文件中读取KMeans聚类得到的簇心
        File clustersFile = ResourceUtils.getFile("data_set/2019.arff");
        ArffLoader loader1 = new ArffLoader();
        loader1.setFile(clustersFile);
        Instances clusters = loader1.getDataSet();

        String[] attrs=new String[]{"A","B","C","D","E","F"};
        List<String> list = Arrays.asList(attrs);

        Attribute attribute = new Attribute("class",list);
        clusters.insertAttributeAt(attribute,clusters.numAttributes());
        System.out.println(clusters);

        System.out.println(clusters.numAttributes());

        for (Instance cluster : clusters) {
            cluster.setValue(clusters.numAttributes()-1,"C");
        }
        System.out.println(clusters);
    }

    @Test
    public void testSaveKMeansClusteringResultForC45() throws Exception {
        JSONObject jsonObject = JSONObject.parseObject("{\"train_set_ratio\":\"1\",\"test_set_ratio\":\"1\",\"dataset_name\":\"ASD\",\"clusters\":[{\"income\":\"1.1794871794871795\",\"client_value\":\"0.7471272844298089\",\"satisfaction\":\"3.7948717948717947\",\"class\":\"A\"},{\"income\":\"2.761904761904762\",\"client_value\":\"0.7278175098095371\",\"satisfaction\":\"3\",\"class\":\"S\"},{\"income\":\"3.725806451612903\",\"client_value\":\"0.5846245148326874\",\"satisfaction\":\"3.8870967741935485\",\"class\":\"D\"},{\"income\":\"3.75\",\"client_value\":\"0.5758082122334579\",\"satisfaction\":\"5\",\"class\":\"A\"},{\"income\":\"3.793103448275862\",\"client_value\":\"1.621959981771195\",\"satisfaction\":\"3.9310344827586205\",\"class\":\"S\"},{\"income\":\"3.8333333333333335\",\"client_value\":\"1.0239164236478524\",\"satisfaction\":\"3.7777777777777777\",\"class\":\"D\"},{\"income\":\"4\",\"client_value\":\"1.1169300317044921\",\"satisfaction\":\"5\",\"class\":\"A\"},{\"income\":\"4.103448275862069\",\"client_value\":\"0.941928955554172\",\"satisfaction\":\"1\",\"class\":\"S\"},{\"income\":\"5\",\"client_value\":\"0.7672827474699343\",\"satisfaction\":\"3.9651162790697674\",\"class\":\"D\"},{\"income\":\"5\",\"client_value\":\"1.4290986110509724\",\"satisfaction\":\"4.285714285714286\",\"class\":\"D\"}],\"attrs\":[\"income\",\"client_value\",\"satisfaction\",\"class\"]}\n");
        dataMiningService.saveKMeansClusteringResultForC45(jsonObject);
    }

//    @Test
//    void testC45() throws Exception {
//        dataMiningService.c45();
//    }

    @Value("${data_set_path}")
    String data_set_path;

    @Test
    void testDecisionTree() throws Exception {

        ObjectInputStream ois=
                new ObjectInputStream(
                        new FileInputStream(ResourceUtils.getFile(data_set_path+"/permanent/defaultDecisionTree.ser")));
        J48 decisionTree = (J48)ois.readObject();


//        System.out.println(decisionTree.toString());
//        System.out.println("_____________________________________________");
//        String[] options = decisionTree.getOptions();
//        for (String option : options) {
//            System.out.println(option);
//        }
//        System.out.println("_____________________________________________");
//        System.out.println(decisionTree.graph());
//        System.out.println(decisionTree.binarySplitsTipText());
//        System.out.println(decisionTree.prefix());
//        System.out.println(decisionTree.collapseTreeTipText());
//        System.out.println(decisionTree.getCollapseTree());
//        System.out.println("_____________________________________________");

        File inputfile = ResourceUtils.
                getFile(data_set_path+"测试_test.arff");
        ArffLoader loader = new ArffLoader();
        loader.setFile(inputfile);
        Instances insTest = loader.getDataSet();
        insTest.setClassIndex(insTest.numAttributes()-1); //标出分类属性

        double sum = insTest.numInstances();
        int right = 0;
        StringBuilder sb=new StringBuilder();
        HashMap<Double,String> map=new HashMap<>();

        sb.append("样例属性：");
        Enumeration<Attribute> attributes = insTest.enumerateAttributes();
        while (attributes.hasMoreElements()){
            sb.append(attributes.nextElement().name()).append(",");
        }
        sb.replace(sb.length()-1,sb.length(),"\t");
        sb.append("样例分类属性：");
        Enumeration<Object> attributeEnumeration = insTest.classAttribute().enumerateValues();
        double e=0;
        while (attributeEnumeration.hasMoreElements()){
            String className=attributeEnumeration.nextElement().toString();
            map.put(e++,className);
            sb.append(className).append(",");
        }
        sb.replace(sb.length()-1,sb.length(),"\n测试详情:\n");
        sb.append("格式:  【{测试样例}】 {样例所属分类} : {决策树分类结果}\n");

        for(int i = 0; i < sum; i++) {
            if(decisionTree.classifyInstance(insTest.instance(i)) == insTest.instance(i).classValue()) {
                right++;
            }
            sb.append("【").append(insTest.instance(i).toString()).append("】 ")
                    .append(map.get(insTest.instance(i).classValue())).append(" : ")
                    .append(map.get(decisionTree.classifyInstance(insTest.instance(i)))).append("\n");
        }
        sb.append("决策树模型分类准确率：").append(right/sum);
        System.out.println(sb.toString());
    }


}
