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
老版本 Consumer 的位移管理是依托于 Apache ZooKeeper
新版本 Consumer 的位移管理机制是将 Consumer 的位移数据作为一条条普通的 Kafka 消息，提交到 __consumer_offsets 中。可以这么说，__consumer_offsets 的主要作用是保存 Kafka 消费者的位移信息。
位移主题就是普通的 Kafka 主题
    但它的消息格式却是 Kafka 自己定义的，用户不能修改，也就是说你不能随意地向这个主题写消息
这个主题存的到底是什么格式的消息
该主题消息的 Key 中应该保存标识 Consumer 的字段，那么，当前 Kafka 中什么字段能够标识 Consumer 呢？
    Group ID 能够标识唯一的 Consumer Group。
    Consumer 提交位移是在分区层面上进行的，即它提交的是某个或某些分区的位移，Key 中还应该保存 Consumer 要提交位移的分区。
总结一下结论。位移主题的 Key 中应该保存 3 部分内容：<Group ID，主题名，分区号 >。
消息体的设计除了保存位移之外还保存了位移提交的一些其他元数据，诸如时间戳和用户自定义的数据等。
    保存这些元数据是为了帮助 Kafka 执行各种各样后续的操作，比如删除过期位移消息等。
    但总体来说，我们还是可以简单地认为消息体就是保存了位移值。
位移主题的消息格式有 3 种,除了刚刚我们说的这种格式，还有 2 种格式：
    用于保存 Consumer Group 信息的消息。
        你只需要记住它是用来注册 Consumer Group 的就可以了。
    用于删除 Group 过期位移甚至是删除 Group 的消息。
        有个专属的名字：tombstone 消息，即墓碑消息，也称 delete mark。
            何时会写入这类消息
                一旦某个 Consumer Group 下的所有 Consumer 实例都停止了，而且它们的位移数据都已被删除时，Kafka 会向位移主题的对应分区写入 tombstone 消息，表明要彻底删除这个 Group 的信息。
通常来说，当 Kafka 集群中的第一个 Consumer 程序启动时，Kafka 会自动创建位移主题。   
总结一下，如果位移主题是 Kafka 自动创建的，那么该主题的分区数是 50，副本数是 3。
    分区数:Broker 端参数 offsets.topic.num.partitions,默认值是 50。
    分区数，副本数或备份因子:Broker 端另一个参数 offsets.topic.replication.factor ,默认值是3。
目前 Kafka Consumer 提交位移的方式有两种：自动提交位移和手动提交位移。
    Consumer 端有个参数叫 enable.auto.commit，如果值是 true，则 Consumer 在后台默默地为你定期提交位移，提交间隔由一个专属的参数 auto.commit.interval.ms 来控制。
    另一种位移提交方式：手动提交位移，即设置 enable.auto.commit = false。一旦设置了 false，作为 Consumer 应用开发的你就要承担起位移提交的责任。
        Kafka Consumer API 为你提供了位移提交的方法，如 consumer.commitSync 等。
Kafka 是怎么删除位移主题中的过期消息
    Compaction
    Kafka 使用Compact 策略来删除位移主题中的过期消息，避免该主题无限期膨胀。
Kafka 提供了专门的后台线程定期地巡检待 Compact 的主题，看看是否存在满足条件的可删除数据。这个后台线程叫 Log Cleaner。
    很多实际生产环境中都出现过位移主题无限膨胀占用过多磁盘空间的问题，如果你的环境中也有这个问题，我建议你去检查一下 Log Cleaner 线程的状态，通常都是这个线程挂掉了导致的。

17 | 消费者组重平衡能避免吗？
重平衡Rebalance
    让一个 Consumer Group 下所有的 Consumer 实例就如何消费订阅主题的所有分区达成共识的过程。
    在 Rebalance 过程中，所有 Consumer 实例共同参与，在协调者组件的帮助下，完成订阅主题分区的分配。
    但是，在整个过程中，所有实例都不能消费任何消息，因此它对 Consumer 的 TPS 影响很大。
协调者Coordinator
    专门为 Consumer Group 服务，负责为 Group 执行 Rebalance 以及提供位移管理和组成员管理等。
        具体来讲，Consumer 端应用程序在提交位移时，其实是向 Coordinator 所在的 Broker 提交位移。
        同样地，当 Consumer 应用启动时，也是向 Coordinator 所在的 Broker 发送各种请求，然后由 Coordinator 负责执行消费者组的注册、成员管理记录等元数据管理操作。
所有 Broker 在启动时，都会创建和开启相应的 Coordinator 组件。
    也就是说，所有 Broker 都有各自的 Coordinator 组件。
Kafka 为某个 Consumer Group 确定 Coordinator 所在的 Broker 的算法有 2 个步骤。
    第 1 步：确定由位移主题的哪个分区来保存该 Group 数据：partitionId=Math.abs(groupId.hashCode() % offsetsTopicPartitionCount)。
    第 2 步：找出该分区 Leader 副本所在的 Broker，该 Broker 即为对应的 Coordinator。
    算法过程解释
    知晓这个算法的最大意义在于，它能够帮助我们解决定位问题。
        当 Consumer Group 出现问题，需要快速排查 Broker 端日志时，我们能够根据这个算法准确定位 Coordinator 对应的 Broker，不必一台 Broker 一台 Broker 地盲查。
Rebalance 的弊端
    Rebalance 影响 Consumer 端 TPS。
        类似STW
    Rebalance 很慢。
    Rebalance 效率不高。
        当前 Kafka 的设计机制决定了每次 Rebalance 时，Group 下的所有成员都要参与进来，而且通常不会考虑局部性原理
Rebalance 发生的时机有三个：
    组成员数量发生变化
    订阅主题数量发生变化
    订阅主题的分区数发生变化
    后面两个通常都是运维的主动操作，所以它们引发的 Rebalance 大都是不可避免的。
因为组成员数量变化而引发的 Rebalance 该如何避免。99% 的 Rebalance，都是这个原因导致的。
如果某个 Consumer 实例不能及时地发送这些心跳请求，Coordinator 就会认为该 Consumer 已经“死”了，从而将其从 Group 中移除，然后开启新一轮 Rebalance。
    Consumer 端有个参数，叫 session.timeout.ms，就是被用来表征此事的。该参数的默认值是 10 秒，即如果 Coordinator 在 10 秒之内没有收到 Group 下某 Consumer 实例的心跳，它就会认为这个 Consumer 实例已经挂了。可以这么说，session.timout.ms 决定了 Consumer 存活性的时间间隔。
    Consumer 还提供了一个允许你控制发送心跳请求频率的参数，就是 heartbeat.interval.ms。这个值设置得越小，Consumer 实例发送心跳请求的频率就越高。频繁地发送心跳请求会额外消耗带宽资源，但好处是能够更加快速地知晓当前是否开启 Rebalance，因为，目前 Coordinator 通知各个 Consumer 实例开启 Rebalance 的方法，就是将 REBALANCE_NEEDED 标志封装进心跳请求的响应体中。
    Consumer 端还有一个参数，用于控制 Consumer 实际消费能力对 Rebalance 的影响，即 max.poll.interval.ms 参数。它限定了 Consumer 端应用程序两次调用 poll 方法的最大时间间隔。它的默认值是 5 分钟，表示你的 Consumer 程序如果在 5 分钟之内无法消费完 poll 方法返回的消息，那么 Consumer 会主动发起“离开组”的请求，Coordinator 也会开启新一轮 Rebalance。
明确一下到底哪些 Rebalance 是“不必要的”
    第一类非必要 Rebalance 是因为未能及时发送心跳，导致 Consumer 被“踢出”Group 而引发的。
        给出一些推荐数值，你可以“无脑”地应用在你的生产环境中。
            设置 session.timeout.ms = 6s。
            设置 heartbeat.interval.ms = 2s。
            要保证 Consumer 实例在被判定为“dead”之前，能够发送至少 3 轮的心跳请求，即 session.timeout.ms >= 3 * heartbeat.interval.ms。
    第二类非必要 Rebalance 是 Consumer 消费时间过长导致的。
        max.poll.interval.ms参数值的设置显得尤为关键。如果要避免非预期的 Rebalance，你最好将该参数值设置得大一点，比你的下游最大处理时间稍长一点。
        总之，你要为你的业务处理逻辑留下充足的时间。这样，Consumer 就不会因为处理这些消息的时间太长而引发 Rebalance 了。
    如果你按照上面的推荐数值恰当地设置了这几个参数，却发现还是出现了 Rebalance，那么我建议你去排查一下Consumer 端的 GC 表现，比如是否出现了频繁的 Full GC 导致的长时间停顿，从而引发了 Rebalance。
小结
总而言之，我们一定要避免因为各种参数或逻辑不合理而导致的组成员意外离组或退出的情形，与之相关的主要参数有：
    session.timeout.ms
    heartbeat.interval.ms
    max.poll.interval.ms
    GC 参数

18 | Kafka中位移提交那些事儿
Consumer 需要向 Kafka 汇报自己的位移数据，这个汇报过程被称为提交位移（Committing Offsets）。
    因为 Consumer 能够同时消费多个分区的数据，所以位移的提交实际上是在分区粒度上进行的，即Consumer 需要为分配给它的每个分区提交各自的位移数据。
    位移提交的语义保障是由你来负责的，Kafka 只会“无脑”地接受你提交的位移。
KafkaConsumer API，提供了多种提交位移的方法。
    从用户的角度来说，位移提交分为自动提交和手动提交；
    从 Consumer 端的角度来说，位移提交分为同步提交和异步提交。
自动提交
    Consumer 端有个参数 enable.auto.commit，把它设置为 true 或者压根不设置它就可以了。因为它的默认值就是 true，即 Java Consumer 默认就是自动提交位移的。如果启用了自动提交，Consumer 端还有个参数就派上用场了：auto.commit.interval.ms。它的默认值是 5 秒，表明 Kafka 每 5 秒会为你自动提交一次位移。
    代码展示
    设置了 enable.auto.commit 为 true可能会出现重复消费
        一旦设置了 enable.auto.commit 为 true，Kafka 会保证在开始调用 poll 方法时，提交上次 poll 返回的所有消息。从顺序上来说，poll 方法的逻辑是先提交上一批消息的位移，再处理下一批消息，因此它能保证不出现消费丢失的情况。
        原因:见文稿。每 5 秒自动提交一次位移, 3秒发生了 Rebalance 操作。
手动提交
    设置 enable.auto.commit 为 false。
    仅仅设置它为 false 还不够，因为你只是告诉 Kafka Consumer 不要自动提交位移而已，你还需要调用相应的 API 手动提交位移。
    最简单的 API 就是KafkaConsumer#commitSync()。该方法会提交 KafkaConsumer#poll() 返回的最新位移。
    它是一个同步操作，即该方法会一直等待，直到位移被成功提交才会返回。如果提交过程中出现异常，该方法会将异常信息抛出。
    手动提交位移的缺陷:
        调用 commitSync() 时，Consumer 程序会处于阻塞状态，直到远端的 Broker 返回提交结果，这个状态才会结束。
    Kafka 社区为手动提交位移提供了另一个 API 方法：KafkaConsumer#commitAsync()。
        会立即返回，不会阻塞，因此不会影响 Consumer 应用的 TPS。由于它是异步的，Kafka 提供了回调函数（callback），供你实现提交之后的逻辑，比如记录日志或处理异常等。        
            代码展示
    commitAsync 是否能够替代 commitSync 呢？
        不能。commitAsync 的问题在于，出现问题时它不会自动重试。
        因为它是异步操作，倘若提交失败后自动重试，那么它重试时提交的位移值可能早已经“过期”或不是最新值了。因此，异步提交的重试其实没有意义，所以 commitAsync 是不会重试的。
    需要将 commitSync 和 commitAsync 组合使用才能到达最理想的效果，原因有两个：
        我们可以利用 commitSync 的自动重试来规避那些瞬时错误，比如网络的瞬时抖动，Broker 端 GC 等。因为这些问题都是短暂的，自动重试通常都会成功，因此，我们不想自己重试，而是希望 Kafka Consumer 帮我们做这件事。
        我们不希望程序总处于阻塞状态，影响 TPS。
        代码展示
进行细粒度的位移提交
    poll了5000条消息,不想把这 5000 条消息都处理完之后再提交位移，因为一旦中间出现差错，之前处理的全部都要重来一遍。
    Kafka Consumer API 为手动提交提供了这样的方法：commitSync(Map<TopicPartition, OffsetAndMetadata>) 和 commitAsync(Map<TopicPartition, OffsetAndMetadata>)。
    代码展示

19 | CommitFailedException异常怎么处理？
所谓 CommitFailedException，顾名思义就是 Consumer 客户端在提交位移时出现了错误或异常，而且还是那种不可恢复的严重异常。
社区对这个异常的最新解释
    本次提交位移失败了，原因是消费者组已经开启了 Rebalance 过程，并且将要提交位移的分区分配给了另一个消费者实例。
    出现这个情况的原因是，你的消费者实例连续两次调用 poll 方法的时间间隔超过了期望的 max.poll.interval.ms 参数值。
    这通常表明，你的消费者实例花费了太长的时间进行消息处理，耽误了调用 poll 方法。
社区给出了两个相应的解决办法
    增加期望的时间间隔 max.poll.interval.ms 参数值。
    减少 poll 方法一次性返回的消息数量，即减少 max.poll.records 参数值。
有两种典型的场景可能遭遇该异常
场景一
    max.poll.interval.ms<程序逻辑执行时长
    防止这种场景下抛出异常，你需要简化你的消息处理逻辑。具体来说有 4 种方法。
        缩短单条消息处理的时间。
        增加 Consumer 端允许下游系统消费一批消息的最大时长。
        减少下游系统一次性消费的消息总数。
            这取决于 Consumer 端参数 max.poll.records 的值。当前该参数的默认值是 500 条，表明调用一次 KafkaConsumer.poll 方法，最多返回 500 条消息。
        下游系统使用多线程来加速消费。
            “最高级”同时也是最难实现的解决办法了。
场景二
    冷门
        Kafka Java Consumer 端还提供了一个名为 Standalone Consumer 的独立消费者。
        独立消费者也要指定 group.id 参数才能提交位移。
        如果你的应用中同时出现了设置相同 group.id 值的消费者组程序和独立消费者程序，那么当独立消费者程序手动提交位移时，Kafka 就会立即抛出 CommitFailedException 异常，因为 Kafka 无法识别这个具有相同 group.id 的消费者实例，于是就向它返回一个错误，表明它不是消费者组内合法的成员。

20 | 多线程开发消费者实例
Kafka Java Consumer 设计原理
    从 Kafka 0.10.1.0 版本开始，KafkaConsumer 就变为了双线程的设计，即用户主线程和心跳线程。
    户主线程，就是你启动 Consumer 应用程序 main 方法的那个线程，而新引入的心跳线程（Heartbeat Thread）只负责定期给对应的 Broker 机器发送心跳请求，以标识消费者应用的存活性（liveness）。
    引入这个心跳线程还有一个目的，那就是期望它能将心跳频率与主线程调用 KafkaConsumer.poll 方法的频率分开，从而解耦真实的消息处理逻辑与消费者组成员存活性管理。
    虽然有心跳线程，但实际的消息获取逻辑依然是在用户主线程中完成的。因此，在消费消息的这个层面上，我们依然可以安全地认为 KafkaConsumer 是单线程的设计。
    老版本 Consumer 是多线程的架构
多线程方案
    KafkaConsumer 类不是线程安全的 (thread-safe)。
    所有的网络 I/O 处理都是发生在用户主线程中，因此，你在使用过程中必须要确保线程安全。
    简单来说，就是你不能在多个线程中共享同一个 KafkaConsumer 实例，否则程序会抛出 ConcurrentModificationException 异常。
    KafkaConsumer 中有个方法是例外的，它就是wakeup()，你可以在其他线程中安全地调用KafkaConsumer.wakeup()来唤醒 Consumer。
制定两套多线程方案
    消费者程序启动多个线程，每个线程维护专属的 KafkaConsumer 实例，负责完整的消息获取、消息处理流程。
    消费者程序使用单或多线程获取消息，同时创建多个消费线程执行消息处理逻辑。
        获取消息的线程可以是一个，也可以是多个，每个线程维护专属的 KafkaConsumer 实例，处理消息则交由特定的线程池来做，从而实现消息获取与消息处理的真正解耦。
    各自优缺点
实现代码示例

21 | Java 消费者是如何管理TCP连接的?
何时创建 TCP 连接？
    和生产者不同的是，构建 KafkaConsumer 实例时是不会创建任何 TCP 连接的
    TCP 连接是在调用 KafkaConsumer.poll 方法时被创建的。再细粒度地说，在 poll 方法内部有 3 个时机可以创建 TCP 连接。
1.发起 FindCoordinator 请求时。
2.连接协调者时。
    Broker 处理完上一步发送的 FindCoordinator 请求之后，会返还对应的响应结果（Response），显式地告诉消费者哪个 Broker 是真正的协调者，因此在这一步，消费者知晓了真正的协调者后，会创建连向该 Broker 的 Socket 连接。只有成功连入协调者，协调者才能开启正常的组协调操作，比如加入组、等待组分配方案、心跳请求处理、位移获取、位移提交等。
3.消费数据时。
    消费者会为每个要消费的分区创建与该分区领导者副本所在 Broker 连接的 TCP。
    举个例子，假设消费者要消费 5 个分区的数据，这 5 个分区各自的领导者副本分布在 4 台 Broker 上，那么该消费者在消费时会创建与这 4 台 Broker 的 Socket 连接。
创建多少个 TCP 连接？
    消费者程序会创建 3 类 TCP 连接：
        确定协调者和获取集群元数据。
        连接协调者，令其执行组成员管理操作。
        执行实际的消息获取。
何时关闭 TCP 连接？
    消费者关闭 Socket 也分为主动关闭和 Kafka 自动关闭
        主动关闭是指你显式地调用消费者 API 的方法去关闭消费者，具体方式就是手动调用 KafkaConsumer.close() 方法，或者是执行 Kill 命令，不论是 Kill -2 还是 Kill -9；
        Kafka 自动关闭是由消费者端参数 connection.max.idle.ms控制的，该参数现在的默认值是 9 分钟，即如果某个 Socket 连接上连续 9 分钟都没有任何请求“过境”的话，那么消费者会强行“杀掉”这个 Socket 连接。
    和生产者有些不同的是，如果在编写消费者程序时，你使用了循环的方式来调用 poll 方法消费消息，那么上面提到的所有请求都会被定期发送到 Broker，因此这些 Socket 连接上总是能保证有请求在发送，从而也就实现了“长连接”的效果。
    当第三类 TCP 连接成功创建后，消费者程序就会废弃第一类 TCP 连接，之后在定期请求元数据时，它会改为使用第三类 TCP 连接。也就是说，最终你会发现，第一类 TCP 连接会在后台被默默地关闭掉。对一个运行了一段时间的消费者程序来说，只会有后面两类 TCP 连接存在。
可能的问题
    第一类 TCP 连接仅仅是为了首次获取元数据而创建的，后面就会被废弃掉。最根本的原因是，消费者在启动时还不知道 Kafka 集群的信息，只能使用一个“假”的 ID 去注册，即使消费者获取了真实的 Broker ID，它依旧无法区分这个“假”ID 对应的是哪台 Broker，因此也就无法重用这个 Socket 连接，只能再重新创建一个新的连接。
    因为目前 Kafka 仅仅使用 ID 这一个维度的数据来表征 Socket 连接信息。这点信息明显不足以确定连接的是哪台 Broker，也许在未来，社区应该考虑使用< 主机名、端口、ID>三元组的方式来定位 Socket 资源，这样或许能够让消费者程序少创建一些 TCP 连接。

22 | 消费者组消费进度监控都怎么实现？
消费者 Lag 或 Consumer Lag
滞后程度，就是指消费者当前落后于生产者的程度。在实际业务场景中必须时刻关注消费者的消费进度。一旦出现 Lag 逐步增加的趋势，一定要定位问题，及时处理，避免造成业务损失。
怎么监控
Kafka 自带命令
    $ bin/kafka-consumer-groups.sh --bootstrap-server <Kafka broker 连接信息 > --describe --group <group 名称 >
Kafka Java Consumer API
    代码展示。只适用于 Kafka 2.0.0 及以上的版本。
Kafka JMX 监控指标
建议你优先考虑方法 3，同时将方法 1 和方法 2 作为备选，装进你自己的工具箱中，随时取出来应对各种实际场景。

23 | Kafka副本机制详解
副本机制有什么好处
    提供数据冗余。
    提供高伸缩性。
    改善数据局部性。
    对于 Apache Kafka 而言，目前只能享受到副本机制带来的第 1 个好处
副本定义
    所谓副本（Replica），本质就是一个只能追加写消息的提交日志。
    根据 Kafka 副本机制的定义，同一个分区下的所有副本保存有相同的消息序列，这些副本分散保存在不同的 Broker 上，从而能够对抗部分 Broker 宕机带来的数据不可用。
    在实际生产环境中，每台 Broker 都可能保存有各个主题下不同分区的不同副本，因此，单个 Broker 上存有成百上千个副本的现象是非常正常的。
副本角色
    基于领导者（Leader-based）的副本机制。
        第一，在 Kafka 中，副本分成两类：领导者副本（Leader Replica）和追随者副本（Follower Replica）。
            每个分区在创建时都要选举一个副本，称为领导者副本，其余的副本自动称为追随者副本。
        第二，Kafka 的副本机制比其他分布式系统要更严格一些。
            在 Kafka 中，追随者副本是不对外提供服务的。这就是说，任何一个追随者副本都不能响应消费者和生产者的读写请求。所有的请求都必须由领导者副本来处理，或者说，所有的读写请求都必须发往领导者副本所在的 Broker，由该 Broker 负责处理。追随者副本不处理客户端请求，它唯一的任务就是从领导者副本异步拉取消息，并写入到自己的提交日志中，从而实现与领导者副本的同步。
        第三，当领导者副本挂掉了，或者说领导者副本所在的 Broker 宕机时，Kafka 依托于 ZooKeeper 提供的监控功能能够实时感知到，并立即开启新一轮的领导者选举，从追随者副本中选一个作为新的领导者。
            老 Leader 副本重启回来后，只能作为追随者副本加入到集群中。
    这种副本机制有两个方面的好处
        1.方便实现“Read-your-writes”。
            使用生产者 API 向 Kafka 成功写入消息后，马上使用消费者 API 去读取刚才生产的消息。
        2.方便实现单调读（Monotonic Reads）。
            所有的读请求都是由 Leader 来处理，那么 Kafka 就很容易实现单调读一致性。
In-sync Replicas（ISR）
    Kafka 要明确地告诉我们，追随者副本到底在什么条件下才算与 Leader 同步。
    基于这个想法，Kafka 引入了 In-sync Replicas，也就是所谓的 ISR 副本集合。
    ISR 中的副本都是与 Leader 同步的副本，相反，不在 ISR 中的追随者副本就被认为是与 Leader 不同步的。
    Leader 副本天然就在 ISR 中。也就是说，ISR 不只是追随者副本集合，它必然包括 Leader 副本。甚至在某些情况下，ISR 只有 Leader 这一个副本。
Kafka 判断 Follower 是否与 Leader 同步的标准，不是看相差的消息数，而是另有“玄机”。
    这个标准就是 Broker 端参数 replica.lag.time.max.ms 参数值。
    这就是说，只要一个 Follower 副本落后 Leader 副本的时间不连续超过 10 秒，那么 Kafka 就认为该 Follower 副本与 Leader 是同步的，即使此时 Follower 副本中保存的消息明显少于 Leader 副本中的消息。
    Follower 副本唯一的工作就是不断地从 Leader 副本拉取消息，然后写入到自己的提交日志中。如果这个同步过程的速度持续慢于 Leader 副本的消息写入速度，那么在 replica.lag.time.max.ms 时间后，此 Follower 副本就会被认为是与 Leader 副本不同步的，因此不能再放入 ISR 中。此时，Kafka 会自动收缩 ISR 集合，将该副本“踢出”ISR。
    值得注意的是，倘若该副本后面慢慢地追上了 Leader 的进度，那么它是能够重新被加回 ISR 的。这也表明，ISR 是一个动态调整的集合，而非静态不变的。
Unclean 领导者选举（Unclean Leader Election）
    既然 ISR 是可以动态调整的，那么自然就可以出现这样的情形：ISR 为空。因为 Leader 副本天然就在 ISR 中，如果 ISR 为空了，就说明 Leader 副本也“挂掉”了，Kafka 需要重新选举一个新的 Leader。
    Kafka 把所有不在 ISR 中的存活副本都称为非同步副本。
    通常来说，非同步副本落后 Leader 太多，因此，如果选择这些副本作为新 Leader，就可能出现数据的丢失。毕竟，这些副本中保存的消息远远落后于老 Leader 中的消息。
    在 Kafka 中，选举这种副本的过程称为 Unclean 领导者选举。Broker 端参数 unclean.leader.election.enable 控制是否允许 Unclean 领导者选举。
    开启 Unclean 领导者选举可能会造成数据丢失，但好处是，它使得分区 Leader 副本一直存在，不至于停止对外提供服务，因此提升了高可用性。
    反之，禁止 Unclean 领导者选举的好处在于维护了数据的一致性，避免了消息丢失，但牺牲了高可用性。
    根据你的实际业务场景决定是否开启 Unclean 领导者选举。不过，我强烈建议你不要开启它，毕竟我们还可以通过其他的方式来提升高可用性。如果为了这点儿高可用性的改善，牺牲了数据一致性，那就非常不值当了。

24 | 请求是怎么被处理的？
无论是 Kafka 客户端还是 Broker 端，它们之间的交互都是通过“请求 / 响应”的方式完成的。
    Apache Kafka 自己定义了一组请求协议，用于实现各种各样的交互操作。比如常见的 PRODUCE 请求是用于生产消息的，FETCH 请求是用于消费消息的，METADATA 请求是用于请求 Kafka 集群元数据信息的。
    Kafka 定义了很多类似的请求格式。我数了一下，截止到目前最新的 2.3 版本，Kafka 共定义了多达 45 种请求格式。所有的请求都是通过 TCP 网络以 Socket 的方式进行通讯的。
如何处理请求，我们很容易想到的方案有两个
1.顺序处理请求。
    吞吐量太差只适用于请求发送非常不频繁的系统。
2.每个请求使用单独线程处理。
    为每个请求都创建线程的做法开销极大，在某些场景下甚至会压垮整个服务。还是那句话，这个方法只适用于请求发送频率很低的业务场景。
Kafka 使用的是Reactor 模式
    Reactor 模式是事件驱动架构的一种实现方式，特别适合应用于处理多个客户端并发向服务器端发送请求的场景。
Reactor 模式的架构图
Kafka的架构图
    Kafka 提供了 Broker 端参数 num.network.threads，用于调整该网络线程池的线程数。其默认值是 3，表示每台 Broker 启动时会创建 3 个网络线程，专门处理客户端发送的请求。
    网络线程接收到请求后,Kafka 在这个环节又做了一层异步线程池的处理
    当网络线程拿到请求后，它不是自己处理，而是将请求放入到一个共享请求队列中。Broker 端还有个 IO 线程池，负责从该队列中取出请求，执行真正的处理。
        如果是 PRODUCE 生产请求，则将消息写入到底层的磁盘日志中；如果是 FETCH 请求，则从磁盘或页缓存中读取消息。
    IO 线程池处中的线程才是执行请求逻辑的线程。Broker 端参数num.io.threads控制了这个线程池中的线程数。目前该参数默认值是 8，表示每台 Broker 启动后自动创建 8 个 IO 线程处理请求。你可以根据实际硬件条件设置此线程池的个数。
    请求队列是所有网络线程共享的，而响应队列则是每个网络线程专属的。
        这么设计的原因就在于，Dispatcher 只是用于请求分发而不负责响应回传，因此只能让每个网络线程自己发送 Response 给客户端，所以这些 Response 也就没必要放在一个公共的地方。
    Purgatory 的组件
        这是 Kafka 中著名的“炼狱”组件。它是用来缓存延时请求（Delayed Request）的。所谓延时请求，就是那些一时未满足条件不能立刻处理的请求。
            比如设置了 acks=all 的 PRODUCE 请求，一旦设置了 acks=all，那么该请求就必须等待 ISR 中所有副本都接收了消息后才能返回，此时处理该请求的 IO 线程就必须等待其他 Broker 的写入结果。
            当请求不能立刻处理时，它就会暂存在 Purgatory 中。稍后一旦满足了完成条件，IO 线程会继续处理该请求，并将 Response 放入对应网络线程的响应队列中。
在 Kafka 内部，除了客户端发送的 PRODUCE 请求和 FETCH 请求之外，还有很多执行其他操作的请求类型
    比如负责更新 Leader 副本、Follower 副本以及 ISR 集合的 LeaderAndIsr 请求，负责勒令副本下线的 StopReplica 请求等。
    与 PRODUCE 和 FETCH 请求相比，这些请求有个明显的不同：它们不是数据类的请求，而是控制类的请求。也就是说，它们并不是操作消息数据的，而是用来执行特定的 Kafka 内部动作的。
    Kafka 社区把 PRODUCE 和 FETCH 这类请求称为数据类请求，把 LeaderAndIsr、StopReplica 这类请求称为控制类请求。
    当前这种一视同仁的处理方式对控制类请求是不合理。
        因为控制类请求有这样一种能力：它可以直接令数据类请求失效！
    问题举例
        社区于 2.3 版本正式实现了数据类请求和控制类请求的分离。
    社区的解决方案

25 | 消费者组重平衡全流程解析
触发与通知
    重平衡的 3 个触发条件：
        组成员数量发生变化。
        订阅主题数量发生变化。
        订阅主题的分区数发生变化。
重平衡过程是如何通知到其他消费者实例的？
    答案就是，靠消费者端的心跳线程（Heartbeat Thread）。
    重平衡的通知机制正是通过心跳线程来完成的。
        当协调者决定开启新一轮重平衡后，它会将“REBALANCE_IN_PROGRESS”封装进心跳请求的响应中，发还给消费者实例。当消费者实例发现心跳响应中包含了“REBALANCE_IN_PROGRESS”，就能立马知道重平衡又开始了，这就是重平衡的通知机制。
    heartbeat.interval.ms 的真实用途
        从字面上看，它就是设置了心跳的间隔时间，但这个参数的真正作用是控制重平衡通知的频率。
        如果你想要消费者实例更迅速地得到通知，那么就可以给这个参数设置一个非常小的值，这样消费者就能更快地感知到重平衡已经开启了。
消费者组状态机
    重平衡一旦开启，Broker 端的协调者组件就要开始忙了，主要涉及到控制消费者组的状态流转。
    当前，Kafka 设计了一套消费者组状态机（State Machine），来帮助协调者完成整个重平衡流程。
    严格来说，这套状态机属于非常底层的设计，Kafka 官网上压根就没有提到过，但你最好还是了解一下，因为它能够帮助你搞懂消费者组的设计原理，比如消费者组的过期位移（Expired Offsets）删除等。
    目前，Kafka 为消费者组定义了 5 种状态，它们分别是：Empty、Dead、PreparingRebalance、CompletingRebalance 和 Stable。
消费者端重平衡流程
    在消费者端，重平衡分为两个步骤：分别是加入组和等待领导者消费者（Leader Consumer）分配方案。
        这里的领导者是具体的消费者实例，它既不是副本，也不是协调者。领导者消费者的任务是收集所有成员的订阅信息，然后根据这些信息，制定具体的分区消费分配方案。
    这两个步骤分别对应两类特定的请求：JoinGroup 请求和 SyncGroup 请求。
    当组内成员加入组时，它会向协调者发送 JoinGroup 请求。在该请求中，每个成员都要将自己订阅的主题上报，这样协调者就能收集到所有成员的订阅信息。一旦收集了全部成员的 JoinGroup 请求后，协调者会从这些成员中选择一个担任这个消费者组的领导者。
        通常情况下，第一个发送 JoinGroup 请求的成员自动成为领导者。
    选出领导者之后，协调者会把消费者组订阅信息封装进 JoinGroup 请求的响应体中，然后发给领导者，由领导者统一做出分配方案后，进入到下一步：发送 SyncGroup 请求。
        在这一步中，领导者向协调者发送 SyncGroup 请求，将刚刚做出的分配方案发给协调者。
        值得注意的是，其他成员也会向协调者发送 SyncGroup 请求，只不过请求体中并没有实际的内容。
        这一步的主要目的是让协调者接收分配方案，然后统一以 SyncGroup 响应的方式分发给所有成员，这样组内所有成员就都知道自己该消费哪些分区了。
    见详细的流程图
Broker 端重平衡场景剖析
场景一：新成员入组。
    全新启动一个消费者组
    图
场景二：组成员主动离组。
    消费者实例所在线程或进程调用 close() 方法主动通知协调者它要退出
    LeaveGroup 请求
    图
场景三：组成员崩溃离组。
    崩溃离组是指消费者实例出现严重故障，突然宕机导致的离组。
    协调者通常需要等待一段时间才能感知到，这段时间一般是由消费者端参数 session.timeout.ms 控制的。
    图
场景四：重平衡时协调者对组内成员提交位移的处理。
    正常情况下，每个组内成员都会定期汇报位移给协调者。当重平衡开启时，协调者会给予成员一段缓冲时间，要求每个成员必须在这段时间内快速地上报自己的位移信息，然后再开启正常的 JoinGroup/SyncGroup 请求发送。
    图

26 | 你一定不能错过的Kafka控制器
控制器组件（Controller），是 Apache Kafka 的核心组件。它的主要作用是在 Apache ZooKeeper 的帮助下管理和协调整个 Kafka 集群。
控制器是重度依赖 ZooKeeper 的
    Apache ZooKeeper 是一个提供高可靠性的分布式协调服务框架。
    ......
控制器是如何被选出来的？
    Broker 在启动时，会尝试去 ZooKeeper 中创建 /controller 节点。Kafka 当前选举控制器的规则是：第一个成功创建 /controller 节点的 Broker 会被指定为控制器。
控制器是做什么的？
    控制器的职责大致可以分为 5 种
        1.主题管理（创建、删除、增加分区）
            这里的主题管理，就是指控制器帮助我们完成对 Kafka 主题的创建、删除以及分区增加的操作。换句话说，当我们执行kafka-topics 脚本时，大部分的后台工作都是控制器来完成的。
        2.分区重分配
            分区重分配主要是指，kafka-reassign-partitions 脚本提供的对已有主题分区进行细粒度的分配功能。这部分功能也是控制器实现的。
        3.Preferred 领导者选举
            Preferred 领导者选举主要是 Kafka 为了避免部分 Broker 负载过重而提供的一种换 Leader 的方案。
        4.集群成员管理（新增 Broker、Broker 主动关闭、Broker 宕机）
            这是控制器提供的第 4 类功能，包括自动检测新增 Broker、Broker 主动关闭及被动宕机。这种自动检测是依赖于前面提到的 Watch 功能和 ZooKeeper 临时节点组合实现的。
        5.数据服务
            控制器的最后一大类工作，就是向其他 Broker 提供数据服务。控制器上保存了最全的集群元数据信息，其他所有 Broker 会定期接收控制器发来的元数据更新请求，从而更新其内存中的缓存数据。
控制器保存了什么数据？
    所有主题信息。包括具体的分区信息，比如领导者副本是谁，ISR 集合中有哪些副本等。
    所有 Broker 信息。包括当前都有哪些运行中的 Broker，哪些正在关闭中的 Broker 等。
    所有涉及运维任务的分区。包括当前正在进行 Preferred 领导者选举以及分区重分配的分区列表。
    这些数据其实在 ZooKeeper 中也保存了一份。
        每当控制器初始化时，它都会从 ZooKeeper 上读取对应的元数据并填充到自己的缓存中。
        有了这些数据，控制器就能对外提供数据服务了。这里的对外主要是指对其他 Broker 而言，控制器通过向这些 Broker 发送请求的方式将这些数据同步到其他 Broker 上。
控制器故障转移（Failover）
    故障转移指的是，当运行中的控制器突然宕机或意外终止时，Kafka 能够快速地感知到，并立即启用备用控制器来代替之前失败的控制器。
    这个过程就被称为 Failover，该过程是自动完成的，无需你手动干预。
控制器内部设计原理
    图
    改进
小结
    一个小窍门。当你觉得控制器组件出现问题时，比如主题无法删除了，或者重分区 hang 住了，你不用重启 Kafka Broker 或控制器。有一个简单快速的方式是，去 ZooKeeper 中手动删除 /controller 节点。具体命令是 rmr /controller。这样做的好处是，既可以引发控制器的重选举，又可以避免重启 Broker 导致的消息处理中断。

27 | 关于高水位和Leader Epoch的讨论
什么是高水位？
高水位的作用
    高水位的作用主要有 2 个
        定义消息可见性，即用来标识分区下的哪些消息是可以被消费者消费的。
            在分区高水位以下的消息被认为是已提交消息，反之就是未提交消息。消费者只能消费已提交消息。
            位移值等于高水位的消息也属于未提交消息。也就是说，高水位上的消息是不能被消费者消费的。
            Log End Offset，简写是 LEO。它表示副本写入下一条消息的位移值。
            同一个副本对象，其高水位值不会大于 LEO 值。
            高水位和 LEO 是副本对象的两个重要属性。
                Kafka 所有副本都有对应的高水位和 LEO 值，而不仅仅是 Leader 副本。只不过 Leader 副本比较特殊，Kafka 使用 Leader 副本的高水位来定义所在分区的高水位。换句话说，分区的高水位就是其 Leader 副本的高水位。
        帮助 Kafka 完成副本同步。
高水位更新机制
    图
    远程副本（Remote Replica）的概念
    为什么要在 Broker 0(Leader副本所在的Broker) 上保存这些远程副本呢
        它们的主要作用是，帮助 Leader 副本确定其高水位，也就是分区高水位。
    表格
    什么叫与 Leader 副本保持同步。判断的条件有两个。
        该远程 Follower 副本在 ISR 中。
        该远程 Follower 副本 LEO 值落后于 Leader 副本 LEO 值的时间，不超过 Broker 端参数 replica.lag.time.max.ms 的值。如果使用默认值的话，就是不超过 10 秒。
    分别从 Leader 副本和 Follower 副本两个维度，来总结一下高水位和 LEO 的更新机制。
    Leader 副本:......
    Follower 副本:......
副本同步机制解析
    图
Leader Epoch 登场
    Follower 副本的高水位更新需要一轮额外的拉取请求才能实现。如果把上面那个例子扩展到多个 Follower 副本，情况可能更糟，也许需要多轮拉取请求。也就是说，Leader 副本高水位更新和 Follower 副本高水位更新在时间上是存在错配的。这种错配是很多“数据丢失”或“数据不一致”问题的根源。基于此，社区在 0.11 版本正式引入了 Leader Epoch 概念，来规避因高水位更新错配导致的各种不一致问题。
    所谓 Leader Epoch，我们大致可以认为是 Leader 版本。它由两部分数据组成。
        Epoch。一个单调增加的版本号。每当副本领导权发生变更时，都会增加该版本号。小版本号的 Leader 被认为是过期 Leader，不能再行使 Leader 权力。
        起始位移（Start Offset）。Leader 副本在该 Epoch 值上写入的首条消息的位移。
小结
    高水位在界定 Kafka 消息对外可见性以及实现副本机制等方面起到了非常重要的作用，但其设计上的缺陷给 Kafka 留下了很多数据丢失或数据不一致的潜在风险。
    为此，社区引入了 Leader Epoch 机制，尝试规避掉这类风险。事实证明，它的效果不错，在 0.11 版本之后，关于副本数据不一致性方面的 Bug 的确减少了很多。

28 | 主题管理知多少?
主题日常管理
    Kafka 提供了自带的 kafka-topics 脚本，用于帮助用户创建主题。
        bin/kafka-topics.sh --bootstrap-server broker_host:port --create --topic my_topic_name  --partitions 1 --replication-factor 1
    社区推荐使用 --bootstrap-server 而非 --zookeeper 的原因主要有两个
        使用 --zookeeper 会绕过 Kafka 的安全体系。
        使用 --bootstrap-server 与集群进行交互，越来越成为使用 Kafka 的标准姿势。
    查询所有主题的列表
        bin/kafka-topics.sh --bootstrap-server broker_host:port --list
    如果要查询单个主题的详细数据
        bin/kafka-topics.sh --bootstrap-server broker_host:port --describe --topic <topic_name>
    1. 修改主题分区。
       其实就是增加分区，目前 Kafka 不允许减少某个主题的分区数。
        bin/kafka-topics.sh --bootstrap-server broker_host:port --alter --topic <topic_name> --partitions < 新分区数 >
    2. 修改主题级别参数。
        bin/kafka-configs.sh --zookeeper zookeeper_host:port --entity-type topics --entity-name <topic_name> --alter --add-config max.message.bytes=10485760
    3. 变更副本数。
        使用自带的 kafka-reassign-partitions 脚本，帮助我们增加主题的副本数。
    4. 修改主题限速。
       先设置 Broker 端参数 leader.replication.throttled.rate 和 follower.replication.throttled.rate，命令如下：
       bin/kafka-configs.sh --zookeeper zookeeper_host:port --alter --add-config 'leader.replication.throttled.rate=104857600,follower.replication.throttled.rate=104857600' --entity-type brokers --entity-name 0
       这条命令结尾处的 --entity-name 就是 Broker ID。倘若该主题的副本分别在 0、1、2、3 多个 Broker 上，那么你还要依次为 Broker 1、2、3 执行这条命令。
       设置好这个参数之后，我们还需要为该主题设置要限速的副本。在这个例子中，我们想要为所有副本都设置限速，因此统一使用通配符 * 来表示，命令如下：
       bin/kafka-configs.sh --zookeeper zookeeper_host:port --alter --add-config 'leader.replication.throttled.replicas=*,follower.replication.throttled.replicas=*' --entity-type topics --entity-name test
    5. 主题删除。
       bin/kafka-topics.sh --bootstrap-server broker_host:port --delete  --topic <topic_name>
       删除主题的命令并不复杂，关键是删除操作是异步的，执行完这条命令不代表主题立即就被删除了。它仅仅是被标记成“已删除”状态而已。Kafka 会在后台默默地开启主题删除操作。因此，通常情况下，你都需要耐心地等待一段时间。
特殊主题管理与运维
    Kafka内部主题 __consumer_offsets 和 __transaction_state。
    这两个内部主题默认都有 50 个分区，因此，分区子目录会非常得多。
    建议是不要手动创建或修改它们，还是让 Kafka 自动帮我们创建好了
    示例操作步骤
常见主题错误处理
    常见错误 1：主题删除失败。
        实际上，造成主题删除失败的原因有很多，最常见的原因有两个：副本所在的 Broker 宕机了；待删除主题的部分分区依然在执行迁移过程。
        如果是因为前者，通常你重启对应的 Broker 之后，删除操作就能自动恢复；如果是因为后者，那就麻烦了，很可能两个操作会相互干扰。
        不管什么原因，一旦你碰到主题无法删除的问题，可以采用这样的方法：
            第 1 步，手动删除 ZooKeeper 节点 /admin/delete_topics 下以待删除主题为名的 znode。
            第 2 步，手动删除该主题在磁盘上的分区目录。
            第 3 步，在 ZooKeeper 中执行 rmr /controller，触发 Controller 重选举，刷新 Controller 缓存。
            在执行最后一步时，你一定要谨慎，因为它可能造成大面积的分区 Leader 重选举。事实上，仅仅执行前两步也是可以的，只是 Controller 缓存中没有清空待删除主题罢了，也不影响使用。
常见错误 2：__consumer_offsets 占用太多的磁盘。
    一旦你发现这个主题消耗了过多的磁盘空间，那么，你一定要显式地用jstack 命令查看一下 kafka-log-cleaner-thread 前缀的线程状态。
    通常情况下，这都是因为该线程挂掉了，无法及时清理此内部主题。倘若真是这个原因导致的，那我们就只能重启相应的 Broker 了。
    另外，请你注意保留出错日志，因为这通常都是 Bug 导致的，最好提交到社区看一下。

29 | Kafka动态配置了解下？
什么是动态 Broker 参数配置？
    静态参数（Static Configs），config 路径下，有个 server.properties 文件。
    Kafka 官网Broker Configs表中增加了 Dynamic Update Mode 列。该列有 3 类值，分别是 read-only、per-broker 和 cluster-wide。
        https://kafka.apache.org/documentation/#brokerconfigs
        read-only。被标记为 read-only 的参数和原来的参数行为一样，只有重启 Broker，才能令修改生效。
        per-broker。被标记为 per-broker 的参数属于动态参数，修改它之后，只会在对应的 Broker 上生效。
        cluster-wide。被标记为 cluster-wide 的参数也属于动态参数，修改它之后，会在整个集群范围内生效，也就是说，对所有 Broker 都生效。你也可以为具体的 Broker 修改 cluster-wide 参数。
使用场景
    动态 Broker 参数的使用场景非常广泛，通常包括但不限于以下几种：
        动态调整 Broker 端各种线程池大小，实时应对突发流量。
        动态调整 Broker 端连接信息或安全配置信息。
        动态更新 SSL Keystore 有效期。
        动态调整 Broker 端 Compact 操作性能。
        实时变更 JMX 指标收集器 (JMX Metrics Reporter)。
如何保存？
Kafka 是如何保存动态配置
Kafka 将动态 Broker 参数保存在 ZooKeeper 中
    /config下
        changes 是用来实时监测动态参数变更的，不会保存参数值；topics 是用来保存 Kafka 主题级别参数的。虽然它们不属于动态 Broker 端参数，但其实它们也是能够动态变更的。
        users 和 clients 则是用于动态调整客户端配额（Quota）的 znode 节点。所谓配额，是指 Kafka 运维人员限制连入集群的客户端的吞吐量或者是限定它们使用的 CPU 资源。
    /config/brokers znode 才是真正保存动态 Broker 参数的地方
        该 znode 下有两大类子节点。第一类子节点就只有一个，它有个固定的名字叫 < default >，保存的是cluster-wide 范围的动态参数；
        另一类则以 broker.id 为名，保存的是特定 Broker 的 per-broker 范围参数。由于是 per-broker 范围，因此这类子节点可能存在多个。
    ephemeralOwner 字段，你会发现它们的值都是 0x0。这表示这些 znode 都是持久化节点，它们将一直存在。即使 ZooKeeper 集群重启，这些数据也不会丢失，这样就能保证这些动态参数的值会一直生效。
如何配置？
    设置动态参数的工具行命令只有一个，那就是 Kafka 自带的 kafka-configs 脚本。
        如何设置 cluster-wide 范围参数
            $ bin/kafka-configs.sh --bootstrap-server kafka-host:port --entity-type brokers --entity-default --alter --add-config unclean.leader.election.enable=true
                如果要设置 cluster-wide 范围的动态参数，需要显式指定 entity-default。
            使用下面的命令来查看一下刚才的配置是否成功
                $ bin/kafka-configs.sh --bootstrap-server kafka-host:port --entity-type brokers --entity-default --describe
        如何设置 per-broker 范围参数
            $ bin/kafka-configs.sh --bootstrap-server kafka-host:port --entity-type brokers --entity-name 1 --alter --add-config unclean.leader.election.enable=false
            Completed updating config for broker: 1.
        $ bin/kafka-configs.sh --bootstrap-server kafka-host:port --entity-type brokers --entity-name 1 --describe
        删除参数
            删除 cluster-wide 范围参数
            $ bin/kafka-configs.sh --bootstrap-server kafka-host:port --entity-type brokers --entity-default --alter --delete-config unclean.leader.election.enable
            Completed updating default config for brokers in the cluster,
            删除 per-broker 范围参数
            $ bin/kafka-configs.sh --bootstrap-server kafka-host:port --entity-type brokers --entity-name 1 --alter --delete-config unclean.leader.election.enable
            Completed updating config for broker: 1.
    如果你想要知道动态 Broker 参数都有哪些，一种方式是在 Kafka 官网中查看 Broker 端参数列表，另一种方式是直接运行无参数的 kafka-configs 脚本，该脚本的说明文档会告诉你当前动态 Broker 参数都有哪些。
有较大几率被动态调整值的参数
    1.log.retention.ms。
        修改日志留存时间应该算是一个比较高频的操作，毕竟，我们不可能完美地预估所有业务的消息留存时长。虽然该参数有对应的主题级别参数可以设置，但拥有在全局层面上动态变更的能力，依然是一个很好的功能亮点。
    2.num.io.threads 和 num.network.threads。
        两组线程池。这是动态 Broker 参数最实用的场景了。毕竟，在实际生产环境中，Broker 端请求处理能力经常要按需扩容。如果没有动态 Broker 参数，我们是无法做到这一点的。
    3. 与 SSL 相关的参数。
       主要是 4 个参数（ssl.keystore.type、ssl.keystore.location、ssl.keystore.password 和 ssl.key.password）。允许动态实时调整它们之后，我们就能创建那些过期时间很短的 SSL 证书。每当我们调整时，Kafka 底层会重新配置 Socket 连接通道并更新 Keystore。新的连接会使用新的 Keystore，阶段性地调整这组参数，有利于增加安全性。
    4.num.replica.fetchers。
       最实用的动态 Broker 参数之一。Follower 副本拉取速度慢，在线上 Kafka 环境中一直是一个老大难的问题。针对这个问题，常见的做法是增加该参数值，确保有充足的线程可以执行 Follower 副本向 Leader 副本的拉取。现在有了动态参数，你不需要再重启 Broker，就能立即在 Follower 端生效

30 | 怎么重设消费者组位移？
为什么要重设消费者组位移？
    Kafka，由于它是基于日志结构（log-based）的消息引擎，消费者在消费消息时，仅仅是从磁盘文件上读取数据而已，是只读的操作，因此消费者不会删除消息数据。同时，由于位移数据是由消费者控制的，因此它能够很容易地修改位移的值，实现重复消费历史数据的功能。
重设位移策略
    重设位移大致可以从两个维度来进行。
        位移维度。这是指根据位移值来重设。也就是说，直接把消费者的位移值重设成我们给定的位移值。
        时间维度。我们可以给定一个时间，让消费者把位移调整成大于该时间的最小位移；也可以给出一段时间间隔，比如 30 分钟前，然后让消费者直接将位移调回 30 分钟之前的位移值。
    7 种重设策略
        Earliest 策略表示将位移调整到主题当前最早位移处。
            这个最早位移不一定就是 0，因为在生产环境中，很久远的消息会被 Kafka 自动删除，所以当前最早位移很可能是一个大于 0 的值。如果你想要重新消费主题的所有消息，那么可以使用 Earliest 策略。
        Latest 策略表示把位移重设成最新末端位移。
            如果你总共向某个主题发送了 15 条消息，那么最新末端位移就是 15。如果你想跳过所有历史消息，打算从最新的消息处开始消费的话，可以使用 Latest 策略。
        Current 策略表示将位移调整成消费者当前提交的最新位移。
            有时候你可能会碰到这样的场景：你修改了消费者程序代码，并重启了消费者，结果发现代码有问题，你需要回滚之前的代码变更，同时也要把位移重设到消费者重启时的位置，那么，Current 策略就可以帮你实现这个功能。
        Specified-Offset 策略则是比较通用的策略，表示消费者把位移值调整到你指定的位移处。
            这个策略的典型使用场景是，消费者程序在处理某条错误消息时，你可以手动地“跳过”此消息的处理。
            在实际使用过程中，可能会出现 corrupted 消息无法被消费的情形，此时消费者程序会抛出异常，无法继续工作。一旦碰到这个问题，你就可以尝试使用 Specified-Offset 策略来规避。
        如果说 Specified-Offset 策略要求你指定位移的绝对数值的话，那么 Shift-By-N 策略指定的就是位移的相对数值，即你给出要跳过的一段消息的距离即可。
            这里的“跳”是双向的，你既可以向前“跳”，也可以向后“跳”。比如，你想把位移重设成当前位移的前 100 条位移处，此时你需要指定 N 为 -100。
        DateTime 允许你指定一个时间，然后将位移重置到该时间之后的最早位移处。常见的使用场景是，你想重新消费昨天的数据，那么你可以使用该策略重设位移到昨天 0 点。
        Duration 策略则是指给定相对的时间间隔，然后将位移调整到距离当前给定时间间隔的位移处，具体格式是 PnDTnHnMnS。如果你熟悉 Java 8 引入的 Duration 类的话，你应该不会对这个格式感到陌生。它就是一个符合 ISO-8601 规范的 Duration 格式，以字母 P 开头，后面由 4 部分组成，即 D、H、M 和 S，分别表示天、小时、分钟和秒。举个例子，如果你想将位移调回到 15 分钟前，那么你就可以指定 PT0H15M0S。
重设消费者组位移的方式有两种
    通过消费者 API 来实现。
    通过 kafka-consumer-groups 命令行脚本来实现。
消费者 API 方式设置
    各个场景的代码示例。
    总之，使用 Java API 的方式来实现重设策略的主要入口方法，就是 seek 方法。
命令行方式设置
    通过 kafka-consumer-groups 脚本
        Earliest 策略直接指定–to-earliest。
            bin/kafka-consumer-groups.sh --bootstrap-server kafka-host:port --group test-group --reset-offsets --all-topics --to-earliest –execute
        Latest 策略直接指定–to-latest。
            bin/kafka-consumer-groups.sh --bootstrap-server kafka-host:port --group test-group --reset-offsets --all-topics --to-latest --execute
        Current 策略直接指定–to-current。
            bin/kafka-consumer-groups.sh --bootstrap-server kafka-host:port --group test-group --reset-offsets --all-topics --to-current --execute
        Specified-Offset 策略直接指定–to-offset。
            bin/kafka-consumer-groups.sh --bootstrap-server kafka-host:port --group test-group --reset-offsets --all-topics --to-offset <offset> --execute
        Shift-By-N 策略直接指定–shift-by N。
            bin/kafka-consumer-groups.sh --bootstrap-server kafka-host:port --group test-group --reset-offsets --shift-by <offset_N> --execute
        DateTime 策略直接指定–to-datetime。
            bin/kafka-consumer-groups.sh --bootstrap-server kafka-host:port --group test-group --reset-offsets --to-datetime 2019-06-20T20:00:00.000 --execute
        最后是实现 Duration 策略，我们直接指定–by-duration。
            bin/kafka-consumer-groups.sh --bootstrap-server kafka-host:port --group test-group --reset-offsets --by-duration PT0H30M0S --execute
小结
    重设位移主要是为了实现消息的重演。目前 Kafka 支持 7 种重设策略和 2 种重设方法。在实际使用过程中，我推荐你使用第 2 种方法，即用命令行的方式来重设位移。毕竟，执行命令要比写程序容易得多。但是需要注意的是，0.11 及 0.11 版本之后的 Kafka 才提供了用命令行调整位移的方法。如果你使用的是之前的版本，那么就只能依靠 API 的方式了。

31 | 常见工具脚本大汇总
    connect-standalone 和 connect-distributed 两个脚本。这两个脚本是 Kafka Connect 组件的启动脚本。
    kafka-acls 脚本。它是用于设置 Kafka 权限的，比如设置哪些用户可以访问 Kafka 的哪些主题之类的权限。
    kafka-broker-api-versions 脚本。这个脚本的主要目的是验证不同 Kafka 版本之间服务器和客户端的适配性。
    kafka-configs 脚本
    重量级的工具行脚本：kafka-console-consumer 和 kafka-console-producer。
    kafka-producer-perf-test 和 kafka-consumer-perf-test。它们分别是生产者和消费者的性能测试工具，非常实用
    kafka-consumer-groups
    kafka-delegation-tokens 脚本可能不太为人所知，它是管理 Delegation Token 的。基于 Delegation Token 的认证是一种轻量级的认证机制，补充了现有的 SASL 认证机制。
    kafka-delete-records 脚本用于删除 Kafka 的分区消息。鉴于 Kafka 本身有自己的自动消息删除策略，这个脚本的实际出场率并不高。
    kafka-dump-log 脚本可谓是非常实用的脚本。它能查看 Kafka 消息文件的内容，包括消息的各种元数据信息，甚至是消息体本身。
    kafka-log-dirs 脚本是比较新的脚本，可以帮助查询各个 Broker 上的各个日志路径的磁盘占用情况。
    kafka-mirror-maker 脚本是帮助你实现 Kafka 集群间的消息同步的。
    kafka-preferred-replica-election 脚本是执行 Preferred Leader 选举的。它可以为指定的主题执行“换 Leader”的操作。
    kafka-reassign-partitions 脚本用于执行分区副本迁移以及副本文件路径迁移。
    kafka-topics 所有的主题管理操作，都是由该脚本来实现的。
    kafka-run-class 脚本则颇为神秘，你可以用这个脚本执行任何带 main 方法的 Kafka 类。在实际工作中，你几乎遇不上要直接使用这个脚本的场景了。
    kafka-server-start 和 kafka-server-stop 脚本，是用于启动和停止 Kafka Broker 进程的。
    kafka-streams-application-reset 脚本用来给 Kafka Streams 应用程序重设位移，以便重新消费数据。如果你没有用到 Kafka Streams 组件，这个脚本对你来说是没有用的。
    kafka-verifiable-producer 和 kafka-verifiable-consumer 脚本是用来测试生产者和消费者功能的。它们是很“古老”的脚本了，你几乎用不到它们。另外，前面提到的 Console Producer 和 Console Consumer 完全可以替代它们。
    zookeeper 开头的脚本是用来管理和运维 ZooKeeper 的
    trogdor 脚本。这是个很神秘的家伙，官网上也不曾出现它的名字。据社区内部资料显示，它是 Kafka 的测试框架，用于执行各种基准测试和负载测试。一般的 Kafka 用户应该用不到这个脚本。
重点脚本操作
    生产消息
        $ bin/kafka-console-producer.sh --broker-list kafka-host:port --topic test-topic --request-required-acks -1 --producer-property compression.type=lz4
    消费消息
        $ bin/kafka-console-consumer.sh --bootstrap-server kafka-host:port --topic test-topic --group test-group --from-beginning --consumer-property enable.auto.commit=false
    测试生产者性能
        $ bin/kafka-producer-perf-test.sh --topic test-topic --num-records 10000000 --throughput -1 --record-size 1024 --producer-props bootstrap.servers=kafka-host:port acks=-1 linger.ms=2000 compression.type=lz4
    测试消费者性能
        $ bin/kafka-consumer-perf-test.sh --broker-list kafka-host:port --messages 10000000 --topic test-topic
    查看主题消息总数
        $ bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list kafka-host:port --time -2 --topic test-topic
    查看消息文件数据
    $ bin/kafka-dump-log.sh --files ../data_dir/kafka_1/test-topic-1/00000000000000000000.log
        如果我们想深入看一下每条具体的消息，那么就需要显式指定 --deep-iteration 参数
            $ bin/kafka-dump-log.sh --files ../data_dir/kafka_1/test-topic-1/00000000000000000000.log --deep-iteration
        还想看消息里面的实际数据，那么还需要指定 --print-data-log 参数
            $ bin/kafka-dump-log.sh --files ../data_dir/kafka_1/test-topic-1/00000000000000000000.log --deep-iteration --print-data-log
    查询消费者组位移

32 | KafkaAdminClient：Kafka的运维利器
引入原因
如何使用？
功能
    9 大类
        主题管理：包括主题的创建、删除和查询。
        权限管理：包括具体权限的配置与删除。
        配置参数管理：包括 Kafka 各种资源的参数设置、详情查询。所谓的 Kafka 资源，主要有 Broker、主题、用户、Client-id 等。
        副本日志管理：包括副本底层日志路径的变更和详情查询。
        分区管理：即创建额外的主题分区。
        消息删除：即删除指定位移之前的分区消息。
        Delegation Token 管理：包括 Delegation Token 的创建、更新、过期和详情查询。
        消费者组管理：包括消费者组的查询、位移查询和删除。
        Preferred 领导者选举：推选指定主题分区的 Preferred Broker 为领导者。
工作原理
    从设计上来看，AdminClient 是一个双线程的设计：前端主线程和后端 I/O 线程。
    AdminClient 在内部大量使用生产者 - 消费者模式将请求生成与处理解耦。
构造和销毁 AdminClient 实例
常见的 AdminClient 应用实例
    创建主题
    查询消费者组位移
    获取 Broker 磁盘占用

33 | Kafka认证机制用哪家？
什么是认证机制？
    认证要解决的是你要证明你是谁的问题，授权要解决的则是你能做什么的问题。
Kafka 认证机制
    截止到2.3 版本，Kafka 支持基于 SSL 和基于 SASL 的安全认证机制。
认证机制的比较
    建议是你可以使用 SSL 来做通信加密，使用 SASL 来做 Kafka 的认证实现。
SASL/SCRAM-SHA-256 配置实例
    见文稿

34 | 云环境下的授权该怎么做？
什么是授权机制？
具体到权限模型，常见的有四种。
    ACL：Access-Control List，访问控制列表。
    RBAC：Role-Based Access Control，基于角色的权限控制。
    ABAC：Attribute-Based Access Control，基于属性的权限控制。
    PBAC：Policy-Based Access Control，基于策略的权限控制。
如何开启 ACL？
    server.properties 文件中配置下面这个参数值：
    authorizer.class.name=kafka.security.auth.SimpleAclAuthorizer
超级用户（Super User）
    server.properties 中，设置 super.users 参数即可，比如：
    super.users=User:superuser1;User:superuser2
    注意，如果你要一次性指定多个超级用户，那么分隔符是分号而不是逗号，这是为了避免出现用户名中包含逗号从而无法分割的问题。
kafka-acls 脚本
    举个例子，如果我们要为用户 Alice 增加了集群级别的所有权限，那么我们可以使用下面这段命令。
    $ kafka-acls --authorizer-properties zookeeper.connect=localhost:2181 --add --allow-principal User:Alice --operation All --topic '*' --cluster
ACL 权限列表
    见文稿
授权机制能否单独使用？
    可以
配置实例
小结

35 | 跨集群备份解决方案MirrorMaker
通常我们把数据在单个集群下不同节点之间的拷贝称为备份，而把数据在集群间的拷贝称为镜像（Mirroring）。
什么是 MirrorMaker？
运行 MirrorMaker
MirrorMaker 配置实例
其他跨集群镜像方案
    1.Uber 的 uReplicator 工具
    2.LinkedIn 开发的 Brooklin Mirror Maker 工具
    3.Confluent 公司研发的 Replicator 工具

36 | 你应该怎么监控Kafka？
主机监控
    所谓主机监控，指的是监控 Kafka 集群 Broker 所在的节点机器的性能。
    常见的主机监控指标包括但不限于以下几种：
        机器负载（Load）
        CPU 使用率
        内存使用率，包括空闲内存（Free Memory）和已使用内存（Used Memory）
        磁盘 I/O 使用率，包括读使用率和写使用率
        网络 I/O 使用率
        TCP 连接数
        打开文件数
        inode 使用情况
JVM 监控
要做到 JVM 进程监控，有 3 个指标需要你时刻关注：
    Full GC 发生频率和时长。这个指标帮助你评估 Full GC 对 Broker 进程的影响。长时间的停顿会令 Broker 端抛出各种超时异常。
    活跃对象大小。这个指标是你设定堆大小的重要依据，同时它还能帮助你细粒度地调优 JVM 各个代的堆大小。
    应用线程总数。这个指标帮助你了解 Broker 进程对 CPU 的使用情况。
集群监控
    1. 查看 Broker 进程是否启动，端口是否建立。
    2. 查看 Broker 端关键日志。
    3. 查看 Broker 端关键线程的运行状态。
    4. 查看 Broker 端的关键 JMX 指标。
    5. 监控 Kafka 客户端。

37 | 主流的Kafka监控框架
JMXTool 工具
Kafka Manager
Burrow
JMXTrans + InfluxDB + Grafana
Confluent Control Center
其他
Kafka Monitor、Kafka Offset Monitor、Kafka Eagle

38 | 调优Kafka，你做到了吗？
调优目标
    对 Kafka 而言，性能一般是指吞吐量和延时。
优化漏斗
    第 1 层：应用程序层。
        它是指优化 Kafka 客户端应用程序代码。比如，使用合理的数据结构、缓存计算开销大的运算结果，抑或是复用构造成本高的对象实例等。这一层的优化效果最为明显，通常也是比较简单的。
    第 2 层：框架层。
        它指的是合理设置 Kafka 集群的各种参数。毕竟，直接修改 Kafka 源码进行调优并不容易，但根据实际场景恰当地配置关键参数的值，还是很容易实现的。
    第 3 层：JVM 层。
        Kafka Broker 进程是普通的 JVM 进程，各种对 JVM 的优化在这里也是适用的。优化这一层的效果虽然比不上前两层，但有时也能带来巨大的改善效果。
    第 4 层：操作系统层。
        对操作系统层的优化很重要，但效果往往不如想象得那么好。与应用程序层的优化效果相比，它是有很大差距的。
基础性调优
    操作系统调优
    JVM 层调优
    1. 设置堆大小。
        一个朴素的答案：将你的 JVM 堆大小设置成 6～8GB。
    2.GC 收集器的选择。
        建议你使用 G1 收集器，主要原因是方便省事，至少比 CMS 收集器的优化难度小得多。
    Broker 端调优
    尽力保持客户端版本和 Broker 端版本一致。
应用层调优
    不要频繁地创建 Producer 和 Consumer 对象实例。
    用完及时关闭。
    合理利用多线程来改善性能。
性能指标调优
    调优吞吐量
        调优参数列表
    调优延时
        优延时的参数列表

39 | 从0搭建基于Kafka的企业级实时日志流处理平台
40 | Kafka Streams与其他流处理平台的差异在哪里？
41 | Kafka Streams DSL开发实例
42 | Kafka Streams在金融领域的应用
加餐 | 搭建开发环境、阅读源码方法、经典学习资料大揭秘
结束语 | 以梦为马，莫负韶华！























