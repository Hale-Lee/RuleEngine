

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for META_CUSTOMER_AUDIT_ITEM
-- ----------------------------
DROP TABLE IF EXISTS `META_CUSTOMER_AUDIT_ITEM`;
CREATE TABLE `META_CUSTOMER_AUDIT_ITEM` (
  `item_no` varchar(32) NOT NULL,
  `content` varchar(128) DEFAULT NULL COMMENT '中文的内容说明',
  `exe_sql` varchar(512) DEFAULT NULL  COMMENT '执行检查的SQL语句',
  `exe_class` varchar(128) DEFAULT NULL COMMENT '执行检查的java类名， 与exe_sql二者只填写一项',
  `param_name` varchar(128) DEFAULT NULL COMMENT 'exe_sql或者exe_class的参数，多个参数用逗号（,）分割。',
  `param_type` varchar(255) DEFAULT NULL COMMENT 'exe_sql或者exe_class的参数类型，多个类型用逗号（,）分割，与param_name需一一对应。',
  `comparison_code` varchar(32) DEFAULT NULL COMMENT '01: == ，  02: > ， 03 : < ， 04 != ， 05 >= ， 06: <= ， 07 include ， 08 exclude ， 09: included by 10: excluded by  11: equal , 12 : not equal 13: euqalIngoreCase',
  `comparison_value` varchar(64) DEFAULT NULL  COMMENT ' =,>,<,>=,<=, !=, include, exclude等内容。' ,
  `baseline` varchar(64) DEFAULT NULL COMMENT '参数值，比较目标值',
  `result` varchar(6) DEFAULT NULL COMMENT '1 - 通过  2 - 关注 3 -拒绝  逻辑运算满足目标值的时候读取改内容。',
  `executor` varchar(128) DEFAULT NULL COMMENT '结果执行后的被执行体，从AbstractCommand中继承下来。',
  `priority` varchar(32) DEFAULT NULL COMMENT '执行的优先顺序，值大的优先执行.', 
  `continue_flag` varchar(2) DEFAULT NULL COMMENT '1: 继续执行下一条 其他：中断执行',
  `parent_item_no` varchar(32) DEFAULT NULL COMMENT '如果是子规则，那么需要填写父规则的item_no',
  `group_express` varchar(256) DEFAULT NULL COMMENT '同一PARENT_ITEM的各个ITEM的运算表达式。 ( A AND B OR C)',
  `remark` varchar(128) DEFAULT NULL,
  `comments` varchar(128) DEFAULT NULL,
  `enable_flag` varchar(2) DEFAULT NULL COMMENT '是否有效，enable_flag = 1表示有效，其余: 无效',
  `create_time` datetime(6) DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`item_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
