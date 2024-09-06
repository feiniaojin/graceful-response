<div align=center><img src="./assets/project-name.png"></div>

<a href="https://hellogithub.com/repository/1fc5b7426d9a4398b53719468a2ae16a" target="_blank"><img src="https://abroad.hellogithub.com/v1/widgets/recommend.svg?rid=1fc5b7426d9a4398b53719468a2ae16a&claim_uid=CGoRi0Ug35dqj42" alt="Featured｜HelloGitHub" style="width: 250px; height: 54px; " width="250" height="54" /></a>

![](https://img.shields.io/github/license/feiniaojin/graceful-response)
[![GitHub stars](https://img.shields.io/github/stars/feiniaojin/graceful-response)](https://github.com/feiniaojin/graceful-response/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/feiniaojin/graceful-response)](https://github.com/feiniaojin/graceful-response/network)
![](https://img.shields.io/github/issues/feiniaojin/graceful-response)
![Maven Central](https://img.shields.io/maven-central/v/com.feiniaojin/graceful-response)

<!-- TOC -->

* [1. 项目介绍](#1-项目介绍)
* [2. 核心功能](#2-核心功能)
* [3. 感谢](#3-感谢)
* [4. 快速入门](#4-快速入门)
    * [4.1 引入Graceful Response](#41-引入graceful-response)
    * [4.2 开启 Graceful Response](#42-开启-graceful-response)
    * [4.4 代码编写](#44-代码编写)
        * [4.4.1 Controller层](#441-controller层)
        * [4.4.2 Service 层](#442-service-层)
* [5. 使用文档](#5-使用文档)
* [6. 交流和反馈](#6-交流和反馈)
* [7. 贡献者](#7-贡献者)

<!-- TOC -->

# 1. 项目介绍

Graceful Response 是 Spring
Boot 技术体系下的响应处理解决方案，可以帮助开发者优雅地完成包括统一响应格式数据封装、全局异常处理、错误码填充、异常消息国际化等处理过程，提高开发效率，提高代码质量。欢迎
star!

![代码现状](./assets/use-gr.png)

项目及案例代码仓库如下:

| Spring Boot 版本 | 项目                                                 | 案例                                                         |
|----------------|----------------------------------------------------|------------------------------------------------------------|
| 3.x            | <https://github.com/feiniaojin/graceful-response>  | <https://github.com/feiniaojin/graceful-response-example>  |
| 2.x            | <https://github.com/feiniaojin/graceful-response2> | <https://github.com/feiniaojin/graceful-response2-example> |

学习资源：

- B 站教学视频 <https://www.bilibili.com/video/BV1Wm411C7vs/>

# 2. 核心功能

- 统一返回值封装
- void 返回类型封装
- 全局异常处理
- 自定义响应体：适应不同项目的响应格式需求
- 参数校验错误码：为参数校验的异常指定错误码，支持全局的参数校验错误码
- 断言增强：执行断言时填充错误码和异常信息到 Response
- 异常别名：适配外部异常，通过注解或者配置文件的方式，为外部异常指定错误码
- 例外请求放行：支持根据路径、返回值、注解、配置等多种方式进行放行，无须担心框架冲突
- 第三方组件适配：目前已完成 Swagger、springdoc、actuator、FastJson 等框架或者组件的适配
- RESTful 支持：可以指定异常的 HTTP 状态码，并且支持统一指定
- 错误码枚举：支持定义错误码枚举，避免创建过多的异常类

更多功能，请到[文档中心](https://doc.feiniaojin.com/graceful-response/home.html)进行了解。

# 3. 感谢

感谢以下公众号或者自媒体对本项目的推广。

| 自媒体名称      | 类型  | 链接                                                  |
|------------|-----|-----------------------------------------------------|
| 芋道源码       | 公众号 | <https://mp.weixin.qq.com/s/VG6n5IsIDez8iZkY8JK6QQ> |
| Java 面试那些事 | 公众号 | <https://mp.weixin.qq.com/s/V9MhNVj3uQRrmnrfy9KkAQ> |
| 编程奇点       | 公众号 | <https://mp.weixin.qq.com/s/cQWgivkpuXGEijRR1WRt0g> |
| macrozheng | 公众号 | <https://mp.weixin.qq.com/s/XAHBzVRFuMJTEh8Y1Xvv4g> |
| Java 后端技术  | 公众号 | <https://mp.weixin.qq.com/s/4ssfZftGUqq0l9nW9lShLA> |
| Java 高性能架构 | 公众号 | <https://mp.weixin.qq.com/s/XImjkUExBHEklgrLnrINyw> |
| 技术老男孩      | 公众号 | <https://mp.weixin.qq.com/s/6gafudNaNTK1FRIdvNMXXw> |
| Java 知音    | 公众号 | <https://mp.weixin.qq.com/s/ZNmpTZnL2XqsPN2ayq0_4Q> |
| Java123    | 公众号 | <https://mp.weixin.qq.com/s/MF0PaawFILzNBMM78O_Pnw> |
| 程序员追风      | 公众号 | <https://mp.weixin.qq.com/s/wlY0phnXEiO7E_basxsWJw> |
| Spring源码项目进行时 | 掘金 | <https://juejin.cn/post/7367195057559371788> |
| 京东云开发者 | 掘金 | <https://juejin.cn/post/7405158247592427560> |
# 4. 快速入门

## 4.1 引入 Graceful Response

通过 Maven 依赖进行引入，GAV如下：

```xml

<dependency>
    <groupId>com.feiniaojin</groupId>
    <artifactId>graceful-response</artifactId>
    <version>{latest.version}</version>
</dependency>
```

**版本选择**：请到中央仓库选择最新的版本，boot2 和 boot3 有不用的 GAV。

> 仓库链接：<https://central.sonatype.com/artifact/com.feiniaojin/graceful-response>

## 4.2 开启 Graceful Response

在启动类中引入@EnableGracefulResponse 注解，即可启用 Graceful Response 组件。

```java

@EnableGracefulResponse
@SpringBootApplication
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
```

## 4.4 代码编写

### 4.4.1 Controller层

引入 Graceful Response 后，不需要再手工进行查询结果的封装，直接返回实际结果即可，Graceful Response 会自动完成封装的操作。

Controller 层示例如下。

```java

@Controller
public class Controller {
    @RequestMapping("/get")
    @ResponseBody
    public UserInfoView get(Long id) {
        return UserInfoView.builder().id(id).name("name" + id).build();
    }
}
```

在示例代码中，Controller 层的方法直接返回了 UserInfoView 对象，没有进行封装的操作，但经过 Graceful Response
处理后，还是得到了以下的响应结果。

```json
{
  "code": "0",
  "msg": "ok",
  "data": {
    "id": 1,
    "name": "name1"
  }
}
```

而对于命令操作（Command，即写操作）尽量不返回数据，因此写操作的方法的返回值应该是 void，Graceful
Response 对于对于返回值类型 void 的方法，也会自动进行封装。

```java
public class Controller {
    @RequestMapping("/command")
    @ResponseBody
    public void command() {
        //业务操作
    }
}
```

成功调用该接口，将得到：

```json
{
  "code": "0",
  "msg": "ok",
  "data": {}
}
```

不再需要在Controller中手工封装Result/Response进行返回。

### 4.4.2 Service 层

在引入 Graceful Response 前，有的开发者为了在接口中返回异常码，直接将 Service 层方法定义为 Response，淹没了方法的正常返回值，非常不优雅。

例如以下伪代码：

```java
/**
 * 直接返回Response的Service
 * 不规范
 */
public interface Service {
    Reponse commandMethod(Command command);
}
```

Graceful Response 通过多种机制，可以异常和错误码关联起来，这样 Service层方法就不需要再维护 Response 的响应码了，直接抛出业务异常即可。

- 方法一

使用@ExceptionMapper 注解修饰业务异常，提供错误码和错误提示，再由业务方法直接抛出该异常。

业务异常定义：

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

Service 接口直接抛异常：

```java
public class QueryServiceImpl implements QueryService {
    @Resource
    private UserInfoMapper mapper;

    public UserInfoView queryOne(Query query) {
        UserInfo userInfo = mapper.findOne(query.getId());
        if (Objects.isNull(userInfo)) {
            //这里直接抛自定义异常
            throw new NotFoundException();
        }
        //……后续业务操作
    }
}
```

当 Service 层的 queryOne 方法抛出 NotFoundException 时，Graceful
Response 会进行异常捕获，并将 NotFoundException 对应的异常码和异常信息封装到统一的响应对象中，最终接口返回以下 JSON。

```json
{
  "code": "1404",
  "msg": "找不到对象",
  "data": {}
}
```

- 方法二

方法一有可能导致定义过多的业务异常类，还可以通过使用GracefulResponse工具类进行异常抛出。

```java

@GetMapping("/raiseException0")
public void raiseException0() {
    //直接指定错误码和提示信息
    GracefulResponse.raiseException("520", "测试手工异常0");
}


@GetMapping("/raiseException1")
public void raiseException1() {
    try {

    } catch (Exception e) {
        //包装异常并继续抛出
        GracefulResponse.raiseException("1314", "测试手工异常1", e);
    }
}


@GetMapping("/test0")
public void test0() {
    //抛出异常枚举
    GracefulResponse.raiseException(ExceptionEnum.CUSTOM_EXCEPTION);
}
```

# 5. 使用文档

链接如下：

```text
https://doc.feiniaojin.com/graceful-response/home.html
```

[点击访问文档中心](https://doc.feiniaojin.com/graceful-response/home.html)

# 6. 交流和反馈

欢迎通过以下二维码联系作者、并加入 Graceful Response 用户交流群，申请好友时请备注“GR”。

<div><img src="./assets/qr.jpg" style="width: 50%"/></div>

公众号: 悟道领域驱动设计

<div><img src="./assets/gzh.jpg" style="width: 50%"/></div>

# 7. 贡献者

<a href="https://github.com/feiniaojin/graceful-response/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=feiniaojin/graceful-response" />
</a>
