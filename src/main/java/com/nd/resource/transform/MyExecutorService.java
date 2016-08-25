package com.nd.resource.transform;

import java.util.concurrent.*;

/**
 * Created by way on 2016/8/25.
 */
public class MyExecutorService {

//    private ExecutorService executorService;
//
//    public MyExecutorService() {
//        this.executorService = executorService;
//    }

    public static ExecutorService newThreadPool() {
        return new ThreadPoolExecutor(2, 4,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2));
    }
}
