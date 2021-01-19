# Kafka核心源码阅读



## 开篇词 | 阅读源码，逐渐成了职业进阶道路上的“必选项”

### 为什么要读源码

**阅读源码正在从“加分项”向“必选项”转变，掌握优秀的框架代码实现从 NICE-TO-DO 变成了 MUST-DO**。

**1. 可以帮助你更深刻地理解内部设计原理，提升你的系统架构能力和代码功力。**

**2. 可以帮你快速定位问题并制定调优方案，减少解决问题的时间成本。**

**如果你对源码了然于心，你会很清楚线上环境的潜在问题，提前避“坑”。在解决问题时，阅读源码其实是事半功倍的“捷径”**。

**3. 你还能参加 Kafka 开源社区，成为一名代码贡献者（Contributor）。**

总而言之，**阅读源码的好处真的很多，既能精进代码功力，又能锤炼架构技巧，还能高效地解决实际问题，有百利而无一害。**

----

### 如何用最短的时间掌握最核心的源码？

**自上而下（Top-Down）**：从最顶层或最外层的代码一步步深入。通俗地说，就是从 main 函数开始阅读，逐渐向下层层深入，直到抵达最底层代码。这个方法的好处在于，你遍历的是完整的顶层功能路径，这对于你了解各个功能的整体流程极有帮助。

**自下而上（Bottom-Up）**：跟自上而下相反，是指先独立地阅读和搞懂每个组件的代码和实现机制，然后不断向上延展，并最终把它们组装起来。该方法不是沿着功能的维度向上溯源的，相反地，它更有助于你掌握底层的基础组件代码。

----

### kafka源码全景图



![image-20210119163538494](/Users/jiayin/Library/Application Support/typora-user-images/image-20210119163538494.png)

----

### 从功能上讲，Kafka 源码分为四大模块

服务器端源码：实现 Kafka 架构和各类优秀特性的基础。

Java 客户端源码：定义了与 Broker 端的交互机制，以及通用的 Broker 端组件支撑代码。

Connect 源码：用于实现 Kafka 与外部系统的高性能数据传输。

Streams 源码：用于实现实时的流处理功能。

服务器端源码是理解 Kafka 底层架构特别是系统运行原理的基础，其他三个模块的源码都对它有着强烈的依赖。因此，**Kafka 最最精华的代码，当属服务器端代码无疑！**我们学习这部分代码的性价比是最高的。

把服务器端源码按照功能划分了 7 个模块，每个模块会进一步划开多个子部分，详细地给出各个组件级的源码分析。

![image-20210119163923146](/Users/jiayin/Library/Application Support/typora-user-images/image-20210119163923146.png)



## 导读 | 构建Kafka工程和源码阅读环境、Scala语言热身

**环境准备**

Oracle，Java 8，Scala 2.12，IDEA + Scala，Git



### **构建 Kafka 工程**

### **Kafka 工程的各个目录以及文件**

**bin 目录**：保存 Kafka 工具行脚本，我们熟知的 kafka-server-start 和 kafka-console-producer 等脚本都存放在这里。

**clients 目录**：保存 Kafka 客户端代码，比如生产者和消费者的代码都在该目录下。

**config 目录**：保存 Kafka 的配置文件，其中比较重要的配置文件是 server.properties。

**connect 目录**：保存 Connect 组件的源代码。Kafka Connect 组件是用来实现 Kafka 与外部系统之间的实时数据传输的。

**core 目录**：保存 Broker 端代码。Kafka 服务器端代码全部保存在该目录下。

**streams 目录**：保存 Streams 组件的源代码。Kafka Streams 是实现 Kafka 实时流处理的组件。

----

### **打包出一个可运行的二进制环境**

**kafka-2.12-2.5.0-SNAPSHOT.tgz**。它是 Kafka 的 Broker 端发布包，把该文件解压之后就是标准的 Kafka 运行环境。该文件位于 core 路径的 /build/distributions 目录。

**kafka-clients-2.5.0-SNAPSHOT.jar**。该 Jar 包是 Clients 端代码编译打包之后的二进制发布包。该文件位于 clients 目录下的 /build/libs 目录。

**kafka-streams-2.5.0-SNAPSHOT.jar**。该 Jar 包是 Streams 端代码编译打包之后的二进制发布包。该文件位于 streams 目录下的 /build/libs 目录。

----

### Kafka core 包的代码架构

**controller** 包：保存了 Kafka 控制器（Controller）代码，而控制器组件是 Kafka 的核心组件，后面我们会针对这个包的代码进行详细分析。

**coordinator **包：保存了**消费者端的 GroupCoordinator 代码**和**用于事务的 TransactionCoordinator 代码**。对 coordinator 包进行分析，特别是对消费者端的 GroupCoordinator 代码进行分析，是我们弄明白 Broker 端协调者组件设计原理的关键。

**log** 包：保存了 Kafka 最核心的日志结构代码，包括日志、日志段、索引文件等。另外，该包下还封装了 Log Compaction 的实现机制，是非常重要的源码包。

**network** 包：封装了 Kafka 服务器端网络层的代码，特别是 SocketServer.scala 这个文件，是 Kafka 实现 Acceptor 模式的具体操作类，非常值得一读。

**server **包：顾名思义，它是 Kafka 的服务器端主代码，里面的类非常多，很多关键的 Kafka 组件都存放在这里，比如专栏后面要讲到的状态机、Purgatory 延时机制等。



**弄懂测试用例是帮助你快速了解 Kafka 组件的最有效的捷径之一**

如果时间允许的话，我建议你多读一读 Kafka 各个组件下的测试用例，它们通常都位于代码包的 src/test 目录下。拿 Kafka 日志源码 Log 来说，它对应的 LogTest.scala 测试文件就写得非常完备，里面多达几十个测试用例，涵盖了 Log 的方方面面，一定要读一下。

----

### Scala热身

