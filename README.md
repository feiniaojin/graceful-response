<h1 align="center"><img src="https://s4.ax1x.com/2021/12/05/orPOnU.png"  alt="naaf-logo" /></h1>

# naaf-graceful-response

![](https://img.shields.io/github/license/feiniaojin/naaf-graceful-response) ![https://maven-badges.herokuapp.com/maven-central/com.feiniaojin.naaf.ngr/naaf-graceful-response-starter/badge.svg](https://maven-badges.herokuapp.com/maven-central/com.feiniaojin.naaf.ngr/naaf-graceful-response-starter/badge.svg) ![](https://img.shields.io/github/issues/feiniaojin/naaf-graceful-response)

这是一个为spring boot web开发准备的优雅响应封装组件。

API接口为了返回如下格式的结果

```json
{
  "code": 0,
  "msg": "ok",
  "data": {
    "id": 1,
    "age": 18
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
    "age": 18
  }
}
```

通常需要进行如下的包装处理

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
    }catch(BizException e){
        //4.异常处理：一堆丑陋的try...catch，如果有错误码的，还需要手工填充错误码
        res.setCode(1024);
        res.setMsg("error");
        return res;
    }
}
```

可以看到，真正执行业务的代码只有

```java
Data data=service.query(params);
```

其他代码都是为了把结果封装为特定的格式，每个接口都需要处理一遍，非常的繁琐，且不优雅。

现在，我们引入**naaf-graceful-response**来解决这个问题。

我们只要直接返回业务结果，**naaf-graceful-response**即可自动完成封装，以下是一个简单的案例。

接口代码：

```java
@RequestMapping("/test1")
public Map<String, Object> test1(){
    logger.info("test1: return map");
    Map<String, Object> map=new HashMap<>();
    map.put("id",1)
    map.put("username","Q");
    return map;
}
```

我们直接返回了一个Map，但是我们调用接口时，返回的却是按照为以下格式封装好的响应：

```json
{
  "status": {
    "code": 0,
    "msg": "ok"
  },
  "payload": {
    "id": 1,
    "username": "Q"
  }
}
```

我们的返回结果已被自动封装到payload字段中。

注：返回结果的格式是可以自定义的，以上的格式只是作者习惯采用的，我们可以根据自己的需要进行封装。

以下为操作步骤：

第一步：引入maven依赖

目前已上传至maven中央仓库，可直接引入到项目中

```xml
<dependency>
    <groupId>com.feiniaojin.naaf.ngr</groupId>
    <artifactId>naaf-graceful-response-starter</artifactId>
    <version>1.0</version>
</dependency>
```

第二步:开启naaf-graceful-response

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

具体的案例可以参考本项目目录下的**naaf-graceful-response-example**包

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

有时候我们需要抛业务异常，而且同时需要将异常与异常码关联起来，**GracefulResponse**提供了简单的方案

首先，创建自定义异常，采用**@ExceptionMapper**注解修饰，注解的`code`属性为返回码，`msg`属性为错误提示信息

```java
@ExceptionMapper(code = 1007, msg = "有内鬼，终止交易")
public static final class RatException extends RuntimeException {

}
```

其次，service执行具体逻辑，需要抛异常的时候直接抛出去

```java
public void throwRuntimeException() {
  if (hasRat()) {
    logger.error("有内鬼终止交易");
    throw new RatException();
  }
  doSomething();
}
```

之后，controller调用sercvice

```java
@RequestMapping("/test3")
public void test3(){
  logger.info("test3: RuntimeException");
  exampleService.throwRuntimeException();
}
```

最后，在浏览器中请求controller的/test3方法，将会返回：

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

具体可以参考本项目目录下的**naaf-graceful-response-example**包

---

使用过程中如遇到问题，可以联系作者。

email: 943868899@qq.com

wx：qyj000100
