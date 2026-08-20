# UserController

UserController


---
## 根据id查询用户

> BASIC

**Path:** /user/getUserById

**Method:** GET

> REQUEST

**Query:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| id |  | NO | 用户id |



> RESPONSE

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| content-type | application/json;charset=UTF-8 | NO |  |

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| code | integer | 状态码（成功:200 失败：其他） |
| message | string | 提示消息 |
| data | object | 返回数据 |
| &ensp;&ensp;&#124;─userId | integer | 用户ID |
| &ensp;&ensp;&#124;─userName | string | 用户账号 |
| &ensp;&ensp;&#124;─nickName | string | 用户昵称 |
| &ensp;&ensp;&#124;─email | string | 用户邮箱 |
| &ensp;&ensp;&#124;─phonenumber | string | 手机号码 |
| &ensp;&ensp;&#124;─sex | string | 用户性别（0男 1女 2未知） |
| &ensp;&ensp;&#124;─avatar | string | 头像地址 |
| &ensp;&ensp;&#124;─password | string | 密码 |
| &ensp;&ensp;&#124;─status | string | 账号状态（0正常 1停用） |
| &ensp;&ensp;&#124;─loginIp | string | 最后登录IP |
| &ensp;&ensp;&#124;─loginDate | string | 最后登录时间 |
| &ensp;&ensp;&#124;─pwdUpdateDate | string | 密码最后更新时间 |
| &ensp;&ensp;&#124;─createBy | string | 创建者 |
| &ensp;&ensp;&#124;─createTime | string | 创建时间 |
| &ensp;&ensp;&#124;─updateBy | string | 更新者 |
| &ensp;&ensp;&#124;─updateTime | string | 更新时间 |
| &ensp;&ensp;&#124;─remark | string | 备注 |
| &ensp;&ensp;&#124;─roles | array | 用户角色列表（非数据库字段） |
| &ensp;&ensp;&ensp;&ensp;&#124;─ | object |  |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─roleId | integer | 角色ID |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─roleName | string | 角色名称 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─roleSort | integer | 显示顺序 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─status | string | 角色状态（0正常 1停用） |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─createBy | string | 创建者 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─createTime | string | 创建时间 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─updateBy | string | 更新者 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─updateTime | string | 更新时间 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─remark | string | 备注 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─permissions | array | 角色权限列表（非数据库字段） |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─ | object |  |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─permissionId | integer | 权限ID |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─permissionName | string | 权限名称 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─component | string | 权限路径 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─createBy | string | 创建者 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─createTime | string | 创建时间 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─updateBy | string | 更新者 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─updateTime | string | 更新时间 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─remark | string | 备注 |

**Response Demo:**

```json
{
  "code": 0,
  "message": "",
  "data": {
    "userId": 0,
    "userName": "",
    "nickName": "",
    "email": "",
    "phonenumber": "",
    "sex": "",
    "avatar": "",
    "password": "",
    "status": "",
    "loginIp": "",
    "loginDate": "",
    "pwdUpdateDate": "",
    "createBy": "",
    "createTime": "",
    "updateBy": "",
    "updateTime": "",
    "remark": "",
    "roles": [
      {
        "roleId": 0,
        "roleName": "",
        "roleSort": 0,
        "status": "",
        "createBy": "",
        "createTime": "",
        "updateBy": "",
        "updateTime": "",
        "remark": "",
        "permissions": [
          {
            "permissionId": 0,
            "permissionName": "",
            "component": "",
            "createBy": "",
            "createTime": "",
            "updateBy": "",
            "updateTime": "",
            "remark": ""
          }
        ]
      }
    ]
  }
}
```




---
## 分页查询用户列表

> BASIC

**Path:** /user/list

**Method:** GET

> REQUEST

**Query:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| pageNum | 1 | YES | 当前页码，默认1 |
| pageSize | 10 | YES | 每页大小，默认10 |
| userName |  | NO | 用户名，可选 |
| status |  | NO | 用户状态，可选 |



> RESPONSE

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| content-type | application/json;charset=UTF-8 | NO |  |

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| code | integer | 状态码（成功:200 失败：其他） |
| message | string | 提示消息 |
| data | object | 返回数据 |

**Response Demo:**

```json
{
  "code": 0,
  "message": "",
  "data": {}
}
```




---
## 新增用户

> BASIC

**Path:** /user/addUser

**Method:** POST

> REQUEST

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | YES |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| userId | integer | 用户ID |
| userName | string | 用户账号 |
| nickName | string | 用户昵称 |
| email | string | 用户邮箱 |
| phonenumber | string | 手机号码 |
| sex | string | 用户性别（0男 1女 2未知） |
| avatar | string | 头像地址 |
| password | string | 密码 |
| status | string | 账号状态（0正常 1停用） |
| loginIp | string | 最后登录IP |
| loginDate | string | 最后登录时间 |
| pwdUpdateDate | string | 密码最后更新时间 |
| createBy | string | 创建者 |
| createTime | string | 创建时间 |
| updateBy | string | 更新者 |
| updateTime | string | 更新时间 |
| remark | string | 备注 |
| roles | array | 用户角色列表（非数据库字段） |
| &ensp;&ensp;&#124;─ | object |  |
| &ensp;&ensp;&ensp;&ensp;&#124;─roleId | integer | 角色ID |
| &ensp;&ensp;&ensp;&ensp;&#124;─roleName | string | 角色名称 |
| &ensp;&ensp;&ensp;&ensp;&#124;─roleSort | integer | 显示顺序 |
| &ensp;&ensp;&ensp;&ensp;&#124;─status | string | 角色状态（0正常 1停用） |
| &ensp;&ensp;&ensp;&ensp;&#124;─createBy | string | 创建者 |
| &ensp;&ensp;&ensp;&ensp;&#124;─createTime | string | 创建时间 |
| &ensp;&ensp;&ensp;&ensp;&#124;─updateBy | string | 更新者 |
| &ensp;&ensp;&ensp;&ensp;&#124;─updateTime | string | 更新时间 |
| &ensp;&ensp;&ensp;&ensp;&#124;─remark | string | 备注 |
| &ensp;&ensp;&ensp;&ensp;&#124;─permissions | array | 角色权限列表（非数据库字段） |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─ | object |  |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─permissionId | integer | 权限ID |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─permissionName | string | 权限名称 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─component | string | 权限路径 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─createBy | string | 创建者 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─createTime | string | 创建时间 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─updateBy | string | 更新者 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─updateTime | string | 更新时间 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─remark | string | 备注 |

**Request Demo:**

```json
{
  "userId": 0,
  "userName": "",
  "nickName": "",
  "email": "",
  "phonenumber": "",
  "sex": "",
  "avatar": "",
  "password": "",
  "status": "",
  "loginIp": "",
  "loginDate": "",
  "pwdUpdateDate": "",
  "createBy": "",
  "createTime": "",
  "updateBy": "",
  "updateTime": "",
  "remark": "",
  "roles": [
    {
      "roleId": 0,
      "roleName": "",
      "roleSort": 0,
      "status": "",
      "createBy": "",
      "createTime": "",
      "updateBy": "",
      "updateTime": "",
      "remark": "",
      "permissions": [
        {
          "permissionId": 0,
          "permissionName": "",
          "component": "",
          "createBy": "",
          "createTime": "",
          "updateBy": "",
          "updateTime": "",
          "remark": ""
        }
      ]
    }
  ]
}
```



> RESPONSE

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| content-type | application/json;charset=UTF-8 | NO |  |

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| code | integer | 状态码（成功:200 失败：其他） |
| message | string | 提示消息 |
| data | object | 返回数据 |

**Response Demo:**

```json
{
  "code": 0,
  "message": "",
  "data": {}
}
```




---
## 修改用户

> BASIC

**Path:** /user/updateUser

**Method:** PUT

> REQUEST

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | YES |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| userId | integer | 用户ID |
| userName | string | 用户账号 |
| nickName | string | 用户昵称 |
| email | string | 用户邮箱 |
| phonenumber | string | 手机号码 |
| sex | string | 用户性别（0男 1女 2未知） |
| avatar | string | 头像地址 |
| password | string | 密码 |
| status | string | 账号状态（0正常 1停用） |
| loginIp | string | 最后登录IP |
| loginDate | string | 最后登录时间 |
| pwdUpdateDate | string | 密码最后更新时间 |
| createBy | string | 创建者 |
| createTime | string | 创建时间 |
| updateBy | string | 更新者 |
| updateTime | string | 更新时间 |
| remark | string | 备注 |
| roles | array | 用户角色列表（非数据库字段） |
| &ensp;&ensp;&#124;─ | object |  |
| &ensp;&ensp;&ensp;&ensp;&#124;─roleId | integer | 角色ID |
| &ensp;&ensp;&ensp;&ensp;&#124;─roleName | string | 角色名称 |
| &ensp;&ensp;&ensp;&ensp;&#124;─roleSort | integer | 显示顺序 |
| &ensp;&ensp;&ensp;&ensp;&#124;─status | string | 角色状态（0正常 1停用） |
| &ensp;&ensp;&ensp;&ensp;&#124;─createBy | string | 创建者 |
| &ensp;&ensp;&ensp;&ensp;&#124;─createTime | string | 创建时间 |
| &ensp;&ensp;&ensp;&ensp;&#124;─updateBy | string | 更新者 |
| &ensp;&ensp;&ensp;&ensp;&#124;─updateTime | string | 更新时间 |
| &ensp;&ensp;&ensp;&ensp;&#124;─remark | string | 备注 |
| &ensp;&ensp;&ensp;&ensp;&#124;─permissions | array | 角色权限列表（非数据库字段） |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─ | object |  |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─permissionId | integer | 权限ID |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─permissionName | string | 权限名称 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─component | string | 权限路径 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─createBy | string | 创建者 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─createTime | string | 创建时间 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─updateBy | string | 更新者 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─updateTime | string | 更新时间 |
| &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&#124;─remark | string | 备注 |

**Request Demo:**

```json
{
  "userId": 0,
  "userName": "",
  "nickName": "",
  "email": "",
  "phonenumber": "",
  "sex": "",
  "avatar": "",
  "password": "",
  "status": "",
  "loginIp": "",
  "loginDate": "",
  "pwdUpdateDate": "",
  "createBy": "",
  "createTime": "",
  "updateBy": "",
  "updateTime": "",
  "remark": "",
  "roles": [
    {
      "roleId": 0,
      "roleName": "",
      "roleSort": 0,
      "status": "",
      "createBy": "",
      "createTime": "",
      "updateBy": "",
      "updateTime": "",
      "remark": "",
      "permissions": [
        {
          "permissionId": 0,
          "permissionName": "",
          "component": "",
          "createBy": "",
          "createTime": "",
          "updateBy": "",
          "updateTime": "",
          "remark": ""
        }
      ]
    }
  ]
}
```



> RESPONSE

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| content-type | application/json;charset=UTF-8 | NO |  |

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| code | integer | 状态码（成功:200 失败：其他） |
| message | string | 提示消息 |
| data | object | 返回数据 |

**Response Demo:**

```json
{
  "code": 0,
  "message": "",
  "data": {}
}
```




---
## 重置密码

> BASIC

**Path:** /user/resetPassword

**Method:** PUT

> REQUEST

**Query:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| userId |  | NO | 用户ID |
| newPassword |  | NO | 新密码 |



> RESPONSE

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| content-type | application/json;charset=UTF-8 | NO |  |

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| code | integer | 状态码（成功:200 失败：其他） |
| message | string | 提示消息 |
| data | object | 返回数据 |

**Response Demo:**

```json
{
  "code": 0,
  "message": "",
  "data": {}
}
```




---
## 修改用户状态

> BASIC

**Path:** /user/changeStatus

**Method:** PUT

> REQUEST

**Query:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| userId |  | NO | 用户ID |
| status |  | NO | 新状态 |



> RESPONSE

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| content-type | application/json;charset=UTF-8 | NO |  |

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| code | integer | 状态码（成功:200 失败：其他） |
| message | string | 提示消息 |
| data | object | 返回数据 |

**Response Demo:**

```json
{
  "code": 0,
  "message": "",
  "data": {}
}
```




---
## 删除用户

> BASIC

**Path:** /user/deleteUser

**Method:** DELETE

> REQUEST

**Query:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| ids |  | NO | 用户ID字符串，多个ID用逗号分割 |



> RESPONSE

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| content-type | application/json;charset=UTF-8 | NO |  |

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| code | integer | 状态码（成功:200 失败：其他） |
| message | string | 提示消息 |
| data | object | 返回数据 |

**Response Demo:**

```json
{
  "code": 0,
  "message": "",
  "data": {}
}
```




---
## assignRoles

> BASIC

**Path:** /user/assignRoles

**Method:** PUT

> REQUEST

**Query:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| userId |  | YES |  |
| roleIds |  | NO |  |



> RESPONSE

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| content-type | application/json;charset=UTF-8 | NO |  |

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| code | integer | 状态码（成功:200 失败：其他） |
| message | string | 提示消息 |
| data | object | 返回数据 |

**Response Demo:**

```json
{
  "code": 0,
  "message": "",
  "data": {}
}
```



