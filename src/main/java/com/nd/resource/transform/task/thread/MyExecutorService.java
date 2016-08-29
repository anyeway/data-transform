package com.nd.resource.transform.task.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by way on 2016/8/29.
 * <p/>
 */
public class MyExecutorService {

    //饿汉式
    private static MyExecutorService instance = new MyExecutorService();

    private MyExecutorService() {
        this.threadPoolExecutor = new ThreadPoolExecutor(4, 4,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(10));
    }

    public static MyExecutorService getInstance() {
        return instance;
    }

    private ThreadPoolExecutor threadPoolExecutor;

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

}
