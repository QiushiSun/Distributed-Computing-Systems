# KMeans

## 待完成:

* 请在 DSPPCode.mapreduce.kmeans.impl 中创建 KMeansMapperImpl, 继承 KMeansMapper, 实现抽象方法

* 请在 DSPPCode.mapreduce.kmeans.impl 中创建 KMeansReducerImpl, 继承 KMeansReducer, 实现抽象方法

## 题目描述:

* 实现KMeans算法，在相邻两次迭代的聚类中心之间的距离小于某一阈值后终止迭代。输入包含数据集和聚类中心集，输出为聚类结果。

  注:
  
  （1）相邻两次迭代的聚类中心之间的距离指欧式距离，所有聚类中心点迭代前后的欧式距离都小于设定阈值时，即可停止迭代。
    
  （2）测试阶段，后台会将所有测试用例的阈值设定为0.05进行测试)

* 数据集输入格式: 坐标1，坐标2，...,坐标n 所属类别

  ```
  0,0	-1
  1,2	-1
  3,1	-1
  8,8	-1
  9,10	-1
  10,7	-1
  ```
* 聚类中心输入格式: 坐标1，坐标2，...,坐标n

  ```
  1,2
  3,1
  ```
* 聚类结果输出格式: 坐标1，坐标2，...,坐标n 所属类别(类别编号从0开始计)

  ```
  0,0	0
  1,2	0
  3,1	0
  8,8	1
  9,10	1
  10,7	1

  ```

