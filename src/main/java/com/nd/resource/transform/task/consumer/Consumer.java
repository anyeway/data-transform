package com.nd.resource.transform.task.consumer;

import com.nd.gaea.core.utils.StringUtils;
import com.nd.resource.transform.context.SpringContext;
import com.nd.resource.transform.repositroy.StoreObjectMapping;
import com.nd.resource.transform.repositroy.StoreObjectMappingRepository;
import com.nd.resource.transform.repositroy.log.StoreLog;
import com.nd.resource.transform.repositroy.log.StoreLogRepository;
import com.nd.sdp.cs.sdk.Dentry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by way on 2016/8/29.
 */
public class Consumer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);


    private StoreObjectMapping storeObjectMapping;
    private StoreObjectMappingRepository storeObjectMappingRepository;
    private static String CS_SERVICE_NAME = null;
    private StoreLogRepository storeLogRepository;

    public Consumer(StoreObjectMapping storeObjectMapping) {
        this.storeObjectMapping = storeObjectMapping;
        this.storeObjectMappingRepository = SpringContext.getBean(StoreObjectMappingRepository.class);
        storeLogRepository = SpringContext.getBean(StoreLogRepository.class);
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        saveLog(" #consumer#  task,begin ",storeObjectMapping.getCloudId(),storeObjectMapping.getCsStatus(),null,storeObjectMapping.getCloudResType(),storeObjectMapping.getCloudFileId());

        // todo 待修改为只改状态
        int uploadCount = storeObjectMapping.getCsUploadCount() + 1;
        storeObjectMapping.setCsUploadCount(uploadCount);
        try {
            boolean uploadResult = false;
            // 上传
            upload(storeObjectMapping);
            uploadResult = true;
            if (uploadResult) {
                storeObjectMapping.setCsStatus(StoreObjectMapping.CsStatus.UPLOAD_SUCCESS.getValue());
            } else {
                if (uploadCount > 5) {
                    //超过重试次数
                    storeObjectMapping.setCsStatus(StoreObjectMapping.CsStatus.UPLOAD_FAIL.getValue());
                } else {
                    //超过重试次数
                    storeObjectMapping.setCsStatus(StoreObjectMapping.CsStatus.NOT_UPLOAD.getValue());
                }
            }
            storeObjectMappingRepository.save(storeObjectMapping);
            long endTime = System.currentTimeMillis();
            saveLog(" #consumer#  task,used time " + (endTime - startTime) + "ms",storeObjectMapping.getCloudId(),storeObjectMapping.getCsStatus(),(endTime-startTime),storeObjectMapping.getCloudResType(),storeObjectMapping.getCloudFileId());
        } catch (Exception e) {
            LOGGER.error("", e);
            saveLog(getAllErrorMsg(e),storeObjectMapping.getCloudId(),storeObjectMapping.getCsStatus(),null,storeObjectMapping.getCloudResType(),storeObjectMapping.getCloudFileId());
            storeObjectMapping.setCsStatus(StoreObjectMapping.CsStatus.UPLOAD_FAIL.getValue());
            storeObjectMappingRepository.save(storeObjectMapping);
        }

    }

    private void upload(StoreObjectMapping storeObjectMapping) throws Exception {
        //文件
        byte[] bytes = null;
        File file = null;
        if (StringUtils.isEmpty(storeObjectMapping.getCloudResPath())) {
            //如果没有物理路径
            bytes = storeObjectMapping.getCloudResContent().getBytes("UTF-8");
        } else {
            String replace = File.separator;
            if ("\\".equals(File.separator)) {
                replace = "\\\\";
            }
            String filePath = storeObjectMapping.getCloudStoreRootPath() + File.separator + storeObjectMapping.getCloudResPath();
            filePath = filePath.replaceAll("/", replace).replaceAll("\\\\", replace);
            file = new File(filePath);
        }
        //自定义元数据
        Map<String, Object> info = new HashMap<>();
        info.put("custom_type", "elearning_data");
        //
        String csResPath = storeObjectMapping.getCsResPath();
        if (CS_SERVICE_NAME == null) {
            CS_SERVICE_NAME = csResPath.substring(1, csResPath.indexOf("/", 1));
        }
        //
        Dentry request = new Dentry();
        if (StringUtils.isNotEmpty(storeObjectMapping.getCsDentryId())) {
            request.setDentryId(storeObjectMapping.getCsDentryId()); //有传此项 覆盖上传，此时不用传parentId path
        } else {
            request.setPath(csResPath);                                    //父目录项路径，支持自动创建目录，（path 和 parent_id 二选一）
        }
        request.setScope(1);                                          //0-私密，1-公开，默认：0，可选
        request.setName(storeObjectMapping.getCloudResFileNickName());//文件名，包括后缀名 必选
        request.setInfo(info);
        //
        Dentry resDentryh;
        if (file == null) {
            resDentryh = request.upload(CS_SERVICE_NAME, bytes, null, storeObjectMapping.getCsSessionId(), null);
        } else {
            resDentryh = request.upload(CS_SERVICE_NAME, file, null, storeObjectMapping.getCsSessionId(), null);
        }
        storeObjectMapping.setCsDentryId(resDentryh.getDentryId());
        storeObjectMapping.setCsParentId(resDentryh.getParentId());
    }


    private void saveLog(String log,Long cloudId,Integer csStatus,Long usedTime,Long cloudResType,Long cloudFileId) {
        StoreLog storeLog = new StoreLog();
        storeLog.setLog(log);
        storeLog.setCloudId(cloudId);
        storeLog.setCsStatus(csStatus);
        storeLog.setUsedTime(usedTime);
        storeLog.setCloudResType(cloudResType);
        storeLog.setCloudFileId(cloudFileId);
        storeLogRepository.save(storeLog);
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
