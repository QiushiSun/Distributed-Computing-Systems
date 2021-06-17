# Chapter4 

**1.与Spark相比，MapReduce有哪些局限性**

1. 编程框架的表达能力有限，用户编程复杂。MapReduce仅提供map和reduce两个编程算子，用户需要基于这两个算子实现数据处理的操作，如join、sort等常用算子，都需要用map和reduce来实现，这增加了编程难度
2. 单个作业的Shuffle阶段的数据以阻塞方式传输，磁盘IO的开销大、延迟高。单个作业的Shuffle阶段，Map端需要将计算结果写入本地磁盘，之后Reduce端才可以读取该计算结果。因此，Shuffle阶段磁盘IO开销大，并且这种阻塞式数据传输方式加剧了MapReduce作业的高延迟
3. 多个作业之间衔接涉及IO开销，应用程序的延迟高。对于单个MapReduce作业来说，通常从HDFS等存储系统读入输入文件，作业执行完的结果输出到HDFS等存储系统。然而，很多应用程序需要通过多个作业来完成，如机器学习的迭代训练过程。迭代计算的中间结果反复读写，使整个应用的延迟非常高

**2.请说明Spark的逻辑计算模型和物理计算模型之间的关系**

逻辑计算模型：

物理执行角度：

**3.什么是RDD Lineage？如果Lineage较长，如何加快故障恢复？**

1. RDD Lineage是Driver中SparkContext维护的记录RDD转换的DAG，RDD Lineage记录的是粗颗粒度的特点数据Transformation操作。若出现故障，可以利用RDD Lineage重新计算丢失分区进行故障恢复
2. 如果Lineage较长，引入检查点机制将RDD写入外部可靠的（本身具有容错机制）分布式文件系统，例如HDFS（在实现层面，写检查点的过程是一个独立的作业，在用户作业结束后运行）

**4.请简述Standalone Clinet与Standalone Cluster两种模式下Spark架构之间的区别，并画出两种模式下的架构图**

区别：

1. Standalone Client模式下，Driver和Client以同一个进程存在
2. Standalone Cluster模式下，Driver为某一个Worker启动的一个名为DriverWrapper的进程

Standalone Client模式

<img src="/Users/sunqiushi/Desktop/徐辰review/standalone-client.png" alt="standalone-client" style="zoom:50%;" />

Standalone Cluster模式

<img src="static/standalone-cluster.png" alt="standalone-cluster" style="zoom:50%;" />

**5.Spark如何划分DAG中的Stage？**



**6.Spark中的应用和作业是何种关系**



**7.Spark中Stage和Task有怎样的联系？**



**8.Spark中Stage内部如何进行数据交换？Stage之间如何进行数据交换？**

Stage内部数据交换：Spark采用流水线（Pipeline）方式进行Stage内部的数据交换

Stage之间数据交换：Spark在Stage之间数据交换时需要Shuffle，Shuffle可能发生在两个ShuffleMapStage之间，或者ShuffleMapStage和ResultStage之间，简化Shuffle过程为Shuffle Write和Shuffle Read两个阶段

- Shuffle Read阶段：ShuffleMapTask或者ResultTask根据partition函数读取相应的ShuffleblockFile，存入缓存区并继续进行后续的计算。
- Shuffle Write阶段：ShuffleMapTask将输出RDD的记录按照分区函数划分道相应的bucket中，物化到本地磁盘形成ShuffleblockFile（之后才能被Shuffle Read阶段拉取）

**9.Spark中RDD持久化和检查点机制有哪些异同点？**



**10.Spark的广播变量机制通常用于什么场景？**
