package com.nd.resource.transform.task.consumer;

import com.nd.gaea.client.http.WafSecurityHttpClient;
import com.nd.resource.transform.context.SpringContext;
import com.nd.resource.transform.repositroy.StoreObjectMapping;
import com.nd.resource.transform.repositroy.StoreObjectMappingRepository;
import com.nd.sdp.cs.sdk.Dentry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by way on 2016/8/29.
 */
public class Consumer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);


    private StoreObjectMapping storeObjectMapping;
    private StoreObjectMappingRepository storeObjectMappingRepository;
    //todo
    private static WafSecurityHttpClient wafSecurityHttpClient = new WafSecurityHttpClient();
    private static String CS_SERVICE_NAME = null;

    public Consumer(StoreObjectMapping storeObjectMapping) {
        this.storeObjectMapping = storeObjectMapping;
        this.storeObjectMappingRepository = SpringContext.getBean(StoreObjectMappingRepository.class);
    }

    @Override
    public void run() {
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
        } catch (Exception e) {
            LOGGER.error("", e);
            storeObjectMapping.setCsStatus(StoreObjectMapping.CsStatus.UPLOAD_FAIL.getValue());
            storeObjectMappingRepository.save(storeObjectMapping);
        }

    }

    private void upload(StoreObjectMapping storeObjectMapping) throws Exception {
        //参数设置
        Dentry request = new Dentry();
        String csResPath = storeObjectMapping.getCsResPath();
        request.setPath(csResPath.substring(0, csResPath.lastIndexOf("/")));                        //父目录项路径，支持自动创建目录，（path 和 parent_id 二选一）
        request.setName(csResPath.substring(csResPath.lastIndexOf("/") + 1));//文件名，包括后缀名 必选
        request.setScope(1);                            //0-私密，1-公开，默认：0，可选 todo 确认
        String replace = File.separator;
        if ("\\".equals(File.separator)) {
            replace = "\\\\";
        }
        String filePath = storeObjectMapping.getCloudStoreRootPath()+File.separator + storeObjectMapping.getCloudResPath();
        filePath = filePath.replaceAll("/", replace).replaceAll("\\\\", replace);
        //
        if (CS_SERVICE_NAME == null) {
            CS_SERVICE_NAME = csResPath.substring(1, csResPath.indexOf("/", 1));
        }

//        byte[] bytes = FileUtils.readFileToByteArray(new File(filePath));
        Dentry resDentryh = request.upload(CS_SERVICE_NAME, new File(filePath), null, storeObjectMapping.getCsSessionId(), null);
    }

}
