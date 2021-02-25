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

Bean的生命周期
bean创建--初始化--销毁的过程
4种实现方式
    1）指定初始化和销毁方法
        通过@Bean指定init-method和destroy-method
    2）通过让Bean实现InitializingBean（定义初始化逻辑）DisposableBean（定义销毁逻辑）
    3）可以使用JSR250；
        @PostConstruct：在bean创建完成并且属性赋值完成；来执行初始化方法
        @PreDestroy：在容器销毁bean之前通知我们进行清理工作
    4）BeanPostProcessor【interface】：bean的后置处理器
        在bean初始化前后进行一些处理工作；
        postProcessBeforeInitialization:在初始化之前工作
        postProcessAfterInitialization:在初始化之后工作
BeanPostProcessor原理&源码分析 Debug
    包含两个方法
        BeanPostProcessor.postProcessBeforeInitialization
        BeanPostProcessor.postProcessAfterInitialization
    源码分析
        前面略去一堆代码......
            populateBean，给bean进行属性赋值
            doCreateBean
                调用initializeBean
                    调用applyBeanPostProcessorsBeforeInitialization
                    调用invokeInitMethods
                    调用applyBeanPostProcessorsAfterInitialization
    Spring底层对BeanPostProcessor 的使用
        ApplicationContextAwareProcessor对Aware的处理
            postProcessBeforeInitialization中调用了invokeAwareInterfaces
            这就是为什么我们实现ApplicationContextAware可以活得ApplicationContext的原因(Debug可以看出来)
        BeanValidationPostProcessor:校验
        InitDestroyAnnotationBeanPostProcessor:处理PreDestroy注解
        AutowiredAnnotationBeanPostProcessor:处理自动装配，对象创建完了之后自动注入属性
        总结:bean赋值，注入其他组件，@Autowired，生命周期注解功能，@Async,xxx 等等都是利用BeanPostProcessor
生命周期中不得不说的Aware
    实现该接口的bean会被Spring以回调的方式进行通知、告诉你某个阶段某件事情发生了。
    Aware接口也是为了能够感知到自身的一些属性。
        比如实现了ApplicationContextAware接口的类，能够获取到ApplicationContext。
        实现了BeanFactoryAware接口的类，能够获取到BeanFactory对象。
        BeanNameAware
        BeanFactoryAware
        ApplicationContextAware
        MessageSourceAware
        ApplicationEventPublisherAware
        ResourceLoaderAware

Profile讲解
    Spring为我们提供的可以根据当前环境，动态的激活和切换一系列组件的功能。
    比如开发环境、测试环境、生产环境的数据源：(/A)(/B)(/C)；
    @Profile：指定组件在哪个环境的情况下才能被注册到容器中，不指定，任何环境下都能注册这个组件
        加了环境标识的bean，只有这个环境被激活的时候才能注册到容器中。默认是default环境
        写在配置类上，只有是指定的环境的时候，整个配置类里面的所有配置才能开始生效
        没有标注环境标识的bean在，任何环境下都是加载的；
    如何激活动态参数:
        使用命令行动态参数: 在虚拟机参数位置加载 -Dspring.profiles.active=test
        代码的方式激活某种环境:applicationContext.getEnvironment().setActiveProfiles("dev");

自动装配讲解
    @Autowired
    @Qualifier
    @Primary
    @Resource(JSR250)
    @Inject(JSR330):需要导入javax.inject的包，和Autowired的功能一样。没有required=false的功能

AOP原理&源码
@EnableAspectJAutoProxy
给容器导入 @Import(AspectJAutoProxyRegistrar.class)
利用AspectJAutoProxyRegistrar自定义给容器中注册AnnotationAwareAspectJAutoProxyCreator
关注AnnotationAwareAspectJAutoProxyCreator的继承与实现关系
-->AspectJAwareAdvisorAutoProxyCreator
    -->AbstractAdvisorAutoProxyCreator
        -->AbstractAutoProxyCreator
            extends ProxyProcessorSupport
                implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryAware
自行debug会有很大的收获
    流程
    1)传入配置类，创建ioc容器
    2)注册配置类，调用refresh（）刷新容器
    3)registerBeanPostProcessors(beanFactory);注册bean的后置处理器来方便拦截bean的创建
        这里有一系列的子流程自行debug发觉
    4)finishBeanFactoryInitialization(beanFactory);完成BeanFactory初始化工作；创建剩下的单实例bean
    这里涉及两块重要的内容
        applyBeanPostProcessorsBeforeInstantiation
        applyBeanPostProcessorsAfterInitialization
AnnotationAwareAspectJAutoProxyCreator作用:
    它也是InstantiationAwareBeanPostProcessor
    1)每一个bean创建之前，调用postProcessBeforeInstantiation()
        略去一堆内容
    2)创建对象调用postProcessAfterInitialization()
        略去一堆内容
    3)目标方法执行
        略去一堆内容

总结:
1) @EnableAspectJAutoProxy 开启AOP功能
2) @EnableAspectJAutoProxy 会给容器中注册一个组件 AnnotationAwareAspectJAutoProxyCreator
3) AnnotationAwareAspectJAutoProxyCreator是一个后置处理器
4) 容器的创建流程：
   registerBeanPostProcessors（）注册后置处理器；创建AnnotationAwareAspectJAutoProxyCreator对象
   finishBeanFactoryInitialization（）初始化剩下的单实例bean
       创建业务逻辑组件和切面组件
       AnnotationAwareAspectJAutoProxyCreator拦截组件的创建过程
       组件创建完之后，判断组件是否需要增强
            是：切面的通知方法，包装成增强器（Advisor）;给业务逻辑组件创建一个代理对象（cglib）
5) 执行目标方法：
   代理对象执行目标方法
   CglibAopProxy.intercept()
       得到目标方法的拦截器链（增强器包装成拦截器MethodInterceptor）
       利用拦截器的链式机制，依次进入每一个拦截器进行执行
       效果：
           正常执行：前置通知-》目标方法-》后置通知-》返回通知
           出现异常：前置通知-》目标方法-》后置通知-》异常通知
   
声名式事务
    环境搭建步骤:
        1) 导入相关依赖:数据源、数据库驱动、Spring-jdbc模块
        2) 配置数据源、JdbcTemplate（Spring提供的简化数据库操作的工具）操作数据
        3) 给方法上标注 @Transactional 表示当前方法是一个事务方法
        4) @EnableTransactionManagement 开启基于注解的事务管理功能
        5) 配置事务管理器来控制事务
           @Bean
           public PlatformTransactionManager transactionManager(){......}
    源码&原理
        1) @EnableTransactionManagement做了什么?
           导入了TransactionManagementConfigurationSelector
           EnableTransactionManagement的默认AdviceMode是AdviceMode.PROXY
           根据逻辑它又会给容器导入AutoProxyRegistrar&ProxyTransactionManagementConfiguration
        2) AutoProxyRegistrar做了什么？
           它给容器中注册一个InfrastructureAdvisorAutoProxyCreator组件
           利用后置处理器机制在对象创建以后，包装对象，返回一个代理对象（增强器），代理对象执行方法利用拦截器链进行调用
           可以参见AOP的执行流程
        3）ProxyTransactionManagementConfiguration又做了什么？
           给容器中注册事务增强器:
           1) 事务增强器要用事务注解的信息，AnnotationTransactionAttributeSource解析事务注解
           2) 事务拦截器:
              TransactionInterceptor: 保存了事务属性信息，事务管理器
              这是一个 MethodInterceptor，在目标方法执行的时候会执行拦截器链，与AOP处的执行一样
              拦截器流程:
                      1)先获取事务相关的属性，AnnotationTransactionAttributeSource解析事务注解后传入
                      2) 再获取PlatformTransactionManager，如果事先没有添加指定任何transactionmanger，最终会从容器中按照类型获取一个PlatformTransactionManager
                      3）执行目标方法，见invoke处，最终会调用到TransactionAspectSupport.invokeWithinTransaction
                         如果异常，获取到事务管理器，利用事务管理回滚操作；
                         如果正常，利用事务管理器，提交事务
              TransactionAspectSupport.invokeWithinTransaction代码可以阅读查看

扩展原理&监听机制












