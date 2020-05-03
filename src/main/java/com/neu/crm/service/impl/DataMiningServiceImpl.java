package com.neu.crm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.neu.crm.bean.ClassificationDataSource;
import com.neu.crm.mapper.DataMiningMapper;
import com.neu.crm.service.DataMiningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import tk.mybatis.mapper.util.StringUtil;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataMiningServiceImpl implements DataMiningService {

    @Autowired
    DataMiningMapper dataMiningMapper;

    /**
     * 为K-Means算法预备训练用的数据集，存储到指定文本
     * @param year 选取指定年份的数据
     * @param attrs 将要进行聚类分析的属性
     */
    @Override
    public void prepareDataSetForKMeans(String year, String[] attrs) throws IOException, InvocationTargetException, IllegalAccessException {
        //属性转换为驼峰命名
        List<String> attrNames = new ArrayList<>();
        for (String attr : attrs) {
            attrNames.add(StringUtil.underlineToCamelhump(attr));
        }

        //数据库查询该年份所有数据
        ClassificationDataSource query=new ClassificationDataSource();
        query.setYear(Integer.parseInt(year));
        List<ClassificationDataSource> dataSet = dataMiningMapper.select(query);

        //预备写入的文本文件
        FileWriter writer=new FileWriter(ResourceUtils.getFile("classpath:data_set/"+year+".txt"));
        writer.write("@Relation "+year+"\n");

        //找出属性名对应的getter方法
        Method[] methods = ClassificationDataSource.class.getMethods();
        List<Method> getters=new ArrayList<>();
        for (Method method : methods) {
            for (String attrName : attrNames) {
                if (method.getName().equalsIgnoreCase("get"+attrName)){
                    getters.add(method);
                    writer.write("@ATTRIBUTE "+StringUtil.camelhumpToUnderline(attrName)+" REAL\n");
                    break;
                }
            }
        }
        writer.write("@Data\n");

        //拼接数据，写入到文本
        StringBuilder sb=new StringBuilder();
        for (ClassificationDataSource data : dataSet) {
            for (Method getter : getters) {
                sb.append(getter.invoke(data)).append(",");
            }
            sb.replace(sb.length()-1,sb.length(),"\n");
        }
        writer.write(sb+"\n");

        writer.close();
    }

    @Override
    public JSONObject doKMeansCluster(int K) {
        Instances ins;
        SimpleKMeans KM;
        JSONObject result=new JSONObject();



        try {
            // 读入样本数据
            File file = new File("data_set/2019.txt");
            ArffLoader loader = new ArffLoader();
            loader.setFile(file);
            ins = loader.getDataSet();

            // 初始化聚类器 （加载算法）
            KM = new SimpleKMeans();
            KM.setNumClusters(K);       //设置聚类要得到的类别数量
            KM.buildClusterer(ins);     //开始进行聚类

            Instances tempIns = KM.getClusterCentroids();   //得到簇心
            double[] clusterSizes = KM.getClusterSizes();   //每个簇所包含点的个数

            JSONObject[] objs=new JSONObject[K];
            int i=0;
            for (Instance temp : tempIns) {
                JSONObject obj=new JSONObject();
                for (int j = 0; j < temp.numAttributes(); j++) {
                    obj.put(temp.attribute(j).name(),temp.value(j));
                }
                objs[i++]=obj;
            }
            result.put("clusters",objs);
            result.put("clusterSizes",clusterSizes);

        } catch(Exception e) {
            e.printStackTrace();
        }

        return result;
    }


}
