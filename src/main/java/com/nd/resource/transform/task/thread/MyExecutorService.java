package com.nd.resource.transform.task.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by way on 2016/8/29.
 * <p/>
 */
public class MyExecutorService {

    // 线程池维护线程的最少数量
    private static final int SIZE_CORE_POOL = 10;
    // 线程池维护线程所允许的空闲时间
    private static final long TIME_KEEP_ALIVE = 0;
    // 线程池所使用的缓冲队列大小
    private static final int SIZE_WORK_QUEUE = 500;

    //饿汉式
    private static MyExecutorService instance = new MyExecutorService();

    private MyExecutorService() {
        this.threadPoolExecutor = new ThreadPoolExecutor(SIZE_CORE_POOL, SIZE_CORE_POOL,
                TIME_KEEP_ALIVE, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(SIZE_WORK_QUEUE));
    }

    public static MyExecutorService getInstance() {
        return instance;
    }

    private ThreadPoolExecutor threadPoolExecutor;


    public int getTaskCanRunCount() {
        return SIZE_WORK_QUEUE - threadPoolExecutor.getQueue().size();
    }

    public boolean isShutdown() {
        return threadPoolExecutor.isShutdown();
    }

    public Future<?> submit(Runnable task) {
        return this.threadPoolExecutor.submit(task);
    }

}
