package com.nd.resource.transform.repositroy.cs;

import lombok.Data;

/**
 * Created by Administrator on 2016/8/15.
 */
@Data
public class CsSession {

    private String session;

    private long expireAt;
}
