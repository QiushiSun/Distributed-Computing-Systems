# **Chapter10 Flink**

1. **Flink使用何种数据模型表示数据？它与MapReduce、Spark等有何区别？**

   Flink使用DataStream表示数据。DataStream与其他的框架的数据模型的区别是DataStream的数据是无界数据。

2. **在Standalone模式下，Flink的架构中都有哪些部件？每个部件的功能是什么？**

   - client：客户端，将DataStream翻译为逻辑执行图并优化，将逻辑执行图提交到JobManager
   - job manager：作业管理器，根据逻辑执行图产生物理执行图，负责协调系统的作业执行；Standalone下负责Flink系统的资源管理
   - task manager：任务管理器，执行JobManager分配的任务，以及数据读入、缓存、传输；Standalone下负责节点的资源管理

3. **在Standalone模式下，Flink中的JobManager和TaskManager与Spark的Master和Worker在功能上是否完全一致？**

   （书P60）不一致，Spark的Master和Worker两个部件负责的是资源管理，在资源管理部分与Flink的部件是一致的。但Flink的部件在Standalone模式下还负责作业管理，在作业管理的范畴内，JobManager负责功能与Driver类似，TaskManager负责的功能与Spark中的Executor类似。

4. **在Standalone模式下，同一个TaskManager可能同时执行不同应用程序的任务，某种程度上会存在应用程序之间的相互干扰，引入Yarn后是如何解决这个问题的？**

   Standalone模式下出现干扰（如同一个TM中有一个任务崩溃，从而影响了其他的任务）的根本原因是资源管理和作业管理没有分离。在Flink引入Yarn进行资源管理之后，不同应用程序之间不会互不干扰。

5. **Flink中的逻辑执行图和物理执行图分别是如何产生的？与Spark有何区别？**

   逻辑执行图由Client进行优化，主要是对窄依赖的算子进行合并；物理执行图由job manager根据逻辑执行图产生。Spark不会在逻辑层面对算子进行合并，而是由Driver进行划分，将同个stage的算子放在同个物理节点，事实上实现了Flink中对逻辑执行图的优化。

6. **Flink中非迭代任务间如何进行数据传输？与Spark和Storm有何区别？**

   采用pipeline进行传输，一次传输一个缓冲区；Spark的数据传输是阻塞式的，而Flink和Storm的是非阻塞的。Flink的粒度是缓冲区，Storm的粒度是行。

7. **简述异步屏障快照的工作过程。**

   在异步屏障快照中，数据不断流入系统，当一个任务收到所有来自标识为n的屏障后将其状态保存。所有算子保存的状态形成检查点n。如果此时发生故障，则将所有算子对应的任务还原为检查点对应的状态。（那么这个时候的状态相当于这个屏障前的所有数据都已经完成了）

8. **Flink中迭代算子如何实现数据反馈？**

   使用两类特殊的任务（迭代前端，迭代末端）实现数据反馈。两类任务成对处于同一个TaskManager，末端的输出可以作为前端的输入。

9. **如果Flink系统不进行状态管理，会带来哪些缺陷？**

   如果不进行状态管理，当发生故障的时候。状态中维护的记录信息就会丢失，需要用户将过去所有的数据重新计算一次，而这对于用户而言是无法实现和接受的。

10. **MemoryStateBackend和FsStateBackend两种状态存储方式哪一种更适合实际生产环境？**

   两者都是将状态存储在系统内存中，在写检查点时，前者存储在JobManager的内存中，后者存储在HDFS等文件系统中。后者更适合生产环境，因为在生产环境中，需要写检查点的数据量可能很大，JobManager的内存可能无法保存全部的信息，且如果JobManager故障则会丢失信息，而HDFS是可靠的，可以保证不会丢失。

11. **Flink为何无法像Spark那样利用Lineage进行故障恢复**

    因为Lineage是基于静态数据实现的故障恢复，也即，当发生故障的时候能够知道运行到了哪个stage，只需要根据DAG和磁盘数据就能够恢复数据；但Flink的数据是动态的，当发生故障的时候，每个算子上都可能有数据在进行计算，因此不可能准确知道故障发生时的状态，无法直接恢复。