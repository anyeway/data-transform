package com.nd.resource.transform.task.produce;

import com.nd.gaea.core.utils.CollectionUtils;
import com.nd.resource.transform.context.SpringContext;
import com.nd.resource.transform.repositroy.StoreObjectMapping;
import com.nd.resource.transform.repositroy.StoreObjectMappingRepository;
import com.nd.resource.transform.repositroy.cs.MyConfig;
import com.nd.resource.transform.repositroy.log.StoreLog;
import com.nd.resource.transform.repositroy.log.StoreLogRepository;
import com.nd.resource.transform.task.consumer.Consumer;
import com.nd.resource.transform.task.thread.MyExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by way on 2016/8/30.
 */
public class Producer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);


    private MyExecutorService myExecutorService;
    private StoreObjectMappingRepository storeObjectMappingRepository;
    private MyConfig myConfig;
    private StoreLogRepository storeLogRepository;

    public Producer(MyExecutorService myExecutorService) {
        this.myExecutorService = myExecutorService;
        storeObjectMappingRepository = SpringContext.getBean(StoreObjectMappingRepository.class);
        myConfig = SpringContext.getBean(MyConfig.class);
        storeLogRepository = SpringContext.getBean(StoreLogRepository.class);
    }

    @Override
    public void run() {
        try{
            saveLog("#produce# network="+myConfig.getNetworkSystem());
            while (true) {
                long startTime = System.currentTimeMillis();
                if (myExecutorService.isShutdown()) {
                    //线程池已关闭
                    sleep();
                    continue;
                }
                int limit = myExecutorService.getTaskCanRunCount();
                if (limit <= 0) {
                    // 线程此队列已满
                    sleep();
                    continue;
                }
                Page<StoreObjectMapping> page = storeObjectMappingRepository.findByCsStatusAndCloudNetworkSystem(StoreObjectMapping.CsStatus.NOT_UPLOAD.getValue(), myConfig.getNetworkSystem(),new PageRequest(0, limit));
                List<StoreObjectMapping> list = page.getContent();
                if (CollectionUtils.isEmpty(list)) {
                    // 没有需要提交的任务
                    sleep();
                    continue;
                }
                for (StoreObjectMapping storeObjectMapping : list) {
                    storeObjectMapping.setCsStatus(StoreObjectMapping.CsStatus.UPLOADING.getValue());
                }
                // 设置为上传中 todo 待修改为批量
                storeObjectMappingRepository.save(list);
                // 提交任务
                for (StoreObjectMapping storeObjectMapping : list) {
                    myExecutorService.submit(new Consumer(storeObjectMapping));
                }
                long endTime = System.currentTimeMillis();
                saveLog(" #produce# " +list.size()+ " tasks,used time "+(endTime-startTime)+"ms");
            }
        }catch (Exception e){
            LOGGER.error("",e);
            saveLog(getAllErrorMsg(e));
        }
    }

    private void saveLog(String log) {
        StoreLog storeLog = new StoreLog();
        storeLog.setLog(log);
        storeLogRepository.save(storeLog);
    }

    private static void sleep() {
        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            LOGGER.error("sleep error ", e);
        }
    }

    private String getAllErrorMsg(Throwable throwable) {
        if (throwable != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            try {
                throwable.printStackTrace(pw);
                return sw.toString();
            } finally {
                pw.close();
            }
        }
        return "";
    }
}
