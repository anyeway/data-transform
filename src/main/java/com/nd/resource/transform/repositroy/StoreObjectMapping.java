package com.nd.resource.transform.repositroy;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by way on 2016/8/30.
 */
@Entity
@Table(name = "c_storeobject_mapping")
@Data
public class StoreObjectMapping {

    @Id
    @Column(name = "cloud_id")
    private Long cloudId;

    @Column(name = "cloud_res_path")
    private String cloudResPath;

    @Column(name = "cloud_res_disk")
    private String cloudResDisk;

    @Column(name = "cloud_storerootpath")
    private String cloudStoreRootPath;

    @Column(name = "cloud_networksystem")
    private Integer cloudNetworkSystem;


    @Column(name = "lc_id")
    private String lcId;

    @Column(name = "cs_id")
    private String csId;

    @Column(name = "cs_res_path")
    private String csResPath;

    @Column(name = "cs_service")
    private String csService;

    @Column(name = "cs_status")
    private Integer csStatus;

    @Column(name = "cs_upload_count")
    private Integer csUploadCount;

    @Column(name = "cs_session_id")
    private String csSessionId;



    public enum CsStatus{
        /*
        未上传（未上传过）
         */
        NOT_UPLOAD(0),
        /**
         * 上传中
         */
        UPLOADING(-1),
        /**
         * 上传成功
         */
        UPLOAD_SUCCESS(1),
        /**
         * 上传失败
         */
        UPLOAD_FAIL(-2),

        ;

        private int value;

        CsStatus(int value){
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
