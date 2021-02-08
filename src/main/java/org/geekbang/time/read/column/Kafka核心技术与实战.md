开篇词 | 为什么要学习Kafka？

01 | 消息引擎系统ABC
Apache Kafka 是一款开源的消息引擎系统。
消息引擎系统的定义
Kafka在不同系统之间传输消息选择的消息格式:纯二进制的字节序列
Kafka 同时支持点对点模型,发布 / 订阅模型
Kafka 并未完全遵照 JMS 规范
削峰填谷
聪明人也要下死功夫

02 | 一篇文章带你快速搞定Kafka术语
Kafka 的三层消息架构：
    第一层是主题层，每个主题可以配置 M 个分区，而每个分区又可以配置 N 个副本。
    第二层是分区层，每个分区的 N 个副本中只能有一个充当领导者角色，对外提供服务；其他 N-1 个副本是追随者副本，只是提供数据冗余之用。
    第三层是消息层，分区中包含若干条消息，每条消息的位移从 0 开始，依次递增。
    最后，客户端程序只能与分区的领导者副本进行交互。
Kafka Broker 是如何持久化数据
    消息日志（Log）来保存数据,顺序 I/O 写
    日志段（Log Segment）机制,定期地删除消息以回收磁盘
    Kafka 中实现这种 P2P 模型的方法就是引入了消费者组（Consumer Group）
总结：
    消息：Record。Kafka 是消息引擎嘛，这里的消息就是指 Kafka 处理的主要对象。
    主题：Topic。主题是承载消息的逻辑容器，在实际使用中多用来区分具体的业务。
    分区：Partition。一个有序不变的消息序列。每个主题下可以有多个分区。
    消息位移：Offset。表示分区中每条消息的位置信息，是一个单调递增且不变的值。
    副本：Replica。Kafka 中同一条消息能够被拷贝到多个地方以提供数据冗余，这些地方就是所谓的副本。副本还分为领导者副本和追随者副本，各自有不同的角色划分。副本是在分区层级下的，即每个分区可配置多个副本实现高可用。
    生产者：Producer。向主题发布新消息的应用程序。
    消费者：Consumer。从主题订阅新消息的应用程序。
    消费者位移：Consumer Offset。表征消费者消费进度，每个消费者都有自己的消费者位移。
    消费者组：Consumer Group。多个消费者实例共同组成的一个组，同时消费多个分区以实现高吞吐。
    重平衡：Rebalance。消费者组内某个消费者实例挂掉后，其他消费者实例自动重新分配订阅主题分区的过程。Rebalance 是 Kafka 消费者端实现高可用的重要手段。
注意:每个名称是针对什么来说的。比如副本是针对分区来说的。

03 | Kafka只是消息引擎系统吗？
Apache Kafka 是消息引擎系统，也是一个分布式流处理平台（Distributed Streaming Platform）
Kafka 在设计之初就旨在提供三个方面的特性：
    提供一套 API 实现生产者和消费者；
    降低网络传输和磁盘存储开销；
    实现高伸缩性架构。
Kafka 与其他主流大数据流式计算框架相比，优势在哪里
    第一点是更容易实现端到端的正确性（Correctness）
    可能助力 Kafka 胜出的第二点是它自己对于流式计算的定位

04 | 我应该选择哪种Kafka？
你知道几种 Kafka？
    1. Apache Kafka
    2. Confluent Kafka
    3. Cloudera/Hortonworks Kafka
特点比较
总而言之，如果你仅仅需要一个消息引擎系统亦或是简单的流处理应用场景，同时需要对系统有较大把控度，那么我推荐你使用 Apache Kafka。
一言以蔽之，如果你需要用到 Kafka 的一些高级特性，那么推荐你使用 Confluent Kafka。
简单来说，如果你需要快速地搭建消息引擎系统，或者你需要搭建的是多框架构成的数据平台且 Kafka 只是其中一个组件，那么我推荐你使用这些大数据云公司提供的 Kafka。

05 | 聊聊Kafka的版本号
Kafka 版本命名
Kafka 版本演进

06 | Kafka线上集群部署方案怎么做？
操作系统
Linux 的表现更胜一筹
    I/O 模型的使用
        Kafka 客户端底层使用了 Java 的 selector，selector 在 Linux 上的实现机制是 epoll，而在 Windows 平台上的实现机制是 select。因此在这一点上将 Kafka 部署在 Linux 上是有优势的，因为能够获得更高效的 I/O 性能。
    数据网络传输效率
        在 Linux 部署 Kafka 能够享受到零拷贝技术所带来的快速数据传输特性。
    社区支持度
        Windows 平台上部署 Kafka 只适合于个人测试或用于功能验证，千万不要应用于生产环境。
磁盘
    使用普通机械硬盘即可
磁盘容量
    考虑下面这几个元素：
        新增消息数
        消息留存时间
        平均消息大小
        备份数
        是否启用压缩
带宽

07 | 最最最重要的集群参数配置（上）
Broker 端参数
    存储信息
        log.dirs:指定了 Broker 需要使用的若干个文件目录路径
        log.dir:不要设置
    ZooKeeper 相关
        zookeeper.connect
    Broker 连接相关
        listeners
        advertised.listeners
        host.name/port:把它们忘掉吧，压根不要为它们指定值，毕竟都是过期的参数了。
        listener.security.protocol.map参数告诉这个协议底层使用了哪种安全协议，比如指定listener.security.protocol.map=CONTROLLER:PLAINTEXT表示CONTROLLER这个自定义协议底层使用明文不加密传输数据。
    关于 Topic 管理
        auto.create.topics.enable：是否允许自动创建 Topic。最好设置成 false
        unclean.leader.election.enable：是否允许 Unclean Leader 选举。建议你还是显式地把它设置成 false。
        auto.leader.rebalance.enable：是否允许定期进行 Leader 选举。建议你在生产环境中把这个参数设置成 false。
    数据留存方面
        log.retention.{hour|minutes|ms}：这是个“三兄弟”，都是控制一条消息数据被保存多长时间。从优先级上来说 ms 设置最高、minutes 次之、hour 最低。
        log.retention.bytes：这是指定 Broker 为消息保存的总磁盘容量大小。默认是 -1，表明你想在这台 Broker 上保存多少数据都可以
        message.max.bytes：控制 Broker 能够接收的最大消息大小。默认的 1000012 太少了，还不到 1MB

08 | 最最最重要的集群参数配置（下）
Topic 级别参数
    Topic 级别参数会覆盖全局 Broker 参数的值
    从保存消息方面来考量
        retention.ms：规定了该 Topic 消息被保存的时长。默认是 7 天，即该 Topic 只保存最近 7 天的消息。一旦设置了这个值，它会覆盖掉 Broker 端的全局参数值。
        retention.bytes：规定了要为该 Topic 预留多大的磁盘空间。和全局参数作用相似，这个值通常在多租户的 Kafka 集群中会有用武之地。当前默认值是 -1，表示可以无限使用磁盘空间。
    从能处理的消息大小这个角度来看
        max.message.bytes：它决定了 Kafka Broker 能够正常接收该 Topic 的最大消息大小。
    有两种方式可以设置：
    创建 Topic 时进行设置
        bin/kafka-topics.sh--bootstrap-serverlocalhost:9092--create--topictransaction--partitions1--replication-factor1--configretention.ms=15552000000--configmax.message.bytes=5242880
    修改 Topic 时设置
        bin/kafka-configs.sh--zookeeperlocalhost:2181--entity-typetopics--entity-nametransaction--alter--add-configmax.message.bytes=10485760
        建议是，你最好始终坚持使用第二种方式来设置，并且在未来，Kafka 社区很有可能统一使用kafka-configs脚本来调整 Topic 级别参数。
JVM 参数
    至少使用 Java 8
    无脑给出一个通用的建议：将你的 JVM 堆大小设置成 6GB 吧，这是目前业界比较公认的一个合理值。
    如果你依然在使用 Java 7，那么可以根据以下法则选择合适的垃圾回收器：
        如果 Broker 所在机器的 CPU 资源非常充裕，建议使用 CMS 收集器。启用方法是指定-XX:+UseCurrentMarkSweepGC。
        否则，使用吞吐量收集器。开启方法是指定-XX:+UseParallelGC。
    如果你已经在使用 Java 8 了，那么就用默认的 G1 收集器就好了。
    如何设置?只需要设置下面这两个环境变量即可：
        KAFKA_HEAP_OPTS：指定堆大小。
        KAFKA_JVM_PERFORMANCE_OPTS：指定 GC 参数。
    $> export KAFKA_HEAP_OPTS=--Xms6g  --Xmx6g
    $> export  KAFKA_JVM_PERFORMANCE_OPTS= -server -XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:+ExplicitGCInvokesConcurrent -Djava.awt.headless=true
    $> bin/kafka-server-start.sh config/server.properties

操作系统参数
Kafka 并不需要设置太多的 OS 参数，但有些因素最好还是关注一下，比如下面这几个：
    文件描述符限制
    文件系统类型
    Swappiness
    提交时间
    ulimit -n。我觉得任何一个 Java 项目最好都调整下这个值。实际上，文件描述符系统资源并不像我们想象的那样昂贵，你不用太担心调大此值会有什么不利的影响。通常情况下将它设置成一个超大的值是合理的做法，比如ulimit -n 1000000。
    根据官网的测试报告，XFS 的性能要强于 ext4，所以生产环境最好还是使用 XFS。对了，最近有个 Kafka 使用 ZFS 的数据报告，貌似性能更加强劲，有条件的话不妨一试。
    swap 的调优。网上很多文章都提到设置其为 0，将 swap 完全禁掉以防止 Kafka 进程使用 swap 空间。我个人反倒觉得还是不要设置成 0 比较好，我们可以设置成一个较小的值。因为一旦设置成 0，当物理内存耗尽时，操作系统会触发 OOM killer 这个组件，它会随机挑选一个进程然后 kill 掉，即根本不给用户任何的预警。但如果设置成一个比较小的值，当开始使用 swap 空间时，你至少能够观测到 Broker 性能开始出现急剧下降，从而给你进一步调优和诊断问题的时间。基于这个考虑，我个人建议将 swappniess 配置成一个接近 0 但不为 0 的值，比如 1。
    Flush 落盘时间

09 | 生产者消息分区机制原理剖析
为什么分区？
    其实分区的作用就是提供负载均衡的能力，或者说对数据进行分区的主要原因，就是为了实现系统的高伸缩性（Scalability）。
    除了提供负载均衡这种最核心的功能之外，利用分区也可以实现其他一些业务级别的需求，比如实现业务级别的消息顺序的问题
都有哪些分区策略？
    所谓分区策略是决定生产者将消息发送到哪个分区的算法。Kafka 为我们提供了默认的分区策略，同时它也支持你自定义分区策略。
    如果要自定义分区策略，你需要显式地配置生产者端的参数partitioner.class。在编写生产者程序时，你可以编写一个具体的类实现org.apache.kafka.clients.producer.Partitioner接口。
        这个接口也很简单，只定义了两个方法：partition()和close()，通常你只需要实现最重要的 partition 方法。
    轮询策略
        轮询策略是 Kafka Java 生产者 API 默认提供的分区策略。如果你未指定partitioner.class参数，那么你的生产者程序会按照轮询的方式在主题的所有分区间均匀地“码放”消息。
    随机策略
        如果要实现随机策略版的 partition 方法，很简单，只需要两行代码即可：
            List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
            return ThreadLocalRandom.current().nextInt(partitions.size());
            本质上看随机策略也是力求将数据均匀地打散到各个分区，但从实际表现来看，它要逊于轮询策略，所以如果追求数据的均匀分布，还是使用轮询策略比较好。事实上，随机策略是老版本生产者使用的分区策略，在新版本中已经改为轮询了。
    按消息键保序策略Key-ordering 策略
        一旦消息被定义了 Key，那么你就可以保证同一个 Key 的所有消息都进入到相同的分区里面，由于每个分区下的消息处理都是有顺序的，故这个策略被称为按消息键保序策略
        实现这个策略的 partition 方法同样简单，只需要下面两行代码即可：
            List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
            return Math.abs(key.hashCode()) % partitions.size();
        Kafka 默认分区策略实际上同时实现了两种策略：如果指定了 Key，那么默认实现按消息键保序策略；如果没有指定 Key，则使用轮询策略。
    其他分区策略
        示例:以地理位置分区
            List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
            return partitions.stream().filter(p -> isSouth(p.leader().host())).map(PartitionInfo::partition).findAny().get();
小结
    切记分区是实现负载均衡以及高吞吐量的关键，故在生产者这一端就要仔细盘算合适的分区策略，避免造成消息数据的“倾斜”，使得某些分区成为性能瓶颈，这样极易引发下游数据消费的性能下降。

10 | 生产者压缩算法面面观
怎么压缩？
    V1和V2
何时压缩？
    在 Kafka 中，压缩可能发生在两个地方：生产者端和 Broker 端。
    props.put("compression.type", "gzip");
        它表明该 Producer 的压缩算法使用的是 GZIP。这样 Producer 启动后生产的每个消息集合都是经 GZIP 压缩过的，故而能很好地节省网络传输带宽以及 Kafka Broker 端的磁盘占用。
    大部分情况下 Broker 从 Producer 端接收到消息后仅仅是原封不动地保存而不会对其进行任何修改，但这里的“大部分情况”也是要满足一定条件的。有两种例外情况就可能让 Broker 重新压缩消息。
    情况一：Broker 端指定了和 Producer 端不同的压缩算法。
    情况二：Broker 端发生了消息格式转换。
        在一个生产环境中，Kafka 集群中同时保存多种版本的消息格式非常常见。为了兼容老版本的格式，Broker 端会对新版本消息执行向老版本格式的转换。这个过程中会涉及消息的解压缩和重新压缩。一般情况下这种消息格式转换对性能是有很大影响的，除了这里的压缩之外，它还让 Kafka 丧失了引以为豪的 Zero Copy 特性。
何时解压缩？
    Producer 端压缩、Broker 端保持、Consumer 端解压缩。
    Kafka 会将启用了哪种压缩算法封装进消息集合中，这样当 Consumer 读取到消息集合时，它自然就知道了这些消息使用的是哪种压缩算法。
    除了在 Consumer 端解压缩，Broker 端也会进行解压缩。
        每个压缩过的消息集合在 Broker 端写入时都要发生解压缩操作，目的就是为了对消息执行各种验证。我们必须承认这种解压缩对 Broker 端性能是有一定影响的，特别是对 CPU 的使用率而言。
各种压缩算法对比
    在 Kafka 2.1.0 版本之前，Kafka 支持 3 种压缩算法：GZIP、Snappy 和 LZ4。
    从 2.1.0 开始，Kafka 正式支持 Zstandard 算法（简写为 zstd）。它是 Facebook 开源的一个压缩算法，能够提供超高的压缩比（compression ratio）。
    看一个压缩算法的优劣，有两个重要的指标：一个指标是压缩比，原先占 100 份空间的东西经压缩之后变成了占 20 份空间，那么压缩比就是 5，显然压缩比越高越好；
        另一个指标就是压缩 / 解压缩吞吐量，比如每秒能压缩或解压缩多少 MB 的数据。同样地，吞吐量也是越高越好。
    Facebook Zstandard 官网提供的一份压缩算法 benchmark 比较结果
        对于 Kafka 而言，它们的性能测试结果却出奇得一致，即在吞吐量方面：LZ4 > Snappy > zstd 和 GZIP；而在压缩比方面，zstd > LZ4 > GZIP > Snappy。具体到物理资源，使用 Snappy 算法占用的网络带宽最多，zstd 最少，这是合理的，毕竟 zstd 就是要提供超高的压缩比；在 CPU 使用率方面，各个算法表现得差不多，只是在压缩时 Snappy 算法使用的 CPU 较多一些，而在解压缩时 GZIP 算法则可能使用更多的 CPU。
最佳实践
    何时启用压缩是比较合适的时机
        Producer 程序运行机器上的 CPU 资源要很充足
        环境中带宽资源有限
        对不可抗拒的解压缩无能为力，但至少能规避掉那些意料之外的解压缩。就像我前面说的，因为要兼容老版本而引入的解压缩操作就属于这类。有条件的话尽量保证不要出现消息格式转换的情况。

11 | 无消息丢失配置怎么实现？
那 Kafka 到底在什么情况下才能保证消息不丢失呢？
    一句话概括，Kafka 只对“已提交”的消息（committed message）做有限度的持久化保证。
        第一个核心要素是“已提交的消息”。
        第二个核心要素就是“有限度的持久化保证”，也就是说 Kafka 不可能保证在任何情况下都做到不丢失消息。
    总结一下，Kafka 是能做到不丢失消息的，只不过这些消息必须是已提交的消息，而且还要满足一定的条件。
“消息丢失”案例
案例 1：生产者程序丢失数据
    Producer 永远要使用带有回调通知的发送 API，也就是说不要使用 producer.send(msg)，而要使用 producer.send(msg, callback)。
        不要小瞧这里的 callback（回调），它能准确地告诉你消息是否真的提交成功了。一旦出现消息提交失败的情况，你就可以有针对性地进行处理。
案例 2：消费者程序丢失数据
    维持先消费消息（阅读），再更新位移（书签）的顺序即可。这样就能最大限度地保证消息不丢失。
    如果是多线程异步处理消费消息，Consumer 程序不要开启自动提交位移，而是要应用程序手动提交位移。
最佳实践
    不要使用 producer.send(msg)，而要使用 producer.send(msg, callback)。记住，一定要使用带有回调通知的 send 方法。
    设置 acks = all。acks 是 Producer 的一个参数，代表了你对“已提交”消息的定义。如果设置成 all，则表明所有副本 Broker 都要接收到消息，该消息才算是“已提交”。这是最高等级的“已提交”定义。
    设置 retries 为一个较大的值。这里的 retries 同样是 Producer 的参数，对应前面提到的 Producer 自动重试。当出现网络的瞬时抖动时，消息发送可能会失败，此时配置了 retries > 0 的 Producer 能够自动重试消息发送，避免消息丢失。
    设置 unclean.leader.election.enable = false。这是 Broker 端的参数，它控制的是哪些 Broker 有资格竞选分区的 Leader。如果一个 Broker 落后原先的 Leader 太多，那么它一旦成为新的 Leader，必然会造成消息的丢失。故一般都要将该参数设置成 false，即不允许这种情况的发生。
    设置 replication.factor >= 3。这也是 Broker 端的参数。其实这里想表述的是，最好将消息多保存几份，毕竟目前防止消息丢失的主要机制就是冗余。
    设置 min.insync.replicas > 1。这依然是 Broker 端参数，控制的是消息至少要被写入到多少个副本才算是“已提交”。设置成大于 1 可以提升消息持久性。在实际环境中千万不要使用默认值 1。
    确保 replication.factor > min.insync.replicas。如果两者相等，那么只要有一个副本挂机，整个分区就无法正常工作了。我们不仅要改善消息的持久性，防止数据丢失，还要在不降低可用性的基础上完成。推荐设置成 replication.factor = min.insync.replicas + 1。
    确保消息消费完成再提交。Consumer 端有个参数 enable.auto.commit，最好把它设置成 false，并采用手动提交位移的方式。就像前面说的，这对于单 Consumer 多线程处理的场景而言是至关重要的。
小结
    明确 Kafka 持久化保证的含义和限定条件。
    熟练配置 Kafka 无消息丢失参数。
开放讨论
    其实，Kafka 还有一种特别隐秘的消息丢失场景：增加主题分区。当增加主题分区后，在某段“不凑巧”的时间间隔后，Producer 先于 Consumer 感知到新增加的分区，而 Consumer 设置的是“从最新位移处”开始读取消息，因此在 Consumer 感知到新分区前，Producer 发送的这些消息就全部“丢失”了，或者说 Consumer 无法读取到这些消息。严格来说这是 Kafka 设计上的一个小缺陷，你有什么解决的办法吗？

12 | 客户端都有哪些不常见但是很高级的功能？
什么是拦截器？
    如果你用过 Spring Interceptor 或是 Apache Flume，那么应该不会对拦截器这个概念感到陌生，其基本思想就是允许应用程序在不修改逻辑的情况下，动态地实现一组可插拔的事件处理逻辑链。
Kafka 拦截器
    Kafka 拦截器分为生产者拦截器和消费者拦截器。
        生产者拦截器允许你在发送消息前以及消息提交成功后植入你的拦截器逻辑；而消费者拦截器支持在消费消息前以及提交位移后编写特定逻辑。值得一提的是，这两种拦截器都支持链的方式，即你可以将一组拦截器串连成一个大的拦截器，Kafka 会按照添加顺序依次执行拦截器逻辑。
        org.apache.kafka.clients.producer.ProducerInterceptor
            onSend：该方法会在消息发送之前被调用。如果你想在发送之前对消息“美美容”，这个方法是你唯一的机会。
            onAcknowledgement：该方法会在消息成功提交或发送失败之后被调用。
        org.apache.kafka.clients.consumer.ConsumerInterceptor
            onConsume：该方法在消息返回给 Consumer 程序之前调用。也就是说在开始正式处理消息之前，拦截器会先拦一道，搞一些事情，之后再返回给你。
            onCommit：Consumer 在提交位移之后调用该方法。通常你可以在该方法中做一些记账类的动作，比如打日志等。
        指定拦截器类时要指定它们的全限定名，即 full qualified name。通俗点说就是要把完整包名也加上，不要只有一个类名在那里，并且还要保证你的 Producer 程序能够正确加载你的拦截器类。
典型使用场景
    Kafka 拦截器可以应用于包括客户端监控、端到端系统性能检测、消息审计等多种功能在内的场景。
案例分享
    业务消息从被生产出来到最后被消费的平均总时长是多少

13 | Java生产者是如何管理TCP连接的？
为何采用 TCP？
    人们能够利用 TCP 本身提供的一些高级功能，比如多路复用请求以及同时轮询多个连接的能力。
Kafka 生产者程序概览
    Kafka 的 Java 生产者 API 主要的对象就是 KafkaProducer。通常我们开发一个生产者的步骤有 4 步。
        第 1 步：构造生产者对象所需的参数对象。
        第 2 步：利用第 1 步的参数对象，创建 KafkaProducer 对象实例。
        第 3 步：使用 KafkaProducer 的 send 方法发送消息。
        第 4 步：调用 KafkaProducer 的 close 方法关闭生产者并释放各种系统资源。
何时创建 TCP 连接？
    在创建 KafkaProducer 实例时，生产者应用会在后台创建并启动一个名为 Sender 的线程，该 Sender 线程开始运行时首先会创建与 Broker 的连接。
    TCP 连接还可能在两个地方被创建：一个是在更新元数据后，另一个是在消息发送时。
        为什么是"可能"?当 Producer 更新了集群的元数据信息之后，如果发现与某些 Broker 当前没有连接，那么它就会创建一个 TCP 连接。同样地，当要发送消息时，Producer 发现尚不存在与目标 Broker 的连接，也会创建一个。
            Producer 更新集群元数据信息的两个场景
        此处设计的合理性?    
何时关闭 TCP 连接？
    Producer 端关闭 TCP 连接的方式有两种：一种是用户主动关闭；一种是 Kafka 自动关闭。
        这里的主动关闭实际上是广义的主动关闭，甚至包括用户调用 kill -9 主动“杀掉”Producer 应用。当然最推荐的方式还是调用 producer.close() 方法来关闭。
        第二种是 Kafka 帮你关闭，这与 Producer 端参数 connections.max.idle.ms 的值有关。
            默认情况下该参数值是 9 分钟，即如果在 9 分钟内没有任何请求“流过”某个 TCP 连接，那么 Kafka 会主动帮你把该 TCP 连接关闭。用户可以在 Producer 端设置 connections.max.idle.ms=-1 禁掉这种机制。一旦被设置成 -1，TCP 连接将成为永久长连接。当然这只是软件层面的“长连接”机制，由于 Kafka 创建的这些 Socket 连接都开启了 keepalive，因此 keepalive 探活机制还是会遵守的。
            值得注意的是，在第二种方式中，TCP 连接是在 Broker 端被关闭的，但其实这个 TCP 连接的发起方是客户端，因此在 TCP 看来，这属于被动关闭的场景，即 passive close。被动关闭的后果就是会产生大量的 CLOSE_WAIT 连接，因此 Producer 端或 Client 端没有机会显式地观测到此连接已被中断。
小结
    KafkaProducer 实例创建时启动 Sender 线程，从而创建与 bootstrap.servers 中所有 Broker 的 TCP 连接。
    KafkaProducer 实例首次更新元数据信息之后，还会再次创建与集群中所有 Broker 的 TCP 连接。
    如果 Producer 端发送消息到某台 Broker 时发现没有与该 Broker 的 TCP 连接，那么也会立即创建连接。
    如果设置 Producer 端 connections.max.idle.ms 参数大于 0，则步骤 1 中创建的 TCP 连接会被自动关闭；如果设置该参数 =-1，那么步骤 1 中创建的 TCP 连接将无法被关闭，从而成为“僵尸”连接。

14 | 幂等生产者和事务生产者是一回事吗？
所谓的消息交付可靠性保障，是指 Kafka 对 Producer 和 Consumer 要处理的消息提供什么样的承诺。常见的承诺有以下三种：
    最多一次（at most once）：消息可能会丢失，但绝不会被重复发送。
    至少一次（at least once）：消息不会丢失，但有可能被重复发送。
    精确一次（exactly once）：消息不会丢失，也不会被重复发送。
    目前，Kafka 默认提供的交付可靠性保障是第二种，即至少一次。
    Kafka 也可以提供最多一次交付保障，只需要让 Producer 禁止重试即可。
    Kafka 是怎么做到精确一次的呢？简单来说，这是通过两种机制：幂等性（Idempotence）和事务（Transaction）。
什么是幂等性（Idempotence）？
    “幂等”这个词原是数学领域中的概念，指的是某些操作或函数能够被执行多次，但每次得到的结果都是不变的。
    幂等性有很多好处，其最大的优势在于我们可以安全地重试任何幂等性操作，反正它们也不会破坏我们的系统状态。
幂等性 Producer
    props.put(“enable.idempotence”, ture)，或 props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG， true)
    enable.idempotence 被设置成 true 后，Producer 自动升级成幂等性 Producer，其他所有的代码逻辑都不需要改变。Kafka 自动帮你做消息的重复去重。
        底层具体的原理很简单，就是经典的用空间去换时间的优化思路，即在 Broker 端多保存一些字段。当 Producer 发送了具有相同字段值的消息后，Broker 能够自动知晓这些消息已经重复了，于是可以在后台默默地把它们“丢弃”掉。
    幂等性 Producer 的作用范围
        首先，它只能保证单分区上的幂等性，即一个幂等性 Producer 能够保证某个主题的一个分区上不出现重复消息，它无法实现多个分区的幂等性。
        其次，它只能实现单会话上的幂等性，不能实现跨会话的幂等性。
            这里的会话，你可以理解为 Producer 进程的一次运行。当你重启了 Producer 进程之后，这种幂等性保证就丧失了。
        如果我想实现多分区以及多会话上的消息无重复，应该怎么做呢？答案就是事务（transaction）或者依赖事务型 Producer。这也是幂等性 Producer 和事务型 Producer 的最大区别！
事务型 Producer
    事务型 Producer 能够保证将消息原子性地写入到多个分区中。这批消息要么全部写入成功，要么全部失败。另外，事务型 Producer 也不惧进程的重启。Producer 重启回来后，Kafka 依然保证它们发送消息的精确一次处理。
    设置事务型 Producer 的方法也很简单，满足两个要求即可：
        和幂等性 Producer 一样，开启 enable.idempotence = true。
        设置 Producer 端参数 transctional. id。最好为其设置一个有意义的名字。
        producer.initTransactions();
        try {
            producer.beginTransaction();
            producer.send(record1);
            producer.send(record2);
            producer.commitTransaction();
        } catch (KafkaException e) {
            producer.abortTransaction();
        }
    在 Consumer 端，读取事务型 Producer 发送的消息也是需要一些变更的。修改起来也很简单，设置 isolation.level 参数的值即可。当前这个参数有两个取值：
        read_uncommitted：这是默认值，表明 Consumer 能够读取到 Kafka 写入的任何消息，不论事务型 Producer 提交事务还是终止事务，其写入的消息都可以读取。很显然，如果你用了事务型 Producer，那么对应的 Consumer 就不要使用这个值。
        read_committed：表明 Consumer 只会读取事务型 Producer 成功提交事务写入的消息。当然了，它也能看到非事务型 Producer 写入的所有消息。
小结
    简单来说，幂等性 Producer 和事务型 Producer 都是 Kafka 社区力图为 Kafka 实现精确一次处理语义所提供的工具，只是它们的作用范围是不同的。幂等性 Producer 只能保证单分区、单会话上的消息幂等性；而事务能够保证跨分区、跨会话间的幂等性。从交付语义上来看，自然是事务型 Producer 能做的更多。
    不过，切记天下没有免费的午餐。比起幂等性 Producer，事务型 Producer 的性能要更差，在实际使用过程中，我们需要仔细评估引入事务的开销，切不可无脑地启用事务。

15 | 消费者组到底是什么？
Consumer Group 是 Kafka 提供的可扩展且具有容错性的消费者机制。
    Consumer Group 下可以有一个或多个 Consumer 实例。这里的实例可以是一个单独的进程，也可以是同一进程下的线程。在实际场景中，使用进程更为常见一些。
    Group ID 是一个字符串，在一个 Kafka 集群中，它标识唯一的一个 Consumer Group。
    Consumer Group 下所有实例订阅的主题的单个分区，只能分配给组内的某个 Consumer 实例消费。这个分区当然也可以被其他的 Group 消费。
Kafka 仅仅使用 Consumer Group 这一种机制，却同时实现了传统消息引擎系统的两大模型：
    如果所有实例都属于同一个 Group，那么它实现的就是消息队列模型；
    如果所有实例分别属于不同的 Group，那么它实现的就是发布 / 订阅模型。
理想情况下，Consumer 实例的数量应该等于该 Group 订阅主题的分区总数。
针对 Consumer Group，Kafka 是怎么管理位移的呢？
    位移（Offset）
    大致可以认为是这样的数据结构，即 Map<TopicPartition, Long>，其中 TopicPartition 表示一个分区，而 Long 表示位移的类型。
        在新版本的 Consumer Group 中，Kafka 社区重新设计了 Consumer Group 的位移管理方式，采用了将位移保存在 Kafka 内部主题的方法。这个内部主题就是让人既爱又恨的 __consumer_offsets。
Rebalance 过程
    Rebalance 本质上是一种协议，规定了一个 Consumer Group 下的所有 Consumer 如何达成一致，来分配订阅 Topic 的每个分区。
    Rebalance 的触发条件有 3 个。
        组成员数发生变更。比如有新的 Consumer 实例加入组或者离开组，抑或是有 Consumer 实例崩溃被“踢出”组。
        订阅主题数发生变更。Consumer Group 可以使用正则表达式的方式订阅主题，比如 consumer.subscribe(Pattern.compile(“t.*c”)) 就表明该 Group 订阅所有以字母 t 开头、字母 c 结尾的主题。在 Consumer Group 的运行过程中，你新创建了一个满足这样条件的主题，那么该 Group 就会发生 Rebalance。
        订阅主题的分区数发生变更。Kafka 当前只能允许增加一个主题的分区数。当分区数增加时，就会触发订阅该主题的所有 Group 开启 Rebalance。
    示例
    Rebalance“遭人恨”的地方
        首先，Rebalance 过程对 Consumer Group 消费过程有极大的影响。类似JVM的STW。
        其次，目前 Rebalance 的设计是所有 Consumer 实例共同参与，全部重新分配所有分区。其实更高效的做法是尽量减少分配方案的变动。
        最后，Rebalance 实在是太慢了。

16 | 揭开神秘的“位移主题”面纱








