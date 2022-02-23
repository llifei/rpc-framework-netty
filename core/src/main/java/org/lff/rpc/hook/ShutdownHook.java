package org.lff.rpc.hook;

import org.lff.rpc.util.NacosUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public class ShutdownHook {


    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook(){
        return shutdownHook;
    }

    public void addClearAllHook(){
        System.out.println("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(NacosUtil::clearRegistry));
    }
}
