# CRM-Hotel-Management-System   
基于SpringBoot的酒店客户关系管理系统   
主要功能：   
- 客户个人信息、入住信息、消费信息管理
- 客户满意度评价录入及评价指标设置
- 客户价值计算
- 按照一定的特征(个人信息，客户价值，消费信息，满意度信息等)，对客户进行聚类分析，定义不同的客户群(KMeans)。对每个客户群采取不同的应对策略
- 建立客户分类决策树模型，对指定客户客户进行分类(C4.5)，查看其所属的客户群。






# 主要功能说明

1. 该系统主要用于收集酒店房客的个人信息，消费信息，对酒店的满意度评价信息等，

2. 系统可根据收集到的信息对客户进行如下操作：

   - 客户价值计算

   - 客户聚类，选定聚类依据，将指定历史客户分为若干个客户类（通过KMeans实现）

   - 客户分类：我们可以将聚类结果保存，作为训练集与测试集，生成用于客户分类的分类器（通过C4.5决策树实现）并保存该分类器。该分类器可对所有已`计算了客户价值` 及 `录入满意度评价`的客户做分类，查看他们属于哪一类。

     对于每一类客户，酒店专业的营销人员可参考该客户类别的参数信息，设置该类别客户的描述说明，对应的营销策略推荐等。

## 客户信息收集：

实现对于客户个人信息，入住信息，服务设施消费信息的增删改查

#### 1. 个人信息：

![主界面](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006222141337.png)

![修改用户信息](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006222301477.png)

#### 2. 入住信息：

![住宿信息](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006222519900.png)

#### 3. 服务设施消费信息

![服务设施消费信息](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006222628055.png)

![添加消费记录](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006222714546.png)



### 满意度评价录入：

记录客户对酒店的满意程度，满意程度分为5个级别：很不满意、不满意、一般、满意、很满意

满意指标可分为三个层级，各层级的评价指标按一定占比隶属上一个评价指标，它们的关系如下：

![](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006224830219.png)

具体设置可在指标设置页面中进行：

![](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006225040147.png)



**客户可选用不同级别的评价指标录入对酒店的评价（以二级评价指标为例）：**

![](C:\Users\Apollos\AppData\Roaming\Typora\typora-user-images\image-20201006225221807.png)

**录入成功后将计算出其对酒店各项评价结果：**

![](C:\Users\Apollos\AppData\Roaming\Typora\typora-user-images\image-20201006225328749.png)



### 客户价值计算：

首先选中客户，可直接通过搜索下拉框检索：

![](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006225458689.png)



**点击查询，查得该客户的累计消费数据：**

![](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006225617322.png)



**再点击"计算客户价值"，由既定的价值计算公式，得客户价值：**

![](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006225815923.png)



## 客户分类：

### 选定客户集，将客户聚类：

1. 点击左侧导航栏的数据预处理，进入客户聚类处理页面：

   ![客户聚类处理页面](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006230220725.png)

   **说明：**

   - 点击“数据源”下拉框， 选定本次聚类所采用的的客户数据源，为某一年份的住店客户记录或所有年份的客户住店记录，由于现存数据量较少，通常选择 “所有年份记录”。
   - 下方勾选的指标：为本次聚类的依据，系统将根据客户的这些选定指标的值 对客户进聚类，各选定指标相近的用户将被归到一类中。
   - K值 ： 将该数据源的客户， 根据选定指标， 划分入K个客户类中

2. 选定数据源，聚类依据，K值，点击确定，进行聚类分析：

   ![](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006231239073.png)

   ![](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006231301356.png)

   聚类后将展示如图所示的聚类结果， 每一行代表一个簇，该行记录有簇心的各项选中指标值，簇中元素个数。

3. 保存该次聚簇结果

   填写对本次聚簇所得用户分类所做的 标记、定义：

   ![](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006233610166.png)

   点击保存分类设置，为本次聚类结果命名，划定训练集，测试集比例（将用于下一步构建用户分类器）

   ![](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006233728622.png)



### 构建决策树分类模型

选用上一步所保存的聚类结果（训练集，测试集），用于构建分类模型，主要采用C4.5决策树算法。

点击左侧导航栏“决策树模型设置” 进入主界面：

![](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006232657805.png)

通过下拉框选定构建分类器所用的训练集测试集，点击确定，得到由训练集构建的分类器，并用测试集去测试该分类器的准确率。

![](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006233839616.png)

准确率不错，我们点击 “保存并应用该决策树模型“ 按钮，保留该决策树分类模型，作为本系统的客户分类器。



### 分类管理

点击左侧菜单栏“分类管理”，进入页面，设置所定义的每个分类的描述及推荐对策：

![](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006235029918.png)

在客户所属分类查询中，可根据客户历史记录由当前分类模型获得其所属分类，并知晓对应的推荐营销策略：

![](https://raw.githubusercontent.com/WinstonSmith1989/mymarkdownpics/master/img/image-20201006235001588.png)