# 流程
1. 接受用户请求参数（接口基本参数+ak、sk）
2. 封装用户请求参数，添加校验参数（timestamp、nonce、sign）
3. 通过http转发给gateway