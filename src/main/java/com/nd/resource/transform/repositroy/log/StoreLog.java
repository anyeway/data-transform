package com.nd.resource.transform.repositroy.log;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by way on 2016/9/5.
 */
@Entity
@Table(name = "c_storeobject_mapping_log")
@Data
public class StoreLog {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "log")
    private String log;

    @Column(name = "cloud_id")
    private Long cloudId;

    @Column(name = "cs_status")
    private Integer csStatus;

    @Column(name = "used_time")
    private String usedTime;

}
