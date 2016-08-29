package com.nd.resource.transform;


import java.util.concurrent.*;

/**
 * Created by way on 2016/8/24.
 */
public class Application {

    public static void main(String[] args) {
        ExecutorService executorService = MyExecutorService.newThreadPool();
        try {
            for(int i=0;i<50;i++){
//                String output = executorService.submit(new MyTask(i)).get();

                executorService.submit(new MyTask(i));
            }
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }
}
