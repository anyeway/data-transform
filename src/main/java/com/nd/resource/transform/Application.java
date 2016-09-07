package com.nd.resource.transform;


import com.nd.gaea.core.utils.ArrayUtils;
import com.nd.resource.transform.context.SpringContext;
import com.nd.resource.transform.repositroy.cs.MyConfig;
import com.nd.resource.transform.task.produce.Producer;
import com.nd.resource.transform.task.thread.MyExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.util.SocketUtils;

/**
 * Created by way on 2016/8/24.
 */
@SpringBootApplication
public class Application implements EmbeddedServletContainerCustomizer{

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        initArgs(args);
        //1. init thread pool
        MyExecutorService myExecutorService = MyExecutorService.getInstance();
        //2. 查询数据库中需要执行的任务进行提交
        new Thread(new Producer(myExecutorService)).start();
    }

    private static void initArgs(String[] args) {
        LOGGER.warn(ArrayUtils.toString(args));
        if(ArrayUtils.isEmpty(args)){
            return;
        }
        for(String arg:args){
            if(arg.startsWith("network")){
                String[] argArray = arg.split("=");
                if(argArray.length==2){
                    //
                    SpringContext.getBean(MyConfig.class).setNetworkSystem(Integer.valueOf(argArray[1]));
                }
            }
        }

    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        container.setPort(SocketUtils.findAvailableTcpPort());
    }
}
