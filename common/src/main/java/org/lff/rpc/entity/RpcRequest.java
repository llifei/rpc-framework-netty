package org.lff.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RpcRequest implements Serializable {
    //待调用接口名称
    private String interfaceName;
    //待调用方法名称
    private String methodName;
    //待调用方法的参数类型
    private Class<?>[] paramTypes;
    //待调用方法的参数
    private Object[] paramters;
}
