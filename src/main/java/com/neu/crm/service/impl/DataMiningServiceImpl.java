package com.neu.crm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.neu.crm.bean.ClassificationDataSource;
import com.neu.crm.mapper.ClassificationDataSourceMapper;
import com.neu.crm.service.DataMiningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import tk.mybatis.mapper.util.StringUtil;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.clusterers.SimpleKMeans;
import weka.core.*;
import weka.core.converters.ArffLoader;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class DataMiningServiceImpl implements DataMiningService {

    @Autowired
    ClassificationDataSourceMapper classificationDataSourceMapper;
    @Value("${data_set_path}")
    String data_set_path;

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
        List<ClassificationDataSource> dataSet;
        if (year.equals("所有年份数据")){
            dataSet = classificationDataSourceMapper.selectAll();
        }else{
            ClassificationDataSource query=new ClassificationDataSource();
            query.setYear(Integer.parseInt(year));
            dataSet = classificationDataSourceMapper.select(query);
        }


        //预备写入的文本文件
        FileWriter writer=new FileWriter(ResourceUtils.getFile(data_set_path+"KMeansTemp.arff"));
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
    public JSONObject doKMeansCluster(int K,String year) {
        Instances ins;
        SimpleKMeans KM;
        JSONObject result=new JSONObject();

        try {
            // 读入样本数据
            File file = ResourceUtils.getFile(data_set_path+"KMeansTemp.arff");
            ArffLoader loader = new ArffLoader();
            loader.setFile(file);
            ins = loader.getDataSet();

            // 初始化聚类器 （加载算法）
            KM = new SimpleKMeans();

            KM.setNumClusters(K);       //设置聚类要得到的类别数量
            KM.buildClusterer(ins);     //开始进行聚类

            Instances tempIns = KM.getClusterCentroids();   //得到簇心
            double[] clusterSizes = KM.getClusterSizes();   //每个簇所包含点的个数

            JSONObject[] objs=new JSONObject[K];    //待返回的JSON结果

            //将聚类结果写入本地文件
            tempIns.setRelationName("kMeansResult");
            FileWriter kMeansResult =new FileWriter(ResourceUtils.getFile(data_set_path+"temp.arff")) ;
            kMeansResult.write(tempIns.toString());
            kMeansResult.close();

            //将聚类结果写入JSON
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

    /**
     * 保存KMeans聚类后管理员自行定义的分类结果，作为C45决策树算法的数据源
     * @param jsonObject 前端传来的Json信息
     */
    @Override
    public void saveKMeansClusteringResultForC45(JSONObject jsonObject) throws Exception {
        //  前端传过来的样例数据：2个特征，6个簇，6种自定义分类:A,B,C,D,E,F
        // {
        // "train_set_ratio":"7",
        // "test_set_ratio":"3",
        // "dataset_name":"TEST",
        // "clusters":[
        //      {"client_value":"0.5207313047052751","satisfaction":"3.806451612903226","class":"A"},
        //      {"client_value":"0.6481171931315389","satisfaction":"5","class":"B"},
        //      {"client_value":"0.8982746523436623","satisfaction":"2.3255813953488373","class":"C"},
        //      {"client_value":"0.9532649258803234","satisfaction":"4","class":"D"},
        //      {"client_value":"1.3090963362019885","satisfaction":"5","class":"E"},
        //      {"client_value":"1.4905052324129813","satisfaction":"3.8214285714285716","class":"F"}
        // ],"attrs":[
        //      "client_value",
        //      "satisfaction",
        //      "class"
        // ]}


        //获得前端传来的数据
        String dataset_name = jsonObject.getString("dataset_name");
        int train_set_ratio = jsonObject.getIntValue("train_set_ratio");
        int test_set_ratio = jsonObject.getIntValue("test_set_ratio");
        String[] attrs = jsonObject.getObject("attrs", String[].class);
        JSONObject[] clustersJsonArray = jsonObject.getObject("clusters",JSONObject[].class);


        //由前端传来的数据构造分类后簇心集合的Instances
        StringBuilder clustersSB=new StringBuilder();
        clustersSB.append("@relation ").append(dataset_name).append("\n");
        for ( int i=0 ; i < attrs.length-1 ; i++ ) {
            clustersSB.append("@attribute ").append(attrs[i]).append(" real\n");
        }
        List<String> classNames = new ArrayList<>();
        List<String> records=new LinkedList<>();
        for (JSONObject clusterJson : clustersJsonArray) {
            String className = clusterJson.getString("class");
            if (!classNames.contains(className)){
                classNames.add(className);
            }
            StringBuilder record=new StringBuilder();
            for (String attr : attrs) {
                record.append(clusterJson.get(attr)).append(",");
            }
            record.replace(record.length()-1,record.length(),"\n");
            records.add(record.toString());
        }
        clustersSB.append("@attribute class {")
                .append(iterableObjToDelimitedString(classNames,","))
                .append("}\n@data\n");
        for (String record : records) {
            clustersSB.append(record);
        }
        //用户分类过的簇心集合Instances
        ArffLoader loader1=new ArffLoader();
        loader1.setSource(new ByteArrayInputStream(clustersSB.toString().getBytes(StandardCharsets.UTF_8)));
        Instances clusters = loader1.getDataSet();


        //数据源：本次KMeans聚类分析所用的数据集（未被分类）
        File dataSourceFile = ResourceUtils.getFile(data_set_path+"KMeansTemp.arff");
        ArffLoader loader2 = new ArffLoader();
        loader2.setFile(dataSourceFile);
        Instances dataSource = loader2.getDataSet();

        //为数据源设置管理员在前端所定义的分类属性
        Attribute attribute = new Attribute("class",classNames);
        dataSource.insertAttributeAt(attribute,dataSource.numAttributes());
        for (Instance instance : dataSource) {
            instance.setValue(instance.numAttributes()-1,classNames.get(0));
        }
        //根据各自所属的分类属性将数据源分类
        HashMap<Double,List<Instance>> classifiedDataSource=new HashMap<>();
        for (int i = 0; i < clusters.attribute(clusters.numAttributes() - 1).numValues(); i++) {
            classifiedDataSource.put((double) i,new ArrayList<>());
        }

        //找出两个数据集除去分类属性外的属性索引对应关系
        HashMap<Integer,Integer> clusterToDatasourceAttrIndexMap=new HashMap<>();
        for (int i = 0; i < clusters.numAttributes(); i++) {
            if (clusters.attribute(i).name().equals("class"))continue;
            for (int j = 0; j < dataSource.numAttributes(); j++) {
                if (clusters.attribute(i).name().equals(dataSource.attribute(j).name())&&
                    !dataSource.attribute(j).name().equals("class")){
                    clusterToDatasourceAttrIndexMap.put(i,j);
                    break;
                }
            }
        }

        //由簇心集合的分类结果为数据源设置分类属性
        for (Instance instance : dataSource) {
            Instance minDistCluster = null;
            double minDist = Double.MAX_VALUE;
            for (Instance cluster : clusters) {
                double dist=distance2(cluster,instance,clusterToDatasourceAttrIndexMap);
                if (dist<minDist){
                    minDist=dist;
                    minDistCluster=cluster;
                }
            }
            assert minDistCluster != null;
            double classBelong = minDistCluster.value(minDistCluster.numAttributes()-1);
            instance.setValue(instance.numAttributes()-1,classBelong);
            classifiedDataSource.get(classBelong).add(instance);
        }



        //训练集、测试集
        Instances trainSet = new Instances(dataSource, 0, 0);
        Instances testSet = new Instances(dataSource, 0, 0);
        trainSet.setRelationName(dataset_name+"_train");
        testSet.setRelationName(dataset_name+"_test");

        //将分类好的数据集按照用户设定的比例分配到 训练集 与 测试集
        for (Double key : classifiedDataSource.keySet()) {
            List<Instance> instanceList = classifiedDataSource.get(key);
            Collections.shuffle(instanceList);  //随机打乱顺序
            int size=instanceList.size();
            int trainSetSize=(train_set_ratio*size)/(train_set_ratio+test_set_ratio);
            for (int i = 0; i < trainSetSize; i++) {
                trainSet.add(instanceList.get(i));
            }
            for (int i = trainSetSize; i < size; i++) {
                testSet.add(instanceList.get(i));
            }
        }

        //写入arff文件，保存本次训练集与测试集
        FileWriter trainSetWriter =new FileWriter(ResourceUtils.getFile(data_set_path+trainSet.relationName()+".arff")) ;
        FileWriter testSetWriter =new FileWriter(ResourceUtils.getFile(data_set_path+testSet.relationName()+".arff")) ;
        trainSetWriter.write(trainSet.toString());
        testSetWriter.write(testSet.toString());
        trainSetWriter.close();
        testSetWriter.close();
    }

    /**
     * 由已分类好的训练集，测试集通过C4.5算法构造决策树
     * @param trainSetFile 训练集文件
     * @param testSetFile 测试集问价
     * @return 该决策树模型测试结果
     */
    @Override
    public String c45(File trainSetFile, File testSetFile) throws Exception {
        ArffLoader loader = new ArffLoader();
        //训练集
        loader.setFile(trainSetFile);
        Instances insTrain = loader.getDataSet();
        insTrain.setClassIndex(insTrain.numAttributes()-1); //标出分类属性
        //测试集
        loader.setFile(testSetFile);
        Instances insTest = loader.getDataSet();
        insTest.setClassIndex(insTest.numAttributes()-1); //标出分类属性
        //构建决策树
        Classifier classifier = new J48();
        classifier.buildClassifier(insTrain);
        //测试决策树模型，得到测试结果
        String testResult = c45ClassifierTestResult(classifier, insTest);
        //序列化保存该决策树模型
        ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(ResourceUtils.
                getFile(data_set_path+"/tempDecisionTree.ser")));
        oos.writeObject(classifier);
        oos.close();

        return testResult;
    }



    private String iterableObjToDelimitedString(Iterable iterable, String delimitSymbol){
        StringBuilder sb=new StringBuilder();
        for (Object o : iterable) {
            sb.append(o.toString()).append(delimitSymbol);
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    /**
     * 指定索引，求两个Instance之间的欧式距离
     * @param X Instance
     * @param Y Instance
     * @param XToYIndexMap X 到 Y 的索引映射，key 为 X 中指定属性的索引, value 为 Y中与之对应的属性的索引
     * @return 两个Instance之间的欧式距离
     */
    private double distance2(Instance X,Instance Y,HashMap<Integer,Integer> XToYIndexMap){
        BigDecimal dist=new BigDecimal(0);
        for (Integer i : XToYIndexMap.keySet()) {
            dist = dist.add(BigDecimal.valueOf(Math.pow(X.value(i) - Y.value(XToYIndexMap.get(i)), 2)));
        }
        return dist.doubleValue();
    }

    /**
     * 使用决策树分类器对测试集进行分类，计算分类的准确率，返回本次测试的详细信息
     * @param decisionTree 决策树分类器
     * @param insTest 测试集
     * @return 本次测试的详细执行信息
     */
    private String c45ClassifierTestResult(Classifier decisionTree, Instances insTest) throws Exception {
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
        return sb.toString();
    }
}
