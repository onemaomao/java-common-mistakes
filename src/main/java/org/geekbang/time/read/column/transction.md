## Spring Transaction实现原理
### 非事务处理方式
技术关键点:
- API `java.sql.Connection#setAutoCommit(true)`

通过设置Connection的自动提交为true，这样Connection在执行完update的SQL之后，就自动提交，代码示例如下：
```java
    /**
     * 非事务插入一条记录，插入后抛出异常，数据依然插入成功
     */
    public void doWithoutTransaction() {
        Connection connection = null;
        try {
            // 获取连接，设置自动提交为true，就是没有事务属性了
            connection = getConnection();
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(true);
    
            // 插入
            PreparedStatement preparedStatement =
                connection.prepareStatement("INSERT INTO test(id) VALUES (?)");
            preparedStatement.setInt(1，1);
            preparedStatement.execute();
            preparedStatement.close();
    
            // 重置原有autoCommit属性
            connection.setAutoCommit(autoCommit);
            
            throw new RuntimeException("模拟插入记录后失败，检查是否回滚");
        } catch (Exception e) {
    
        } finally {
            // 关闭连接
            closeConnection(connection);
        }
    }

```
### JDBC API 实现事务
技术关键点: 
- 手动提交API  `java.sql.Connection#setAutoCommit(false)`
- 使用同一个Connection执行SQL
- Commit API  `java.sql.Connection#commit()`

代码示例如下：
```java
    /**
     * 事务中插入两条记录
     */
    public void doWithTransaction() {
        Connection connection = null;
        try {
            // 获取连接，设置自动提交为false，保证多个sql使用同一个Connection
            connection = getConnection();
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            // 插入第一条记录
            PreparedStatement preparedStatement =
                connection.prepareStatement("INSERT INTO test(id) VALUES (?)");
            preparedStatement.setInt(1，1);
            preparedStatement.execute();
            preparedStatement.close();

            // 插入第二条记录
            preparedStatement =
                connection.prepareStatement("INSERT INTO test(id) VALUES (?)");
            preparedStatement.setInt(1，2);
            preparedStatement.execute();
            preparedStatement.close();

            // 提交事务
            connection.commit();

            // 重置原有autoCommit属性
            connection.setAutoCommit(autoCommit);
        } catch (Exception e) {
            // 失败回滚
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception ex) {
                //
            }
        } finally {
            // 关闭连接
            closeConnection(connection);
        }
    }

```
JDBC 事务API的缺点：
- API编程麻烦，处理的异常比较多
- 不能实现事务的传播特性

### Spring 实现事务管理
#### 事务配置
##### Spring boot 单数据源
只需要引入spring-boot-starter-jdbc的依赖，然后增加数据源的配置，通过自动装配来装配TransactionManager
DataSourceTransactionManagerAutoConfiguration 会自动创建DataSourceTransactionManager的Bean

```java
@Configuration
@ConditionalOnClass({ JdbcTemplate.class，PlatformTransactionManager.class })
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceTransactionManagerAutoConfiguration {

	@Configuration
	@ConditionalOnSingleCandidate(DataSource.class)
	static class DataSourceTransactionManagerConfiguration {
        
	    ...
	    
		@Bean
		@ConditionalOnMissingBean(PlatformTransactionManager.class)
		public DataSourceTransactionManager transactionManager(
				DataSourceProperties properties) {
			DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(
					this.dataSource);
			if (this.transactionManagerCustomizers != null) {
				this.transactionManagerCustomizers.customize(transactionManager);
			}
			return transactionManager;
		}
		...
	}
}
```

TransactionAutoConfiguration会装配一个TransactionTemplate，然后激活EnableTransactionManagement注解
```java
@Configuration
@ConditionalOnClass(PlatformTransactionManager.class)
@AutoConfigureAfter({ JtaAutoConfiguration.class，HibernateJpaAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		Neo4jDataAutoConfiguration.class })
@EnableConfigurationProperties(TransactionProperties.class)
public class TransactionAutoConfiguration {

	@Configuration
	@ConditionalOnSingleCandidate(PlatformTransactionManager.class)
	public static class TransactionTemplateConfiguration {

		private final PlatformTransactionManager transactionManager;

		public TransactionTemplateConfiguration(
				PlatformTransactionManager transactionManager) {
			this.transactionManager = transactionManager;
		}

		@Bean
		@ConditionalOnMissingBean
		public TransactionTemplate transactionTemplate() {
			return new TransactionTemplate(this.transactionManager);
		}

	}

	@Configuration
	@ConditionalOnBean(PlatformTransactionManager.class)
	@ConditionalOnMissingBean(AbstractTransactionManagementConfiguration.class)
	public static class EnableTransactionManagementConfiguration {

		@Configuration
		@EnableTransactionManagement(proxyTargetClass = false)
		@ConditionalOnProperty(prefix = "spring.aop"，name = "proxy-target-class",
				havingValue = "false"，matchIfMissing = false)
		public static class JdkDynamicAutoProxyConfiguration {

		}

		@Configuration
		@EnableTransactionManagement(proxyTargetClass = true)
		@ConditionalOnProperty(prefix = "spring.aop"，name = "proxy-target-class",
				havingValue = "true"，matchIfMissing = true)
		public static class CglibAutoProxyConfiguration {

		}
	}

}
```

EnableTransactionManagement注解用于Annotation注解驱动的事务管理
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TransactionManagementConfigurationSelector.class)
public @interface EnableTransactionManagement {
    ...
}
```

EnableTransactionManagement注解的Import注解TransactionManagementConfigurationSelector会导入ProxyTransactionManagementConfiguration配置
```java
public class TransactionManagementConfigurationSelector extends AdviceModeImportSelector<EnableTransactionManagement> {

	@Override
	protected String[] selectImports(AdviceMode adviceMode) {
		switch (adviceMode) {
			case PROXY:
				return new String[] {AutoProxyRegistrar.class.getName(),
						ProxyTransactionManagementConfiguration.class.getName()};
			case ASPECTJ:
				return new String[] {determineTransactionAspectClass()};
			default:
				return null;
		}
	}
}
```

ProxyTransactionManagementConfiguration配置会创建BeanFactoryTransactionAttributeSourceAdvisor，AnnotationTransactionAttributeSource和TransactionInterceptor；这三个bean是用于注解驱动的事务管理(基于@Transactional注解AOP
事务管理)
ProxyTransactionManagementConfiguration的父类AbstractTransactionManagementConfiguration还会创建一个TransactionalEventListenerFactory（@since 4.2）的Bean用于事务提交前，提交后，回滚后的一些回调处理

引入jdbc-starter依赖
```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```

##### Spring boot 多单数据源
参照multiple-datasource中关于事务的配置

##### Spring XML 事务配置
- 增加spring-tx依赖，一般引入spring-jdbc的依赖会间接依赖spring-tx
```xml
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-tx</artifactId>
    </dependency>
```

- xml配置文件中定义
```xml
    <bean name="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">    
        <property name="dataSource" ref="dataSource" />
    </bean> 
 
    <tx:annotation-driven transaction-manager="transactionManager" proxy-target-class="true" />
```

#### 编程式事务使用
用TransactionTemplate来简化编程式事务的使用
核心方法: org.springframework.transaction.support.TransactionTemplate#execute(TransactionCallback<T> action)

```java
    public <T> T execute(TransactionCallback<T> action) throws TransactionException {
        Assert.state(this.transactionManager != null，"No PlatformTransactionManager set");

        if (this.transactionManager instanceof CallbackPreferringPlatformTransactionManager) {
            return ((CallbackPreferringPlatformTransactionManager) this.transactionManager).execute(this，action);
        }
        else {
            // 获取TransactionStatus，这里会获取Connection，设置Connection的autoCommit为false
            TransactionStatus status = this.transactionManager.getTransaction(this);
            T result;
            try {
                // 执行SQL操作
                result = action.doInTransaction(status);
            }
            catch (RuntimeException | Error ex) {
                // Transactional code threw application exception -> rollback
                // RuntimeException或者Error回滚事务，并清理Connection的autoCommit值为初始值
                rollbackOnException(status，ex);
                throw ex;
            }
            catch (Throwable ex) {
                // Transactional code threw unexpected exception -> rollback
                // Unexpected回滚事务，并清理Connection的autoCommit值为初始值
                rollbackOnException(status，ex);
                throw new UndeclaredThrowableException(ex，"TransactionCallback threw undeclared checked exception");
            }
            
            // 没有异常则提交事务，并清理Connection的autoCommit值为初始值
            this.transactionManager.commit(status);
            return result;
        }
    }

```

通过查看TransactionTemplate的源代码发现，编程式事务使用起来也挺简单的，将我们业务SQL相关逻辑封装到TransactionCallback，然后调用execute方法执行即可；需要回滚，只需要在业务执行过程中抛出异常即可
代码示例:

```java
    /**
     * 提交事务
     */
    private void doWithTransactionCommit() {
        // 清空数据
        clearData();

        // 这里应该是0条记录
        System.out.println("Before transaction count is " + count());

        // 事务执行插入两条记录
        transactionTemplate.execute(transactionStatus -> {
            jdbcTemplate.update("INSERT INTO test(id) VALUES (?)"，1);
            jdbcTemplate.update("INSERT INTO test(id) VALUES (?)"，2);
            return null;
        });

        System.out.println("Transaction committed");

        // 这里应该是2，上面插入了2条记录
        System.out.println("After transaction count is " + count());
    }

    /**
     * 回滚事务
     */
    private void doWithTransactionRollback() {
        // 清空数据
        clearData();

        // 这里应该是0条记录
        System.out.println("Before transaction count is " + count());

        // 事务执行插入两条记录
        try {
            transactionTemplate.execute(transactionStatus -> {
                jdbcTemplate.update("INSERT INTO test(id) VALUES (?)"，1);
                jdbcTemplate.update("INSERT INTO test(id) VALUES (?)"，2);
                // 通过抛出异常来回滚事务
                throw new RuntimeException("模拟插入记录后失败，检查是否回滚");
            });
        } catch (Exception e) {
            System.out.println("Transaction rollback");
        }

        // 这里应该是0，上面事务执行过程中抛出了异常
        System.out.println("After transaction count is " + count());
    }

```
#### 申明式事务使用
通过@Transactional标注方法，来实现申明式事务处理
Transactional注解关键属性

- propagation
    - 传播属性，用于事务嵌套时事务处理，（事务方法中嵌套事务方法，嵌套事务方法到底该如何处理的问题）， 默认为Propagation.REQUIRED
- timeout
    - 定义本次事务执行的超时，用于控制事务执行时长，默认为-1，不限制，单位为秒
    - 实现原理是通过设置Statement的超时时间实现（Statement.setQueryTimeout），每次执行SQL都会去获取距离超时的剩余时间设置到Statement的QueryTime中
- rollbackFor
    - 定义回滚的异常基类Class类型，可以定义多个类型
    - 默认回滚类型为RunTimeException，这点一定要特别注意
- rollbackForClassName
    - 定义回滚的异常基类名，可以定义多个，和rollbackFor功能类似，只是这里是设置异常基类的全类名（String）
- noRollbackFor
    - 定义不回滚的异常基类Class类型，可以定义多个类型
- rollbackForClassName
    - 定义不回滚的异常基类名，可以定义多个，和rollbackFor功能类似，只是这里是设置异常基类的全类名（String）
    

**注: 在出现异常时，先用rollbackFor和rollbackForClassName定义的异常基类型判断是否匹配，如果匹配到就回滚，再判断是否匹配noRollbackFor和rollbackForClassName属性定义的基类类型，如果能够匹配到，就不回滚也就是进行提交**

通过在需要事务管理的**public**方法上注解@Transactional注解来实现申明式事务管理；通过将transactionTemplate中执行的代码块迁移到AnnotationTransactionService类的方法中，并标注@Transactional注解
代码示例:

```java
    @Component
    public class AnnotationTransactionService {
    
        @Autowired
        private JdbcTemplate jdbcTemplate;
    
        /**
         * 提交事务
         */
        @Transactional(rollbackFor = Throwable.class)
        public void transactionCommit() {
            jdbcTemplate.update("INSERT INTO test(id) VALUES (?)", 1);
            jdbcTemplate.update("INSERT INTO test(id) VALUES (?)", 2);
        }
    
        /**
         * 回滚事务
         */
        @Transactional(rollbackFor = Throwable.class)
        public void transactionRollback() {
            jdbcTemplate.update("INSERT INTO test(id) VALUES (?)", 1);
            jdbcTemplate.update("INSERT INTO test(id) VALUES (?)", 2);
            // 通过抛出异常来回滚事务
            throw new RuntimeException("模拟插入记录后失败, 检查是否回滚");
        }
    }
```

### Spring事务处理机制

有了上面这些使用过程之后，在来分析下Spring事务的实现机制和原理

#### 核心API抽象

![image-20201026175551208](images/transaction-api.png)

##### PlatformTransactionManager

Spring并不直接管理事务，而是提供了多种事务管理器，他们将事务管理的职责委托给Hibernate或者JTA等持久化机制所提供的相关平台框架的事务来实现。

Spring事务管理器的接口是org.springframework.transaction.PlatformTransactionManager，通过这个接口，Spring为各个平台如JDBC、Hibernate等都提供了对应的事务管理器，但是具体的实现就是各个平台自己的事情了

Spring事务管理的为不同的事务API提供一致的编程模型

##### TransactionStatus

事务状态，定义了事务一些属性

- 是否新事务
- 是否设置回滚
- 设置回滚

##### TransactionDefinition

事务属性定义

- 传播属性
- 是否只读
- 隔离规则
- 回滚规则
- 事务超时

#### 实现原理分析

##### 编程实现事务步骤

基于TransactionTemplate来分析下这些API是如何协同工作的

```java
	@Override
	@Nullable
	public <T> T execute(TransactionCallback<T> action) throws TransactionException {
		Assert.state(this.transactionManager != null, "No PlatformTransactionManager set");

		if (this.transactionManager instanceof CallbackPreferringPlatformTransactionManager) {
			return ((CallbackPreferringPlatformTransactionManager) this.transactionManager).execute(this, action);
		}
		else {
			TransactionStatus status = this.transactionManager.getTransaction(this);
			T result;
			try {
				result = action.doInTransaction(status);
			}
			catch (RuntimeException | Error ex) {
				// Transactional code threw application exception -> rollback
				rollbackOnException(status, ex);
				throw ex;
			}
			catch (Throwable ex) {
				// Transactional code threw unexpected exception -> rollback
				rollbackOnException(status, ex);
				throw new UndeclaredThrowableException(ex, "TransactionCallback threw undeclared checked exception");
			}
			this.transactionManager.commit(status);
			return result;
		}
	}
```

通过TransactionTemplate可以大致看出事务的编程实现步骤

- 通过TransactionManager传递TransactionDefinition获取TransactionStatus
- 业务处理
- 业务抛出异常回滚，否则执行提交

猜想：

- **猜想1**：为什么提交和回滚需要传递TransactionStatus，对比JDBC的Connection提交和回滚事务的api，难道TransactionStatus可以获取Connection
- **猜想2**：为什么业务TransactionCallback需要传递TransactionStatus，难道可以通过TransactionStatus来控制事务回滚

##### Commit分析

我们找到TransactionManager的直接实现类AbstractTransactionManager，查看下commit的实现逻辑

AbstractTransactionManager#commit()

```java
	@Override
	public final void commit(TransactionStatus status) throws TransactionException {
		if (status.isCompleted()) {
			throw new IllegalTransactionStateException(
					"Transaction is already completed - do not call commit or rollback more than once per transaction");
		}

        // 逻辑1. 通过defStatus.isLocalRollbackOnly()方法判断是否需要回滚
		DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
		if (defStatus.isLocalRollbackOnly()) {
			if (defStatus.isDebug()) {
				logger.debug("Transactional code has requested rollback");
			}
			processRollback(defStatus, false);
			return;
		}

        // 逻辑2. 通过shouldCommitOnGlobalRollbackOnly()和defStatus.isGlobalRollbackOnly()方法判断是需要回滚
		if (!shouldCommitOnGlobalRollbackOnly() && defStatus.isGlobalRollbackOnly()) {
			if (defStatus.isDebug()) {
				logger.debug("Global transaction is marked as rollback-only but transactional code requested commit");
			}
			processRollback(defStatus, true);
			return;
		}
		
        // 逻辑3. 回滚条件不满足，执行提交
		processCommit(defStatus);
	}
```

- 逻辑1

得到 TransactionStatus默认实现类为 DefaultTransactionStatus，DefaultTransactionStatus又继承了AbstractTransactionStatus这个抽象类。

isLocalRollbackOnly方法是不是和setRollbackOnly有关联，通过查看源代码发现，两个方法是相呼应的，这也印证了方法猜想2，在TransactionCallback#doInTransaction法中确实可以通过设置setRollbackOnly来实现回滚，因此TransactionCallback#doInTransaction方法可以有两种方式实现方法回滚，抛出异常和调用TransactionStatus#setRollbackOnly()方法

```java
// AbstractTransactionStatus#isLocalRollbackOnly方法

public boolean isLocalRollbackOnly() {
    return this.rollbackOnly;
}

@Override
public void setRollbackOnly() {
    this.rollbackOnly = true;
}
```

- 逻辑2

shouldCommitOnGlobalRollbackOnly()方法默认返回false，只有在JtaTransactionManager中返回true，我么这里只关系jdbc的事务，所以就只关心DefaultTransactionStatus#isGlobalRollbackOnly这个方法，从名字上看是全局回滚，结合isLocalRollbackOnly方法命名，应该可以理解为局部回滚，为什么会有全局和局部概念，是不是和我们背的很熟的事务传播属性有关系，因此引出另一个猜想。

**猜想3**：DefaultTransactionStatus#isGlobalRollbackOnly是判断全局事务回滚，DefaultTransactionStatus#isLocalRollbackOnly是判断局部事务回滚，这里的判断是不是和事务嵌套有关系。

- 逻辑3

处理提交processCommit方法

```java
private void processCommit(DefaultTransactionStatus status) throws TransactionException {
    try {
        boolean beforeCompletionInvoked = false;

        try {
            boolean unexpectedRollback = false;
            prepareForCommit(status);
            triggerBeforeCommit(status);
            triggerBeforeCompletion(status);
            beforeCompletionInvoked = true;

            if (status.hasSavepoint()) {
                if (status.isDebug()) {
                    logger.debug("Releasing transaction savepoint");
                }
                unexpectedRollback = status.isGlobalRollbackOnly();
                status.releaseHeldSavepoint();
            }
            else if (status.isNewTransaction()) {
                if (status.isDebug()) {
                    logger.debug("Initiating transaction commit");
                }
                unexpectedRollback = status.isGlobalRollbackOnly();
                doCommit(status);
            }
            else if (isFailEarlyOnGlobalRollbackOnly()) {
                unexpectedRollback = status.isGlobalRollbackOnly();
            }

            // Throw UnexpectedRollbackException if we have a global rollback-only
            // marker but still didn't get a corresponding exception from commit.
            if (unexpectedRollback) {
                throw new UnexpectedRollbackException(
                    "Transaction silently rolled back because it has been marked as rollback-only");
            }
        }
        catch (UnexpectedRollbackException ex) {
            // can only be caused by doCommit
            triggerAfterCompletion(status, TransactionSynchronization.STATUS_ROLLED_BACK);
            throw ex;
        }
        catch (TransactionException ex) {
            // can only be caused by doCommit
            if (isRollbackOnCommitFailure()) {
                doRollbackOnCommitException(status, ex);
            }
            else {
                triggerAfterCompletion(status, TransactionSynchronization.STATUS_UNKNOWN);
            }
            throw ex;
        }
        catch (RuntimeException | Error ex) {
            if (!beforeCompletionInvoked) {
                triggerBeforeCompletion(status);
            }
            doRollbackOnCommitException(status, ex);
            throw ex;
        }

        // Trigger afterCommit callbacks, with an exception thrown there
        // propagated to callers but the transaction still considered as committed.
        try {
            triggerAfterCommit(status);
        }
        finally {
            triggerAfterCompletion(status, TransactionSynchronization.STATUS_COMMITTED);
        }

    }
    finally {
        cleanupAfterCompletion(status);
    }
}
```

只有在status.isNewTransaction()为true时，才会执行doCommit，从方法的命名上理解，难道还有不存在非新事务，如果存在非新事务，怎么理解呢，是否也是和是事务传播有关系？

**猜想4**：DefaultTransactionStatus#isNewTransaction是否和事务传播有关系。

doCommit(status)方法的实现。AbstractTransactionManager的doCommit是一个抽象方法，具体是先由子类去执行，我们找JDBC的实现为DataSourceTransactionManager。因此看下DataSourceTransactionManager#doCommit方法实现

```java
@Override
protected void doCommit(DefaultTransactionStatus status) {
    DataSourceTransactionObject txObject = (DataSourceTransactionObject) status.getTransaction();
    Connection con = txObject.getConnectionHolder().getConnection();
    if (status.isDebug()) {
        logger.debug("Committing JDBC transaction on Connection [" + con + "]");
    }
    try {
        con.commit();
    }
    catch (SQLException ex) {
        throw new TransactionSystemException("Could not commit JDBC transaction", ex);
    }
}
```

找到经典提交代码con.commit()，果真可以通过DefaultTransactionStatus获取到Connection，这里也印证了猜想1，终于找到Connection.commit方法，至此事务提交过程分析完毕，这中间可能涉及到一些事务传播的疑问和猜想，还有DefaultTransactionStatus是如何关联Connection的部分还没解惑，后续慢慢分析。

##### Rollback分析

有了事务提交分析的基础，我们在来分析事务回滚，应该会相对简单一些。看下AbstractTransactionManager#rollback的实现逻辑。

```java
public final void rollback(TransactionStatus status) throws TransactionException {
    if (status.isCompleted()) {
        throw new IllegalTransactionStateException(
            "Transaction is already completed - do not call commit or rollback more than once per transaction");
    }

    DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
    processRollback(defStatus, false);
}
```

实现逻辑比较简单，就是调用了processRollback方法。再来看下processRollback方法的实现。

```java
private void processRollback(DefaultTransactionStatus status, boolean unexpected) {
   try {
      boolean unexpectedRollback = unexpected;

      try {
         triggerBeforeCompletion(status);

         if (status.hasSavepoint()) {
            if (status.isDebug()) {
               logger.debug("Rolling back transaction to savepoint");
            }
            status.rollbackToHeldSavepoint();
         }
         // 逻辑1，status.isNewTransaction()为true，执行doRollback
         else if (status.isNewTransaction()) {
            if (status.isDebug()) {
               logger.debug("Initiating transaction rollback");
            }
            doRollback(status);
         }
         // 逻辑2，status.isNewTransaction()为false，并且status.hasTransaction()true
         // status.isLocalRollbackOnly()或isGlobalRollbackOnParticipationFailure
         else {
            // Participating in larger transaction
            if (status.hasTransaction()) {
               if (status.isLocalRollbackOnly() || isGlobalRollbackOnParticipationFailure()) {
                  if (status.isDebug()) {
                     logger.debug("Participating transaction failed - marking existing transaction as rollback-only");
                  }
                  doSetRollbackOnly(status);
               }
               else {
                  if (status.isDebug()) {
                     logger.debug("Participating transaction failed - letting transaction originator decide on rollback");
                  }
               }
            }
            else {
               logger.debug("Should roll back transaction but cannot - no transaction available");
            }
            // Unexpected rollback only matters here if we're asked to fail early
            if (!isFailEarlyOnGlobalRollbackOnly()) {
               unexpectedRollback = false;
            }
         }
      }
      catch (RuntimeException | Error ex) {
         triggerAfterCompletion(status, TransactionSynchronization.STATUS_UNKNOWN);
         throw ex;
      }

      triggerAfterCompletion(status, TransactionSynchronization.STATUS_ROLLED_BACK);

      // Raise UnexpectedRollbackException if we had a global rollback-only marker
      if (unexpectedRollback) {
         throw new UnexpectedRollbackException(
               "Transaction rolled back because it has been marked as rollback-only");
      }
   }
   finally {
      cleanupAfterCompletion(status);
   }
}
```

- 逻辑1

只有在status.isNewTransaction()为true时，才会执行doRollback，这里的逻辑和commit类似，都是需要判断isNewTransaction为true，可能当前事务为内嵌事务时，isNewTransaction为false；先看doRollback的执行逻辑，应该是通过DefaultTransactionStatus获取Connection，然后执行connection.rollback方法，还是看下DataSourceTransactionManager#doRollback方法实现

```java
@Override
protected void doRollback(DefaultTransactionStatus status) {
    DataSourceTransactionObject txObject = (DataSourceTransactionObject) status.getTransaction();
    Connection con = txObject.getConnectionHolder().getConnection();
    if (status.isDebug()) {
        logger.debug("Rolling back JDBC transaction on Connection [" + con + "]");
    }
    try {
        con.rollback();
    }
    catch (SQLException ex) {
        throw new TransactionSystemException("Could not roll back JDBC transaction", ex);
    }
}
```

确实是获取到Connection后执行rollback方法

- 逻辑2

status.isLocalRollbackOnly() 前面猜想过应该是局部事务回滚，isGlobalRollbackOnParticipationFailure()方法，从命名上理解是在参与者失败时是否全局回滚，应该也是和事务传播嵌套有关系，globalRollbackOnParticipationFailure字段默认为true。

```java
public final boolean isGlobalRollbackOnParticipationFailure() {
   return this.globalRollbackOnParticipationFailure;
}
```

假定逻辑2是处理分支事务回滚的，在看看doSetRollbackOnly的处理逻辑，DataSourceTransactionManager#doSetRollbackOnly方法实现，最终调用DataSourceTransactionObject#setRollbackOnly()方法

```java
@Override
protected void doSetRollbackOnly(DefaultTransactionStatus status) {
   DataSourceTransactionObject txObject = (DataSourceTransactionObject) status.getTransaction();
   if (status.isDebug()) {
      logger.debug("Setting JDBC transaction [" + txObject.getConnectionHolder().getConnection() +
            "] rollback-only");
   }
   txObject.setRollbackOnly();
}
```

DataSourceTransactionObject#setRollbackOnly()方法，最终调用ConnectionHolder的setRollbackOnly方法

```java
public void setRollbackOnly() {
   getConnectionHolder().setRollbackOnly();
}
```

这里setRollbackOnly和DefaultTransactionStatus#isGlobalRollbackOnly和DefaultTransactionStatus#isLocalRollbackOnly有什么关联关系呢，查看DefaultTransactionStatus的代码发现DefaultTransactionStatus的isGlobalRollbackOnly就是使用DataSourceTransactionObject.isRollbackOnly()来判断的。

从一些代码分析来看，逻辑2要实现逻辑大致意思就是如果分支事务失败了， 并且分支事务失败时需要回滚全局事务参数为true，就调用DataSourceTransactionObject#setRollbackOnly()方法，用于后续DefaultTransactionStatus#isGlobalRollbackOnly()的判断， 在AbstractPlatformTransactionManager的commit方法中的逻辑2就是用DefaultTransactionStatus#isGlobalRollbackOnly()判断是否需要回滚的。

##### TransactionStatus构建分析

commit和rollback过程中大量涉及到TransactionStatus，并且通过TransactionStatus的isLocalRollbackOnly，isGlobalRollbackOnly，isNewTransaction，getConnectionHolder等方法在commit和rollback处理过程中起得很重要的作用，现在来分析下Spring 事务编程第一个步骤PlatformTransactionManager.getTransaction实现逻辑。

AbstractPlatformTransactionManager#getTransaction

```java
@Override
public final TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException {
   // 逻辑1 获取transactionObject
   // 这个由具体子类实现，DataSourceTransactionManager返回的DataSourceTransactionObject
   Object transaction = doGetTransaction();

   // Cache debug flag to avoid repeated checks.
   boolean debugEnabled = logger.isDebugEnabled();

   if (definition == null) {
      // Use defaults if no transaction definition given.
      definition = new DefaultTransactionDefinition();
   }

   // 逻辑2 检测是否存在事务，存在事务就进行事务传播处理
   if (isExistingTransaction(transaction)) {
      // Existing transaction found -> check propagation behavior to find out how to behave.
      return handleExistingTransaction(definition, transaction, debugEnabled);
   }

   // Check definition settings for new transaction.
   if (definition.getTimeout() < TransactionDefinition.TIMEOUT_DEFAULT) {
      throw new InvalidTimeoutException("Invalid transaction timeout", definition.getTimeout());
   }

   // No existing transaction found -> check propagation behavior to find out how to proceed.
   if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_MANDATORY) {
      throw new IllegalTransactionStateException(
            "No existing transaction found for transaction marked with propagation 'mandatory'");
   }
   // 逻辑3，当前执行环境下不存在事务处理，并且传播属性为PROPAGATION_REQUIRED，PROPAGATION_REQUIRES_NEW或PROPAGATION_NESTED，则需要开启事务
   else if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRED ||
         definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW ||
         definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NESTED) {
      SuspendedResourcesHolder suspendedResources = suspend(null);
      if (debugEnabled) {
         logger.debug("Creating new transaction with name [" + definition.getName() + "]: " + definition);
      }
      try {
         boolean newSynchronization = (getTransactionSynchronization() != SYNCHRONIZATION_NEVER);
         DefaultTransactionStatus status = newTransactionStatus(
               definition, transaction, true, newSynchronization, debugEnabled, suspendedResources);
         
         doBegin(transaction, definition);
         prepareSynchronization(status, definition);
         return status;
      }
      catch (RuntimeException | Error ex) {
         resume(null, suspendedResources);
         throw ex;
      }
   }
   // 其他传播属性，非事务方式运行
   else {
      // 非事务方式运行
      // Create "empty" transaction: no actual transaction, but potentially synchronization.
      if (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT && logger.isWarnEnabled()) {
         logger.warn("Custom isolation level specified but no actual transaction initiated; " +
               "isolation level will effectively be ignored: " + definition);
      }
      boolean newSynchronization = (getTransactionSynchronization() == SYNCHRONIZATION_ALWAYS);
      return prepareTransactionStatus(definition, null, true, newSynchronization, debugEnabled, null);
   }
}
```

- 逻辑1 

doGetTransaction做了两件事，创建DataSourceTransactionObject，通过TransactionSynchronizationManager获取当前数据源绑定的ConnectionHolder对象，设置到DataSourceTransactionObject中。

产生的DataSourceTransactionObject后续会设置到TransactionStatus对象中，用于后续的commit和rollback

```java
@Override
protected Object doGetTransaction() {
    // 创建DataSourceTransactionObject
    DataSourceTransactionObject txObject = new DataSourceTransactionObject();
    txObject.setSavepointAllowed(isNestedTransactionAllowed());
    // 这里是关键， 获取ConnectionHolder, 并设置到txObject中
    // 这里通过TransactionSynchronizationManager的getResource方法获取当前数据源绑定的ConnectionHolder
    // ConnectionHolder从字面上就可以看出是存储了Connection对象
    ConnectionHolder conHolder =
        (ConnectionHolder) TransactionSynchronizationManager.getResource(obtainDataSource());
    txObject.setConnectionHolder(conHolder, false);
    return txObject;
}

```

- 逻辑2

检测是否存在事务，存在事务就进行事务传播处理。何为存在事务中，就是当前执行位于事务包裹之中，通俗来讲，就是事务嵌套。对于编程式事务就是TransactionTemplate#execute(TransactionCallback<T> action)的action中又套了一层TransactionTemplate#execute(TransactionCallback<T> action)；

对于申明式事务就是注解了@Transactional的方法嵌套另一个类的注解@Transactional的方法，事务传播的后面单独分析。

```java
// 编程式事务嵌套
transactionTemplate#execute(transactionStatus -> {
    transactionTemplate#execute(transactionStatus -> {
        return null;
    });
	return null;
});

// 申明式事务嵌套
public class A {
    
    @Autowired
    private B b;
    
    @Transactional
    public void insert() {
        b.insert();
    }
}

public class B {
    
    @Transactional
    public void insert() {
        ...
    }
}

```

- 逻辑3

这里便是我们熟悉的传播属性的判断处理，PROPAGATION_REQUIRED，PROPAGATION_REQUIRES_NEW和PROPAGATION_NESTED，默认是PROPAGATION_REQUIRED。先做suspend(null)，一般来说进入到逻辑3这里，应该是不存在事务的， 进行suspend传入null，其实啥也不做。后面准备开始事务的逻辑，先创建DefaultTransactionStatus，在这里传入上面获取到的TransactionObject， 并且newTransaction设置为true，这里就和之前commit和rollback中status.isNewTransaction()逻辑相呼应起来，执行一次doBegin方法，就会真真执行后面的commit或rollback，接下来就是doBegin，做事务开始的处理。

```java
@Override
protected void doBegin(Object transaction, TransactionDefinition definition) {
    DataSourceTransactionObject txObject = (DataSourceTransactionObject) transaction;
    Connection con = null;

    try {
        if (!txObject.hasConnectionHolder() ||
            txObject.getConnectionHolder().isSynchronizedWithTransaction()) {
            // 获取Connection
            Connection newCon = obtainDataSource().getConnection();
            if (logger.isDebugEnabled()) {
                logger.debug("Acquired Connection [" + newCon + "] for JDBC transaction");
            }
            // 创建ConnectionHolder，并传入Connection
            txObject.setConnectionHolder(new ConnectionHolder(newCon), true);
        }

        // 设置ConnectionHolder的synchronizedWithTransaction属性为true
        txObject.getConnectionHolder().setSynchronizedWithTransaction(true);
        con = txObject.getConnectionHolder().getConnection();

        Integer previousIsolationLevel = DataSourceUtils.prepareConnectionForTransaction(con, definition);
        txObject.setPreviousIsolationLevel(previousIsolationLevel);

        // Switch to manual commit if necessary. This is very expensive in some JDBC drivers,
        // so we don't want to do it unnecessarily (for example if we've explicitly
        // configured the connection pool to set it already).
        
        // 设置Connection的自动提交为false
        if (con.getAutoCommit()) {
            txObject.setMustRestoreAutoCommit(true);
            if (logger.isDebugEnabled()) {
                logger.debug("Switching JDBC Connection [" + con + "] to manual commit");
            }
            con.setAutoCommit(false);
        }

        prepareTransactionalConnection(con, definition);

        // 设置ConnectionHolder的transactionActive为true，在判断是否存在事务中需要用到
        // 这里和isExistingTransaction(transaction)方法形成呼应
        txObject.getConnectionHolder().setTransactionActive(true);

        int timeout = determineTimeout(definition);
        if (timeout != TransactionDefinition.TIMEOUT_DEFAULT) {
            txObject.getConnectionHolder().setTimeoutInSeconds(timeout);
        }

        // Bind the connection holder to the thread.
        // 绑定ConnectionHolder，之前在创建DataSourceTransactionObject的时候获取过
        // 用于后续事务嵌套中获取
        // 这里是基于ThreadLocal机制来实现，所有的事务嵌套如果存在跨线程的情况，也就不存在嵌套的说法
        if (txObject.isNewConnectionHolder()) {
            TransactionSynchronizationManager.bindResource(obtainDataSource(), txObject.getConnectionHolder());
        }
    }

    catch (Throwable ex) {
        if (txObject.isNewConnectionHolder()) {
            DataSourceUtils.releaseConnection(con, obtainDataSource());
            txObject.setConnectionHolder(null, false);
        }
        throw new CannotCreateTransactionException("Could not open JDBC Connection for transaction", ex);
    }
}
```

doBegin的处理逻辑，较简单， 就是去获取连接，然后将ConnectionHolder和Datasource做下绑定，便于后续根据Datasource获取到ConnectionHolder，然后设置Connection的自动提交为false，设置ConnectionHolder的transactionActive为true。

doBegin的处理逻辑会关联到很多方法逻辑

- 获取Connection，用于后续的AbstractTransactionManager的commit和rollback

- 设置Connection的自动提交为false，开启事务的关键
- 设置ConnectionHolder的transactionActive为true，这里和DataSourceTransactionManager#isExistingTransaction方法形成呼应，在判断是否存在事务时会用到transactionActive属性
- 绑定ConnectionHolder，用于doGetTransaction()中获取connectionHolder

总结：

- 先创建DataSourceTransactionObject，并获取当前绑定的ConnectionHolder，设置到DataSourceTransactionObject属性中
- 如果当前存在事务（判断DataSourceTransactionObject关联的ConnectionHolder的transactionActive属性是否为true），进行事务传播处理
- 如果当前不存在事务，传播属性为PROPAGATION_REQUIRED，PROPAGATION_REQUIRES_NEW，PROPAGATION_NESTED时，创建DefaultTransactionStatus，设置newTranasction为true
- 获取Connection，设置自动提交为false，并设置ConnectionHolder的transactionActive为true，最后将ConnectionHolder进行绑定。

##### 事务传播分析

接下来我们分析下事务传播，这里主要针对事务传播时嵌套事务的传播属性做判断，然后根据不同的事务传播属性进行处理。

- TransactionDefinition.PROPAGATION_NEVER

  - 当前存在事务时，抛出异常

- TransactionDefinition.PROPAGATION_NOT_SUPPORTED

  - 当前存在事务时，挂起当前事务（将当前事务现场属性保存SuspendedResourcesHolder中，清空当前事务属性信息，然后将SuspendedResourcesHolder关联到自子事务的TransactionStatus中，用于后续的resume操作）。

  - 产生子事务的TransactionStatus，这里子事务的TransactionStatus是不包含TransactionObject的，因此

    hasTransaction方法和isNewTransaction方法都是返回false， 不会执行TransactionManager的commit和rollback逻辑。

  - 由于挂起了当前事务，所以无法从TransactionSynchronizationManager中获取ConnectionHolder，一次在执行sql的时候，都是获取新连接，然后自动执行提交，这就是非事务运行的最关键的地方。

- TransactionDefinition.PROPAGATION_REQUIRES_NEW

  - 当前存在事务时，挂起当前事务
  - 产生子事务的TransactionStatus，父事务的SuspendedResourcesHolder关联到自子事务的TransactionStatus中
  - 再次执行doBegin方法，获取Connection,  并绑定ConnectionHolder

- TransactionDefinition.PROPAGATION_NESTED
  - 当前存在事务时，挂起当前事务
  - 如果支持JDBC 3.0 Savepoint的机制的话，创建TransactionStatus，newTransaction为false，在进行commit或者rollback的时候不是直接用Connection进行commit和rollback的， 而是进行savePoint的操作，即便时回滚，也是回滚到指定的SavePoint
  - 如果useSavepointForNestedTransaction方法为false，则按照TransactionDefinition.PROPAGATION_REQUIRES_NEW的方式进行处理
- 其他传播属性
  
  - 没有执行过挂起事务的操作，就不会从TransactionSynchronizationManager中清理ConnectionHolder，从而复用Connection，来达到加入到当前事务中效果

```java
private TransactionStatus handleExistingTransaction(
    TransactionDefinition definition, Object transaction, boolean debugEnabled)
    throws TransactionException {
	
    // 存在事务中，并且嵌套事务的传播属性为PROPAGATION_NEVER，则抛出异常
    if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NEVER) {
        throw new IllegalTransactionStateException(
            "Existing transaction found for transaction marked with propagation 'never'");
    }

    // 存在事务中，并且嵌套事务的传播属性为PROPAGATION_NOT_SUPPORTED，则挂起当前事务，以非事务方式运行
    if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NOT_SUPPORTED) {
        if (debugEnabled) {
            logger.debug("Suspending current transaction");
        }
        // 当前事务的挂起
        Object suspendedResources = suspend(transaction);
        boolean newSynchronization = (getTransactionSynchronization() == SYNCHRONIZATION_ALWAYS);
        // 非事务生成TransactionStatus
        return prepareTransactionStatus(
            definition, null, false, newSynchronization, debugEnabled, suspendedResources);
    }
	
    // 当前存在事务，并且嵌套事务传播属性为PROPAGATION_REQUIRES_NEW，则挂起当前事务，开启新事务
    if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW) {
        if (debugEnabled) {
            logger.debug("Suspending current transaction, creating new transaction with name [" +
                         definition.getName() + "]");
        }
        // 挂起当前事务
        SuspendedResourcesHolder suspendedResources = suspend(transaction);
        
        // 开启新事务
        try {
            boolean newSynchronization = (getTransactionSynchronization() != SYNCHRONIZATION_NEVER);
            DefaultTransactionStatus status = newTransactionStatus(
                definition, transaction, true, newSynchronization, debugEnabled, suspendedResources
            
            // 又进入到doBgein，重新获取Connection，绑定ConnectionHolder
            doBegin(transaction, definition);
            prepareSynchronization(status, definition);
            return status;
        }
        catch (RuntimeException | Error beginEx) {
            resumeAfterBeginException(transaction, suspendedResources, beginEx);
            throw beginEx;
        }
    }

    // 当前存在事务，并且嵌套事务传播属性为PROPAGATION_NESTED
    // 如果是JDBC事务的话，使用的是savepoint的机制实现
    if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NESTED) {
        if (!isNestedTransactionAllowed()) {
            throw new NestedTransactionNotSupportedException(
                "Transaction manager does not allow nested transactions by default - " +
                "specify 'nestedTransactionAllowed' property with value 'true'");
        }
        if (debugEnabled) {
            logger.debug("Creating nested transaction with name [" + definition.getName() + "]");
        }
        if (useSavepointForNestedTransaction()) {
            // Create savepoint within existing Spring-managed transaction,
            // through the SavepointManager API implemented by TransactionStatus.
            // Usually uses JDBC 3.0 savepoints. Never activates Spring synchronization.
            DefaultTransactionStatus status =
                prepareTransactionStatus(definition, transaction, false, false, debugEnabled, null);
            status.createAndHoldSavepoint();
            return status;
        }
        else {
            // Nested transaction through nested begin and commit/rollback calls.
            // Usually only for JTA: Spring synchronization might get activated here
            // in case of a pre-existing JTA transaction.
            boolean newSynchronization = (getTransactionSynchronization() != SYNCHRONIZATION_NEVER);
            DefaultTransactionStatus status = newTransactionStatus(
                definition, transaction, true, newSynchronization, debugEnabled, null);
            doBegin(transaction, definition);
            prepareSynchronization(status, definition);
            return status;
        }
    }

    // Assumably PROPAGATION_SUPPORTS or PROPAGATION_REQUIRED.
    if (debugEnabled) {
        logger.debug("Participating in existing transaction");
    }
    if (isValidateExistingTransaction()) {
        if (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
            Integer currentIsolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
            if (currentIsolationLevel == null || currentIsolationLevel != definition.getIsolationLevel()) {
                Constants isoConstants = DefaultTransactionDefinition.constants;
                throw new IllegalTransactionStateException("Participating transaction with definition [" +
                                                           definition + "] specifies isolation level which is incompatible with existing transaction: " +
                                                           (currentIsolationLevel != null ?
                                                            isoConstants.toCode(currentIsolationLevel, DefaultTransactionDefinition.PREFIX_ISOLATION) :
                                                            "(unknown)"));
            }
        }
        if (!definition.isReadOnly()) {
            if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
                throw new IllegalTransactionStateException("Participating transaction with definition [" +
                                                           definition + "] is not marked as read-only but existing transaction is");
            }
        }
    }
    
    // 其他情况的传播属性, PROPAGATION_REQUIRED处理
    // 直接复用存在事务的ConnectionHolder，从而复用Connection
    boolean newSynchronization = (getTransactionSynchronization() != SYNCHRONIZATION_NEVER);
    return prepareTransactionStatus(definition, transaction, false, newSynchronization, debugEnabled, null);
}
```

在进行事务传播处理并创建TransactionStatus时，只有PROPAGATION_REQUIRES_NEW和PROPAGATION_NESTED（非savepoint机制下）这两种事务传播属性下才会设置TransactionStatus的newTransaction属性为true，而且都会执行 doBegin方法去获取Connection，因此可以总结只有这两种传播属性下的事务嵌套会创建新的连接，然后事务也是独立提交和回滚的。

##### 事务挂起分析

在进行事务传播时，如果当前存在事务，并且子事务的传播属性为PROPAGATION_NOT_SUPPORTED，PROPAGATION_REQUIRES_NEW，PROPAGATION_NESTED时会对当前事务进行挂起，也就是执行suspend(transaction)方法，到底挂起的是啥，为什么挂起了，后续就没有事务了？

```java
@Nullable
protected final SuspendedResourcesHolder suspend(@Nullable Object transaction) throws TransactionException {
    // synchronizationActive为true时处理，获取所有当前事务绑定的TransactionSynchronization
    // 然后挨个执行TransactionSynchronization的suspend方法
    // TransactionSynchronization其实是事务回调的一个接口，用于执行事务挂起，回复，提交前，提交后的一系列回调处理，后面单独讲这个知识
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
        // 获取当前线程绑定的TransactionSynchronization，挨个执行suspend方法，并从当前线程中清空
        List<TransactionSynchronization> suspendedSynchronizations = doSuspendSynchronization();
        try {
            // 返回当前线程绑定的ConnectionHolder，并清空当前线程绑定的ConnectionHolder
            Object suspendedResources = null;
            if (transaction != null) {
                suspendedResources = doSuspend(transaction);
            }
            // 下面都是获取当前线程绑定的属性，并清空相关属性
            String name = TransactionSynchronizationManager.getCurrentTransactionName();
            TransactionSynchronizationManager.setCurrentTransactionName(null);
            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            TransactionSynchronizationManager.setCurrentTransactionReadOnly(false);
            Integer isolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
            TransactionSynchronizationManager.setCurrentTransactionIsolationLevel(null);
            boolean wasActive = TransactionSynchronizationManager.isActualTransactionActive();
            TransactionSynchronizationManager.setActualTransactionActive(false);
            
            // 将当前线程绑定的属性构建SuspendedResourcesHolder对象
            return new SuspendedResourcesHolder(
                suspendedResources, suspendedSynchronizations, name, readOnly, isolationLevel, wasActive);
        }
        catch (RuntimeException | Error ex) {
            // doSuspend failed - original transaction is still active...
            doResumeSynchronization(suspendedSynchronizations);
            throw ex;
        }
    }
    // 如果当前线程没有设置synchronization，返回当前线程绑定的ConnectionHolder，并清空当前线程绑定的ConnectionHolder，使用ConnectionHolder构建SuspendedResourcesHolder对象
    else if (transaction != null) {
        // Transaction active but no synchronization active.
        Object suspendedResources = doSuspend(transaction);
        return new SuspendedResourcesHolder(suspendedResources);
    }
    else {
        // Neither transaction nor synchronization active.
        return null;
    }
}
```

挂起涉及到的操作并不复杂，就是获取当前事务绑定到当前线程的一系列属性，通过这些属性构建SuspendedResourcesHolder，并将SuspendedResourcesHolder对象关联到子事务的TransactionStatus上，用于子事务执行完后的恢复。其中最关键就是将ConnectionHolder从当前事务绑定中移除，并关联到SuspendedResourcesHolder中。在TransactionStatus构建分析中，doBegin()方法中会获取新的Connection，然后重新将ConnectionHolder绑定到TransactionSynchronizationManager中。

再看看下TransactionSynchronizationManager的内部结构

```java
public abstract class TransactionSynchronizationManager {
	
    // 多个ThreadLocal的用于保存当前事务的属性，由于是ThreadLocal实现，因此事务是不能跨线程传播的
    // 跨线程时，DataSourceTransactionManager#doGetTransactiondoGetTransaction()方法中获取ConnectionHolder一定是为null的
    // 后续DataSourceTransactionManager#isExistingTransaction()方法返回false，因此是不会进行事务传播处理的
	private static final ThreadLocal<Map<Object, Object>> resources =
			new NamedThreadLocal<>("Transactional resources");

	private static final ThreadLocal<Set<TransactionSynchronization>> synchronizations =
			new NamedThreadLocal<>("Transaction synchronizations");

	private static final ThreadLocal<String> currentTransactionName =
			new NamedThreadLocal<>("Current transaction name");

	private static final ThreadLocal<Boolean> currentTransactionReadOnly =
			new NamedThreadLocal<>("Current transaction read-only status");

	private static final ThreadLocal<Integer> currentTransactionIsolationLevel =
			new NamedThreadLocal<>("Current transaction isolation level");

	private static final ThreadLocal<Boolean> actualTransactionActive =
			new NamedThreadLocal<>("Actual transaction active");
    
    ...
}
```

TransactionSynchronizationManager内部的很多ThreadLocal的结构用于保存事务的属性，由于ThreadLocal线程隔离的特性，因此事务的传播在跨线程是一定是失效的，事务的挂起的过程中，会从TransactionSynchronizationManager内部的多个ThreadLocal中获取当前事务的属性，然后从ThreadLocal中清空，后续需要创建子事务时，进行重新设置，在doBegin方法中设置了ConnectionHolder，在prepareSynchronization方法中如果新开启了事务，会重新设置其他属性到ThreadLocal中。

##### 事务恢复分析

事务挂起会将挂起事务的属性设置到SuspendedResourcesHolder对象中，然后和子事务的TransactionStatus进行关联，子事务的TransactionStatus能够获取挂起事务的SuspendedResourcesHolder，用于后续的恢复，那么子事务执行完后，在哪里进行恢复挂起事务呢？答案肯定是在commit或者rollback逻辑执行完之后。回去在看看processCommit和processRollback方法的最后都有一段cleanupAfterCompletion(status)方法逻辑，就是这里进行事务的恢复逻辑。

```java
private void cleanupAfterCompletion(DefaultTransactionStatus status) {
    status.setCompleted();
    // 从TransactionManager的ThreadLocal中清理事务相关属性
    if (status.isNewSynchronization()) {
        TransactionSynchronizationManager.clear();
    }
    
    // 如果开启过事务(获取过新的Connection)，清理TransactionObject相关属性
    // 具体包含
    // (1) 取消ConnectionHolder的绑定
    // (2) 恢复Connection的autoCommit属性为原始值
    // (3) 释放Connection
    if (status.isNewTransaction()) {
        doCleanupAfterCompletion(status.getTransaction());
    }
   
    // 如果存在挂起资源，调用resume恢复处理
    if (status.getSuspendedResources() != null) {
        if (status.isDebug()) {
            logger.debug("Resuming suspended transaction after completion of inner transaction");
        }
        Object transaction = (status.hasTransaction() ? status.getTransaction() : null);
        resume(transaction, (SuspendedResourcesHolder) status.getSuspendedResources());
    }
}
```

cleanupAfterCompletion方法内部逻辑比较简单，最主要就是释放资源和恢复挂起的事务，恢复挂起的事务其实就是和挂起的操作时对立的，挂起的时候是清空属性，恢复的时候应该是重新设置被挂起事务属性到TransactionManager的ThreadLocal中。

```java
protected final void resume(@Nullable Object transaction, @Nullable SuspendedResourcesHolder resourcesHolder)
      throws TransactionException {

   if (resourcesHolder != null) {
      // 结合挂起的逻辑，这里suspendedResources其实就是ConnectionHolder
      Object suspendedResources = resourcesHolder.suspendedResources;
      if (suspendedResources != null) {
         // 恢复，就是重新将ConnectionHolder绑定到TransactionManager的ThreadLocal中
         doResume(transaction, suspendedResources);
      }
      
      // 获取TransactionSynchronization，恢复挂起事务的属性，重新设置到TransactionManager的ThreadLocal中，并挨个执行TransactionSynchronization的resume方法
      List<TransactionSynchronization> suspendedSynchronizations = resourcesHolder.suspendedSynchronizations;
      if (suspendedSynchronizations != null) {
         TransactionSynchronizationManager.setActualTransactionActive(resourcesHolder.wasActive);
         TransactionSynchronizationManager.setCurrentTransactionIsolationLevel(resourcesHolder.isolationLevel);
         TransactionSynchronizationManager.setCurrentTransactionReadOnly(resourcesHolder.readOnly);
         TransactionSynchronizationManager.setCurrentTransactionName(resourcesHolder.name);
         doResumeSynchronization(suspendedSynchronizations);
      }
   }
}
```

恢复的过程主要就是重新绑定事务资源如ConnectionHolder，并从挂起资源中获取事务相关属性（actualTransactionActive，transactionIsolationLevel，transactionReadOnly，transactionName等属性），并进行重新设置到TransactionManager的ThreadLocal中，然后执行TransactionSynchronization的resume回调方法。

##### TransactionSynchronization事务回调分析

事务的挂起和恢复过程中都涉及到TransactionSynchronization的回调，在commit和rollback的逻辑中其实也涉及TransactionSynchronization的回调，只是在分析commit和rollback过程中，回调不是我们的关注重点。现在我们来分析下TransactionSynchronization到底能实现什么功能。

先来看下TransactionSynchronization的接口定义

```java
public interface TransactionSynchronization extends Flushable {

	/** Completion status in case of proper commit */
	int STATUS_COMMITTED = 0;

	/** Completion status in case of proper rollback */
	int STATUS_ROLLED_BACK = 1;

	/** Completion status in case of heuristic mixed completion or system errors */
	int STATUS_UNKNOWN = 2;


	/**
	 * Suspend this synchronization.
	 * Supposed to unbind resources from TransactionSynchronizationManager if managing any.
	 * @see TransactionSynchronizationManager#unbindResource
	 */
	default void suspend() {
	}

	/**
	 * Resume this synchronization.
	 * Supposed to rebind resources to TransactionSynchronizationManager if managing any.
	 * @see TransactionSynchronizationManager#bindResource
	 */
	default void resume() {
	}

	/**
	 * Flush the underlying session to the datastore, if applicable:
	 * for example, a Hibernate/JPA session.
	 * @see org.springframework.transaction.TransactionStatus#flush()
	 */
	@Override
	default void flush() {
	}

	/**
	 * Invoked before transaction commit (before "beforeCompletion").
	 * Can e.g. flush transactional O/R Mapping sessions to the database.
	 * <p>This callback does <i>not</i> mean that the transaction will actually be committed.
	 * A rollback decision can still occur after this method has been called. This callback
	 * is rather meant to perform work that's only relevant if a commit still has a chance
	 * to happen, such as flushing SQL statements to the database.
	 * <p>Note that exceptions will get propagated to the commit caller and cause a
	 * rollback of the transaction.
	 * @param readOnly whether the transaction is defined as read-only transaction
	 * @throws RuntimeException in case of errors; will be <b>propagated to the caller</b>
	 * (note: do not throw TransactionException subclasses here!)
	 * @see #beforeCompletion
	 */
	default void beforeCommit(boolean readOnly) {
	}

	/**
	 * Invoked before transaction commit/rollback.
	 * Can perform resource cleanup <i>before</i> transaction completion.
	 * <p>This method will be invoked after {@code beforeCommit}, even when
	 * {@code beforeCommit} threw an exception. This callback allows for
	 * closing resources before transaction completion, for any outcome.
	 * @throws RuntimeException in case of errors; will be <b>logged but not propagated</b>
	 * (note: do not throw TransactionException subclasses here!)
	 * @see #beforeCommit
	 * @see #afterCompletion
	 */
	default void beforeCompletion() {
	}

	/**
	 * Invoked after transaction commit. Can perform further operations right
	 * <i>after</i> the main transaction has <i>successfully</i> committed.
	 * <p>Can e.g. commit further operations that are supposed to follow on a successful
	 * commit of the main transaction, like confirmation messages or emails.
	 * <p><b>NOTE:</b> The transaction will have been committed already, but the
	 * transactional resources might still be active and accessible. As a consequence,
	 * any data access code triggered at this point will still "participate" in the
	 * original transaction, allowing to perform some cleanup (with no commit following
	 * anymore!), unless it explicitly declares that it needs to run in a separate
	 * transaction. Hence: <b>Use {@code PROPAGATION_REQUIRES_NEW} for any
	 * transactional operation that is called from here.</b>
	 * @throws RuntimeException in case of errors; will be <b>propagated to the caller</b>
	 * (note: do not throw TransactionException subclasses here!)
	 */
	default void afterCommit() {
	}

	/**
	 * Invoked after transaction commit/rollback.
	 * Can perform resource cleanup <i>after</i> transaction completion.
	 * <p><b>NOTE:</b> The transaction will have been committed or rolled back already,
	 * but the transactional resources might still be active and accessible. As a
	 * consequence, any data access code triggered at this point will still "participate"
	 * in the original transaction, allowing to perform some cleanup (with no commit
	 * following anymore!), unless it explicitly declares that it needs to run in a
	 * separate transaction. Hence: <b>Use {@code PROPAGATION_REQUIRES_NEW}
	 * for any transactional operation that is called from here.</b>
	 * @param status completion status according to the {@code STATUS_*} constants
	 * @throws RuntimeException in case of errors; will be <b>logged but not propagated</b>
	 * (note: do not throw TransactionException subclasses here!)
	 * @see #STATUS_COMMITTED
	 * @see #STATUS_ROLLED_BACK
	 * @see #STATUS_UNKNOWN
	 * @see #beforeCompletion
	 */
	default void afterCompletion(int status) {
	}
}
```

TransactionSynchronization接口中提供了挂起，恢复，提交前，事务完成前，提交后，事务完成后的一系列回调接口，挂起的恢复的回调，在前面分析事务挂起和恢复的时候已经分析过。提前前，提交后，事务完成前，事务完成后的回调处理是穿插到commit和rollback的处理逻辑中，有兴趣的可以去查看下这些方法的实现逻辑，方法在AbstractPlatformTransactionManager类中已trigger开头的方法。

在这些回调方法中， 用得最多的是afterCommit方法，应为这个方法是在确定事务提交成功后才执行。我们考虑一下这种场景，需要在业务成功之后发送一条通知消息，我们该怎么做呢？

```java
// 伪代码实现
@Transactional(rollbackFor = Throwable.class)
public void doService() {
    // 业务处理
    doBusiness();
    // 获取业务数据
    Object businessData = getBusinessData();
    // 发送通知
    sendNotification(businessData);
}
```

这种方式看似没有问题，先执行业务，然后获取业务数据，最后根据业务数据发送通知，而且发送通知在方法最后，如果发送通知失败了， 整个业务也回滚了，如果没有失败，就执行提交。这里其实潜在有一个问题，就是在执行sendNotification方法后，当前依然在处于事务中，事务还没真正提交，后面还需要执行PlatformTransactionManager的commit方法，如果commit方法执行失败了怎么办？虽然这种几率会很小，但是也不能100%保证不出现。一旦真的出现commit失败，业务处理产生数据会回滚掉，但是通知发送已经将业务处理后的数据发送出去了，发生这种场景就可能导致数据不一致，那么有没有什么机制保证在业务成功提交后发送呢？

一种方式将sendNotification方法从业务处理方法抽离出来，放到业务方法执行完成之后，这种方法需要增加额外的聚合类去聚合业务处理和通知的调用

```java
// 伪代码实现
public class BusinessService {
    @Transactional(rollbackFor = Throwable.class)
	public Object doService() {
	    // 业务处理
	    doBusiness();
	    // 获取业务数据
	    Object businessData = getBusinessData();
        return businessData
	}
}

doServiceAndSendNotification() {
    Object businessData = businessService.doService();
    notificationService.sendNotification(businessData);

}
```

那更好的方法实现就是使用TransactionSynchronization的afterCommit方法回调来执行，详见下面的伪代码实现

```java
// 伪代码实现
@Transactional(rollbackFor = Throwable.class)
public void doService() {
    // 业务处理
    doBusiness();
    // 获取业务数据
    Object businessData = getBusinessData();
    
    // 注册事务成功提交后的回调处理逻辑
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            public void afterCommit() {
                sendNotification(businessData);
            }
        })
    }
}

```

#### 注解申明式事务分析

前面详细的从代码层面分析了Spring事务的实现原理，以及编程式事务的实现步骤，现在我们在来分析下基于注解申明式事务的处理流程。

前面已经介绍过，使用事务时，我们需要做一些配置，spring boot下引入spring-boot-starter-jdbc会自动开启事务；xml的配置方式需要使用tx:annotation-driven命名空间来开启。无论是xml配置方式还是springboot自动装配的方式，本质都是一样的，都是需要基于@Transactional注解进行AOP拦截，然后通过事务API来进行处理。

前面进行事务的配置的时候，了解到EnableTransactionManagement注解会导入ProxyTransactionManagementConfiguration配置，而ProxyTransactionManagementConfiguration配置内部有BeanFactoryTransactionAttributeSourceAdvisor，TransactionInterceptor，AnnotationTransactionAttributeSource三个Bean的定义；这三个Bean就是实现AOP连接的关键。

AnnotationTransactionAttributeSource有两个作用

- 负责识别哪些方法需要进行事务代理处理

- 负责获取@Transactional注解上定义的属性，然后将这些属性转换成TransactionDefinition，然后参与到事务处理中

TransactionInterceptor

- 事务方法拦截处理，结合Transaction的API进行事务的处理。实现逻辑和编程式事务差不多，先通过TransactionManager传递TransactionDefinition获取TransactionStatus，然后执行业务逻辑方法，最后根据异常匹配来判断执行commit或者rollback方法

BeanFactoryTransactionAttributeSourceAdvisor

- 整合了TransactionInterceptor和AnnotationTransactionAttributeSource组成一个Advisor，用于Spring aop生成动态代理

这三个核心的类支撑了Spring申明式事务的逻辑处理流程。

### JdbcTemplate & Mybatis 使用Spring事务管理

前面介绍过事务的实现机制，现在结合JdbcTemplate和Mybatis来分析下是如何配合Spring事务管理来进行事务控制的。

#### JdbcTemplate

JdbcTemplate内部的几个核心方法就是execute方法，有几种重载形式，是针对于sql创建Statement和基于ResultSet处理结果集的，内部实现逻辑其实都是相似的，我们以JdbcTemplate#execute(StatementCallback<T>)方法为例，来分析。

```java
public <T> T execute(StatementCallback<T> action) throws DataAccessException {
    Assert.notNull(action, "Callback object must not be null");
	
    // 先获取连接，结合spring事务管理，如果在事务中执行，则需要通过TransactionSynchronizationManager获得ConnectionHolder，从ConnectionHolder中获取Connection
    Connection con = DataSourceUtils.getConnection(obtainDataSource());
    Statement stmt = null;
    try {
        stmt = con.createStatement();
        applyStatementSettings(stmt);
        T result = action.doInStatement(stmt);
        handleWarnings(stmt);
        return result;
    }
    catch (SQLException ex) {
        // Release Connection early, to avoid potential connection pool deadlock
        // in the case when the exception translator hasn't been initialized yet.
        String sql = getSql(action);
        // 关闭Statement
        JdbcUtils.closeStatement(stmt);
        stmt = null;
        // 执行失败关闭连接，在事务中的话，应为有事务存在，连接必须保持到事务处理的最后，在事务中不会真正关闭连接
        DataSourceUtils.releaseConnection(con, getDataSource());
        con = null;
        throw translateException("StatementCallback", sql, ex);
    }
    finally {
        // 执行失败关闭连接，在事务中的话，应为有事务存在，连接必须保持到事务处理的最后，在事务中不会真正关闭连接， 上面把con设置为null了，这里就不会再执行了
        JdbcUtils.closeStatement(stmt);
        DataSourceUtils.releaseConnection(con, getDataSource());
    }
}
```

这里的逻辑也是模板化的， 获取连接，执行sql，处理结果，释放连接。我们猜想在事务开始时获取到连接并且将ConnectionHolder绑定到事务TransactionSynchronizationManager的ThreadLocal中，这里应该直接从ThreadLocal中获取连接，不然就达不到事务中使用同一个Connection的要求，于是是我们看下DataSourceUtils.getConnection方法是如何获取Connection的。

```java
// DataSourceUtils.getConnection
public static Connection getConnection(DataSource dataSource) throws CannotGetJdbcConnectionException {
   try {
      return doGetConnection(dataSource);
   }
   catch (SQLException ex) {
      throw new CannotGetJdbcConnectionException("Failed to obtain JDBC Connection", ex);
   }
   catch (IllegalStateException ex) {
      throw new CannotGetJdbcConnectionException("Failed to obtain JDBC Connection: " + ex.getMessage());
   }
}

// DataSourceUtils.doGetConnection
public static Connection doGetConnection(DataSource dataSource) throws SQLException {
    Assert.notNull(dataSource, "No DataSource specified");
	
    // 从TransactionSynchronizationManager中获取绑定的ConnectionHolder，如果存在事务中，这里肯定能够获取到Connection
    ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);
    if (conHolder != null && (conHolder.hasConnection() || conHolder.isSynchronizedWithTransaction())) {
        // conHolder引用计数+1，用于后续的released，当引用计数为0时，可以接触Connection和ConnectionHolder的绑定
        conHolder.requested();
        if (!conHolder.hasConnection()) {
            logger.debug("Fetching resumed JDBC Connection from DataSource");
            conHolder.setConnection(fetchConnection(dataSource));
        }
        return conHolder.getConnection();
    }
    // Else we either got no holder or an empty thread-bound holder here.
	// 上面获取不到Connection，要么是不在事务中，要么就TransactionSynchronizationManager中绑定的ConnectonHolder被解绑了，这里重新获取Connection
    logger.debug("Fetching JDBC Connection from DataSource");
    Connection con = fetchConnection(dataSource);

    // 在事务中就重新绑定ConnectionHolder
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
        logger.debug("Registering transaction synchronization for JDBC Connection");
        // Use same Connection for further JDBC actions within the transaction.
        // Thread-bound object will get removed by synchronization at transaction completion.
        ConnectionHolder holderToUse = conHolder;
        if (holderToUse == null) {
            holderToUse = new ConnectionHolder(con);
        }
        else {
            holderToUse.setConnection(con);
        }
        holderToUse.requested();
        TransactionSynchronizationManager.registerSynchronization(
            new ConnectionSynchronization(holderToUse, dataSource));
        holderToUse.setSynchronizedWithTransaction(true);
        if (holderToUse != conHolder) {
            TransactionSynchronizationManager.bindResource(dataSource, holderToUse);
        }
    }

    return con;
}
```

DataSourceUtils.getConnection内部是调用DataSourceUtils.doGetConnection方法，DataSourceUtils.doGetConnection内部实现逻辑

- 从TransactionSynchronizationManager中获取ConnectionHolder
- 判断ConnectionHolder不为空，并且包含connection，我们知道，在执行过doBegin方法后，是会获取到Connection的，设置到ConnectionHolder中，并将ConnectionHolder绑定到TransactionSynchronizationManager的TheadLocal中，所以如果没有进行手动清理ThreadLocal和跨线程，这里肯定是可以获取到Connection
- 对ConnectionHolder进行引用计数处理，主要用于ConnectionHolder的released处理
- 如果不存在事务，就直接从DataSource中获取连接，直接返回连接
- 后面的逻辑是对ConnectionHolder为null或者ConnectionHolder中不包含Connection的处理，理论上是不会执行的，为了保持逻辑的严谨性，必须做充分的考虑

最后释放连接调用DataSourceUtils.releaseConnection(con, getDataSource())，存在事务中应该是不会关闭连接的，因为Connection可能还需要给后续的sql执行使用，如果不存在事务，就可以直接关闭连接了。看下这个方法内部是如何实现的。

```java
// DataSourceUtils.releaseConnection
public static void releaseConnection(@Nullable Connection con, @Nullable DataSource dataSource) {
    try {
        doReleaseConnection(con, dataSource);
    }
    catch (SQLException ex) {
        logger.debug("Could not close JDBC Connection", ex);
    }
    catch (Throwable ex) {
        logger.debug("Unexpected exception on closing JDBC Connection", ex);
    }
}

// DataSourceUtils.doReleaseConnection
public static void doReleaseConnection(@Nullable Connection con, @Nullable DataSource dataSource) throws SQLException {
   if (con == null) {
      return;
   }
    
   if (dataSource != null) {
      // 能够从TransactionSynchronizationManager中获取绑定的ConnectionHolder就执行released()方法， conHolder应用计数-1，当引用技术为0时，则设置ConnectionHolder关联的Connection为null
      ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);
      if (conHolder != null && connectionEquals(conHolder, con)) {
         // It's the transactional Connection: Don't close it.
         conHolder.released();
         return;
      }
   }
    
   // 这里做Connection的关闭（连接池的技术会将连接归还到连接池中，不会真正关闭连接）
   logger.debug("Returning JDBC Connection to DataSource");
   doCloseConnection(con, dataSource);
}
```

总结：

JdbcTemplate对于Spring事务的支持：

- 从TransactionSynchronizationManager中获取绑定的ConnectionHolder，通过ConnectionHolder获取Connection，如果能够获取到，说明是在事务中，直接返回；
- 如果获取不到Connecton，就认为不在事务中，重新获取一个Connection返回；
- 使用Connection执行sql
- 最后释放Connection的时候也是先从TransactionSynchronizationManager中获取绑定的ConnectionHolder，如果能够获取到就将ConnectionHolder引用技计数-1，返回；否则不在事务中，就执行Connection的close方法关闭连接

#### Mybatis

在单独使用Mybatis进行SQL操作时，SqlSession是需要我们手动commit的。也就是说单独使用Mybatis时，mybatis是默认就开启了事务，可以通过sqlSessionFactory.openSession方法来指定创建SqlSession时是否autoCommit为true。即便是通过sqlSessionFactory.openSession方法传入autoCommit为true时，最后任然需要调用sqlSession.commit()。所以单独使用Mybatis比在Spring中集成使用要繁琐很多。

```java
public void testUpdate() {
	// 获取SqlSession， 默认实现类为DefaultSqlSession，sqlSessionFactory.openSession()没有指定autoCommit参数时， 默认为false，也就是需要执行sqlSession.commit()
    SqlSession sqlSession = sqlSessionFactory.openSession();
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    User paramUser = new User();
    paramUser.setAge(null);
    paramUser.setId(60);
    paramUser.setName("${name}");
    userMapper.update(paramUser);
    
   	// sqlsession提交，包含事务的提交，缓存的刷新，以及如果是批量执行时，批量发送请求到服务端
    sqlSession.commit();
    sqlSession.close();
}
```

Mybatis集成Spring的情况下主要是用SqlSessionFactoryBean和SqlSessionTemplate来实现的。SqlSessionFactoryBean负责构建SqlSessionFactory，这里面有个关键的步骤就是设置TransactionFactory为SpringManagedTransactionFactory，也就是SpringManagedTransaction参与到Mybatis运行中。这里我们看下Transaction接口定义，这里主要涉及Connection和事务相关的。这里Connection就是参与最终sql执行的，使用SpringManagedTransaction的来管理Mybatis事务的话，肯定个JdbcTemplate是类似的，也是从TransactionSynchronizationManager中获取绑定的ConnectionHolder，通过ConnectionHolder获取Connection，如果能够获取到，说明是在事务中，直接返回。最后事务的提交是委托给Spring DataSourceTransactionManager来管理的。

```java
public interface Transaction {

  /**
   * Retrieve inner database connection
   * @return DataBase connection
   * @throws SQLException
   */
  Connection getConnection() throws SQLException;

  /**
   * Commit inner database connection.
   * @throws SQLException
   */
  void commit() throws SQLException;

  /**
   * Rollback inner database connection.
   * @throws SQLException
   */
  void rollback() throws SQLException;

  /**
   * Close inner database connection.
   * @throws SQLException
   */
  void close() throws SQLException;

  /**
   * Get transaction timeout if set
   * @throws SQLException
   */
  Integer getTimeout() throws SQLException;

}
```

SqlSessionTemplate的实现逻辑主要是负责生成Sqlsseion，如果在事务中就复用现有的Sqlsession，感兴趣的可以看下内部实现，代码逻辑比较清晰。我这里贴几段主要的代码逻辑，方便大家理清思路

```java
// SqlSessionInterceptor用于生成SqlSession代理类，内部去通过SqlSessionFactory获取SqlSession或者从TransactionSynchronizationManager中获取绑定的SqlSession
private class SqlSessionInterceptor implements InvocationHandler {
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // 从TransactionSynchronizationManager中获取绑定的SqlSession，不存在则通过SessionFactory获取，获取之后，如果当前存在事务中，则绑定到TransactionSynchronizationManager；存在SqlSession就复用
    SqlSession sqlSession = getSqlSession(
        SqlSessionTemplate.this.sqlSessionFactory,
        SqlSessionTemplate.this.executorType,
        SqlSessionTemplate.this.exceptionTranslator);
    try {
      Object result = method.invoke(sqlSession, args);
      // 不在事务中，执行sqlSession.commit()方法
      if (!isSqlSessionTransactional(sqlSession, SqlSessionTemplate.this.sqlSessionFactory)) {
        // force commit even on non-dirty sessions because some databases require
        // a commit/rollback before calling close()
        sqlSession.commit(true);
      }
      return result;
    } catch (Throwable t) {
      Throwable unwrapped = unwrapThrowable(t);
      if (SqlSessionTemplate.this.exceptionTranslator != null && unwrapped instanceof PersistenceException) {
        // release the connection to avoid a deadlock if the translator is no loaded. See issue #22
        closeSqlSession(sqlSession, SqlSessionTemplate.this.sqlSessionFactory);
        sqlSession = null;
        Throwable translated = SqlSessionTemplate.this.exceptionTranslator.translateExceptionIfPossible((PersistenceException) unwrapped);
        if (translated != null) {
          unwrapped = translated;
        }
      }
      throw unwrapped;
    } finally {
      // 减少SqlSession的引用或者关闭SqlSession，逻辑和DataSourceUtils.releaseConnection方法比较类似
      if (sqlSession != null) {
        closeSqlSession(sqlSession, SqlSessionTemplate.this.sqlSessionFactory);
      }
    }
  }
}
```

### Spring事务的陷阱

虽然Spring事务极大程度上简化了JDBC事务的实现逻辑，提高了代码开发效率，但Spring事务的涉及到中的知识很多，如果我们对这些知识理解不透彻，就容易发生一些事务失效和死锁等问题。

Spring事务的陷阱问题总结：

- @Transactional 应用在非 public 修饰的方法上， 非public方法没法进行代理，所以会失效
- @Transactional 注解属性 propagation 设置错误， 对于传播属性理解不透彻
- @Transactional 注解属性 rollbackFor 设置错误， 记住默认rollbackFor为RuntimeException和Error，抛出其他Exception是不会导致rollback的
- 同一个类中方法调用，导致@Transactional失效， @Transactional实现依赖于AOP，如果同一个类中调用是不会再次进行AOP拦截的。
- 异常被catch捕获导致@Transactional失效，异常被捕获，导致没法进行rollback的处理
- 加入到当前事务的嵌套事务抛出异常，主事务捕获该异常，最终导致事务回滚
- 在事务中异步去获取数据，导致获取到更新前的数据
- 在事务中嵌套PROPAGATION_REQUIRES_NEW传播属性的事务更新相同的记录导致死锁



