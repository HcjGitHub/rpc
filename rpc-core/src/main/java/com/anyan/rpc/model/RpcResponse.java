package com.anyan.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * rpc响应
 *
 * @author anyan
 * DateTime: 2024/6/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse implements Serializable {

    /**
     * 响应数据
     */
    private Object data;

    /**
     * 响应数据类型
     */

    private Class<?> dataType;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 异常信息
     */
    private Exception exception;
}
