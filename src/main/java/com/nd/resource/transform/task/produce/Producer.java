package com.nd.resource.transform.task.produce;

import com.nd.resource.transform.task.consumer.Consumer;
import com.nd.resource.transform.task.thread.MyExecutorService;

/**
 * Created by way on 2016/8/29.
 *
 * 生产者生产任务
 */
public class Producer {

    public void create(){
        Consumer consumer = new Consumer();
        MyExecutorService.getInstance().getThreadPoolExecutor().submit(consumer);
    }

    public void produce(){
        while (true){
            //todo 当前可用的队列有多少


        }
    }


}
