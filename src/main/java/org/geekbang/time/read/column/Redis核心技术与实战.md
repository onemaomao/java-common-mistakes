02-数据结构：快速的Redis有哪些慢操作？
Redis数据类型和底层数据结构的对应关系
    String: 简单动态字符串
    List: 双向链表+压缩列表
    Hash: 压缩列表+哈希表
    Sorted Set: 压缩列表+跳表  (源码zset可以看到底层还用了字典和跳表,参见redis设计与实现的第八章的讲解)
    Set: 哈希表+整数数组
键和值用什么结构组织？
    哈希表
为什么哈希表操作变慢了？
    哈希冲突
    rehash
    渐进式rehash
集合数据操作效率
有哪些底层数据结构？
    集合类型的底层数据结构主要有5种：整数数组、双向链表、哈希表、压缩列表和跳表
    压缩列表有点像数组,要查找定位第一个元素和最后一个元素，可以通过表头三个字段的长度直接定位，复杂度是O(1)。而查找其他元素时，就没有这么高效了，只能逐个查找，此时的复杂度就是O(N)了。
    哈希表O(1)
    跳表O(logn)
    双向链表,压缩列表,整数数组时间复杂度都是O(n)
不同操作的复杂度
    单元素操作是基础；
        每一种集合类型对单个数据实现的增删改查操作。复杂度都是O(1)。
        增加M个元素时，复杂度就从O(1)变成O(M)了。
    范围操作非常耗时；
        尽量避免。
    统计操作通常高效；
        集合类型对集合中所有元素个数的记录。集合中结构中专门记录了元素的个数统计，因此可以高效地完成相关操作
    例外情况只有几个。
        是指某些数据结构的特殊记录，例如压缩列表和双向链表都会记录表头和表尾的偏移量。这样一来，对于List类型的LPOP、RPOP、LPUSH、RPUSH这四个操作来说，它们是在列表的头尾增删元素，这就可以通过偏移量直接定位，所以它们的复杂度也只有O(1)，可以实现快速操作。
小结
集合类型的范围操作，因为要遍历底层数据结构，复杂度通常是O(N)。这里，我的建议是：用其他命令来替代，例如可以用SCAN来代替，避免在Redis内部产生费时的全集合遍历操作。
因地制宜地使用List类型。例如，既然它的POP/PUSH效率很高，那么就将它主要用于FIFO队列场景，而不是作为一个可以随机读写的集合。

03-高性能IO模型：为什么单线程Redis能那么快？
Redis的网络IO和键值对读写是由一个线程来完成的，这也是Redis对外提供键值存储服务的主要流程。但Redis的其他功能，比如持久化、异步删除、集群数据同步等，其实是由额外的线程执行的。

Redis为什么用单线程？
多线程的开销
多线程编程模式面临的共享资源的并发访问控制问题。

单线程Redis为什么那么快？
    一方面，Redis的大部分操作在内存上完成，再加上它采用了高效的数据结构，例如哈希表和跳表，这是它实现高性能的一个重要原因。另一方面，就是Redis采用了多路复用机制，使其在网络IO操作中能并发处理大量的客户端请求，实现高吞吐率。
    基本IO模型与阻塞点
    非阻塞模式
    基于多路复用的高性能I/O模型
        图非常重要
        Redis线程不会阻塞在某一个特定的监听或已连接套接字上，也就是说，不会阻塞在某一个特定的客户端请求处理上。正因为此，Redis可以同时和多个客户端连接并处理请求，从而提升并发性。
        select/epoll提供了基于事件的回调机制，即针对不同事件的发生，调用相应的处理函数。
        kafka在这里的应用有点相似
小结

04-AOF日志：宕机了，Redis如何避免数据丢失？
AOF日志是如何实现的？
    WAL
    AOF日志的内容
    先让系统执行命令，只有命令能执行成功，才会被记录到日志中,避免出现记录错误命令的情况
    不会阻塞当前的写操作
两个潜在的风险
    丢数据
    可能会给下一个操作带来阻塞风险
三种写回策略
    Always，同步写回：每个写命令执行完，立马同步地将日志写回磁盘；
    Everysec，每秒写回：每个写命令执行完，只是先把日志写到AOF文件的内存缓冲区，每隔一秒把缓冲区中的内容写入磁盘；
    No，操作系统控制的写回：每个写命令执行完，只是先把日志写到AOF文件的内存缓冲区，由操作系统决定何时将缓冲区内容写回磁盘。
    这三种写回策略都无法做到两全其美
        各自优缺点分析
        具体选择场景
日志文件太大了怎么办？
    AOF重写机制:记录最新的命令
AOF重写会阻塞吗?
    和AOF日志由主线程写回不同，重写过程是由后台线程bgrewriteaof来完成的，这也是为了避免阻塞主线程，导致数据库性能下降。
    一个拷贝，两处日志
    “一个拷贝”就是指，每次执行重写时，主线程fork出后台的bgrewriteaof子进程。
    因为主线程未阻塞，仍然可以处理新来的操作。此时，如果有写操作，第一处日志就是指正在使用的AOF日志，Redis会把这个操作写到它的缓冲区。这样一来，即使宕机了，这个AOF日志的操作仍然是齐全的，可以用于恢复。
    而第二处日志，就是指新的AOF重写日志。这个操作也会被写到重写日志的缓冲区。这样，重写日志也不会丢失最新的操作。等到拷贝数据的所有操作记录重写完成后，重写日志记录的这些最新操作也会写入新的AOF文件，以保证数据库最新状态的记录。此时，我们就可以用新的AOF文件替代旧文件了。
    总结来说，每次AOF重写时，Redis会先执行一个内存拷贝，用于重写；然后，使用两个日志保证在重写过程中，新写入的数据不会丢失。而且，因为Redis采用额外的线程进行数据重写，所以，这个过程并不会阻塞主线程。
小结

05-内存快照：宕机后，Redis如何实现快速恢复？
给哪些内存数据做快照？
    Redis提供了两个命令来生成RDB文件，分别是save和bgsave。
    save：在主线程中执行，会导致阻塞；
    bgsave：创建一个子进程，专门用于写入RDB文件，避免了主线程的阻塞，这也是Redis RDB文件生成的默认配置。
快照时数据能修改吗?
    Redis就会借助操作系统提供的写时复制技术（Copy-On-Write, COW），在执行快照的同时，正常处理写操作。
    参见流程图
可以每秒做一次快照吗？
    如果频繁地执行全量快照，也会带来两方面的开销。
        一方面，频繁将全量数据写入磁盘，会给磁盘带来很大压力
        另一方面，bgsave子进程需要通过fork操作从主线程创建出来
    解决方式:增量快照
        Redis 4.0中提出了一个混合使用AOF日志和内存快照的方法。简单来说，内存快照以一定的频率执行，在两次快照之间，使用AOF日志记录这期间的所有命令操作。
小结
    数据不能丢失时，内存快照和AOF的混合使用是一个很好的选择；
    如果允许分钟级别的数据丢失，可以只使用RDB；
    如果只用AOF，优先使用everysec的配置选项，因为它在可靠性和性能之间取了一个平衡。

06-数据同步：主从库如何实现数据一致？
主从库间如何进行第一次同步？
    第一次同步的三个阶段(结合文稿图)
        第一阶段是主从库间建立连接、协商同步的过程，主要是为全量复制做准备。
            从->主    
                psync
                runID=?
                offset=-1
            主->从
                FULLRESYNC响应
        在第二阶段，主库将所有数据同步给从库。从库收到数据后，在本地完成数据加载。
            主库执行bgsave命令，生成RDB文件，接着将文件发给从库。
            从库接收到RDB文件后，会先清空当前数据库，然后加载RDB文件。
            从库接收到RDB文件后，会先清空当前数据库，然后加载RDB文件。为了保证主从库的数据一致性，主库会在内存中用专门的replication buffer，记录RDB文件生成后收到的所有写操作。
        第三个阶段，主库会把第二阶段执行过程中新收到的写命令，再发送给从库。
主从级联模式分担全量复制时的主库压力
    “主-从-从”模式
主从库间网络断了怎么办？
    在Redis 2.8之前,从库就会和主库重新进行一次全量复制，开销非常大。
    从Redis 2.8开始，网络断了之后，主从库会采用增量复制的方式继续同步。
        当主从库断连后，主库会把断连期间收到的写操作命令，写入replication buffer，同时也会把这些操作命令也写入repl_backlog_buffer这个缓冲区。
        repl_backlog_buffer是一个环形缓冲区，主库会记录自己写到的位置，从库则会记录自己已经读到的位置。
        repl_backlog_buffer是一个环形缓冲区，主库会记录自己写到的位置，从库则会记录自己已经读到的位置。
        结合文稿的图示
        因为repl_backlog_buffer是一个环形缓冲区，所以在缓冲区写满后，主库会继续写入，此时，就会覆盖掉之前写入的操作。如果从库的读取速度比较慢，就有可能导致从库还未读取的操作被主库新写的操作覆盖了，这会导致主从库间的数据不一致。
    要想办法避免这一情况，一般而言，我们可以调整repl_backlog_size这个参数。
    公式
小结
    Redis的主从库同步的基本原理，总结来说，有三种模式：全量复制、基于长连接的命令传播，以及增量复制。

07-哨兵机制：主库挂了，如何不间断服务？
哨兵机制的基本流程
    哨兵主要负责的就是三个任务：监控、选主（选择主库）和通知。
主观下线和客观下线
    哨兵集群
    简单来说，“客观下线”的标准就是，当有N个哨兵实例时，最好要有N/2 + 1个实例判断主库为“主观下线”，才能最终判定主库为“客观下线”。
如何选定新主库？
    把哨兵选择新主库的过程称为“筛选+打分”。
    在多个从库中，先按照一定的筛选条件，把不符合条件的从库去掉。然后，我们再按照一定的规则，给剩下的从库逐个打分，将得分最高的从库选为新主库。
        检查从库的当前在线状态，还要判断它之前的网络连接状态。
        按照三个规则依次进行三轮打分，这三个规则分别是从库优先级、从库复制进度以及从库ID号。
小结

08-哨兵集群：哨兵挂了，主从库还能切换吗？
基于pub/sub机制的哨兵集群组成
    结合文稿的图示  哨兵发布的消息是发布到主的频道上的
    通过pub/sub机制，哨兵之间可以组成集群，同时，哨兵又通过INFO命令，获得了从库连接信息，也能和从库建立连接，并进行监控
基于pub/sub机制的客户端事件通知
    结合文稿的图示
    有了事件通知，客户端不仅可以在主从切换后得到新主库的连接信息，还可以监控到主从库切换过程中发生的各个重要事件。这样，客户端就可以知道主从切换进行到哪一步了，有助于了解切换进度。
    有了pub/sub机制，哨兵和哨兵之间、哨兵和从库之间、哨兵和客户端之间就都能建立起连接了，加上主库下线判断和选主依据，哨兵集群的监控、选主和通知三个任务就基本可以正常工作了。
由哪个哨兵执行主从切换？
    Leader选举
小结
支持哨兵集群的这些关键机制，包括：
    基于pub/sub机制的哨兵集群组成过程；
    基于INFO命令的从库列表，这可以帮助哨兵和从库建立连接；
    基于哨兵自身的pub/sub功能，这实现了客户端和哨兵之间的事件通知。
一个经验：要保证所有哨兵实例的配置是一致的，尤其是主观下线的判断值down-after-milliseconds。

11-“万金油”的String，为什么不好用了？
为什么String类型内存开销大？
String类型就会用简单动态字符串（Simple Dynamic String，SDS）
    buf,len,alloc
    RedisObject的结构
    int、embstr和raw这三种编码模式的内存示意图
用什么数据结构可以节省内存？
    压缩列表（ziplist）
如何用集合类型保存单值的键值对？
    为了能充分使用压缩列表的精简内存布局，我们一般要控制保存在Hash集合中的元素个数。
重点:如何解决文稿中的案例

12-有一亿个keys要统计，应该用哪种集合？
聚合统计
    小建议：你可以从主从集群中选择一个从库，让它专门负责聚合计算，或者是把数据读取到客户端，在客户端来完成聚合统计
排序统计
    List和Sorted Set就属于有序集合
    List是按照元素进入List的顺序进行排序的，而Sorted Set可以根据元素的权重来排序
    LRANGE,ZRANGEBYSCORE
二值状态统计
    记录签到（1）或未签到（0）是非常典型的二值状态
    Bitmap
    SETBIT uid:sign:3000:202008 2 1
    GETBIT uid:sign:3000:202008 2
    BITCOUNT uid:sign:3000:202008
    在实际应用时，最好对Bitmap设置过期时间，让Redis自动删除不再需要的签到记录，以节省内存开销
基数统计
    网页的UV
    SADD page1:uv user1
    HSET page1:uv user1 1
    Set,Hash在数据量很大时也会消耗很大的内存空间
    使用HyperLogLog
    PFADD page1:uv user1 user2 user3 user4 user5
    PFCOUNT page1:uv
        在Redis中，每个 HyperLogLog只需要花费 12 KB 内存，就可以计算接近 2^64 个元素的基数。
        和元素越多就越耗费内存的Set和Hash类型相比，HyperLogLog就非常节省空间。
小结
    见表格

13-GEO是什么？还可以定义新的数据类型吗？
面向LBS应用的GEO数据类型
GEO的底层结构
GeoHash的编码方法
如何操作GEO类型？
如何自定义数据类型？
Redis的基本对象结构
开发一个新的数据类型(4步)

14-如何在Redis中保存时间序列数据？
时间序列数据的读写特点
基于Hash和Sorted Set保存时间序列数据
基于RedisTimeSeries模块保存时间序列数据







28-Pika-如何基于SSD实现大容量Redis？
大内存Redis实例的潜在问题
Pika的整体架构
Pika如何基于SSD保存更多数据？
Pika如何实现Redis数据类型兼容？
Pika的其他优势与不足



29-无锁的原子操作：Redis如何应对并发访问？
并发访问中需要对什么进行控制？
Redis的两种原子操作方法
    把多个操作在Redis中实现成一个操作，也就是单命令操作；
    把多个操作写到一个Lua脚本中，以原子性方式执行单个Lua脚本。


30-如何使用Redis实现分布式锁？
分布式锁的两个要求
    原子性、可靠性
单机上的锁和分布式锁的联系与区别
    单机: SETNX和DEL
    两个潜在风险与解决:
        务必给锁指定过期时间
        防止误删除,需要能区分来自不同客户端的锁操作
基于单个Redis节点实现分布式锁
    Redlock
基于多个Redis节点实现高可靠的分布式锁
    基本思路:让客户端和多个独立的Redis实例依次请求加锁，如果客户端能够和半数以上的实例成功地完成加锁操作，那么我们就认为，客户端成功地获得分布式锁了，否则加锁失败。
3步来完成加锁
    第一步是，客户端获取当前时间。
    第二步是，客户端按顺序依次向N个Redis实例执行加锁操作。
    第三步是，一旦客户端完成了和所有Redis实例的加锁操作，客户端就要计算整个加锁过程的总耗时。
        客户端只有在满足下面的这两个条件时，才能认为是加锁成功。
            条件一：客户端从超过半数（大于等于 N/2+1）的Redis实例上成功获取到了锁；
            条件二：客户端获取锁的总耗时没有超过锁的有效时间。





