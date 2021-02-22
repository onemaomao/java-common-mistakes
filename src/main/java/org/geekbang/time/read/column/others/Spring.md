Spring中的编程思想
    OOP BOP AOP IOC DI/DL

Spring5模块结构图
    核心模块
        spring-core:依赖注入与DI的最基本实现
        spring-beans:Bean工厂与Bean的装配
        spring-context:定义基础的Spring的Context上下文即IOC容器
        spring-context-support:对Spring IOC容器的扩展支持，以及IOC子容器
        spring-context-indexer:Spring的类管理组件和Classpath扫描
        spring-expression:Spring表达式语言
    切面编程模块
        spring-aop:面向切面编程的应用模块，整合ASM，CGLIB，JDKProxy
        spring-aspects:集成AspectJ,AOP应用框架
        spring-instrument:动态Class Loading模块
    数据访问与集成模块
        spring-jdbc:Spring提供的JDBC抽象框架的主要实现模块，用于简化JDBC操作
        spring-tx:Spring JDBC事务控制实现模块
        spring-orm:主要集成Hibernate，Java Persistence API(JPA)和Java Data Objects(JDO)
        spring-oxm:将Java对象映射成XML数据，或者将XML数据映射成Java对象
        spring-jms:Java Messaging Service能够发送和接收信息
    Web模块
        spring-web:提供了最基础Web支持，主要建立于核心容器之上，通过Servlet或者Listeners来初始化IOC容器
        spring-webmvc:实现了Spring MVC的Web应用
        spring-websocket:主要是Web前端的全双工通讯的协议。
        spring-webflux:一个全新的非阻塞函数式Reactive Web框架，可以用来建立异步的，非阻塞，事件驱动的服务。
    通信报文模块
        spring-messaging:从Spring4开始加入的一个模块，主要职责是卫Spring框架集成一些基础的报文传送应用。
    集成测试模块
        spring-test:主要卫测试提供支持
    兼容模块
        spring-frame-bom:解决Spring的不同模块依赖版本不同问题
    各个模块之间的依赖关系图🌟

Spring注解编程基础组件
    配置组件 Configure Components
        @Configuration:
        @ComponentScan:可指定Filter
        @Scope:四种
        @Lazy:
        @Conditional:Spring4开始提供，满足条件才会注册Bean
        @Import:结合ImportSelector，ImportBeanDefinitionRegistrar
        生命周期控制:
            @PostConstruct
            @PreDestroy
            @DependsOn
    赋值组件 Injection Components
        @Component:
        @Service:
        @Controller:       
        @Repository:
        @Value:
        @Autowired:
        @PropertySource:读取配置文件
        @Qualifier:多实例配合
        @Primary:
        @Resource:默认按名称匹配，当找不到与名称匹配的bean才会按类型装配
    织入组件 Weave Components
        ApplicationContextAware:可以通过这个上下文环境对象得到Spring容器的Bean
        BeanDefinitionRegistryPostProcessor:该接口继承了BeanFactoryPostProcessor，是Spring框架的BeanDefinitionRegistry的后置处理器，用来注册额外的BeanDefinition
    切面组件 Aspect Components
        @EnableTransactionManagement:添加对事务管理的支持
        @Transactional:配置声名式事务信息



















