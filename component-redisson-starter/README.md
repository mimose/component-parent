### **REDISSON-COMPONENT**

#### redisson组件

#### 一、分布式锁功能

1、在项目pom中引入该组件
~~~
<properties>
    <redisson.component.version>1.0.0</redisson.component.version>
</properties>
<dependencies>
    <dependency>
        <groupId>com.mimose.redisson</groupId>
        <artifactId>redisson-component</artifactId>
        <version>$redisson.component.version}</version>
    </dependency>
</dependencies>
~~~
2、在项目配置文件中增加属性
~~~
## 单机模式
redisson.base.address=redis://127.0.0.1:6379
redisson.base.password=123456

## 哨兵模式
redisson.base.master-name=mymaster
redisson.base.password=xxxx
redisson.base.sentinel-addresses=redis://127.0.0.1:26379,redis://127.0.0.1:26380,redis://127.0.0.1:26381
~~~
3、在需要增加锁的逻辑处，调用LockUtil进行加锁（支持超时锁），并通过unlock进行解锁
~~~
// LockKeyEnum是一个锁标识的枚举类，根据该key进行分布式锁的控制
Lock lock = LockUtil.lock(LockKeyEnum.xxx.getName());
try{
    // dosomething
}catch(final Exception e){
    // excpetion
}finally{
    lock.unlock();
}
~~~
4、项目启动脚本增加-Dredisson.open=true
~~~
java -jar -Dredisson.open=true -Dspring.profiles.active=dtest -Dserver.port=8080 xxx.jar
~~~

##### 若想兼容普通的ReentrantLock机制，需要进行以下几步工作：
1、在项目中使用枚举类，设置锁的key，实现redisson组件提供的DefaultLockKey接口，实现getName返回key值
~~~
public enum LockKeyEnum implements DefaultLockKey {
    /**预约锁**/
    REG_LOCK,
    /**取消锁**/
    CANCEL_LOCK;

    @Override
    public String getName() {
        return this.name();
    }
}
~~~
2、加锁时使用该枚举类进行key值的设置
~~~
Lock lock = LockUtil.lock(LockKeyEnum.REG_LOCK.getName());
xxxxx
~~~
3、使组件初始化该枚举类的方法（一定要进行）：   
3.1、在项目的配置文件中增加配置项（单体应用的情况）
~~~
## redisson lock key package url
redisson.lock.enumPackUrl=com.mimose.lock.emun
~~~
3.2、当项目不是一个单体应用时，为了不需要在各个项目中修改配置文件，可以在统一的引用code项目中的resource新增默认锁配置文件   
redisson-default-lock.yml：
~~~
redisson.lock.enumPackUrl: com.mimose.lock.emun
~~~
3.3、若不想增加配置，可以枚举类的名称规则来规定，规则为：类名中包含 ock 。   
如这里使用的枚举类是LockKeyEnum，名称中包含了 ock ，便会被初始化。   
#### 二、分布式缓存机制

1、在项目pom中引入该组件
~~~
<properties>
    <redisson.component.version>1.0.0</redisson.component.version>
</properties>
<dependencies>
    <dependency>
        <groupId>com.mimose.redisson</groupId>
        <artifactId>redisson-component</artifactId>
        <version>$redisson.component.version}</version>
    </dependency>
</dependencies>
~~~
2、增加缓存配置文件

redisson-cache.yml：
~~~
# cache key
testCache1:
  # 过期时间 ms
  ttl: 300000
  # 最大空闲时间 ms
  maxIdleTime: 300000

testCache2:
  ttl: 300000
  maxIdleTime: 300000

~~~

默认使用resource下的redisson-cache.yml文件，若有不同，需要在项目配置文件中指定，如：
~~~
redisson.cache.config=classpath:mycache.yml
~~~

3、在需要使用cache的地方，使用spring提供的注解即可实现
~~~
@Cacheable
@CacheEvict
...
~~~

4、项目启动脚本增加-Dredisson.open=true
~~~
java -jar -Dredisson.open=true -Dspring.profiles.active=dtest -Dserver.port=8080 xxx.jar
~~~

##### TIP: 
当引入了redisson-component，却不启动redisson时，即没有设置redisson.open或者设置了redisson.open=false时  
1、分布式锁将不可使用，将根据实现的DefaultLockKey接口实例化ReentrantLock锁，进行普通java全局锁；  
2、RedissonCache将不可使用，需要自己实现缓存，如集成ehcache

#### **配置项：**

| 配置名 | 含义   | 默认值 | 备注   |
| ------ | ------ | ------ | ------ |
| address | redis连接地址 |  | 单机模式下生效 |
| password | redis连接密码 |  | 单机模式及哨兵模式下生效，无密码则不设置 | 
| database | 数据库编号 | 0 |  | 
| timeout | 命令等待超时，单位：毫秒 | 5000 | 单机模式及哨兵模式下生效 | 
| connectionTimeout | 连接超时时间，单位：毫秒 | 5000 | 单机模式及哨兵模式下生效 | 
| connectionPoolSize | 连接池大小 | 250 | 单机模式下生效 | 
| connectionMinimumIdleSize | 最小空闲连接数 | 20 | 单机模式下生效 | 
| masterName | 主节点名 |  | 哨兵模式下生效 | 
| sentinelAddresses | 哨兵地址 |  | 哨兵模式下生效，多个地址使用逗号隔开 |
| masterConnectionPoolSize | 主节点连接池大小 | 400 | 哨兵模式下生效 | 
| slaveConnectionPoolSize | 从节点连接池大小 | 400 | 哨兵模式下生效 | 
