package com.nd.resource.transform.repositroy;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by way on 2016/9/7.
 */
@Data
public class StoreObjectMappingPk implements Serializable {
    private Long cloudId;

    private Long cloudResType;

    private Long cloudFileId;


}
