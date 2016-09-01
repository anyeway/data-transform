package com.nd.resource.transform.repositroy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Created by way on 2016/8/30.
 */
public interface StoreObjectMappingRepository extends JpaRepository<StoreObjectMapping, Long> {


    Page<StoreObjectMapping> findByCsStatusAndCloudNetworkSystem(int csStatus,int networkSystem, Pageable pageable);
}
