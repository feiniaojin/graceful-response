[![vAPppF.jpg](https://s1.ax1x.com/2022/08/01/vAPppF.jpg)](https://imgtu.com/i/vAPppF)

![](https://img.shields.io/github/license/feiniaojin/graceful-response) 
![](https://img.shields.io/github/issues/feiniaojin/graceful-response)

# 一、简介
Graceful Response是一个Spring Boot体系下的优雅响应处理器。

# 二、Java Web API接口数据返回的现状及解决方案

通常我们进行Java Web API接口时，大部分的Controller代码是这样的：

```java
@GetMapping
@ResponseBody
public Response query(Parameter params){

    Response res=new Response();
    try{
        //1.校验params参数，非空校验、长度校验
        if(illegal(params)){
            res.setCode(1);
            res.setMsg("error");
            return res;
        }
        //2.调用Service的一系列操作
        Data data=service.query(params);
        //3.将操作结果设置到res对象中
        res.setData(data);
        res.setCode(0);
        res.setMsg("ok");
        return res;
    }catch(BizException1 e){
        //4.异常处理：一堆丑陋的try...catch，如果有错误码的，还需要手工填充错误码
        res.setCode(1024);
        res.setMsg("error");
        return res;
    }catch(BizException2 e){
        //4.异常处理：一堆丑陋的try...catch，如果有错误码的，还需要手工填充错误码
        res.setCode(2048);
        res.setMsg("error");
        return res;
    }catch(Exception e){
        //4.异常处理：一堆丑陋的try...catch，如果有错误码的，还需要手工填充错误码
        res.setCode(1);
        res.setMsg("error");
        return res;
    }
}
```

这段代码存在什么问题呢？

* 处理逻辑繁琐

可以看到，真正执行业务的代码只有

```java
Data data=service.query(params);
```
其他代码都是为了把结果封装为特定的格式，例如以下两种格式：

```json
{
  "code": 0,
  "msg": "ok",
  "data": {
    "id": 1,
    "name": "username"
  }
}
```
或者

```json
{
  "status": {
    "code": 0,
    "msg": "ok"
  },
  "payload": {
    "id": 1,
    "name": "username"
  }
}
```

而且，这样的逻辑每个接口都需要处理一遍，都是繁琐的重复劳动。

现在，在引入**Graceful Response**组件后，我们只要直接返回业务结果，**Graceful Response**即可自动完成封装。

以下是一个简单的案例。

接口Controller代码：

```java
@RequestMapping("/get")
@ResponseBody
public UserInfoView get(Long id) {
    log.info("id=" + id);
    return UserInfoView.builder().id(id).name("name" + id).build();
}
```

这个接口我们直接返回了`UserInfoView`，但是我们调用接口时，返回的却是按照为以下格式封装好的响应：

```json
{
  "status":{
    "code":"0",
    "msg":"ok"
  },
  "payload":{
    "id":1,
    "name":"name1"
  }
}
```

我们的返回结果已被自动封装到payload字段中。

注：返回结果的格式是可以自定义的，以上的格式只是作者习惯采用的，我们可以根据自己的需要进行封装。
在本组件的案例工程( https://github.com/feiniaojin/graceful-response-example.git )中,
提供了封装为以下格式的`ResponseFactory`，详细见`CustomResponseFactoryImpl`。
另外一种常见的返回值格式:
```json
{
    "code":"0",
    "msg":"ok",
    "data":{
        "id":1,
        "name":"name1"
    }
}
```
* 手工进行错误码封装

在上面的示例代码中可以看到，为了根据不同的异常返回不同的错误码，捕获了多个异常，并且手工set错误码。

```java
    try{
       //省略业务操作代码……
    }catch(BizException1 e){
        //4.异常处理：一堆丑陋的try...catch，如果有错误码的，还需要手工填充错误码
        res.setCode(1024);
        res.setMsg("error");
        return res;
    }catch(BizException2 e){
        //4.异常处理：一堆丑陋的try...catch，如果有错误码的，还需要手工填充错误码
        res.setCode(2048);
        res.setMsg("error");
        return res;
    }catch(Exception e){
        //4.异常处理：一堆丑陋的try...catch，如果有错误码的，还需要手工填充错误码
        res.setCode(1);
        res.setMsg("error");
        return res;
    }
```

我们可以通过Graceful Response自动完成这个过程。

(1)创建自定义异常，采用`@ExceptionMapper`注解修饰，注解的`code`属性为返回码，`msg`属性为错误提示信息

```java
@ExceptionMapper(code = 1007, msg = "有内鬼，终止交易")
public static final class RatException extends RuntimeException {

}
```

(2)service执行具体逻辑，需要抛异常的时候直接抛出去即可，不需要在关心异常与错误码关联的问题

```java
public void throwRuntimeException() {
  if (hasRat()) {
    logger.error("有内鬼终止交易");
    throw new RatException();
  }
  doSomething();
}
```

(3)controller调用service

```java
@RequestMapping("/test3")
public void test3(){
  logger.info("test3: RuntimeException");
  exampleService.throwRuntimeException();
}
```

(4)在浏览器中请求controller的/test3方法，将会返回：

```json
{
    "status":{
        "code":1007,
        "msg":"有内鬼，终止交易"
    },
    "payload":{

    }
}
```

# 三、外部异常别名

案例工程( https://github.com/feiniaojin/graceful-response-example.git )启动后，
通过浏览器访问一个不存在的接口，例如 http://localhost:9090/example/get2?id=1 

将会跳转到404页面页面，主要原因是应用内部产生了`NoHandlerFoundException`异常。

这类非自定义的异常，如果需要自定义一个错误码返回，将不得不对每个异常编写Advice逻辑，在Advice中设置错误码和提示信息，这样做非常不繁琐。

Graceful Response可以非常轻松地解决给这类外部异常定义错误码和提示信息的问题。

以下为操作步骤：

(1)创建异常别名，并用`@ExceptionAliasFor`注解修饰

code为发生NoHandlerFoundException时的错误码，msg为提示信息，aliasFor表示将成为哪个异常的别名。

```java
@ExceptionAliasFor(code = "1404", msg = "not found", aliasFor = NoHandlerFoundException.class)
public class NotFoundException extends RuntimeException {
}
```

(2)注册异常别名
创建一个继承了AbstractExceptionAliasRegisterConfig的配置类，在实现的registerAlias方法中进行注册。

```java
@Configuration
public class GracefulResponseConfig extends AbstractExceptionAliasRegisterConfig {

    @Override
    protected void registerAlias(ExceptionAliasRegister aliasRegister) {
        aliasRegister.doRegisterExceptionAlias(NotFoundException.class);
    }
}
```

(3)浏览器访问不存在的URL

再次访问 http://localhost:9090/example/get2?id=1 ,服务端将返回以下json，正是在ExceptionAliasFor中定义的内容

```json
{
    "code":"1404",
    "msg":"not found",
    "data":{

    }
}
```

# 四、快速入门

第一步：引入maven依赖

目前由于作者比较忙，还没有来得及上传至maven中央仓库，可以克隆源码进行编译，然后直接引入到项目中

```xml
<dependency>
    <groupId>com.feiniaojin.ddd.ecosystem</groupId>
    <artifactId>graceful-response</artifactId>
    <version>1.0</version>
</dependency>
```

第二步:开启Graceful Response

在Spring Boot的配置类上，引入@EnableGracefulResponse注解

```java
@EnableGracefulResponse
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

第三步：开发接口

具体的案例可以参考本项目的示例工程,https://github.com/feiniaojin/graceful-response-example.git

特殊情况：

（1）controller方法返回void

例如：

```java
@RequestMapping("/test0")
public void test0(){
    logger.info("test0: return void");
}
```

将会封装为：

```json
{
  "status": {
    "code": 0,
    "msg": "ok"
  },
  "payload": {}
}
```

（2）自定义异常码与错误提示

见上文

（3）异常别名

见上文

---

使用过程中如遇到问题，可以联系作者。

公众号: MarkWord

目前正在连载更新《Thinking in DDD》系列文章，未来将会对团队管理、架构方法论进行系列分享，欢迎关注
[![vA1OFU.jpg](https://s1.ax1x.com/2022/08/01/vA1OFU.jpg)](https://imgtu.com/i/vA1OFU)
[![vA8Dbt.jpg](https://s1.ax1x.com/2022/08/01/vA8Dbt.jpg)](https://imgtu.com/i/vA8Dbt)
