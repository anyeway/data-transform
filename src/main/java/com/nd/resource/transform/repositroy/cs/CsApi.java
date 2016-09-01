package com.nd.resource.transform.repositroy.cs;

import com.nd.gaea.client.http.WafSecurityHttpClient;
import com.nd.sdp.cs.common.CsConfig;
import com.nd.sdp.cs.sdk.Dentry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * 内容服务
 * <p/>
 * Created by jsc on 2016/8/15.
 */
@Repository
public class CsApi {

//    @Value("${cs.service.id}")
//    private String serviceId;

    @Value("${cs.service.name}")
    private String serviceName;

    @Value("${cs.host}")
    private String host;

    // 客户端
    private static WafSecurityHttpClient httpClient = new WafSecurityHttpClient();

    // 缓存session
    private static Map<Long, CsSession> USER_SESSION_MAP = new HashMap<>();

    public CsSession getSession(long userId) {
        CsSession csSession = USER_SESSION_MAP.get(userId);
        if (csSession != null && csSession.getExpireAt() > System.currentTimeMillis()) {
            return csSession;
        }
        CsConfig.setHost(this.host);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("path", "/" + serviceName); //必须以 "/"+服务名称作为起始路径(例如：申请的服务名称为:example,path的开头为"/example");
        param.put("uid", "113216"); //用户uid
        param.put("role", "admin"); //取值仅限字符串"user"、"admin"(user：只能管理授权的路径下自己的目录项,admin：可以管理授权的路径下全部的目录项)。
//        param.put("service_id", serviceId);
//        param.put("expires", 5*60);//  session过期时间，单位秒
        String url = CsConfig.getHostUrl() + "/sessions";
        ResponseEntity<Map> sessionMap = httpClient.postForEntity(url, param, Map.class);
//        System.out.println("session=" + sessionMap);
        csSession = new CsSession();
        csSession.setSession((String) sessionMap.getBody().get("session"));
        csSession.setExpireAt((Long) sessionMap.getBody().get("expire_at"));
        USER_SESSION_MAP.put(userId, csSession);
        return csSession;
    }


    public Dentry uploadFile(long userId, byte[] bytes, String originalFilename) throws Exception {
        CsSession session = getSession(userId);
        Dentry dentry = new Dentry();
        dentry.setPath("/" + serviceName);
        dentry.setName(originalFilename);
        return dentry.upload(serviceName, bytes, null, session.getSession(), null);
    }
}
