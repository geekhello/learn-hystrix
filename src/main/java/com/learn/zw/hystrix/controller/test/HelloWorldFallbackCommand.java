package com.learn.zw.hystrix.controller.test;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

import java.util.concurrent.TimeUnit;

public class HelloWorldFallbackCommand extends HystrixCommand<String>{

    private final String name;

    public HelloWorldFallbackCommand(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(""))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationThreadTimeoutInMilliseconds(10000)));
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        TimeUnit.MILLISECONDS.sleep(5000);
        return "Hello，" + name + "，Thread：" + Thread.currentThread().getName();
    }

    // 重载getFallback方法实现降级处理
    @Override
    protected String getFallback() {
        return "getFallback";
    }

    public static void main(String[] args) {
        HelloWorldFallbackCommand command = new HelloWorldFallbackCommand("test-fallback");
        String result = command.execute();
        System.out.println(result);
    }
}
