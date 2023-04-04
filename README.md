# api-project

# api-backend
接口平台后台管理系统

# api-gateway
接口平台网关

# api-provider
接口提供者

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
- timestamp 用于减少存储nonce带来的压力，首先检查携带的timestamp是否在15分钟内，如超出时间范围，则拒绝，然后查询携带的nonce，如存在已有集合，则拒绝。否则，记录该nonce，并删除集合内时间戳大于15分钟的nonce。

### 注意事项
- 区分get/post的请求，获取参数的方式不同