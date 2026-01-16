CREATE TABLE `category` (
 `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY  COMMENT 'ID',
`category_name` varchar(500) NOT NULL DEFAULT '' COMMENT '类目名称',
`top_category_id` int(11) NOT NULL DEFAULT 0 COMMENT '一级类目ID',
`second_category_id` int(11) NOT NULL DEFAULT 0 COMMENT '二级类目ID',
`third_category_id` int(11) NOT NULL DEFAULT 0 COMMENT '三级类目ID',
`status` int(11) NOT NULL DEFAULT 0 COMMENT '状态。1已上架；-1未上架',
`valid` int(11) NOT NULL DEFAULT 1 COMMENT '状态',
`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间') ENGINE=InnoDB DEFAULT CHARSET=utf8mb4