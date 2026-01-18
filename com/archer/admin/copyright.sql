CREATE TABLE `copyright` (
 `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY  COMMENT 'ID',
`copyright_name` varchar(500) NOT NULL DEFAULT '' COMMENT 'IP名称',
`cp_name` varchar(500) NOT NULL DEFAULT '' COMMENT '授权方',
`status` int(11) NOT NULL DEFAULT 0 COMMENT '状态。1正常；-1已到期',
`auth_expire_date` timestamp NOT NULL  COMMENT '授权到期日期',
`top_category_id` int(11) NOT NULL DEFAULT 0 COMMENT '授权类目',
`clear_days` int(11) NOT NULL DEFAULT 0 COMMENT '预留清货天数',
`auth_url` varchar(500) NOT NULL DEFAULT '' COMMENT '授权书',
`placard_url` varchar(500) NOT NULL DEFAULT '' COMMENT '海报',
`valid` int(11) NOT NULL DEFAULT 1 COMMENT '状态',
`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间') ENGINE=InnoDB DEFAULT CHARSET=utf8mb4