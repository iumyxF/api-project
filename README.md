# api-project

# api-backend

接口平台后台管理系统

# api-gateway

接口平台网关

# api-provider

接口提供者

# api-client-sdk

sdk 的作用是帮助backend去调用gateway的

# 接口认证流程

### 识别请求身份

- AccessKey（开发者标识，确保唯一）
- SecretKey（用于接口加密，确保不易被穷举，生成算法不易被猜测）。

### 防止篡改

参数签名
签名流程：

1. 按照请求参数名的字母升序排列非空请求参数（包含AccessKey），使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串stringA
2. 在stringA最后拼接上SecretKey得到字符串stringSignTemp
3. 对stringSignTemp进行MD5运算，并将得到的字符串所有字符转换为大写，得到sign值。

请求携带参数AccessKey和Sign，只有拥有合法的身份AccessKey和正确的签名Sign才能放行。这样就解决了身份验证和参数篡改问题，即使请求参数被劫持，由于获取不到SecretKey（仅作本地加密使用，不参与网络传输），无法伪造合法的请求。

### 防止重攻击

timestamp + nonce

- nonce指唯一的随机字符串，用来标识每个被签名的请求。通过为每个请求提供一个唯一的标识符，服务器能够防止请求被多次使用（记录所有用过的nonce以阻止它们被二次使用）。
- timestamp
  用于减少存储nonce带来的压力，首先检查携带的timestamp是否在15分钟内，如超出时间范围，则拒绝，然后查询携带的nonce，
  如存在已有集合，则拒绝。否则，记录该nonce，并删除集合内时间戳大于15分钟的nonce。

### 参数列表

1. 接口所需的参数（eg:name=jack）
2. timestamp
3. nonce
4. AccessKey
5. sign

### 注意事项

- 区分get/post的请求，获取参数的方式不同

# Dubbo的使用

1. 依赖于springboot进行开发
2. 使用IDL和Triple协议进行开发（后期使用）

## 相关配置

- dubbo version 3.2.0-beta.4
- zookeeper version windows 3.7.1

## 流程

- 注册中心
  - zookeeper 下载链接(windows) https://dlcdn.apache.org/zookeeper/zookeeper-3.7.1/apache-zookeeper-3.7.1-bin.tar.gz
  - nacos 下载链接(windows) https://github.com/alibaba/nacos/releases/tag/2.2.0
- 服务提供者：api-backend
- 服务消费者：api-gateway

## 接口调用流程

- api-backend 提供一个接口调用接口("/invoke")
    - 判断接口是否存在
    - 判断接口状态、用户是否有权限、剩余次数
    - 使用client进行调用远程接口
- api-sdk 提供封装参数功能（类似工具类）
    - 根据签名规则生成sign
    - 请求gateway
- api-gateway
    - 记录日志、黑白名单、
    - 校验ak、sk合法性（dubbo）（sdk封装进行校验规则）
    - 根据ak获取当前用户id，根据method和url获取接口id
    - 根据配置的路由，请求转发
    - 判断调用是否成功，次数+1
- api-provider
    - 提供接口数据

# 后续可以优化的点
1. 将GateWay的配置文件交给Nacos管理，实现动态的配置路由
2. Dubbo中使用IDL定义跨语言服务