package com.nd.resource.transform;

import java.util.concurrent.Callable;

/**
 * Created by way on 2016/8/25.
 */
public class MyTask implements Callable<String> {

    private int i;

    public String call() throws Exception {
        Thread.sleep(5000l);
        System.out.println(Thread.currentThread().getName()+"_"+i);
        return Thread.currentThread().getName()+"_"+i;
    }

    public MyTask(int i) {
        this.i = i;
    }
}
