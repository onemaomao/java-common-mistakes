01 | 日志段：保存消息文件的对象是怎么实现的？
Kafka日志结构概览
    Log Segment
        .log 消息日志文件
        .index 位移索引文件
        .timeindex 时间戳索引文件
        .txnindex 已终止事务索引文件
日志段代码解析
    kafka.log.LogSegment
        LogSegment class
        LogSegment object
        LogSegment object
日志段类声名
    baseOffset-磁盘上看到的文件名就是它的值
    lazyOffsetIndex-位移索引文件
    lazyTimeIndex-时间戳索引文件
    txnIndex-已终止事务索引文件
    indexIntervalBytes-Broker端的参数log.index.interval.bytes。
        它控制了日志段新增索引项的频率。
        默认情况下，日志至少写入4KB的消息才会新增一条索引项。
    rollJitterMs-Broker端的参数log.roll.jitter.ms。
        日志段对象新增倒计时的"扰动值"。（缓解全局设置情况下同时创建多个日志段的磁盘IO压力）

append方法
    结合图例的5步阅读源码
read方法
    结合图例的3步阅读源码
recover方法
    用于恢复日志段。
    Broker在启动时会从磁盘上加载所有的日志段信息到内存中，并创建响应的LogSegment对象实例。
    结合图例的3步阅读源码，其中第二步又分了5小步。
总结
    这三个方法是日志段对象最重要的功能。一定要仔细阅读，尽量做到对源码中每行代码的作用都了然于心。

02 | 日志（上）：日志究竟是如何加载日志段的？
Log源码结构 Log.scala:10个类和对象
Log(C)
    两个最重要属性
        dir:这个日志所在文件夹路径，也就是主体分区的路径
        logStartOffset:日志的当前最早位移
    其他重要属性
        nextOffsetMetadata:基本等同LEO
        highWatermarkMetadata:分区日志高水位置
        segments:Log类中最重要的属性。保存了分区日志下所有的日志段信息，只不过是用Map的数据结构来保存的。
        leaderEpochCache
Log类的初始化逻辑
    locally{
        图示5大步骤，其中第2步又分成两步
    }
    重点分析loadSegments的部分操作
        removeTempFilesAndCollectSwapFiles
        loadSegmentFiles
        completeSwapOperations
        recoverLog会调用recoverSegment

03 | 日志（下）：彻底搞懂Log对象的常见操作
Log的常见操作分为4大部分
高水位管理操作:
日志段管理:
关键位移值管理:
读写操作:


