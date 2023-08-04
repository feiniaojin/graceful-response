<div align=center><img src="https://s1.ax1x.com/2023/02/27/pp9rANF.png"></div>

![](https://img.shields.io/github/license/feiniaojin/graceful-response)
[![GitHub stars](https://img.shields.io/github/stars/feiniaojin/graceful-response)](https://github.com/feiniaojin/graceful-response/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/feiniaojin/graceful-response)](https://github.com/feiniaojin/graceful-response/network)
![](https://img.shields.io/github/issues/feiniaojin/graceful-response)
![Maven Central](https://img.shields.io/maven-central/v/com.feiniaojin/graceful-response)

# 1. 简介

Graceful Response是一个Spring Boot体系下的优雅响应处理器，提供一站式统一返回值封装、全局异常处理、自定义异常错误码等功能，使用Graceful Response进行web接口开发不仅可以节省大量的时间，还可以提高代码质量，使代码逻辑更清晰。

强烈推荐你花3分钟学会它！

本项目案例工程代码：https://github.com/feiniaojin/graceful-response-example.git ，注意选择最新版本的分支。

# 2. Java Web API接口数据返回的现状及解决方案

通常我们进行Java Web API接口时，大部分的Controller代码是这样的：

```java
public class Controller {
    
    @GetMapping("/query")
    @ResponseBody
    public Response query(Parameter params) {
        Response res = new Response();
        try {
            //1.校验params参数，非空校验、长度校验
            if (illegal(params)) {
                res.setCode(1);
                res.setMsg("error");
                return res;
            }
            //2.调用Service的一系列操作
            Data data = service.query(params);
            //3.将操作结果设置到res对象中
            res.setData(data);
            res.setCode(0);
            res.setMsg("ok");
            return res;
        } catch (BizException1 e) {
            //4.异常处理：一堆丑陋的try...catch，如果有错误码的，还需要手工填充错误码
            res.setCode(1024);
            res.setMsg("error");
            return res;
        } catch (BizException2 e) {
            //4.异常处理：一堆丑陋的try...catch，如果有错误码的，还需要手工填充错误码
            res.setCode(2048);
            res.setMsg("error");
            return res;
        } catch (Exception e) {
            //4.异常处理：一堆丑陋的try...catch，如果有错误码的，还需要手工填充错误码
            res.setCode(1);
            res.setMsg("error");
            return res;
        }
    }
}
```

这段代码存在什么问题呢？

- 真正的业务逻辑被冗余代码淹没，真正执行业务的代码只有

```java
Data data=service.query(params);
```

其他代码不管是正常执行还是异常处理，都是为了异常封装、把结果封装为特定的格式，例如以下格式：

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

这样的逻辑每个接口都需要处理一遍，都是繁琐的重复劳动。

现在，在引入**Graceful Response**组件后，我们只要直接返回业务结果，**Graceful Response**即可自动完成response的格式封装。

# 3. 快速入门

## 3.1 引入maven依赖

**graceful-response**已发布至maven中央仓库，可以直接引入到项目中，maven依赖如下：

```xml
<dependency>
    <groupId>com.feiniaojin</groupId>
    <artifactId>graceful-response</artifactId>
    <version>3.1.0</version>
</dependency>
```
目前最新版本为`3.1.0`。

## 3.2 在启动类中引入@EnableGracefulResponse注解

```java
@EnableGracefulResponse
@SpringBootApplication
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
```

## 3.3 配置对应的扫描包

ps:  支持*和**扫描

```yaml
gr:
  scan-packages:
    - com.lizhiadmin.pro.module.*.controller
```

## 3.4 Controller方法直接返回结果

- 普通的查询

```java
@Controller
public class Controller {
    @RequestMapping("/get")
    @ResponseBody
    public UserInfoView get(Long id) {
        log.info("id={}", id);
        return UserInfoView.builder().id(id).name("name" + id).build();
    }
}
```

这个接口直接返回了 `UserInfoView`的实例对象，调用接口时，Graceful Response将自动封装为以下格式：

```json
{
  "status": {
    "code": "0",
    "msg": "ok"
  },
  "payload": {
    "id": 1,
    "name": "name1"
  }
}
```

`UserInfoView`被自动封装到payload字段中。

返回结果的格式是可以自定义的，Graceful
Response提供了两种风格的Response，可以通过配置的方式进行切换，如果这两种风格也不能满足需要，我们还可以根据自己的需要进行自定义返回的Response格式。

- 返回值为空的场景

```java
public class Controller {
    @RequestMapping("/void")
    @ResponseBody
    public void testVoidResponse() {
        //业务操作
    }
}
```

`testVoidResponse`方法的返回时void，调用这个接口时，将返回：

```json
{
  "status": {
    "code": "200",
    "msg": "success"
  },
  "payload": {}
}
```

## 3.5 Service方法业务处理

在引入Graceful Response后，Service将：

- 接口直接返回业务数据类型，而不是Response

```java
public interface ExampleService {
    UserInfoView query1(Query query);
}
```

- Service接口实现类中，直接抛业务异常，接口调用异常时将直接返回错误码和错误提示

```java
public class ExampleServiceImpl implements ExampleService {
    @Resource
    private UserInfoMapper mapper;

    UserInfoView query1(Query query) {
        UserInfo userInfo = mapper.findOne(query.getId());
        if (Objects.isNull(userInfo)) {
            //这里直接抛自定义异常
            throw new NotFoundException();
        }
        //……后续业务操作
    }
}
```

```java
/**
 * NotFoundException的定义，使用@ExceptionMapper注解修饰
 * code:代表接口的异常码
 * msg:代表接口的异常提示
 */
@ExceptionMapper(code = "1404", msg = "找不到对象")
public class NotFoundException extends RuntimeException {

}
```

当Service方法抛出NotFoundException异常时，接口将直接返回错误码，不需要手工set。

```json
{
  "status": {
    "code": "1404",
    "msg": "找不到对象"
  },
  "payload": {}
}
```

> 验证：启动example工程后，请求http://localhost:9090/example/notfound

## 3.6 通用异常类和通用工具类

`@ExceptionMapper`设计的初衷，是将异常与错误码关联起来，用户只需要抛异常，不需要再关注异常与错误码的对应关系。

部分用户反馈，希望在不自定义新异常类的情况下，也能可以按照预期返回错误码和异常信息，因此从`2.1`版本开始，新增了`GracefulResponseException`异常类，用户只需要抛出该异常即可。

```java
public class Service {
  
  public void method() {
    throw new GracefulResponseException("自定义的错误码","自定义的错误信息");
  }
}
```
为简化使用，从`2.1`版本开始提供了`GracefulResponse`通用工具类，在需要抛出`GracefulResponseException`时，只需要调用`raiseException`方法即可。 这样做的目的是将用户的关注点从异常转移到错误码。

示例如下：

```java
public class Service {

    public void method() {
        //当condition==true时，抛出GracefulResponseException异常，返回自定义的错误码和错误信息
        if (condition) {
            GracefulResponse.raiseException("自定义的错误码", "自定义的错误信息");
        }
    }
}
```

## 3.7 参数校验异常以及错误码

在3.0版本以前，如果validation发生了校验异常，Graceful Response在默认情况下会捕获并返回code=1，参数校验发生的异常信息会丢失；如果使用异常别名功能，可以对大的校验异常返回统一的错误码，但是不够灵活并且依旧没有解决参数异常提示的问题。

Graceful Response从3.0版本开始，引入`@ValidationStatusCode`注解，可以非常方便地支持validation校验异常。

`@ValidationStatusCode`注解目前只有一个`code`属性，用于指定参数校验异常时的错误码，错误提示则取自validation校验框架。

- 对入参类进行参数校验

```java
@Data
public class UserInfoQuery {

    @NotNull(message = "userName is null !")
    @Length(min = 6, max = 12)
    @ValidationStatusCode(code = "520")
    private String userName;
}
```
当`userName`字段任意一项校验不通过时，接口将会返回异常码`520`和校验注解中的`message`：
```json
{
  "status": {
    "code": "520",
    "msg": "userName is null !"
  },
  "payload": {}
}
```
详细见example工程ExampleController的validateDto方法
``
http://localhost:9090/example/validateDto
``
>注意：@ValidationStatusCode校验参数对象字段的情况，code取值顺序为：会先取字段上的注解，再去该属性所在对象的类（即UserInfoQuery类）上的注解，再取全局配置的参数异常码`gr.defaultValidateErrorCode`，最后取默认的全局默认的错误码（默认code=1）

- 直接在Controller中校验方法入参

直接在Controller方法中进行参数校验：

```java
public class ExampleController {

  @RequestMapping("/validateMethodParam")
  @ResponseBody
  @ValidationStatusCode(code = "1314")
  public void validateMethodParam(@NotNull(message = "userId不能为空") Long userId,
                                  @NotNull(message = "userName不能为空") Long userName{
      //省略业务逻辑
  }
}
```
当userId、或者userName校验不通过时，将会返回code=1314，msg为对应的校验信息。
```json
{
  "status": {
    "code": "1314",
    "msg": "userId不能为空"
  },
  "payload": {}
}
```

详细见example工程ExampleController的validateMethodParam方法
``
http://localhost:9090/example/validateMethodParam
``
>注意：@ValidationStatusCode校验Controller方法参数字段的情况，code取值顺序为：会先取当前方法上的注解，再去该方法所在类（即ExampleController类）上的注解，再取全局配置的参数异常码`gr.defaultValidateErrorCode`，最后取默认的全局默认的错误码（默认code=1）

# 4. 进阶用法

## 4.1 Graceful Response异常错误码处理

以下是使用**Graceful Response**进行异常、错误码处理的开发步骤。

创建自定义异常，采用 `@ExceptionMapper`注解修饰，注解的 `code`属性为返回码，`msg`属性为错误提示信息

```java
@ExceptionMapper(code = 1007, msg = "有内鬼，终止交易")
public static final class RatException extends RuntimeException {

}
```

Service执行具体逻辑，需要抛异常的时候直接抛出去即可，不需要再关心异常与错误码关联的问题

```java
public class Service {
    public void illegalTransaction() {
        //需要抛异常的时候直接抛
        if (hasRat()) {
            logger.error("有内鬼终止交易");
            throw new RatException();
        }
        doIllegalTransaction();
    }
}

```

Controller调用Service

```java
public class Controller {
    @RequestMapping("/test3")
    public void test3() {
        logger.info("test3: RuntimeException");
        //Controller中不会进行异常处理，也不会手工set错误码，只关心核心操作，其他的通通交给Graceful Response
        exampleService.illegalTransaction();
    }
}

```

在浏览器中请求controller的/test3方法，有异常时将会返回：

```json
{
  "status": {
    "code": 1007,
    "msg": "有内鬼，终止交易"
  },
  "payload": {
  }
}
```

## 4.2 外部异常别名

案例工程( https://github.com/feiniaojin/graceful-response-example.git )启动后，
通过浏览器访问一个不存在的接口，例如 http://localhost:9090/example/get2?id=1

如果没开启Graceful Response，将会跳转到404页面页面，主要原因是应用内部产生了 `NoHandlerFoundException`异常。如果开启了Graceful
Response，默认会返回code=1的错误码。

这类非自定义的异常，如果需要自定义一个错误码返回，将不得不对每个异常编写Advice逻辑，在Advice中设置错误码和提示信息，这样做非常繁琐。

Graceful Response可以非常轻松地解决给这类外部异常定义错误码和提示信息的问题。

以下为操作步骤：

- 创建异常别名，并用 `@ExceptionAliasFor`注解修饰

```java
@ExceptionAliasFor(code = "1404", msg = "not found", aliasFor = NoHandlerFoundException.class)
public class NotFoundException extends RuntimeException {
}
```

code:捕获异常时返回的错误码

msg:为提示信息

aliasFor:表示将成为哪个异常的别名，通过这个属性关联到对应异常。

- 注册异常别名

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

- 浏览器访问不存在的URL

再次访问 http://localhost:9090/example/get2?id=1 ,服务端将返回以下json，正是在ExceptionAliasFor中定义的内容

```json
{
  "code": "1404",
  "msg": "not found",
  "data": {
  }
}
```

## 4.3 自定义Response格式

Graceful Response内置了两种风格的响应格式，并通过`gr.responseStyle`进行配置

- gr.responseStyle=0，或者不配置（默认情况）

将以以下的格式进行返回：

```json
{
  "status": {
    "code": 1007,
    "msg": "有内鬼，终止交易"
  },
  "payload": {
  }
}
```

- gr.responseStyle=1

将以以下的格式进行返回：

```json
{
  "code": "1404",
  "msg": "not found",
  "data": {
  }
}
```

- 自定义响应格式
  如果以上两种格式均不能满足业务需要，可以通过自定义。

例如以下响应：

```java
public class CustomResponseImpl implements Response {

    private String code;

    private Long timestamp = System.currentTimeMillis();

    private String msg;

    private Object data = Collections.EMPTY_MAP;

    @Override
    public void setStatus(ResponseStatus statusLine) {
        this.code = statusLine.getCode();
        this.msg = statusLine.getMsg();
    }

    @Override
    @JsonIgnore
    public ResponseStatus getStatus() {
        return null;
    }

    @Override
    public void setPayload(Object payload) {
        this.data = payload;
    }

    @Override
    @JsonIgnore
    public Object getPayload() {
        return null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
```

> 注意，不需要返回的属性可以返回null或者加上@JsonIgnore注解

- 配置`gr.responseClassFullName`

将CustomResponseImpl的全限定名配置到gr.responseClassFullName属性。

```yaml
gr:
  response-class-full-name: com.feiniaojin.gracefuresponse.example.config.CustomResponseImpl
```

注意，配置gr.responseClassFullName后，gr.responseStyle将不再生效。

## 4.4 通过@ExcludeFromGracefulResponse过滤不需要包装的接口

```java
/**
 * @author lihao3
 * @date 2023/6/30 10:10
 */
@Api("用户相关接口")
@Slf4j
@RestController
@RequestMapping("system/user")
@RequiredArgsConstructor
public class SysUserController {

  private final SysUserService service;

  @ApiOperation("删除")
  @DeleteMapping("{id}")
  @ExcludeFromGracefulResponse
  public String delete(@PathVariable Long id) {
    service.delete(id);
    return "删除成功";
  }

}
```

这样配置就会直接返回"删除成功";

# 5. 常用配置

```yaml
gr:
  # 自定义Response类的全限定名，默认为空。 配置gr.responseClassFullName后，gr.responseStyle将不再生效
  response-class-full-name:
  # 是否打印异常日志，默认为false
  print-exception-in-global-advice: 
  # Response风格，不配置默认为0
  response-style: 
  # 自定义的成功响应码，不配置则为0
  default-success-code: 
  # 自定义的成功提示，默认为ok
  default-success-msg: 
  # 自定义的失败响应码，默认为1
  default-error-code: 
  # 自定义的失败提示，默认为error
  default-error-msg: 
  # 全局的参数校验错误码，默认等于gr.defaultErrorCode
  default-validate-error-code: 
  # 扫描包(支持数字, *和**通配符匹配)
  scan-packages:
    - com.lizhiadmin.pro.module.*.controller
```

# 6. 点赞趋势图

[![Star History Chart](https://api.star-history.com/svg?repos=feiniaojin/graceful-response&type=Date)](https://star-history.com/#feiniaojin/graceful-response&Date)



# 7. 贡献者

<a href="https://github.com/feiniaojin/graceful-response/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=feiniaojin/graceful-response" />
</a>

------

使用过程中如遇到问题，可以联系作者。

公众号: MarkWord