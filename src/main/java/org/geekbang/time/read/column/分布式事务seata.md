
seata是什么: http://seata.io/zh-cn/docs/overview/what-is-seata.html

官网: http://seata.io/zh-cn/

快速开始:通过这里对seata的使用有个大致的认知,图文并茂
http://seata.io/zh-cn/docs/user/quickstart.html

seata所支持的四种事务模式
Seata AT 模式: http://seata.io/zh-cn/docs/dev/mode/at-mode.html
Seata TCC 模式: http://seata.io/zh-cn/docs/dev/mode/tcc-mode.html
SEATA Saga 模式: http://seata.io/zh-cn/docs/user/saga.html
Seata XA 模式: http://seata.io/zh-cn/docs/dev/mode/xa-mode.html
前三者都是补偿性事务，最后XA是一种全局一致性事务。
Saga是那种业务链路很长的场景适用
根据个人理解,AT和TCC在一般场景中可以使用,XA是强事务的模式需要资源(数据库or消息服务)对XA协议的支持。

github地址:
https://github.com/seata

不同技术栈的代码演示:
https://github.com/seata/seata-samples

整体上来说加入的依赖和配置还是比较多的,系统需要适配它做出部分改造,有一定的侵入性。
如果确认使用的话,引入并适配之后分布式事务完全依赖框架自己实现,对开发还是很友好的,业务开发成员只需要关注CRUD即可。

一般情况的分布式事务解决方案:可以参考龙果学院几年之前的那套课件,现在也不算特别过时,可以基本用于生产。
