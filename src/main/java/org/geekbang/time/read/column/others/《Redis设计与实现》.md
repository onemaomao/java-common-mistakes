CH2 简单动态字符串
SDS的数据结构
sds.h
free len buf
C字符串和SDS之间的区别表格
SDS比C字符串的优势
    常数复杂度获取字符串长度
    杜绝缓冲区溢出
    减少修改字符串长度时内存重分配次数
    二进制安全
    兼容部分C字符串函数
--------

CH3 链表
链表和链表节点的实现
adlist.h
listNode: pre-前置结点, next-后置结点, value-结点的值
list: head-表头结点, tail-表尾结点, len-链表结点数量, dup-结点复制函数, free-结点释放函数, match-结点值对比函数
特性:双端; 无环; 带表头指针和表尾指针; 带链表长度计数器; 多态?
    多态: 链表结点使用指针保存结点的值,并且可以通过list结构的dup, free, match三个属性为结点值设置类型特定函数, 所以链表可以保存各种不同类型的值

--------
CH4 字典
字典的实现-哈希表
dict.h
哈希表结构: 哈希表数组, 哈希表大小, 哈希表大小掩码(用于计算索引值,总是等于size-1), 该哈希表已有结点数量
哈希表结点结构: 键, 值, 指向下个哈希表结点的指针(形成链表)
字典结构: 类型特定函数, 私有数据, 哈希表, rehash索引
    这里有书中的详细图解
哈希算法
    MurmurHash
解决键冲突
    链地址法 sperate chaining
rehash
    让哈希表的负载因子维持在一个合理的范围。
    当哈希表保存的键值对数量太多或者太少时,程序需要对哈希表的大小进行相应的扩展或者收缩。
    rehash的步骤
        1)为ht[1]分配空间
            扩容:ht[1]大小=第一个大于等于ht[0].used*2的2^n
            缩减:ht[1]大小=第一个大于等于ht[0].used的2^n
        2)h[0] rehash到h[1]
        3)释放h[0],h[1]设置为h[0],h[1]上新建一个空白哈希表为下一次rehash做准备
    哈希表的扩展与收缩
        扩展操作的两个条件,注意为什么还要与BGSAVE或者BGREWRITEAOF相关
        收缩操作的条件:负载因子小于0.1
渐进式rehash
    为避免rehash对服务器性能造成的影响, 服务器分多次、渐进式地将h[0]里面的键值对慢慢地rehash到h[1]
    步骤……过程中rehashidx由-1变为0再逐步递增再变为-1
渐进式rehash执行期间哈希表操作
    删除、查找、更新会在两个哈希表上进行, 新增会在h[1]上执行
重点回顾:
    字典被广泛用于Redis的各种功能,包括数据库和哈希键
    字典使用哈希表实现,两个哈希表,一个平时使用另一个rehash时使用
    当字典备用做数据库或者哈希键的底层实现, 采用MurmurHash2算法计算键的哈希值
    链地址解决哈希冲突
    rehash过程渐进式
--------

CH5 跳跃表
平均O(logN),最坏O(n)
Redis只有在两个地方用到了跳表,一个是实现有序集合键,另一个是在集群结点中用作内部数据结构
跳表的实现
redis.h/zskiplist  redis.h/zskiplistNode
zskiplist的各个属性: *header, *tail, length, level
zskiplistNode的各个属性:
    *obj,score,*backward,level[]
    level:*forward, span
跳表的结点:
层, 前进指针, 跨度, 后退指针, 分值和成员
重点回顾:
    跳表是有序集合的底层实现之一
    跳表实现是由zskiplist和zskiplistNode组成,
        其中zskiplist用于保存跳跃表信息(比如表头结点,表尾结点,长度)
        zskiplistNode表示跳表结点
    每个跳表的层高都是1至32之间的随机数
    在同一个跳表中多个结点可以包含相同的分值,但每个结点的成员对象必须是唯一的
    跳表中的结点按照分值大小进行排序,当分值相同时,结点按照成员对象的大小进行排序
--------

CH6 整数集合
示例:
    sadd numbers 1 3 5 7 9
    OBJECT encoding numbers 结果是 intset
    SADD bbs "tianya.cn" "groups.google.com"
    OBJECT encoding bbs 结果是 hashtable
整数集合的实现
    intset.h/intset结构
        typedef struct intset {
        uint32_t encoding;
        uint32_t length;
        int8_t contents[];
    } intset;
    contents真正的类型取决于encoding的值
升级
    升级的过程:
        每次向整数集合中添加新元素都可能会引起升级, 
        每次升级都需要对底层数组中已有的元素进行类型转换,
        所以向整数集合添加新元素的时间复杂度为O(N)
    升级的好处:
        提升整数集合的灵活性:不必担心类型转换的错误
        节约内存:要让一个数组可以同时保存int16_t, int32_t, int64_t三种类型的值
        不必一开始就使用int64_t,这样浪费内存,只有进行升级的时候才转换
降级
    不支持
重点回顾
    整数集合是集合键的底层实现之一
    整数集合的底层实现为数组,这个数组以有序、无重复的方式保存集合元素,在有需要时程序会根据新添加元素的类型,改变这个数组的类型
    升级操作为集合带来了操作上的灵活性,并且尽可能地节约了内存
    整数集合只支持升级操作,不支持降级操作
--------

CH7 压缩列表
示例:
    RPUSH lst 1 3 5 10086 "hello" "world"
    OBJECT encoding lst 结果是 ziplist
    HMSET profile "name" "jack" "age" 28 "job" "Programer"
    OBJECT encoding profile 结果是 ziplist
压缩列表(ziplist)是列表键和哈希键的底层实现之一。
    当一个列表键中只包含少量列表项, 并且每个列表项要么就是小整数值, 要么就是长度较短的字符串, 
        那么Redis就会使用压缩列表来做列表键的底层实现
    当一个哈希键只包含少量键值对，并且每个键值对的键和值要么就是小整数值,要么就是长度比较短的字符串,
        那么Redis就会使用压缩列表实现哈希键
压缩列表的构成
    zlbytes, 记录整个压缩列表占用的内存字节数:在对压缩列表进行内存重分配或者计算zlend的位置时使用
    zltail, 记录压缩列表的尾结点距离压缩列表起始地址有多少个字节:通过这个偏移量程序无需遍历整个压缩列表就可以确定尾结点的地址
    zlen, 压缩列表包含的结点数量
    entry1, entry2......, entryN, 长度不定的列表结点
    zlend 标记压缩列表的末端
压缩列表结点的构成
每个压缩列表可以保存一个字节数组或者一个整数值
    字节数组的三种长度
    整数值的六种长度
三部分组成
    previous_entry_length:以字节为单位,记录了压缩列表前一个结点的长度
        表尾向表头遍历的实现
    encoding:记录了结点的content属性所保存的数据类型及长度
    content:保存结点的值,结点的值可以是一个字节数组或者整数,值的类型和长度由结点的encoding属性决定
连锁更新
    场景描述:
    在最坏的情况下需要对压缩列表执行n次空间重分配操作,而每次空间重分配最坏复杂度为O(n),所以连锁更新的最坏复杂度为O(n^2)
    要注意的是尽管连锁更新的复杂度较高,但它真正造成性能问题的概率还是很低的:
        首先, 压缩列表里恰好有多个连续的、长度介于250字节至253字节的之间的结点, 连锁更新才有可能被引发
        其次, 即使出现连锁更新, 但只要被更新的结点数量不多, 就不会对性能造成任何影响:
            比如说对三五个结点进行连锁更新是不会影响性能的;
        因为以上的原因,ziplistPush等命令的平均复杂度仅为O(n), 在实际中我们可以放心使用。
重点回顾
    压缩列表是一种为节约内存而开发的顺序性数据结构
    压缩列表被用作列表键和哈希键的底层实现之一
    压缩列表可以包含多个结点,每个结点可以保存一个字节数组或者整数值
    添加新结点到压缩列表,或者从压缩列表中删除结点,可能会引发连锁更新,但这种操作出现的概率并不高
--------
第八章需要仔细学习
CH8 对象
Redis中的每个对象都由一个redisObject结构表示
    typedef struct redisObject {
        //类型
        unsigned type:4;
        //编码
        unsigned encoding:4;
        unsigned lru:REDIS_LRU_BITS; /* lru time (relative to server.lruclock) */
        int refcount;
        //指向底层实现的数据结构的指针
        void *ptr;
    } robj;
对象的类型和编码
    本小节的图示表格很重要
    type:5种。
        示例表格
        可以使用TYPE命令输出查看
    encoding:记录了对象所使用的编码,也就是说这个对象使用了什么数据结构作为对象的底层实现
        对象的ptr指针指向了对象的底层数据结构,而这些数据结构是由encoding属性决定
        示例表格  
    使用encoding属性设定对象所使用的编码,而不是为特定类型的对象关联一种固定的编码,
    极大地提升了Redis的灵活性和效率,因为Redis可以为不同的使用场景来为一个对象使用不同的编码,
    从而优化对象在某一场景下的效率......
    举例说明:
        数据量少,使用压缩列表作为列表对象的底层实现;
        元素越来越多,压缩列表的优势逐渐消失转向双端列表
字符串对象
    int
    embstr
    raw
列表对象
    ziplist
    linkedlist
    字符串对象是Redis五种类型的对象中唯一一种会被其他四种类型对象嵌套的对象
    编码转换的条件
    编码转换的配置是可以修改的list-max-ziplist-value和list-max-ziplist-entries
哈希对象
    ziplist
    hashtable
    采用压缩列表情况下的图示,键值都是在列表上
    编码转换的条件,同样可以修改配置变更
集合对象
    intset
    hashtable
    编码转换的配置也可以修改
有序集合对象
    ziplist
    skiplist
    同时使用这两种结构的原因
    采用压缩列表情况下的图示,成员和分值都是在列表上
    编码转换的配置也可以修改
类型检查和命令多态
    任何类型的键执行的命令:del, expire, rename, type, object
    特定类型的键执行的命令:参考文稿或者官方或者http://doc.redisfans.com/
    可以人为del, expire, rename, type, object是多态命令
    List的命令LLEN不管是ziplist还是linkedlist都可以正常执行
内存回收
    redisObject中的refcount引用计数实现内存回收机制
    创建是1,被新程序使用+1,不再被使用-1,为0释放
对象共享
    对象的引用计数属性除了用于实现内存回收之外还带有对象共享的作用
    只针对整数字符串值
    目前来说,Redis会在初始化服务器的时候创建一万个字符串对象(0-9999),服务器需要用到时候就会共享而不是重新创建
    创建共享字符串的数量可以修改redis.h中的REDIS_SHARED_INTEGERS常量
    代码示例:
        set A 100
        OBJECT refcount A 结果是2
        set B 100
        OBJECT refcount A 结果是3
        OBJECT refcount B 结果是3
    共享对象不单单只有字符串可以使用,那些在数据结构中嵌套了对象的对象
    (linkedlist编码的列表对象,hashtable编码的列表对象,hashtable编码的集合对象,zet编码的有序集合对象)
    都可以使用这些共享对象
    为什么Redis不共享包含字符串的对象?
        ......CPU时间的限制
对象的空转时长
    redisObject中的lru,记录了对象最后一次被访问的时间
    代码示例:OBJECT idletime得到的时间就是当前时间减去lru时间
        set aa "hello"
        OBJECT idletime aa 注意这个命令不会修改lru属性
        get aa
        OBJECT idletime aa
重点回顾
    Redis数据库中每个键值对的键和值都是一个对象
    五种对象类型
    服务器执行某些命令之前会先检查给定键的类型能否执行指定的命令
    Redis的对象系统带有引用计数的内存回收机制,当一个对象不在被使用时,该对象所占的内存会被自动释放
    Redis会共享0-9999的字符串对象
    对象会记录自己的最后一次被访问的时间,这个时间可以用于计算对象的空转时间














