# 待做
> todo

> 日志和监控

warn.log
error.log

> jar 运行

java -jar xxx.jar network=1

# 线程池框架

1. 自定义线程池 ：线程数和队列类型
2. feature 回等待当前线程 （无）
3. 拒绝策略（无）

# 转gradle

gradle init --type pom

但是pom.xml中必须无parent，否则会报错。

# 调优
> 1


  thread.core.pool.size=100
  thread.work.queue.size=500


  2016-09-06 11:15:58 2016-09-06 11:18:05 200 条

> 2

  2016-09-06 11:25:48 2016-09-06 11:27:09 200条

  2016-09-06 11:31:02 2016-09-06 11:33:42 199+1 readtime out

  //todo error

> spring boot tiaoyou

https://dzone.com/articles/spring-boot-memory-performance