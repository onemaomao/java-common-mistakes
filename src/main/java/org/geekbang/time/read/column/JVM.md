gupao2019

前奏篇
01 官网
    官网 :https://docs.oracle.com/javase/8
    Reference -> Developer Guides -> 定位到:https://docs.oracle.com/javase/8/docs/index.html
02 源码到类文件
编译过程
    Person.java -> 词法分析器 -> tokens流 -> 语法分析器 
    -> 语法树/抽象语法树 -> 语义分析器-> 注解抽象语法树 -> 字节码生成器 -> Person.class文件
类文件(Class文件)
    官网The class File Format :https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html
    ClassFile
        魔数与class文件版本
        常量池
        访问标志
        类索引、父类索引、接口索引
        字段表集合
        方法表集合
        属性表集合
03 类文件到虚拟机(类加载机制)
3.1 装载(Load)
查找和导入class文件
    (1)通过一个类的全限定名获取定义此类的二进制字节流
    (2)将这个字节流所代表的静态存储结构转化为方法区的运行时数据结构
    (3)在Java堆中生成一个代表这个类的java.lang.Class对象，作为对方法区中这些数据的访问入
3.2 链接(Link)
    3.2.1 验证(Verify)
        保证被加载类的正确性
            文件格式验证
            元数据验证
            字节码验证
            符号引用验证
    3.2.2 准备(Prepare)
        为类的静态变量分配内存，并将其初始化为默认值
    3.2.3 解析(Resolve)
        把类中的符号引用转换为直接引用
    3.3 初始化(Initialize)
    3.4 类加载机制图解
04 类装载器ClassLoader
通过类的全限定名获取其定义的二进制字节流，需要借助类装载器完成，顾名思义，就是用来装载Class文件的.
4.1 分类
    1）Bootstrap ClassLoader负责加载$JAVA_HOME中jre/lib/rt.jar里所有的class或Xbootclassoath选项指定的jar包。由C++实现，不是ClassLoader子类。
    2）Extension ClassLoader负责加载java平台中扩展功能的一些jar包，包括$JAVA_HOME中jre/lib/*.jar 或 -Djava.ext.dirs指定目录下的jar包。
    3）App ClassLoader 负责加载classpath中指定的jar包及 Djava.class.path 所指定目录下的类和jar包。
    4）Custom ClassLoader通过java.lang.ClassLoader的子类自定义加载class，属于应用程序根据
4.2 图解
4.3 加载原则-简单点说自底向上检查,自顶向下加载
    检查某个类是否已经加载：顺序是自底向上，从Custom ClassLoader到BootStrap ClassLoader逐层检查，只要某个Classloader已加载，就视为已加载此类，保证此类只所有ClassLoader加载一次。
    加载的顺序：加载的顺序是自顶向下，也就是由上层来逐层尝试加载此类。
    双亲委派机制
        定义：
        优势：
        破坏：
05 运行时数据区(Run-Time Data Areas)
5.1 官网概括
    https://docs.oracle.com/javase/specs/jvms/se8/html/index.html
5.2 图解
    图很重要图很重要图很重要
5.3 常规理解
    每一步图很重要图很重要图很重要
    5.3.1 Method Area(方法区)
        方法区是各个线程共享的内存区域，在虚拟机启动时创建。
        用于存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。
        虽然Java虚拟机规范把方法区描述为堆的一个逻辑部分，但是它却又一个别名叫做Non-Heap(非堆)，目的是与Java堆区分开来。
        当方法区无法满足内存分配需求时，将抛出OutOfMemoryError异常。
        值得说明的
            (1)方法区在JDK 8中就是Metaspace，在JDK6或7中就是Perm Space
            (2)Run-Time Constant Pool
                Class文件中除了有类的版本、字段、方法、接口等描述信息外，还有一项信息就是常量池，用于存放编译时期生成的各种字面量和符号引用，这部分内容将在类加载后进入方法区的运行时常量池中存放。
    5.3.2 Heap(堆)
        Java堆是Java虚拟机所管理内存中最大的一块，在虚拟机启动时创建，被所有线程共享。
        Java对象实例以及数组都在堆上分配。
    5.3.3 Java Virtual Machine Stacks(虚拟机栈)
        虚拟机栈是一个线程执行的区域，保存着一个线程中方法的调用状态。
        换句话说，一个Java线程的运行状态，由一个虚拟机栈来保存，所以虚拟机栈肯定是线程私有的，独有的，随着线程的创建而创建。
        每一个被线程执行的方法，为该栈中的栈帧，即每个方法对应一个栈帧。
        调用一个方法，就会向栈中压入一个栈帧；一个方法调用完成，就会把该栈帧从栈中弹出。
    5.3.4 The pc Register(程序计数器)
        程序计数器占用的内存空间很小，由于Java虚拟机的多线程是通过线程轮流切换，并分配处理器执行时间的方式来实现的，在任意时刻，一个处理器只会执行一条线程中的指令。
        因此，为了线程切换后能够恢复到正确的执行位置，每条线程需要有一个独立的程序计数器(线程私有)。
        如果线程正在执行Java方法，则计数器记录的是正在执行的虚拟机字节码指令的地址；
        如果正在执行的是Native方法，则这个计数器为空。
    5.3.5 Native Method Stacks(本地方法栈)
        如果当前线程执行的方法是Native类型的，这些方法就会在本地方法栈中执行。
----

进行篇
01 结合字节码指令理解Java虚拟机栈和栈帧
    栈帧：每个栈帧对应一个被调用的方法，可以理解为一个方法的运行空间。
    每个栈帧中包括
        局部变量表(Local Variables)
            方法中定义的局部变量以及方法的参数存放在这张表中
            局部变量表中的变量不可直接使用，如需要使用的话，必须通过相关指令将其加载至操作数栈中作为操作数使用。
        操作数栈(Operand Stack)
            以压栈和出栈的方式存储操作数的
        指向运行时常量池的引用(A reference to the run-time constant pool)
            动态链接:每个栈帧都包含一个指向运行时常量池中该栈帧所属方法的引用，持有这个引用是为了支持方法调用过程中的动态连接(Dynamic Linking)。
        方法返回地址(Return Address)和附加信息。
            当一个方法开始执行后,只有两种方式可以退出，一种是遇到方法返回的字节码指令；
            一种是遇见异常，并且这个异常没有在方法体内得到处理。
02 折腾一下-不同指向的场景
各个场景的图很重要各个场景的图很重要各个场景的图很重要
2.1 栈指向堆
    在栈帧中有一个变量，类型为引用类型，比如Object obj=new Object()
2.2 方法区指向堆
    方法区中会存放静态变量，常量等数据。private static Object obj=new Object();
2.3 堆指向方法区
    方法区中会包含类的信息，堆中会有对象，那怎么知道对象是哪个类创建的呢？
    堆中new Object(),需要知道是由哪个类创建出来的
2.4 Java对象内存布局
    一个Java对象在内存中包括3个部分：对象头、实例数据和对齐填充
    对象头:
        Mark Word:
            一系列的标记位(哈希码、分代年龄、锁状态标志等)
            64位系统:8字节
        Class Pointer:
            指向对象对应的类元数据的内存地址
            64位系统:8字节
        Length:
            数组对象持有,数组长度
            4字节
    实例数据:
        包含了对象的所有成员变量,大小由各个类型变量决定
            boolean和byte:1字节
            short和char:2字节
            int和float:4字节
            long和dubbo:8字节
            reference:8字节(64位系统)
    对齐填充:
        为了保证对象的大小为8字节的整数倍
03 内存模型
3.1 图解
    一块是非堆区，一块是堆区。
    堆区分为两大块，一个是Old区，一个是Young区。
    Young区分为两大块，一个是Survivor区（S0+S1），一块是Eden区。 Eden:S0:S1=8:1:1
    S0和S1一样大，也可以叫From和To。
3.2 对象创建所在区域
    一般情况下，新创建的对象都会被分配到Eden区，一些特殊的大的对象会直接分配到Old区。
3.3 Survivor区详解
    Survivor区分为两块S0和S1，也可以叫做From和To。
    在同一个时间点上，S0和S1只能有一个区有数据，另外一个是空的。
3.4 Old区详解
    般Old区都是年龄比较大的对象，或者相对超过了某个阈值的对象。
    在Old区也会有GC的操作，Old区的GC我们称作为Major GC。
3.5 对象的一辈子理解
    看图看图看图
3.6 常见问题
    如何理解Minor/Major/Full GC
        Minor GC:新生代
        Major GC:老年代
        Full GC:新生代+老年代
    为什么需要Survivor区?只有Eden不行吗？
        如果没有Survivor区会……
        Survivor的存在意义,就是减少被送到老年代的对象,进而减少Full GC的发生,Survivor的预筛选保证,只有经历16次Minor GC还能在新生代中存活的对象,才会被送到老年代。
    为什么需要两个Survivor区？
        最大的好处就是解决了碎片化......
    新生代中Eden:S1:S2为什么是8:1:1？
        新生代中的可用内存：复制算法用来担保的内存为9：1
        可用内存中Eden：S1区为8：1
        即新生代中Eden:S1:S2 = 8：1：1
04 体验与验证
4.1 使用jvisualvm查看
4.1 堆内存溢出
    Exception in thread "http-nio-8080-exec-2" java.lang.OutOfMemoryError: GC overhead limit exceeded
4.2 方法区内存溢出
    java.lang.OutOfMemoryError: Metaspace
4.3 虚拟机栈
    java.lang.StackOverflowError
    -Xss128k
    线程栈的大小是个双刃剑，如果设置过小，可能会出现栈溢出，特别是在该线程内有递归、大的循环时出现溢出的可能性更大，
    如果该值设置过大，就有影响到创建栈的数量，如果是多线程的应用，就会出现内存溢出的错误。
----

升华篇
01 Garbage Collect(垃圾回收)
1.1 如何确定一个对象是垃圾？
    1.1.1 引用计数法
        如果AB相互持有引用，导致永远不能被回收
    1.1.2 可达性分析
        通过GC Root的对象，开始向下寻找，看某个对象是否可达
        能作为GC Root:类加载器、Thread、虚拟机栈的本地变量表、static成员、常量引用、本地方法栈的变量等。
1.2 垃圾收集算法
    1.2.1 标记-清除(Mark-Sweep)
        分标记-清除两步
        两步都耗时，效率不高
        产生大量内存碎片，可能导致大对象创建再次引发GC
    1.2.2 复制(Copying)
        内存分为两块区域，每次只使用其中一块。
        当其中一块内存使用完了，就将还存活的对象复制到另一块上面，然后把已使用过的内存空间一次释放掉
        空间利用率低
    1.2.3 标记-整理(Mark-Compact)
        标记之后不是对可回收对象进行清理，而是让存活对象都向一端移动，然后清理掉端边界以外的内存。
1.3 分代收集算法
    3种垃圾收集算法，那么在堆内存中到底用哪一个
    Young区：复制算法(对象在被分配之后，可能生命周期比较短，Young区复制效率比较高)
    Old区：标记清除或标记整理(Old区对象存活时间比较长，复制来复制去没必要，不如做个标记再清理)
1.4 垃圾收集器
1.4.1 Serial收集器
    单线程
    优点：简单高效，拥有很高的单线程收集效率 
    缺点：收集过程需要暂停所有线程 
    算法：复制算法 
    适用范围：新生代
    应用：Client模式下的默认新生代收集器
1.4.2 ParNew收集器
    器理解为Serial收集器的多线程版本
    优点：在多CPU时，比Serial效率高。
    缺点：收集过程暂停所有应用程序线程，单CPU时比Serial效率差。
    算法：复制算法
    适用范围：新生代
    应用：运行在Server模式下的虚拟机中首选的新生代收集器
1.4.3 Parallel Scavenge收集器
    新生代收集器，它也是使用复制算法的收集器，又是并行的多线程收集器，
    看上去和ParNew一样，但是Parallel Scanvenge更关注 系统的吞吐量 。
    吞吐量=运行用户代码的时间/(运行用户代码的时间+垃圾收集时间)
1.4.4 Serial Old收集器
    Serial收集器的老年代版本，也是一个单线程收集器，
    不同的是采用"标记-整理算法"，运行过程和Serial收集器一样。
1.4.5 Parallel Old收集器
    Parallel Scavenge收集器的老年代版本，使用多线程和"标记-整理算法"进行垃圾回收。
1.4.6 CMS收集器
    CMS(Concurrent Mark Sweep)收集器是一种以获取 最短回收停顿时间为目标的收集器。
    采用的是"标记-清除算法",整个过程分为4步(看图说话看图说话看图说话)
        (1)初始标记 CMS initial mark-标记GC Roots能关联到的对象 Stop The World-->速度很快
        (2)并发标记 CMS concurrent mark-进行GC Roots Tracing
        (3)重新标记 CMS remark-修改并发标记因用户程序变动的内容 Stop The World
        (4)并发清除 CMS concurrent sweep
    整个过程中，并发标记和并发清除，收集器线程可以与用户线程一起工作，总体上来说，CMS收集器的内存回收过程是与用户线程一起并发地执行的。
    优点：并发收集、低停顿 缺点：产生大量空间碎片、并发阶段会降低吞吐量。
1.4.7 G1收集器
G1特点
    并行与并发
    分代收集（仍然保留了分代的概念）
    空间整合（整体上属于“标记-整理”算法，不会导致空间碎片）
    可预测的停顿（比CMS更先进的地方在于能让使用者明确指定一个长度为M毫秒的时间片段内，消耗在垃圾收集 上的时间不得超过N毫秒）
    工作过程(看图说话看图说话看图说话)
        初始标记（Initial Marking）-标记一下GC Roots能够关联的对象，并且修改TAMS的值，需要暂停用户线程
        并发标记（Concurrent Marking）-从GC Roots进行可达性分析，找出存活的对象，与用户线程并发 执行
        最终标记（Final Marking）-修正在并发标记阶段因为用户程序的并发执行导致变动的数据，需暂停用户线程
        筛选回收（Live Data Counting and Evacuation）-对各个Region的回收价值和成本进行排序，根据 用户所期望的GC停顿时间制定回收计划
1.4.8 垃圾收集器分类
    串行收集器->Serial和Serial Old
        只能有一个垃圾回收线程执行，用户线程暂停。适用于内存比较小的嵌入式设备。
    并行收集器[吞吐量优先]->Parallel Scanvenge、Parallel Old
        多条垃圾收集线程并行工作，但此时用户线程仍然处于等待状态。适用于科学计算、后台处理等若交互场景 。
    并发收集器[停顿时间优先]->CMS、G1
        用户线程和垃圾收集线程同时执行(但并不一定是并行的，可能是交替执行的)，垃圾收集线程在执行的时候不会停顿用户线程的运行。 适用于相对时间有要求的场景，比如Web 。
1.4.9 理解吞吐量和停顿时间
    停顿时间->垃圾收集器 进行 垃圾回收终端应用执行响应的时间
    吞吐量->运行用户代码时间/(运行用户代码时间+垃圾收集时间)
    停顿时间越短就越适合需要和用户交互的程序，良好的响应速度能提升用户体验； 
    高吞吐量则可以高效地利用CPU时间，尽快完成程序的运算任务，主要适合在后台运算而不需要太多交互的任务。
1.4.10 如何选择合适的垃圾收集器
    https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/collectors.html#sthref28
    优先调整堆的大小让服务器自己来选择
    如果内存小于100M，使用串行收集器
    如果是单核，并且没有停顿时间要求并且没有停顿时间要求，使用串行或JVM自己选
    如果允许停顿时间超过1秒，选择并行或JVM自己选
    如果响应时间最重要，并且不能超过1秒，使用并发收集器
1.4.11 再次理解G1
    JDK 7开始使用，JDK 8非常成熟，JDK 9默认的垃圾收集器，适用于新老生代。
    判断是否需要使用G1收集器？
        （1）50%以上的堆被存活对象占用 
        （2）对象分配和晋升的速度变化非常大 
        （3）垃圾回收时间比较长
1.4.12 如何开启需要的垃圾收集器
    （1）串行 -XX：+UseSerialGC -XX：+UseSerialOldGC
    （2）并行(吞吐量优先)： -XX：+UseParallelGC -XX：+UseParallelOldGC
    （3）并发收集器(响应时间优先) -XX：+UseConcMarkSweepGC -XX：+UseG1GC
----

实战篇
1.1 JVM参数
1.1.1 标准参数
    -version -help -server -cp
1.1.2 -X参数
    -Xint 解释执行 
    -Xcomp 第一次使用就编译成本地代码 
    -Xmixed 混合模式，JVM自己来决定
1.1.3 -XX参数
使用得最多的参数类型
非标准化参数，相对不稳定，主要用于JVM调优和Debug
    a.Boolean类型
        格式：-XX:[+-]<name> +或-表示启用或者禁用name属性
        比如：-XX:+UseConcMarkSweepGC 表示启用CMS类型的垃圾回收器
    b.非Boolean类型
        格式：-XX<name>=<value>表示name属性的值是value
        比如：-XX:MaxGCPauseMillis=500
1.1.4 其他参数
    -Xms1000等价于-XX:InitialHeapSize=1000 
    -Xmx1000等价于-XX:MaxHeapSize=1000 
    -Xss100等价于-XX:ThreadStackSize=100
    所以这块也相当于是-XX类型的参数
1.1.5 查看参数
    java -XX:+PrintFlagsFinal -version > flags.txt
    值得注意的是"="表示默认值，":="表示被用户或JVM修改后的值
1.1.6 设置参数的方式
    开发工具中设置比如IDEA，eclipse
    运行jar包的时候:java -XX:+UseG1GC xxx.jar
    web容器比如tomcat，可以在脚本中的进行设置
    通过jinfo实时调整某个java进程的参数(参数只有被标记为manageable的flags可以被实时修改)
1.1.7 实践和单位换算
    1Byte(字节)=8bit(位) 
    1KB=1024Byte(字节) 
    1MB=1024KB 
    1GB=1024MB 
    1TB=1024GB
    (1)设置堆内存大小和参数打印
        -Xmx100M -Xms100M -XX:+PrintFlagsFinal
    (2)查询+PrintFlagsFinal的值
        :=true
    (3)查询堆内存大小MaxHeapSize
        := 104857600
    (4)换算
        104857600(Byte)/1024=102400(KB) 102400(KB)/1024=100(MB) 
    (5)结论
        104857600是字节单位
1.1.8 常用参数含义
    见文稿表格
1.2 常用命令
    1.2.1 jps
    查看java进程
1.2.2 jinfo
    (1)实时查看和调整JVM配置参数
    (2)查看
        jinfo -ﬂag name PID查看某个java进程的name属性的值
        jinfo -flag MaxHeapSize PID
        jinfo -flag UseG1GC PID
    (3)修改
    参数只有被标记为manageable的ﬂags可以被实时修改
        jinfo -ﬂag [+|-] PID
        jinfo -ﬂag = PID
1.2.3 jstat
    (1)查看虚拟机性能统计信息
    (2)查看类装载信息
    jstat -class PID 1000 
    查看某个java进程的类装载信息，每1000毫秒输出一次，共输出10次
    (3)查看垃圾收集信息
    jstat -gc PID 1000 10
1.2.4jstack
    (1)查看线程堆栈信息
    (2)用法
        jstack PID
    (3)排查死锁案例
1.2.5jmap
    (1)生成堆转储快照
    (2)打印出堆内存相关信息
        -XX:+PrintFlagsFinal -Xms300M -Xmx300M 
        jmap-heap PID
    (3)dump出堆内存相关信息
        jmap-dump:format=b,ﬁle=heap.hprofPID
1.3 常用工具
1.3.1jconsole
1.3.2jvisualvm
1.3.3Arthas
1.3.4MAT
1.3.4GC日志分析工具
-XX:+PrintGCDetails-XX:+PrintGCTimeStamps-XX:+PrintGCDateStamps -Xloggc:gc.log
    gceasy
    GCVIEWER
----

终结篇
1.1 重新认知JVM
    JVM的物理结构图
1.2 GC优化
1.2.1 垃圾收集发生的时机
GC是由JVM自动完成的，根据JVM系统环境而定，所以时机是不确定的。
当然，我们可以手动进行垃圾回收，比如调用System.gc()方法通知JVM进行一次垃圾回收，但是具体什么时刻运行也无法控制。
也就是说System.gc()只是通知要回收，什么时候回收由JVM决定。 但是不建议手动调用该方法，因为消耗的资源比较大。
一般以下几种情况会发生垃圾回收
    （1）当Eden区或者S区不够用了 
    （2）老年代空间不够用了 
    （3）方法区空间不够用了 
    （4）System.gc()
1.2.2 实验环境准备
1.2.3 GC日志文件
    1.2.3.1 Parallel GC日志
    1.2.3.2 CMS日志
    1.2.3.3 G1日志
1.2.4 GC日志文件分析工具
    1.2.4.1 gceasy
    1.2.4.2 GCViewer
1.2.5 G1调优与最佳指南
是否选用G1垃圾收集器的判断依据
https://docs.oracle.com/javase/8/docs/technotes/guides/vm/G1.html#use_cases
（1）50%以上的堆被存活对象占用 
（2）对象分配和晋升的速度变化非常大 
（3）垃圾回收时间比较长
思考: https://blogs.oracle.com/poonam/increased-heap-usage-with-g1-gc

1.2.5.1 调优
1.2.5.2 最佳指南

