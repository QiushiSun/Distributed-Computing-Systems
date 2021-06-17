# Chapter 5 Yarn

1. **对于MapReduce和Spark来说，应用和作业是否有区别？**

   对于MapReduce来说，应用和作业没有区别。

   对于Spark来说，应用包含作业，一个应用中可以由多个作业。在程序上，一个应用与一个spark.context对应；一个作业与一个action对应。

2. **Spark架构和Yarn架构在设计思想上有无共同点？**

   两者都是主从架构，而且将资源管理和作业相分离。使得作业间相互独立地控制执行。

3. **第二代Hadoop和第一代相比所具备的优势是什么？**

   Yarn将资源管理功能从第一代MapReduce系统中独立出来，通过资源管理与作业相分离形成通用的资源管理系统，并使得作业之间相互独立地运行。

4. **Yarn的主要部件有哪些？各个部件都有什么作用？**

   主要部件包括RM,NM,AM,container

   1. RM: resource manager
      1. RS资源调度器:分配container
      2. 监控AM
   2. AM: 管理应用,申请应用资源
   3. NM:
      1. 向RM汇报资源和container的状态
      2. 处理AM的container的启动/停止等请求(不关心运行内容)
   4. container:资源的抽象表示,包括硬件资源,是动态资源划分单位

5. **Yarn中container内的任务由谁负责启动/停止？**

   由NM接收AM的请求，然后启动/停止container

6. **简要分析FIFO Scheduler，Capacity Scheduler，Fair Scheduler三种调度器的优缺点。**

   FIFO调度器的实现非常简单，但可能导致一个应用独占所有的资源而其他的应用需要不断等待。

   Capacity调度器，可以避免一个应用独占所有的资源而其他的应用需要不断等待的情况，但当有队列空闲时，该队列的资源无法被其他应用使用，造成资源的浪费。

   Fair调度器可以避免资源的浪费，但实现比较复杂.

7. **请简述ApplicationMaster申请资源的过程。**

   1. AM将应用分解成任务，向RM申请任务的资源；
   2. RM以container的形式分配资源；
   3. AM在任务间进行资源分配；
   4. AM与对应的NM通信，在container启动进程

8. **NodeManager是否监控container中任务的运行情况？**

   不监控任务的具体情况，由AM处理

9. **ApplicationManager由谁监控，并简述其容错恢复过程。**

   AM由RM监控。如果AM发生故障，yarn会重启进程，但不会恢复运行状态，运行状态需要由框架恢复

10. **为什么引入Yarn之后，MapReduce无法独立运行？**

   第二代的MapReduce实现了资源和计算的相分离，换言之，第二代的MapReduce本身不进行资源管理，而是由yarn进行资源管理。因此，MapReduce不可能独立对自己的任务分配资源，也就无法独立运行。

11. **Yarn平台运行MapReduce的方式与Spark的Yarn Cluster和Yarn Client两种模式中的哪一种更像？为什么？**

    与Cluster模式更像。三种模式都会在提交应用之后启动一个对应进程对应用进行管理。但Client模式的进程Executor Launcher不负责应用的管理，只负责分配资源和启动任务，而Cluster模式和MapReduce的管理进程既负责应用的管理，也负责资源分配和启动任务。

12. **试画出Yarn同时运行MapReduce和Spark的架构图，其中Spark应用程序以Yarn Cilent模式执行。**

    ![image-20210617162805269](.\static\yarn_struct.png)

