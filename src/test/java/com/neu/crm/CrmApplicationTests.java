package com.neu.crm;

import com.alibaba.fastjson.JSONObject;
import com.neu.crm.controller.ValueController;
import com.neu.crm.dto.ClientValueDTO;
import com.neu.crm.mapper.DataMiningMapper;
import com.neu.crm.mapper.StatisticsInfoMapper;
import com.neu.crm.service.ClientBaseInfoService;
import com.neu.crm.service.DataMiningService;
import com.neu.crm.util.MyKMeans;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import weka.clusterers.SimpleKMeans;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


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
    public void testClientValueCalculating(){
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

    @Test
    public void testMyKMeans() throws IOException {
        //读取数据
        MyKMeans.readF1();
        //第一次迭代
        MyKMeans.Kluster();
        //第一次迭代后计算簇心
        MyKMeans.CalCentroid();
        //不断迭代，直到收敛
        MyKMeans.RecursionKluster();
    }

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
        JSONObject jsonObject = dataMiningService.doKMeansCluster(4);
        System.out.println(jsonObject);
    }


}
