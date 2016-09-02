package com.nd.resource.transform;


import com.nd.resource.transform.task.produce.Producer;
import com.nd.resource.transform.task.thread.MyExecutorService;
import com.nd.sdp.cs.common.CsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by way on 2016/8/24.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        //1. init thread pool
        MyExecutorService myExecutorService = MyExecutorService.getInstance();
        //2. 查询数据库中需要执行的任务进行提交
        new Thread(new Producer(myExecutorService)).start();
    }

}
