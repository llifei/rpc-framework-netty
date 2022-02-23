package org.lff.rpc.test;

import org.lff.api.HelloObject;
import org.lff.api.HelloService;
import org.lff.rpc.annotation.Service;

@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(HelloObject helloObject) {
        System.out.println("接收到：" + helloObject.getMessage());
        return "这是调用的返回值：id=" +helloObject.getId();
    }
}
