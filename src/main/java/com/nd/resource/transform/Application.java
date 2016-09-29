package com.nd.resource.transform;


import com.nd.gaea.core.utils.ArrayUtils;
import com.nd.resource.transform.context.SpringContext;
import com.nd.resource.transform.repositroy.cs.MyConfig;
import com.nd.resource.transform.repositroy.log.StoreLog;
import com.nd.resource.transform.repositroy.log.StoreLogRepository;
import com.nd.resource.transform.task.produce.Producer;
import com.nd.resource.transform.task.thread.MyExecutorService;
import com.nd.resource.transform.task.thread.VIPExecutorService;
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
public class Application implements EmbeddedServletContainerCustomizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        initArgs(args);
        //1. init thread pool
        MyExecutorService myExecutorService = MyExecutorService.getInstance();
        //1.2 vip thread pool
        VIPExecutorService vipExecutorService = VIPExecutorService.getInstance();
        //2. 查询数据库中需要执行的任务进行提交
        new Thread(new Producer(myExecutorService,vipExecutorService)).start();
        //3. 统计任务完成情况
        new Thread(new Counter(myExecutorService)).start();

    }

    private static void initArgs(String[] args) {
        LOGGER.warn(ArrayUtils.toString(args));
        if (ArrayUtils.isEmpty(args)) {
            return;
        }
        for (String arg : args) {
            if (arg.startsWith("network")) {
                String[] argArray = arg.split("=");
                if (argArray.length == 2) {
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

    private static class Counter implements Runnable {
        private MyExecutorService myExecutorService;
        private StoreLogRepository storeLogRepository;


        public Counter(MyExecutorService myExecutorService) {
            this.myExecutorService = myExecutorService;
            this.storeLogRepository = SpringContext.getBean(StoreLogRepository.class);

        }

        @Override
        public void run() {
            while (true) {
                saveLog(" #counter# ,taskCount=" + myExecutorService.getThreadPoolExecutor().getTaskCount()
                        + ",activeCount=" + myExecutorService.getThreadPoolExecutor().getActiveCount()
                        + ",completedTaskCount=" + myExecutorService.getThreadPoolExecutor().getCompletedTaskCount()
                        + ",taskCanRunCount=" + myExecutorService.getTaskCanRunCount()
                );
                sleep();
            }

        }

        private void saveLog(String log) {
            StoreLog storeLog = new StoreLog();
            storeLog.setLog(log);
            storeLogRepository.save(storeLog);
        }

        private static void sleep() {
            try {
                Thread.sleep(60000l);
            } catch (InterruptedException e) {
                LOGGER.error("sleep error ", e);
            }
        }
    }
}
