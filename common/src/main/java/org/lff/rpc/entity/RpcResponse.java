package org.lff.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lff.rpc.enumeration.ResponseCode;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> implements Serializable {
    //响应状态码
    private Integer statusCode;
    //响应状态补充信息
    private String message;
    //响应数据
    private T data;

    /**
     * 响应成功，将响应数据包装为 RpcResponse 对象并设置状态码等信息
     * @param data
     * @param <T>
     * @return
     */
    public static <T> RpcResponse<T> success(T data){
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setMessage(ResponseCode.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }

    /**
     * 响应失败，返回保存失败状态的 RpcResponse 对象
     * @param code 保存失败状态信息的 ResponseCode 对象
     * @param <T>
     * @return
     */
    public static <T> RpcResponse<T> fail(ResponseCode code){
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
