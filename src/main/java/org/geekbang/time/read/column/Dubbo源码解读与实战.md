
gupao2020

基础
为什么要用dubbo
    服务治理框架
    服务的监控
    服务的注册发现
    服务的通信
    服务的容错
    服务的负载均衡
Dubbo怎么去使用
dubbo支持的注册中心
----

高级
多协议支持
    如何新增rest支持
dubbo的负载均衡
    Random默认
    roundrobin轮询
    一致性哈希
    最小活跃度
    shortestrespose loadbalance
集群容错
    Failover(默认)
    失败自动重试重试其他服务器，失败自动切换
    failfast cluster
    快速失败
    failbak cluster
    失败自动恢复，记录失败请求，定时重发
    forking cluster
    并行调用多个服务结点，只要其中一个成功返回，那么就直接返回结果
    broadcast cluster
    广播调用。一个请求调用所有的服务提供者。只要其中一个结点报错，那么就认为这个请求失败。
dubbo泛化
服务降级
    mock
dubbo常见配置讲解
    启动时检查
    主机绑定的问题
    配置的优先级问题
        客户端优先，服务端次之
        方法层面配置优于接口层面的配置，接口层面的配置要由于全局配置
----

内核剖析
两张图非常重要
dubbo常见功能
    多序列化支持
    性能调优参数
    缓存文件
    qos运维平台
dubbo是如何实现的
dubbo的模块说明
    cluster-路由层，包含负载均衡、容错
    common-公共包
    compatible-适配历史版本中com.alibaba.dubbo
    config-加载配置，然后提供统一的对外配置的类
    config-center-动态配置中心，通过第三方来统一管理dubbo的配置
    container-容器 Main.main(args)启动dubbo
    filter-过滤
    metadata-元数据
    monitor-监控模块
    plugin-auth,qos
    registry-注册中心
    remoting-远程协议支持
    rpc-rpc-协议/通信的支持
    serialization-序列化支持
dubbo的服务发布链路
    基于xml
    基于annotation

源码分析
注册流程
注解
    注解 @DubboService
    注解扫描 @DubboComponentScan
引发思考
    扫描注解（解析注解）
    url驱动， url的组装
    注册到zookeeper（？）
    启动服务（根据url中配置的协议、端口去发布对应的服务） （？）

注解的解析流程
DubboComponentScan
DubboComponentScanRegistrar
    把那个bean注册到SpringIOC容器?
        registerBeanDefinitions->registerServiceAnnotationBeanPostProcessor
            把 ServiceAnnotationBeanPostProcessor注册进去
                它的父类是 ServiceClassPostProcessor
                bean装载完成之后，会触发下面这个方法.
                ServiceClassPostProcessor.postProcessBeanDefinitionRegistry
                这里其实最种重要的就是: registerBeans 和 registerServiceBeans
                registerBeans， 注册了 DubboBootstrapApplicationListener 这个bean会在spring 容器的上下文装载完成之后，触发监听
                registerServiceBeans，是把DubboService，还有com.alibaba....Service注解的bean注册进去
                在以上逻辑执行之前还注册了一个 DubboBootstrapApplicationListener
    DubboBootstrapApplicationListener 监听 ContextRefreshedEvent 和  ContextClosedEvent
    registerServiceBeans 会调用 registerServiceBean
        和服务有关的信息，实际上都在我们刚刚定义的@DubboService
        最终registry.registerBeanDefinition(beanName, serviceBeanDefinition);dubbo中提供的ServiceBean注入到Spring IOC容器
        org.apache.dubbo.config.spring.ServiceBean
        服务以什么协议发布
        服务的负载均衡策略
        服务的容错策略
        服务发布端口
ServiceBean的初始化阶段
构造器调用super
当ServiceBean初始化完成之后，会调用afterPropertiesSet
DubboBootstrapApplicationListener在ContextRefreshedEvent会调用dubboBootstrap.start()
代码流程非常清晰
    元数据/远程配置信息的初始化
    拼接url（）
    如果是dubbo协议，则启动netty server
    服务注册
exportServices 遍历所有dubbo服务，进行服务发布
    ->export->doExportUrls->doExportUrlsFor1Protocol
    doExportUrls  主要流程，根据开发者配置的协议列表，遍历协议列表逐项进行发布。
    doExportUrlsFor1Protocol 生成url 根据url中配置的协议类型，调用指定协议进行服务的发布 启动服务 注册服务












