package com.anyan.rpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian 序列化器
 *
 * @author anyan
 * DateTime: 2024/6/6
 */
public class HessianSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        // 使用 ByteArrayOutputStream 来存储序列化后的字节数据
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 创建 Hessian 的 HessianOutput 实例
        HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
        // 将对象写入 Hessian 输出流
        hessianOutput.writeObject(obj);
        // 返回序列化后的字节数组
        return byteArrayOutputStream.toByteArray();

    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classType) throws IOException {
        // 使用 ByteArrayInputStream 来读取字节数据
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        // 创建 Hessian 的 HessianInput 实例
        HessianInput hessianInput = new HessianInput(byteArrayInputStream);
        // 从 Hessian 输入流读取对象并返回
        return (T) hessianInput.readObject(classType);

    }
}

