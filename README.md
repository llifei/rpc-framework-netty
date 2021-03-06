# rpc-framework-netty
基于 Netty + Nacos + Kryo  的 RPC 框架实现。
项目架构分析地址：https://kdocs.cn/l/cll3DnsFZbiF

## 功能描述

- 服务自动注册/注销（Nacos）
- 服务发现
- 负载均衡（随机、轮询）
- 支持多种序列化方式（Json、Kryo、Hessian）

## 项目结构

### api
  	服务接口、客户端调用服务传递的参数实体
### common
  	整体框架需要用到的 请求/回复消息实体、枚举类、自定义异常、一些工具包
### core
  	框架的具体实现包括：客户端、服务端、序列化器、编码解码器、负载均衡处理、服务注册及发现等
### test-client
  	测试客户端：从客户端视角调用服务验证框架可用性
### test-server
  	测试服务端：运行服务器以供测试客户端调用服务
