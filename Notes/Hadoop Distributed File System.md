# 分布式文件系统 HDFS

## 文件系统review

**文件系统出现的的原因**：用户直接操作和管理辅助存储器上信息（二进制序列），繁琐复杂、易于出错可靠性差

**文件系统**：管理和存取信息的模块

- 统一管理用户和系统信息的存储、 检索、更新、共享和保护
- 为用户提供一整套方便有效的文件使用和操作方法

**文件系统的功能**

- 文件按名存取
- 文件目录的建立和维护
- 实现逻辑文件到物理文件的转换
- 文件存储空间的分配和管理
- 数据保密、保护和共享
- 提供一组用户使用的操作

## Hadoop分布式文件系统（HDFS）

### Hadoop的设计目的

解决如下问题

1. 如何存储TB级别的大文件（如按某一主题的网页构成的数据集）
2. 如何保证文件系统的容错，HDFS需要确保在节点发生故障时依然能够正常提供文件读写服务
3. 如何进行大文件的并发读写控制，为了避免文件的读写冲突，是否需要使用加锁等复杂操作

### Hadoop的设计思想

#### 大文件存储：分块存储

HDFS将大文件分切割为数据块，通常每个数据块的大小为128MB（或其它大小）。每个数据块在本地系统中是以单独的文件进行存储的，HDFS采用分而治之的策略让多个节点对逻辑层面的大文件在物理层面进行分布式存储，使用跨机器索引的方式访问。

**物理结构：**跨机器索引

<img src="images/block-idx.png" width=60%>

#### 分块冗余存储：HDFS将文件切分成块，每个小文件块做冗余备份，多个备份会分散到不同节点上，因此除非某个文件块所在的所有节点全部故障，否则都可以满足容错。

#### 简化文件读写：

- 避免读写冲突：“一次写入，多次读取”，HDFS的文件一次写入后不再修改，而是多次读取
- 不支持随机写：仅支持顺序写入而不支持随机写入

### HDFS体系架构

概念：

1. 元数据：指HDFS文件系统中，文件和目录的属性信息。HDFS实现时，采用了镜像文件（Fsimage） + 日志文件（EditLog）的备份机制。文件的镜像文件中内容包括：修改时间、访问时间、数据块大小、组成文件的数据块的存储位置信息。目录的镜像文件内容包括：修改时间、访问控制权限等信息。日志文件记录的是：HDFS的更新操作。
2. 机架Rack：HDFS集群，由分布在多个机架上的大量DataNode组成，不同机架之间节点通过交换机通信，HDFS通过机架感知策略，使NameNode能够确定每个DataNode所属的机架ID，使用副本存放策略，来改进数据的可靠性、可用性和网络带宽的利用率。



HDFS采取主从结构，如架构图所示

- NameNode：负责HDFS的管理工作，包括管理文件目录结构、位置等元数据、维护DataNode的状态等，**NameNode并不实际存储文件**
- Secondary NameNode：充当NameNode的备份，NameNode故障时利用Secondary NameNode进行恢复
- DataNode：负责存储文件，根据NameNode的控制信息存储和管理对应的文件块，并定期向NameNode汇报自己的状态（通过TCP协议发送心跳信息）



Secondary NameNode作为Name Node的备份，一般在另一台独立的物理计算机运行，定期备份FsImage和EditLog两个文件，合并形成文件目录以及其元信息的远程检查点，并将检查点返回给NameNode（避免日志文件被拉的无限长）



Remark：不涉及Secondary NameNode的实现 https://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithNFS.html

*Note that, in an HA cluster, the Standby NameNodes also perform checkpoints of the namespace state, and thus it is not necessary to run a Secondary NameNode, CheckpointNode, or BackupNode in an HA cluster. In fact, to do so would be an error. This also allows one who is reconfiguring a non-HA-enabled HDFS cluster to be HA-enabled to reuse the hardware which they had previously dedicated to the Secondary NameNode.*



### Hadoop容错机制

#### 故障恢复

故障指的是：无法通信，交换机故障，磁盘故障，其他天灾人祸...

硬件异常比软件异常更加常见

- NameNode故障：NameNode一旦宕机，利用Secondary NameNode中的FsImage和Editlog数据进行恢复。但是，这种恢复可能会造成一部分文件修改操作的丢失。Secondary NameNode的设计方式是“冷备份”而不是“热备份”，因为Secondary NameNode无法获知NameNode重的EditLog.new中的信息，也就是说Secondary NameNode没有保留最近的文件修改操作记录，恢复后会失去EditLog.new中的信息。这个问题的解决通过在Hadoop2.x版本中通过增加对高可用性（HA）的支持完成，其配备了Active-Standby NameNode（活动-备用NameNode）。当活动NameNode失效，备用NameNode会接管其任务，并开始服务于客户端的请求，不会有任何明显中断。
- DataNode故障：DataNode定期向NameNode发送heartbeat来表示该节点存活，如果NameNode在一定时间内没有收到heartbeat，那么NameNode会判定其宕机，此时所有在该DataNode上的数据块都会被标记为不可读。如果此时客户端发起读该文件块的请求，那么NameNode提供的文件块存储位置将不包含该DataNode，如果此时客户端发起写文件块的请求，那么该DataNode可存储文件块的位置会被NameNode排除在外。总之，DataNode**定期检查备份因子**，若出现宕机，那么**节点上所有数据都会被标记为"不可读"**
- 此外，DataNode故障时可能会导致部分文件块的副本数量小于预先设定的副本数量。这时需要在其他的DataNode进行备份以恢复到预期的副本数量。同时NameNode中文件块的元信息也需要进行修改，从而使得所有文件块的副本数量恢复到3个



#### 文件分块与备份：

接上述内容，NameNode单单存储文件的元信息，不真正存储数据，而DataNode才真正存储数据。HDFS的文件块分布存储在DataNode中，一般而言，每个文件块有三个副本，写入时采用启发式策略决定副本放置

- 副本1：如果客户端和某一DataNode位于同物理节点，那么HDFS将第一个副本放置在该DataNode；如果客户端不与任何DataNode在同一个物理节点，那么HDFS随机把副本1存放在磁盘不太满，CPU不太繁忙的节点上，这种策略为了支持快速写入
- 副本2：NameNode将第二个副本放置在与副本1不同的机架的某一个节点上，减少跨机架的网络流量
- 副本3：NameNode将副本3放在副本1所在的机架的不同节点。这种策略防止副本1挂掉的时候又出现交换机故障
- 更多副本：放置在随机节点上



### Hadoop文件读写

#### 文件写入

向HDFS写入文件，客户端向NameNode发起文件写入请求，NameNode根据文件的路径判断是否合法，如果合法，以文件块大小的数据为单位不断地向DataNode写入文件块

#### 文件读取

向HDFS读取文件，客户端向NameNode发起文件读取请求，NameNode根据文件的路径判断是否合法，合法则返回文件所有数据块的存放地址。对于第一个数据块，客户端从最近的存放该数据块的DataNode读取数据，当读取完后，客户端从最近的存放第二个数据块的DataNode读取数据，以此类推直到读取所有数据块

#### 文件读写一致性

单机编程时，如果对同一文件进行读写并发访问，那么需要使用读写操作加锁等方式对文件互斥访问。HDFS的模型事先做一些“约定”，使得读写不一致不会出现，用户在编程时不用考虑加锁等复杂操作的逻辑

基于上述思想，HDFS采取“一次写入、多次读取”的简化一致性模型

1. 一个文件经过创建、写入和关闭后就不能改变文件中的已有内容
2. 已经写入到HDFS的文件，仅允许在文件末尾追加数据，即append
3. 当对一个文件进行写入或追加操作时，NameNode将拒绝其他针对这个文件的读写请求
4. 当一个文件进行读取操作时候，NameNode允许其他针对该文件的读请求































