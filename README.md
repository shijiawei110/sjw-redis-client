# sjw-redis-client
自己写的一款开源redis客户端(主要使用netty完成)
An open source redis client based on netty

# 目前支持的功能 / Functions currently supported:
1：连接池  
2: string,hash,list的各种功能,set和sortset还没时间写  
3: 对象序列化默认采用 fastjson 封装（目前仅支持fastjson）  
4: scan功能还没有写完,需要修改解码逻辑  

# 使用方法 / how to use it:
## 获取连接池 / get a connection pool:
### 1:简单获取(快速获取一个单client的pool) / simple get a pool
SjwRedisClientPool pool = SjwRedisClientPoolImpl.simplePool("127.0.0.1", 6379);  
SjwRedisClientPool pool = SjwRedisClientPoolImpl.simplePool("127.0.0.1", 6379, "password");  

### 2:详细参数指定获取 / detailed get a pool
SjwRedisClientPoolImpl.Build build = new SjwRedisClientPoolImpl.Build();  
/**设置host**/  
build.setHost("127.0.0.1");  
/**设置端口**/  
build.setPort(6379);  
/**设置密码(没有就不填写)**/  
build.setPassword("password");  
/**客户端数目**/  
build.setPoolSize(1);  
/**tcp连接超时时间**/  
build.setOutTimeMills(5000L);  
/**client的netty工作线程**/  
build.setWorkerGroupSize(1);    
SjwRedisClientPool pool = build.build();  

## 获取客户端client(如果都在使用就会有异常需要处理) / how to get a client:
SjwRedisClient client = pool.getClient();  

## 使用实例 / use demo just like jedis style:
/**帮你封装好了分布式锁**/  
client.stringSetNx("lock", "1", 100);  

/**string demo**/  
client.stringSet("test", "ddd");  
client.stringSet("test", "ddd", 100);  
String res = client.stringGet("test");  
String[] resList = client.stringMget("test", "test1", "test2");  

/**list demo**/  
Book book = Book.getOneBook();  
client.lpush("list", "11");  
client.lpush("list", book);  
String listRes = client.rpop("list");  
Book listBook = client.rpop("list",Book.class);  

/**hash demo**/  
client.hset("hash","h","dd");  

# 意见反馈和BUG请联系我哦,一起学习.
# 联系方式 / call me by wechat if you want to give me some advice :
微信(wechat) : shijiawei110


