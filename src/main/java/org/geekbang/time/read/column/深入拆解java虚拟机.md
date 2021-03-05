01 | Java代码是怎么运行的？
为什么 Java 要在虚拟机里运行？
Java 虚拟机具体是怎样运行 Java 字节码的？
    内存区域图
    解释执行,即时编译（Just-In-Time compilation，JIT）
    HotSpot 默认采用混合模式
Java 虚拟机的运行效率究竟是怎么样的？
    C1、C2 和 Graal
    C1 又叫做 Client 编译器
    C2 又叫做 Server 编译器
    从 Java 7 开始，HotSpot 默认采用分层编译的方式：热点方法首先会被 C1 编译，而后热点方法中的热点会进一步被 C2 编译。
总结与实践
    Java为何要在虚拟机中运行
    内存区域的五部分划分
    HotSpot混合执行的策略
    HotSpot的即时编译器

02 | Java的基本类型
Java 虚拟机的 boolean 类型
在 Java 虚拟机规范中，boolean 类型则被映射成 int 类型，“true”被映射为整数 1，而“false”被映射为整数 0。
Java 的基本类型
    布尔类型 boolean Z
    整数类型 byte B、short S、char C、int I、long J
    浮点类型 float F 和 double D
    了解下值域、默认值和虚拟机内部符号
    存储超出它们取值范围
    浮点类型中的正无穷及负无穷,NaN的比较(除了“!=”始终返回 true 之外，所有其他比较结果都会返回 false)
Java 基本类型的大小
    boolean、byte、char、short、int
        栈上占用的空间(局部)
        堆上占用的空间
    boolean 字段和 boolean 数组
    加载
        boolean、char加载伴随着零扩展。
        byte、short加载伴随着符号扩展。
总结与实践

03 | Java虚拟机是如何加载Java类的?
基本类型（primitive types）和引用类型（reference types）-类、接口、数组类和泛型参数
字节流

加载
    加载，是指查找字节流，并且据此创建类的过程。
        类加载器
            启动类加载器（boot class loader）,由 C++ 实现的，没有对应的 Java 对象
                java9之前，负责加载最为基础、最为重要的类，比如存放在 JRE 的 lib 目录下 jar 包中的类（以及由虚拟机参数 -Xbootclasspath 指定的类）
            类加载器都是 java.lang.ClassLoader 的子类，有对应的 Java 对象
            扩展类加载器（extension class loader）
                负责加载相对次要、但又通用的类，比如存放在 JRE 的 lib/ext 目录下 jar 包中的类（以及由系统变量 java.ext.dirs 指定的类）
            应用类加载器（application class loader）
                负责加载应用程序路径下的类，虚拟机参数 -cp/-classpath、系统变量 java.class.path 或环境变量 CLASSPATH 所指定的路径。）默认情况下，应用程序中包含的类便是由应用类加载器加载的。
        双亲委派模型
        Java 9 引入了模块系统后类加载器的略微修改
        在 Java 虚拟机中，类的唯一性是由类加载器实例以及类的全名一同确定的。
            即便是同一串字节流，经由不同的类加载器加载，也会得到两个不同的类。在大型应用中，我们往往借助这一特性，来运行同一个类的不同版本。
链接
    链接，是指将创建成的类合并至 Java 虚拟机中，使之能够执行的过程。它可分为验证、准备以及解析三个阶段。
初始化
    为标记为常量值的字段赋值，以及执行 < clinit > 方法的过程。Java 虚拟机会通过加锁来确保类的 < clinit > 方法仅被执行一次。
    类的初始化何时会被触发
总结与实践
    Java 虚拟机将字节流转化为 Java 类的过程:加载、链接以及初始化三大步骤

04 | JVM是如何执行方法调用的？（上）
重载与重写
    重载的方法在编译过程中即可完成识别。选取取重载方法过程共分为三个阶段
JVM 的静态绑定和动态绑定
    在某些文章中，重载也被称为静态绑定（static binding），或者编译时多态（compile-time polymorphism）；而重写则被称为动态绑定（dynamic binding）
    这个说法在 Java 虚拟机语境下并非完全正确。这是因为某个类中的重载方法可能被它的子类所重写，因此 Java 编译器会将所有对非私有实例方法的调用编译为需要动态绑定的类型。
Java 虚拟机怎么识别方法
    关键在于类名、方法名以及方法描述符（method descriptor）
        方法描述符，它是由方法的参数类型以及返回类型所构成。
        Java 虚拟机中关于方法重写的判定同样基于方法描述符。
    由于对重载方法的区分在编译阶段已经完成，我们可以认为 Java 虚拟机不存在重载这一概念。
    Java 字节码中与调用相关的指令共有五种:invokestatic,invokespecial,invokevirtual,invokeinterface,invokedynamic
调用指令的符号引用
    非接口符号引用查找流程
    接口符号引用查找流程
总结与实践

05 | JVM是如何执行方法调用的？（下）
1. 虚方法调用
    Java 里所有非私有实例方法调用都会被编译成 invokevirtual 指令，而接口方法调用都会被编译成 invokeinterface 指令。
    这两种指令，均属于 Java 虚拟机中的虚方法调用。
    Java 虚拟机中采取了一种用空间换取时间的策略来实现动态绑定。它为每个类生成一张方法表，用以快速定位目标方法。
2. 方法表
    类加载的准备阶段，它除了为静态字段分配内存之外，还会构造与该类相关联的方法表。
    这个数据结构，便是 Java 虚拟机实现动态绑定的关键所在。
    方法表本质上是一个数组，每个数组元素指向一个当前类及其祖先类中非私有的实例方法。
    方法调用指令中的符号引用会在执行之前解析成实际引用。对于静态绑定的方法调用而言，实际引用将指向具体的目标方法。对于动态绑定的方法调用而言，实际引用则是方法表的索引值（实际上并不仅是索引值）。
    即时编译还拥有另外两种性能更好的优化手段：内联缓存（inlining cache）和方法内联（method inlining）
3. 内联缓存
    单态（monomorphic）指的是仅有一种状态的情况。
    多态（polymorphic）指的是有限数量种状态的情况。二态（bimorphic）是多态的其中一种。
    超多态（megamorphic）指的是更多种状态的情况。通常我们用一个具体数值来区分多态和超多态。在这个数值之下，我们称之为多态。否则，我们称之为超多态。

06 | JVM是如何处理异常的？
捕获异常则涉及了如下三种代码块try catch finally
异常的基本概念
Java 虚拟机是如何捕获异常的？
    异常表
    Foo的示例?
Java 7 的 Supressed 异常以及语法糖
    try-with-resources
    catch (SomeException | OtherException e)
总结与实践





