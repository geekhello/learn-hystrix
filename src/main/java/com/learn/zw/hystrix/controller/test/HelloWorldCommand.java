package com.learn.zw.hystrix.controller.test;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

import java.util.concurrent.TimeUnit;

public class HelloWorldCommand extends HystrixCommand<String> {

    private final String name;

    public HelloWorldCommand(String name) {
        // 指定命令组名（最少配置）
        super(HystrixCommandGroupKey.Factory.asKey("HelloWorld"));
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        return "Hello," + name + ",Thread：" + Thread.currentThread().getName();
    }

    public static void main(String[] args) throws Exception{
        // ===========通过命令方式进行依赖调用处理=================
        // 创建实例，每个command对象只能调用一次不可以重复调用
        HelloWorldCommand command = new HelloWorldCommand("synchronous hystrix");
        /**
         * 调用方式分为两种：同步调用和异步调用
         * 同步调用：execute()，等同于queue().get()
         * 异步调用：queue().get(timeout,TimeUnit)，timeout不能超过command定义的超时时间
         */
        String result1 = command.execute();
        System.out.println("result1=" + result1);

        HelloWorldCommand command1 = new HelloWorldCommand("asynchronous hystrix");
        String result2 = command1.queue().get(100, TimeUnit.MILLISECONDS);
        System.out.println("result2=" + result2);

        // =================注册异步事件回调执行==========================
        // 1、注册观察者事件
        HelloWorldCommand command2 = new HelloWorldCommand("World");
        Observable<String> observe = command2.observe();
        // 2、注册结果回调
        observe.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                // 对返回结果做处理
                System.out.println("call：" + s);
            }
        });
        // 3、注册完整的执行生命周期
        observe.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                // 完成之后的回调，在onNext、onError之后执行
                System.out.println("onCompleted。。。");
            }

            @Override
            public void onError(Throwable throwable) {
                // 发生异常回调
                System.out.println("onError：" + throwable.getMessage());
            }

            @Override
            public void onNext(String s) {
                // 获取结果后回调
                System.out.println("onNext：" + s);
            }
        });

        // =============使用fallback实现降级回调======================



    }


}
