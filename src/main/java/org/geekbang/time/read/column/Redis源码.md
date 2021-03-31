基于redis6.0

dict.h
typedef struct dictEntry {
    void *key; /*key关键字定义*/
    union {
        void *val; /*value关键字定义*/
        uint64_t u64;
        int64_t s64;
        double d;
    } v;
    struct dictEntry *next;/*指向下一个键值对结点*/
} dictEntry;
--
server.h
typedef struct redisDb {
    dict *dict;  /*所有的键值对*/               /* The keyspace for this DB */
    dict *expires;    /*设置了过期时间的键值对*/          /* Timeout of keys with a timeout set */
    dict *blocking_keys;        /* Keys with clients waiting for data (BLPOP)*/
    dict *ready_keys;           /* Blocked keys that received a PUSH */
    dict *watched_keys;         /* WATCHED keys for MULTI/EXEC CAS */
    int id;                     /* Database ID */
    long long avg_ttl;          /* Average TTL, just for stats */
    unsigned long expires_cursor; /* Cursor of the active expire cycle. */
    list *defrag_later;         /* List of key names to attempt to defrag one by one, gradually. */
} redisDb;
--
server.h
typedef struct redisObject {
    unsigned type:4;/*对象的类型，包括： OBJ_STRING, OBJ_LIST,OBJ_HASH,OBJ_SET,OBJ_ZSET*/
    unsigned encoding:4;/*具体的数据结构*/
    unsigned lru:LRU_BITS; /*24位,对象最后一次被命令访问的时间，与内存回收有关*/
                            /* LRU time (relative to global lru_clock) or
                            * LFU data (least significant 8 bits frequency
                            * and most significant 16 bits access time). */
    int refcount;/*引用计数,当refcount为0的时候，表示该对象已经不被任何对象引用，则可以进行垃圾回收了*/
    void *ptr;/*指向对象实际的数据结构*/
} robj;
--
obj.c
define OBJ_ENCODING_EMBSTR_SIZE_LIMIT 44

--
sds.h
struct __attribute__ ((__packed__)) sdshdr8 {
    uint8_t len; /*当前字符串数组的长度*/ /* used */
    uint8_t alloc; /*当前字符串数组总共分配的内存大小*/ /* excluding the header and null terminator */
    unsigned char flags; /*当前字符数组的属性、用来标识到底是sdshdr8还是sdshdr16等*//* 3 lsb of type, 5 unused bits */
    char buf[];/*字符串真正的值*/
};

这里提出问题
1、SDS是什么？
    Simple Dynamic String简单动态字符串
    本质上还是字符数组
    SDS有多种结构,sdshdr5,sdshdr8,sdshdr16,sdshdr32,sdshdr64
        分别代表2^5=32byte,2^8=256byte......
2、为什么Redis要用SDS实现字符串
    见《Redis设计与实现》&gupao2020的课件
3、embstr和raw编码的区别？为什么要为不同大小设计不同编码？
    embstr的使用只分配一次内存空间,raw需要分配两次内存空间
    embstr的好处在于创建时少分配一次空间，删除时少释放一次空间，对象的所有数据在一起，寻找方便
    embstr坏处是如果字符串的长度增加需要重新分配内存时，整个RedisObject和SDS都需要重新分配空间，因此Redis众的embstr实现为只读
4、int和embstr什么时候转化为raw？
    int 数据不再是整数 - raw
    int 超过long的范围2^63-1 - embstr
    embstr长度超过44个字节 - raw
    补充:明明没有超过44个字节，为什么变成raw了？
        embstr的实现是只读的，在对embstr对象进行修改时都会先转化为raw再进行修改
        因此只要是修改embstr对象，修改后一定是raw的，无论是否达到了44个字节
5、当长度小于阈值时，会还原吗？
    内部转码符合写入数据时完成并且转换过程不可逆，只能从小内存编码向大内存编码转换(但是不包括重新set)
6、为什么要对底层的数据结构使用redisObject进行一层包装呢？
    无论是设计redisObject还是对存储字符设计这多的SDS，都是为了根据存储的不同内容选择不同的存储方式，
    这样可以尽量地节省内存空间和提升查询速度的目的

--
ziplist.c
有关压缩列表数据结构的注释
<zlbytes> <zltail> <zllen> <entry> <entry> ... <entry> <zlend>
Entry内容的源码
typedef struct zlentry {
    unsigned int prevrawlensize; /*存储上一个链表节点的长度数值所需要的字节数*/ /* Bytes used to encode the previous entry len*/
    unsigned int prevrawlen;  /*上一个链表结点占用的长度*/   /* Previous entry len. */
    unsigned int lensize;   /*存储当前链表结点长度数值所需要的字节数*/     /* Bytes used to encode this entry type/len.
                                                                        For example strings have a 1, 2 or 5 bytes
                                                                        header. Integers always use a single byte.*/
    unsigned int len;   /*当前链表结点占用的长度*/         /* Bytes used to represent the actual entry.
                                                        For strings this is just the string length
                                                        while for integers it is 1, 2, 3, 4, 8 or
                                                        0 (for 4 bit immediate) depending on the
                                                        number range. */
    unsigned int headersize;  /*当前链表结点的头部大小(prevrawlensize+lensize),即非数据域的大小*/   /* prevrawlensize + lensize. */
    unsigned char encoding;   /*编码方式*/   /* Set to ZIP_STR_* or ZIP_INT_* depending on
                                            the entry encoding. However for 4 bits
                                            immediate integers this can assume a range
                                            of values and must be range-checked. */
    unsigned char *p;   /*压缩链表以字符串的形式保存，该指针指向当前节点起始位置*/         /* Pointer to the very start of the entry, that
                                                is, this points to prev-entry-len field. */
} zlentry;

问题
1、什么时候用ziplist存储？
当哈希对象同时满足以下两个条件的时候使用ziplist编码
    a.哈希对象保存的键值对数量<512个
    b.所有的键值对的键和值的字符串长度都<64byte
    可以修改redis.conf做配置调整
--
dict.h文件
dictEntry放到了dictht(hashtable里面)
typedef struct dictht {
    dictEntry **table; /*哈希表数组*/
    unsigned long size; /*哈希表大小*/
    unsigned long sizemask;/*掩码大小，用于计算索引值。总是等于size-1*/
    unsigned long used;/*已有节点数*/
} dictht;
dictht又放到了dict里面
typedef struct dict {
    dictType *type;/*字典类型*/
    void *privdata;/*私有数据*/
    dictht ht[2];/*一个字典有两个哈希表*/
    long rehashidx; /*rehash索引*//* rehashing not in progress if rehashidx == -1 */
    unsigned long iterators; /*当前正在使用的迭代器数量*//* number of iterators currently running */
} dict;
所以hash整体的结构从最底层到最高层dictEntry->dictht->dict
它是一个数组+链表的结构

问题
1、为什么要定义两个哈希表，其中一个不用呢？
    退化
    rehash
2、什么时候触发扩容？
static int dict_can_resize = 1;
static unsigned int dict_force_resize_ratio = 5;

--
list类型，早期使用ziplist和linkedlist
3.2版本之后统一用quicklist存储
quicklist.h
typedef struct quicklist {
    quicklistNode *head;/*指向双向链表的表头*/
    quicklistNode *tail;/*指向双向链表的表尾*/
    unsigned long count;/*所有的ziplist中一共存了多少元素*/        /* total count of all entries in all ziplists */
    unsigned long len; /*双向链表的长度，node的数量*/         /* number of quicklistNodes */
    int fill : QL_FILL_BITS; /*ziplist最大大小，对应list-max-ziplist-size*/             /* fill factor for individual nodes */
    unsigned int compress : QL_COMP_BITS; /*压缩深度，对应list-compress-depth*//* depth of end nodes not to compress;0=off */
    unsigned int bookmark_count: QL_BM_BITS;/*4位，bookmarks数组的大小*/
    quicklistBookmark bookmarks[];/*bookmarks是一个可选字段，quicklist重新分配内存空间时使用，不使用时不占用空间*/
} quicklist;

typedef struct quicklistNode {
    struct quicklistNode *prev;/*指向前一个节点*/
    struct quicklistNode *next;/*指向后一个节点*/
    unsigned char *zl;/*指向实际的ziplist*/
    unsigned int sz;  /*当前ziplist占用多少字节*/           /* ziplist size in bytes */
    unsigned int count : 16; /*当前ziplist中存储了多少元素，占16bit(下同)，最大65536个*/    /* count of items in ziplist */
    unsigned int encoding : 2; /*是否采用了LZF压缩算法压缩节点*/  /* RAW==1 or LZF==2 */
    unsigned int container : 2; /*2标识ziplist，未来可能支持其他存储结构*/  /* NONE==1 or ZIPLIST==2 */
    unsigned int recompress : 1; /*当前ziplist是不是已经被解压出来作临时使用*/ /* was this node previous compressed? */
    unsigned int attempted_compress : 1;/*测试用*/ /* node can't compress; too small */
    unsigned int extra : 10; /*预留给未来使用*//* more bits to steal for future usage */
} quicklistNode;
总结一下: quicklist是一个数组+链表的结构
--
intset.h
typedef struct intset {
    uint32_t encoding;/*编码类型 int16_t, int32_t, int64_t*/
    uint32_t length;/*长度 最大长度2^32*/
    int8_t contents[];/*用来存储成员的动态数组*/
} intset;
--
server.h
typedef struct zskiplistNode {
    sds ele;/*zset的元素*/
    double score;/*分值*/
    struct zskiplistNode *backward;/*后退指针*/
    struct zskiplistLevel {
        struct zskiplistNode *forward;/*前进指针*/
        unsigned long span;/*当前节点到下一节点的跨度(跨越的节点数)*/
    } level[];/*层*/
} zskiplistNode;

typedef struct zskiplist {
    struct zskiplistNode *header, *tail;/*指向跳跃表的头节点和尾结点*/
    unsigned long length;/*跳表的节点总数*/
    int level;/*最大层数*/
} zskiplist;

typedef struct zset {
    dict *dict;
    zskiplist *zsl;
} zset;
--
ae.c
Redis的多路复用,提供了select, epoll, evport, kqueue几种选择,在编译的时候选择一种。
#ifdef HAVE_EVPORT
#include "ae_evport.c"
#else
    #ifdef HAVE_EPOLL
    #include "ae_epoll.c"
    #else
        #ifdef HAVE_KQUEUE
        #include "ae_kqueue.c"
        #else
        #include "ae_select.c"
        #endif
    #endif
#endif
evport是Solaris系统内核提供支持的
epoll是Linux系统内核提供支持的
kqueue是Mac系统提供支持的
select是POSIX提供的，一般的操作系统都有支撑（保底方案）
源码见:ae_epoll.c, ae_evport.c, ae_kqueue.c, ae_select.c
--

内存回收
立即过期(主动淘汰)、惰性过期(被动淘汰)、定期过期
redis使用了惰性过期和定期过期两种策略,并不是实时清理过期的key
最大内存设置见redis.conf
如果不设置maxmemory或者设置为0,32位操作系统最多使用3GB内存,64位操作系统不限制内存
8种内存淘汰策略
LRU淘汰原理源码

问题
1、如何找出热度最低的数据
server.h
typedef struct redisDb {
    dict *dict;  /*所有的键值对*/               /* The keyspace for this DB */
    dict *expires;    /*设置了过期时间的键值对*/          /* Timeout of keys with a timeout set */
    dict *blocking_keys;        /* Keys with clients waiting for data (BLPOP)*/
    dict *ready_keys;           /* Blocked keys that received a PUSH */
    dict *watched_keys;         /* WATCHED keys for MULTI/EXEC CAS */
    int id;                     /* Database ID */
    long long avg_ttl;          /* Average TTL, just for stats */
    unsigned long expires_cursor; /* Cursor of the active expire cycle. */
    list *defrag_later;         /* List of key names to attempt to defrag one by one, gradually. */
} redisDb;

......

2、为什么不获取精确的时间而是放在全局变量中？不会有延迟问题吗？
3、除了消耗资源外,传统LRU还有什么问题？
4、要实现基于访问频率的淘汰机制，要怎么做？



string使用场景
    缓存、分布式数据共享(比如session)、分布式锁、全局id(int类型，incrby，利用原子性)、计数器(int 类型，incr)、限流(int 类型，incr)
hash使用场景
    string可以做得事情hash都可以做
    存储对象类型的数据，比如对象或者一张表的数据，比string节省了更多key的空间，也更便于集中管理。
    购物车
    key:用户id
    field:商品id
    value:商品数量
    +1:hincr
    -1:hdecr
    删除:hincrby key field -1
    全选:hgetall
    商品数:hlen
list使用场景
    列表
        用户的消息列表
        网站的公告列表
        活动列表
        博客的文章列表、评论列表
        思路:存储所有字段，LRANGE取出一页，按顺序显示
    队列/栈
        list还可以当做分布式环境的队列/栈使用
        list提供了两个阻塞的弹出操作:BLPOP/BRPOP，可以设置超时时间(单位：秒)
        队列：先进先出:rpush blpop，左头右尾，右边进入队列，左边出队列
        栈：先进先出:rpush brpop
set使用场景
    抽奖
        随机获取元素:spop myset
    点赞、签到、打卡
        eg:微博id是t1001,用户id是u3001，用like:t1001来维护t1001这条微博所有点赞用户
        点赞: sadd like:t1001 u3001
        取消点赞: srem like:t1001 u3001
        是否点赞: sismember like:t1001 u3001
        点赞的所有用户: smembers like:t1001
        点赞数: scard like:t1001
    商品标签
        eg:用tags:i5001来维护商品所有的标签
        sadd tags:i5001 画面清晰细腻
        sadd tags:i5001 真彩清晰显示屏
        sadd tags:i5001 流畅至极
    商品筛选
        差集: sdiff set1 set2
        交集: sinter set1 set2
        并集: sunion set1 set2
        eg: P40上市了
        sadd brand:huawei p40
        sadd os:android p40
        sadd screensize:6.0-6.24 p40
        筛选商品，华为的，安卓的，屏幕6.0-6.24之间的
        sinter brand:huawei os:android screensize:6.0-6.24
    用户关注、推荐模型
        相互关注?
        我关注的人也关注了他?
        可能认识的人?
zset使用场景
    排行榜，例如百度热榜、微博热搜
    id为6001的新闻点击数+1:zincrby hotNews:20251111 1 n6001
    获取今天点击最多的15条:zrevrange hotNews:20251111 0 15 withscores

bitmap使用场景
    set k1 a   a对应的asii码是97，转换为二进制是01100001
    getbit k1 0
    修改二进制数据
    setbit k1 6 1
    setbit k1 7 0
    get k1 发现是 b
    bitcount k1 统计二进制中1的个数
    bitpos k1 1       bitpos k1 0 获取第一个1或者0的位置
    因为bit非常节省空间(1M=8388608bit),可以用来做大数据量的统计，例如统计在线用户、留存用户
    setbit onlineusers 0 1
    setbit onlineusers 1 1
    setbit onlineusers 2 0
    支持按位与按位或等操作
    计算7天都在线的用户
    场景：用户访问统计，在线用户统计

Hyperloglogs使用场景
    用来统计一个集合哄不重复元素个数，日活、月活等，存在一定误差，
    在Redis中实现HyperLogLog只需要12k内存就能统计2^64个数据
Geo使用场景
    经纬度的保存
    GEOADD location 112.881953 28.238426 aa
    GEOPOS location aa
Streams
    5.0推出的数据类型，支持多播的可持久化消息队列，用于实现发布订阅功能，借鉴了kafka的设计


