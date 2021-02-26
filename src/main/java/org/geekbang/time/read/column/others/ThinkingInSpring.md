第一章: Spring Framework总览
    1. 课前准备
    2. Spring 特性总览
    3. Spring 版本特性
    4. Spring 模块化设计
    5. Spring 对 Java 语言特性运用
    6. Spring 对 JDK API 实践
    7. Spring 对 Java EE API 整合
    8. Spring 编程模型
    9. Spring 核心价值

第二章：重新认识 IoC
    1. IoC 发展简介
    2. IoC 主要实现策略
    3. IoC 容器的职责
    4. IoC 容器的实现
    5. 传统 IoC 容器实现
    6. 轻量级 IoC 容器
    7. 依赖查找 VS. 依赖注入
    8. 构造器注入 VS. Setter 注入
    Spring 作为 IoC 容器有什么优势？
        典型的 IoC 管理，依赖查找和依赖注入
        AOP 抽象
        事务抽象
        事件机制
        SPI 扩展
        强大的第三方整合
        易测试性
        更好的面向对象
代码示例
    依赖查找
        按照类型查找单个对象
        按照类型查找集合对象-ListableBeanFactory
        通过注解查找对象-ListableBeanFactory
        实时查找
        延迟查找
    依赖注入
        依赖来源一：自定义 Bean
        依赖来源二：依赖注入（內建依赖）
        依赖来源三：容器內建 Bean-Environment
    容器
        BeanFactory作为容器
        AnnotationApplicationContext作为容器
    涉及到的类
        BeanFactory,ClassPathXmlApplicationContext,ListableBeanFactory
        ObjectFactory
        ApplicationContext
        Environment
        DefaultListableBeanFactory,XmlBeanDefinitionReader,
        RootBeanDefinition,GenericBeanDefinition,BeanDefinition,BeanDefinitionRegistry


第三章：Spring IoC 容器概述
    1. Spring IoC 依赖查找
    2. Spring IoC 依赖注入
    3. Spring IoC 依赖来源
    4. Spring IoC 配置元信息
        Bean 定义配置:XML,Properties文件,Java注解,Java API
        IoC 容器配置:XML,Java注解,Java API
        外部化属性配置:Java注解
    5. Spring IoC 容器
        BeanFactory 和 ApplicationContext 谁才是 Spring IoC 容器？
    6. Spring 应用上下文
        ApplicationContext 除了 IoC 容器角色，还有提供：
            面向切面（AOP）
            配置元信息（Configuration Metadata）
            资源管理（Resources）
            事件（Events）
            国际化（i18n）
            注解（Annotations）
            Environment 抽象（Environment Abstraction）
        BeanFactory 是 Spring 底层 IoC 容器
        ApplicationContext 是具备应用特性的 BeanFactory 超集
    7. 使用 Spring IoC 容器
    8. Spring IoC 容器生命周期

第四章：Spring Bean 基础
    1. 定义 Spring Bean
        什么是 BeanDefinition？
    2. BeanDefinition 元信息
        BeanDefinition 元信息有哪些?
        BeanDefinition 构建:
            通过 BeanDefinitionBuilder
            通过 AbstractBeanDefinition 以及派生类
    3. 命名 Spring Bean
        名称生成器（BeanNameGenerator）
    4. Spring Bean 的别名
    5. 注册 Spring Bean
        XML 配置元信息
        Java 注解配置元信息
        Java API 配置元信息
        外部单例对象注册
    6. 实例化 Spring Bean
        常规方式:通过构造器;通过静态工厂方法;通过 Bean 工厂方法;通过 FactoryBean
        特殊方式:通过 ServiceLoaderFactoryBean;通过 AutowireCapableBeanFactory#createBean;通过 BeanDefinitionRegistry#registerBeanDefinition
    7. 初始化 Spring Bean
        三种方式:@PostConstruct 标注方法;实现 InitializingBean 接口的 afterPropertiesSet() 方法;自定义初始化方法
        三种并存的情况下顺序是:@PostConstruct-afterPropertiesSet-自定义
    8. 延迟初始化 Spring Bean
        xml;java注解
    9. 销毁 Spring Bean
        三种方式:@PreDestroy 标注方法;实现 DisposableBean 接口的 destroy() 方法;自定义销毁方法
        三种并存的情况下顺序也是上面这样
    10. 垃圾回收 Spring Bean
        关闭 Spring 容器（应用上下文）;执行 GC;Spring Bean 覆盖的 finalize() 方法被回调
    涉及到的类
        BeanDefinition
        BeanDefinitionBuilder
        AbstractBeanDefinition
        MutablePropertyValues
        BeanNameGenerator
        DefaultBeanNameGenerator
        AnnotationBeanNameGenerator
        BeanDefinitionReaderUtils
        AnnotatedBeanDefinitionReader
        SingletonBeanRegistry
        AutowireCapableBeanFactory
        ServiceLoader
        ServiceLoaderFactoryBean
        @PostConstruct
        InitializingBean, DisposableBean

第五章：Spring IoC 依赖查找
1. 依赖查找的今世前生
2. 单一类型依赖查找
   单一类型依赖查找接口 - BeanFactory
        根据 Bean 名称查找;根据 Bean 类型查找;根据 Bean 名称 + 类型查找：getBean(String,Class)
3. 集合类型依赖查找
   集合类型依赖查找接口 - ListableBeanFactory
        根据 Bean 类型查找;通过注解类型查找
4. 层次性依赖查找
   层次性依赖查找接口 - HierarchicalBeanFactory
        双亲 BeanFactory：getParentBeanFactory();层次性查找
5. 延迟依赖查找
   ObjectFactory;ObjectProvider
6. 安全依赖查找
   依赖查找安全性对比
7. 内建可查找的依赖
   AbstractApplicationContext 内建可查找的依赖
   注解驱动 Spring 应用上下文内建可查找的依赖
8. 依赖查找中的经典异常
   BeansException 子类型



问题记录
ObjectFactory为什么是延迟查找
ObjectProvider
whoIsIoCContainer为什么不成立
BeanFactory 与 FactoryBean？
    BeanFactory 是 IoC 底层容器
    FactoryBean 是 创建 Bean 的一种方式，帮助实现复杂的初始化逻辑
初始化、实例化的概念，谁先谁后的问题
Bean实例化特殊部分ServiceLoaderFactoryBean









沙雕面试题 - 如何注册一个 Spring Bean？
答：通过 BeanDefinition 和外部单体对象来注册
996 面试题 - 什么是 Spring BeanDefinition？
答：回顾“定义 Spring Bean” 和 “BeanDefinition 元信息”
劝退面试题 - Spring 容器是怎样管理注册 Bean

沙雕面试题 - ObjectFactory 与 BeanFactory 的区别？
ObjectFactory 与 BeanFactory 均提供依赖查找的能力。
不过 ObjectFactory 仅关注一个或一种类型的 Bean 依赖查找，并且
自身不具备依赖查找的能力，能力则由 BeanFactory 输出。
BeanFactory 则提供了单一类型、集合类型以及层次性等多种依赖查
找方式。

996 面试题 - BeanFactory.getBean 操作是否线程安全？
答：BeanFactory.getBean 方法的执行是线程安全的，超过过程中会增加互
斥锁
劝退面试题 - Spring 依赖查找与注入在来源上的区别?