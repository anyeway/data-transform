-- 更新sql
ALTER TABLE `c_storeobject_mapping_log`
ADD COLUMN `cloud_file_id`  bigint NULL ;


ALTER TABLE `c_storeobject_mapping`
ADD COLUMN `upload_vip` TINYINT DEFAULT 0  NULL  COMMENT '上传vip，为1的可以走vip通道';