开篇词丨SQL可能是你掌握的最有用的技能
不论是产品经理、运营人员，还是开发人员、数据分析师，你都可以用到 SQL 语言。它就像一把利剑，除了可以提高你的工作效率之外，还能扩大你的工作视野。

1丨了解SQL：一门半衰期很长的语言
半衰期很长的 SQL
    SQL 有两个重要的标准，分别是 SQL92 和 SQL99
入门 SQL 并不难
    DDL,DML,DCL,DQL
开启 SQL 之旅
    ER 图

02丨DBMS的前世今生
    DB、DBS 和 DBMS 的区别是什么
    排名前 20 的 DBMS 都是那些
    SQL 阵营与 NoSQL 阵营
    SQL 阵营中的 DBMS
    总结

03丨学会用数据库的方式思考SQL是如何执行的
Oracle 中的 SQL 是如何执行的
    执行过程图
    语法检查->语义检查->权限检查->共享池检查->硬解析->优化器->执行
                                        ->软解析->执行
    共享池检查的过程
    绑定变量来减少硬解析

MySQL 中的 SQL 是如何执行的
    MySQL 由三层组成
        连接层：客户端和服务器端建立连接，客户端发送 SQL 至服务器端；
        SQL 层：对 SQL 语句进行查询处理；
        存储引擎层：与数据库文件打交道，负责数据的存储和读取。
    SQL 层的结构,图
        查询缓存：Server 如果在查询缓存中发现了这条 SQL 语句，就会直接将结果返回给客户端；如果没有，就进入到解析器阶段。需要说明的是，因为查询缓存往往效率不高，所以在 MySQL8.0 之后就抛弃了这个功能。
        解析器：在解析器中对 SQL 语句进行语法分析、语义分析。
        优化器：在优化器中会确定 SQL 语句的执行路径，比如是根据全表检索，还是根据索引来检索等。
        执行器：在执行之前需要判断该用户是否具备权限，如果具备权限就执行 SQL 查询并返回结果。在 MySQL8.0 以下的版本，如果设置了查询缓存，这时会将查询结果进行缓存。
    SQL 语句在 MySQL 中的流程是：SQL 语句→缓存查询→解析器→优化器→执行器
    常见的存储引擎&各自特点
        InnoDB,MyISAM,Memory,NDB,Archive

数据库管理系统也是一种软件
    开启profiling让MySQL 收集在 SQL 执行时所使用的资源情况

04丨使用DDL创建数据库&数据表时需要注意什么？
DDL 的基础语法及设计工具
    1.对数据库进行定义
        CREATE DATABASE nba; // 创建一个名为 nba 的数据库
        DROP DATABASE nba; // 删除一个名为 nba 的数据库
    2.对数据表进行定义
        CREATE TABLE table_name
创建表结构
修改表结构
    ALTER TABLE player ADD (age int(11));
    ALTER TABLE player RENAME COLUMN age to player_age;
    ALTER TABLE player MODIFY (player_age float(3,1));
    ALTER TABLE player DROP COLUMN player_age;
数据表的常见约束
    主键约束,外键约束,唯一性约束,NOT NULL 约束,DEFAULT,CHECK 约束
设计数据表的原则
    1.数据表的个数越少越好
    2.数据表中的字段个数越少越好
    3.数据表中联合主键的字段个数越少越好
    4.使用主键和外键越多越好
总结

05丨检索数据：你还在SELECT * 么？
SELECT 查询的基础语法
查询列
    SQL：SELECT name FROM heros
    SQL：SELECT name, hp_max, mp_max, attack_max, defense_max FROM heros
    SQL：SELECT * FROM heros
起别名
    SQL：SELECT name AS n, hp_max AS hm, mp_max AS mm, attack_max AS am, defense_max AS dm FROM heros
查询常数
    SQL：SELECT '王者荣耀' as platform, name FROM heros
    SQL：SELECT 123 as platform, name FROM heros
去除重复行
    SQL：SELECT DISTINCT attack_range FROM heros
    SQL：SELECT DISTINCT attack_range, name FROM heros
    DISTINCT 需要放到所有列名的前面
    DISTINCT 其实是对后面所有列名的组合进行去重
如何排序检索数据
    排序的列名：ORDER BY 后面可以有一个或多个列名，如果是多个列名进行排序，会按照后面第一个列先进行排序，当第一列的值相同的时候，再按照第二列进行排序，以此类推。
    排序的顺序：ORDER BY 后面可以注明排序规则，ASC 代表递增排序，DESC 代表递减排序。如果没有注明排序规则，默认情况下是按照 ASC 递增排序。我们很容易理解 ORDER BY 对数值类型字段的排序规则，但如果排序字段类型为文本数据，就需要参考数据库的设置方式了，这样才能判断 A 是在 B 之前，还是在 B 之后。比如使用 MySQL 在创建字段的时候设置为 BINARY 属性，就代表区分大小写。
    非选择列排序：ORDER BY 可以使用非选择列进行排序，所以即使在 SELECT 后面没有这个列名，你同样可以放到 ORDER BY 后面进行排序。
    ORDER BY 的位置：ORDER BY 通常位于 SELECT 语句的最后一条子句，否则会报错。
    SQL：SELECT name, hp_max FROM heros ORDER BY hp_max DESC
    SQL：SELECT name, hp_max FROM heros ORDER BY mp_max, hp_max DESC
约束返回结果的数量
    SQL：SELECT name, hp_max FROM heros ORDER BY hp_max DESC LIMIT 5
    SQL：SELECT TOP 5 name, hp_max FROM heros ORDER BY hp_max DESC
    SQL：SELECT name, hp_max FROM heros ORDER BY hp_max DESC FETCH FIRST 5 ROWS ONLY
    SQL：SELECT name, hp_max FROM heros WHERE ROWNUM <=5 ORDER BY hp_max DESC
SELECT 的执行顺序
1. 关键字的顺序是不能颠倒的：
   SELECT ... FROM ... WHERE ... GROUP BY ... HAVING ... ORDER BY ... 
2.SELECT 语句的执行顺序（在 MySQL 和 Oracle 中，SELECT 执行顺序基本相同）：
   FROM > WHERE > GROUP BY > HAVING > SELECT 的字段 > DISTINCT > ORDER BY > LIMIT
什么情况下用 SELECT*，如何提升 SELECT 查询效率？
   生产环境下，不推荐你直接使用SELECT *进行查询
总结

06丨数据过滤：SQL数据过滤都有哪些方法？
比较运算符
逻辑运算符
使用通配符进行过滤
总结

07丨什么是SQL函数？为什么使用SQL函数可能会带来问题？
什么是 SQL 函数
常用的 SQL 函数有哪些
    算术函数
    字符串函数
    日期函数
    转换函数
为什么使用 SQL 函数会带来问题
    可移植性是很差的
关于大小写的规范
    建议：
        关键字和函数名称全部大写；
        数据库名、表名、字段名称全部小写；
        SQL 语句必须以分号结尾。
总结

08丨什么是SQL的聚集函数，如何利用它们汇总表的数据？
聚集函数都有哪些
    5个:COUNT-会忽略NULL,MAX,MIN,SUM,AVG
如何对数据进行分组，并进行聚集统计
    SQL: SELECT COUNT(*), role_main FROM heros GROUP BY role_main
    SELECT COUNT(*), role_assist FROM heros GROUP BY role_assist
    SELECT COUNT(*) as num, role_main, role_assist FROM heros GROUP BY role_main, role_assist ORDER BY num DESC
如何使用 HAVING 过滤分组，它与 WHERE 的区别是什么？
    WHERE 是用于数据行，而 HAVING 则作用于分组
    SQL: SELECT COUNT(*) as num, role_main, role_assist FROM heros GROUP BY role_main, role_assist HAVING num > 5 ORDER BY num DESC
    SQL: SELECT COUNT(*) as num, role_main, role_assist FROM heros WHERE hp_max > 6000 GROUP BY role_main, role_assist HAVING num > 5 ORDER BY num DESC
总结

09丨子查询：子查询的种类都有哪些，如何提高子查询的性能？
什么是关联子查询，什么是非关联子查询
    非关联子查询,SQL: SELECT player_name, height FROM player WHERE height = (SELECT max(height) FROM player)
    联子查询,SQL:SELECT player_name, height, team_id FROM player AS a WHERE height > (SELECT avg(height) FROM player AS b WHERE a.team_id = b.team_id)
EXISTS 子查询
    SQL：SELECT player_id, team_id, player_name FROM player WHERE EXISTS (SELECT player_id FROM player_score WHERE player.player_id = player_score.player_id)
    SQL: SELECT player_id, team_id, player_name FROM player WHERE NOT EXISTS (SELECT player_id FROM player_score WHERE player.player_id = player_score.player_id)
集合比较子查询
    IN、ANY、ALL 和 SOME 操作符,它们的含义和英文意义一样：
    该使用 IN 还是 EXISTS 呢？
    ANY、ALL 关键字必须与一个比较操作符一起使用
将子查询作为计算字段
    SQL: SELECT team_name, (SELECT count(*) FROM player WHERE player.team_id = team.team_id) AS player_num FROM team
总结

10丨常用的SQL标准有哪些，在SQL92中是如何使用连接的？
常用的 SQL 标准有哪些
    SQL 有两个主要的标准，分别是 SQL92 和 SQL99。92 和 99 代表了标准提出的时间，SQL92 就是 92 年提出的标准规范。
    除了 SQL92 和 SQL99 以外，还存在 SQL-86、SQL-89、SQL:2003、SQL:2008、SQL:2011 和 SQL:2016 等其他的标准。
    实际上最重要的 SQL 标准就是 SQL92 和 SQL99。
在 SQL92 中是如何使用连接的
笛卡尔积
    SQL: SELECT * FROM player, team
    笛卡尔积也称为交叉连接，英文是 CROSS JOIN，它的作用就是可以把任意表进行连接，即使这两张表不相关。
等值连接
    SQL: SELECT player_id, player.team_id, player_name, height, team_name FROM player, team WHERE player.team_id = team.team_id
    SELECT player_id, a.team_id, player_name, height, team_name FROM player AS a, team AS b WHERE a.team_id = b.team_id
非等值连接
    SQL：SELECT p.player_name, p.height, h.height_level
    FROM player AS p, height_grades AS h
    WHERE p.height BETWEEN h.height_lowest AND h.height_highest
外连接
    左外连接，就是指左边的表是主表，需要显示左边表的全部行，而右侧的表是从表，（+）表示哪个是从表。
        SQL：SELECT * FROM player, team where player.team_id = team.team_id(+)
    相当于 SQL99 中的：
        SQL：SELECT * FROM player LEFT JOIN team on player.team_id = team.team_id
    右外连接，指的就是右边的表是主表，需要显示右边表的全部行，而左侧的表是从表。
        SQL：SELECT * FROM player, team where player.team_id(+) = team.team_id
    相当于 SQL99 中的：
        SQL：SELECT * FROM player RIGHT JOIN team on player.team_id = team.team_id
    需要注意的是，LEFT JOIN 和 RIGHT JOIN 只存在于 SQL99 及以后的标准中，在 SQL92 中不存在，只能用（+）表示。
自连接
    SQL：SELECT b.player_name, b.height FROM player as a , player as b WHERE a.player_name = '布雷克 - 格里芬' and a.height < b.height
总结

11丨SQL99是如何使用连接的，与SQL92的区别是什么？
SQL99 标准中的连接查询
交叉连接
    SQL: SELECT * FROM player CROSS JOIN team
    SQL: SELECT * FROM t1 CROSS JOIN t2 CROSS JOIN t3
自然连接
    SELECT player_id, a.team_id, player_name, height, team_name FROM player as a, team as b WHERE a.team_id = b.team_id
    在 SQL99 中你可以写成：
    SELECT player_id, team_id, player_name, height, team_name FROM player NATURAL JOIN team
ON 连接
    SELECT player_id, player.team_id, player_name, height, team_name FROM player JOIN team ON player.team_id = team.team_id
    SQL99：SELECT p.player_name, p.height, h.height_level
    FROM player as p JOIN height_grades as h
    ON height BETWEEN h.height_lowest AND h.height_highest
    SQL92：SELECT p.player_name, p.height, h.height_level
    FROM player AS p, height_grades AS h
    WHERE p.height BETWEEN h.height_lowest AND h.height_highest
    一般来说在 SQL99 中，我们需要连接的表会采用 JOIN 进行连接，ON 指定了连接条件，后面可以是等值连接，也可以采用非等值连接。
USING 连接
    当我们进行连接的时候，可以用 USING 指定数据表里的同名字段进行等值连接。比如：
    SELECT player_id, team_id, player_name, height, team_name FROM player JOIN team USING(team_id)
    你能看出与自然连接 NATURAL JOIN 不同的是，USING 指定了具体的相同的字段名称，你需要在 USING 的括号 () 中填入要指定的同名字段。同时使用 JOIN USING 可以简化 JOIN ON 的等值连接，它与下面的 SQL 查询结果是相同的：
    SELECT player_id, player.team_id, player_name, height, team_name FROM player JOIN team ON player.team_id = team.team_id
外连接
    左外连接：LEFT JOIN 或 LEFT OUTER JOIN
        SQL92:SELECT * FROM player, team where player.team_id = team.team_id(+)
        SQL99:SELECT * FROM player LEFT JOIN team ON player.team_id = team.team_id
    右外连接：RIGHT JOIN 或 RIGHT OUTER JOIN
        SQL92:SELECT * FROM player, team where player.team_id(+) = team.team_id
        SQL99:SELECT * FROM player RIGHT JOIN team ON player.team_id = team.team_id
    全外连接：FULL JOIN 或 FULL OUTER JOIN
        SELECT * FROM player FULL JOIN team ON player.team_id = team.team_id
    MySQL 不支持全外连接
自连接
    SQL92:SELECT b.player_name, b.height FROM player as a , player as b WHERE a.player_name = '布雷克 - 格里芬' and a.height < b.height
    SQL99:SELECT b.player_name, b.height FROM player as a JOIN player as b ON a.player_name = '布雷克 - 格里芬' and a.height < b.height
SQL99 和 SQL92 的区别
    它们都对连接进行了定义，只是操作的方式略有不同。
    建议多表连接使用 SQL99 标准，因为层次性更强，可读性更强
不同 DBMS 中使用连接需要注意的地方
    1. 不是所有的 DBMS 都支持全外连接
    2.Oracle 没有表别名 AS
    3.SQLite 的外连接只有左连接 
连接的性能问题需要你注意：
    1. 控制连接表的数量
    2. 在连接时不要忘记 WHERE 语句
    3. 使用自连接而不是子查询
总结

12丨视图在SQL中的作用是什么，它是怎样工作的？
如何创建，更新和删除视图
    创建视图：CREATE VIEW
    嵌套视图
        当我们创建好一张视图之后，还可以在它的基础上继续创建视图
    修改视图：ALTER VIEW
    删除视图：DROP VIEW
如何使用视图简化 SQL 操作
    利用视图完成复杂的连接
    利用视图对数据进行格式化
    使用视图与计算字段
总结

13丨什么是存储过程，在实际项目中用得多么？
什么是存储过程，如何创建一个存储过程
    CREATE PROCEDURE 存储过程名称 (参数列表)
        BEGIN
        需要执行的语句
    END
流控制语句
关于存储过程使用的争议


########################

15丨初识事务隔离：隔离的级别有哪些，它们都解决了哪些异常问题？
事务并发处理可能存在的异常都有哪些？
    脏读：读到了其他事务还没有提交的数据。
    不可重复读：对某数据进行读取，发现两次读取的结果不同，也就是说没有读到相同的内容。这是因为有其他事务对这个数据同时进行了修改或删除。
    幻读：事务 A 根据条件查询得到了 N 条数据，但此时事务 B 更改或者增加了 M 条符合事务 A 查询条件的数据，这样当事务 A 再次进行查询的时候发现会有 N+M 条数据，产生了幻读。
事务隔离的级别有哪些？
    脏读、不可重复读和幻读这三种异常情况，是在 SQL-92 标准中定义的，同时 SQL-92 标准还定义了 4 种隔离级别来解决这些异常情况。
    解决异常数量从少到多的顺序（比如读未提交可能存在 3 种异常，可串行化则不会存在这些异常）决定了隔离级别的高低，
    这四种隔离级别从低到高分别是：读未提交（READ UNCOMMITTED ）、读已提交（READ COMMITTED）、可重复读（REPEATABLE READ）和可串行化（SERIALIZABLE）。
        读已提交就是只能读到已经提交的内容，可以避免脏读的产生，属于 RDBMS 中常见的默认隔离级别（比如说 Oracle 和 SQL Server）
        可重复读，保证一个事务在相同查询条件下两次查询得到的数据结果是一致的，可以避免不可重复读和脏读，但无法避免幻读。MySQL 默认的隔离级别就是可重复读。
使用 MySQL 客户端来模拟三种异常
    模拟“脏读”
    模拟“不可重复读”
    模拟“幻读”
总结

20丨当我们思考数据库调优的时候，都有哪些维度可以选择？
数据库调优的目标
    响应的时间更快，吞吐量更大。
    一般情况下，有两种方式可以得到反馈。
        用户的反馈,日志分析
    服务器资源使用监控
        通过监控服务器的 CPU、内存、I/O 等使用情况，可以实时了解服务器的性能使用，与历史情况进行对比。
    数据库内部状况监控
        在数据库的监控中，活动会话（Active Session）监控是一个重要的指标。通过它，你可以清楚地了解数据库当前是否处于非常繁忙的状态，是否存在 SQL 堆积等。
        除了活动会话监控以外，我们也可以对事务、锁等待等进行监控，这些都可以帮助我们对数据库的运行状态有更全面的认识。
对数据库进行调优，都有哪些维度可以进行选择？
    第一步，选择适合的 DBMS
        阵营,商业
    第二步，优化表设计
        参考的优化的原则
    第三步，优化逻辑查询
        子查询优化、等价谓词重写、视图重写、条件简化、连接消除和嵌套连接消除等。
    第四步，优化物理查询
        根据实际情况来创建索引
            如果数据重复度高，就不需要创建索引。通常在重复度超过 10% 的情况下，可以不创建这个字段的索引。比如性别这个字段（取值为男和女）。
            要注意索引列的位置对索引使用的影响。比如我们在 WHERE 子句中对索引字段进行了表达式的计算，会造成这个字段的索引失效。
            要注意联合索引对索引使用的影响。我们在创建联合索引的时候会对多个字段创建索引，这时索引的顺序就很重要了。比如我们对字段 x, y, z 创建了索引，那么顺序是 (x,y,z) 还是 (z,y,x)，在执行的时候就会存在差别。
            要注意多个索引对索引使用的影响。索引不是越多越好，因为每个索引都需要存储空间，索引多也就意味着需要更多的存储空间。此外，过多的索引也会导致优化器在进行评估的时候增加了筛选出索引的计算时间，影响评估的效率。
        SQL 查询时需要对不同的数据表进行查询，因此在物理查询优化阶段也需要确定这些查询所采用的路径，具体的情况包括：
            单表扫描：对于单表扫描来说，我们可以全表扫描所有的数据，也可以局部扫描。
            两张表的连接：常用的连接方式包括了嵌套循环连接、HASH 连接和合并连接。
            多张表的连接：多张数据表进行连接的时候，顺序很重要，因为不同的连接路径查询的效率不同，搜索空间也会不同。我们在进行多表连接的时候，搜索空间可能会达到很高的数据量级，巨大的搜索空间显然会占用更多的资源，因此我们需要通过调整连接顺序，将搜索空间调整在一个可接收的范围内。
    第五步，使用 Redis 或 Memcached 作为缓存
    第六步，库级优化
        控制一个库中的数据表数量
        主从架构优化我们的读写策略
        对数据库分库分表,垂直切分,水平切分
我们该如何思考和分析数据库调优这件事
    首先，选择比努力更重要。
    另外，你可以把 SQL 查询优化分成两个部分，逻辑查询优化和物理查询优化。
    最后，我们可以通过外援来增强数据库的性能。


23丨索引的概览：用还是不用索引，这是一个问题
索引是万能的吗？
    索引不是万能的，在有些情况下使用索引反而会让效率变低。
    在数据量不大的情况下，索引就发挥不出作用
    索引的价值是帮你快速定位。如果想要定位的数据有很多，那么索引就失去了它的使用价值，比如通常情况下的性别字段。不过有时候，我们还要考虑这个字段中的数值分布的情况,性别字段的数值分布非常特殊，男性的比例非常少,这时候用索引还是能够提升效率的。
索引的种类有哪些？
从功能逻辑上说，索引主要有 4 种，分别是普通索引、唯一索引、主键索引和全文索引。

按照物理实现方式，索引可以分为 2 种：聚集索引和非聚集索引。我们也把非聚集索引称为二级索引或者辅助索引。
    聚集索引可以按照主键来排序存储数据，这样在查找行的时候非常有效。找到了索引的位置，在它后面就是我们想要找的数据行。
    在数据库系统会有单独的存储空间存放非聚集索引，这些索引项是按照顺序存储的，但索引项指向的内容是随机存储的。也就是说系统会进行两次查找，第一次先找到索引，第二次找到索引对应的位置取出数据行。
        非聚集索引不会把索引指向的内容像聚集索引一样直接放到索引的后面，而是维护单独的索引表（只维护索引，不维护索引指向的数据），为数据检索提供方便。
    聚集索引与非聚集索引的原理不同，在使用上也有一些区别：
        聚集索引的叶子节点存储的就是我们的数据记录，非聚集索引的叶子节点存储的是数据位置。非聚集索引不会影响数据表的物理存储顺序。
        一个表只能有一个聚集索引，因为只能有一种排序存储的方式，但可以有多个非聚集索引，也就是多个索引目录提供数据检索。
        使用聚集索引的时候，数据的查询效率高，但如果对数据进行插入，删除，更新等操作，效率会比非聚集索引低。

除了业务逻辑和物理实现方式，索引还可以按照字段个数进行划分，分成单一索引和联合索引。
联合索引存在最左匹配原则，也就是按照最左优先的方式进行索引的匹配。
总结

24丨索引的原理：我们为什么用B+树来做索引？
如何评价索引的数据结构设计好坏
二叉树的局限性
    二分查找法是一种高效的数据检索方式，时间复杂度为 O(log2n)
    退化成链表，查找数据的时间复杂度变成了 O(n)
    平衡二叉搜索树（AVL 树）
    简单说，就是深度的问题。如果用二叉树作为索引的实现结构，会让树变得很高，增加硬盘的 I/O 次数，影响数据查询的时间。
什么是 B 树 Balance Tree
    图&过程
什么是 B+ 树
    B+ 树基于 B 树做出了改进，主流的 DBMS 都支持 B+ 树的索引方式，比如 MySQL。
    图&过程

25丨Hash索引的底层原理是什么？
动手统计 Hash 检索效率
    Python 的数据结构中有数组和字典两种，其中数组检索数据类似于全表扫描，需要对整个数组的内容进行检索；而字典是由 Hash 表实现的，存储的是 key-value 值，对于数据检索来说效率非常快。
MySQL 中的 Hash 索引
    采用 Hash 进行检索效率非常高，基本上一次检索就可以找到数据，而 B+ 树需要自顶向下依次查找，多次访问节点才能找到数据，中间需要多次 I/O 操作，从效率来说 Hash 比 B+ 树更快。
    Hash 冲突
Hash 索引与 B+ 树索引的区别
    Hash 索引不能进行范围查询
    Hash 索引不支持联合索引的最左侧原则
    Hash 索引不支持 ORDER BY 排序

26丨索引的使用原则：如何通过索引让SQL查询效率最大化？
创建索引有哪些规律？
    1. 字段的数值有唯一性的限制，比如用户名
    2. 频繁作为 WHERE 查询条件的字段，尤其在数据表大的情况下
    3. 需要经常 GROUP BY 和 ORDER BY 的列
    4.UPDATE、DELETE 的 WHERE 条件列，一般也需要创建索引
    5.DISTINCT 字段需要创建索引
    6. 做多表 JOIN 连接操作时，创建索引需要注意以下的原则
       首先，连接表的数量尽量不要超过 3 张，因为每增加一张表就相当于增加了一次嵌套的循环，数量级增长会非常快，严重影响查询的效率。
       其次，对 WHERE 条件创建索引，因为 WHERE 才是对数据条件的过滤。如果在数据量非常大的情况下，没有 WHERE 条件过滤是非常可怕的。
       最后，对用于连接的字段创建索引，并且该字段在多张表中的类型必须一致。
什么时候不需要创建索引
    WHERE 条件（包括 GROUP BY、ORDER BY）里用不到的字段不需要创建索引，索引的价值是快速定位，如果起不到定位的字段通常是不需要创建索引的。
    第二种情况是，如果表记录太少，比如少于 1000 个，那么是不需要创建索引的。
    第三种情况是，字段中如果有大量重复数据，也不用创建索引，比如性别字段。
    最后一种情况是，频繁更新的字段不一定要创建索引。
什么情况下索引失效
    1. 如果索引进行了表达式计算，则会失效
    2. 如果对索引使用函数，也会造成失效
    3. 在 WHERE 子句中，如果在 OR 前的条件列进行了索引，而在 OR 后的条件列没有进行索引，那么索引会失效。
        因为 OR 的含义就是两个只要满足一个即可，因此只有一个条件列进行了索引是没有意义的，只要有条件列没有进行索引，就会进行全表扫描，因此索引的条件列也会失效    
    4. 当我们使用 LIKE 进行模糊查询的时候，后面不能是 %
    5. 索引列与 NULL 或者 NOT NULL 进行判断的时候也会失效。
    6. 我们在使用联合索引的时候要注意最左原则

27丨从数据页的角度理解B+树查询
数据库中的存储结构是怎样的
    在数据库中，不论读一行，还是读多行，都是将这些行所在的页进行加载。也就是说，数据库管理存储空间的基本单位是页（Page）。
    看图
数据页内的结构是怎样的
    页（Page）如果按类型划分的话，常见的有数据页（保存 B+ 树节点）、系统页、Undo 页和事务数据页等。数据页是我们最常使用的页。
从数据页的角度看 B+ 树是如何进行查询的
1.B+ 树是如何进行记录检索的？
2. 普通索引和唯一索引在查询效率上有什么不同？

28丨从磁盘I/O的角度理解SQL查询的成本
数据库缓冲池
    那么缓冲池如何读取数据呢？
        缓冲池管理器会尽量将经常使用的数据保存起来，在数据库进行页面读操作的时候，首先会判断该页面是否在缓冲池中，如果存在就直接读取，如果不存在，就会通过内存或磁盘将页面存放到缓冲池中再进行读取。
查看缓冲池的大小
数据页加载的三种方式
    1. 内存读取
    2. 随机读取
    3. 顺序读取
通过 last_query_cost 统计 SQL 语句的查询成本

29丨为什么没有理想的索引？
索引片和过滤因子
    索引片就是 SQL 查询语句在执行中需要扫描的一个索引片段，我们会根据索引片中包含的匹配列的数量不同，将索引分成窄索引（比如包含索引列数为 1 或 2）和宽索引（包含的索引列数大于 2）。
如何通过宽索引避免回表
    通过宽索引将 SELECT 中需要用到的列（主键列可以除外）都设置在宽索引中，这样就避免了回表扫描的情况，从而提升 SQL 查询效率。
什么是过滤因子
    在索引片的设计中，我们还需要考虑一个因素，那就是过滤因子，它描述了谓词的选择性。在 WHERE 条件语句中，每个条件都称为一个谓词，谓词的选择性也等于满足这个条件列的记录数除以总记录数的比例。
针对 SQL 查询的理想索引设计：三星索引
    在 WHERE 条件语句中，找到所有等值谓词中的条件列，将它们作为索引片中的开始列；
    将 GROUP BY 和 ORDER BY 中的列加入到索引中；
    将 SELECT 字段中剩余的列加入到索引片中。
为什么很难存在理想的索引设计









14丨什么是事务处理，如何使用COMMIT和ROLLBACK进行操作？
事务的特性：ACID
事务的控制
MySQL，可以通过 SHOW ENGINES 命令来查看当前 MySQL 支持的存储引擎都有哪些，以及这些存储引擎是否支持事务。

16丨游标：当我们需要逐条处理数据时，该怎么做？

17丨如何使用Python操作MySQL？
18丨SQLAlchemy：如何使用Python ORM框架来操作MySQL？
19丨基础篇总结：如何理解查询优化、通配符以及存储过程？
21丨范式设计：数据表的范式有哪些，3NF指的是什么？
22丨反范式设计：3NF有什么不足，为什么有时候需要反范式设计？

