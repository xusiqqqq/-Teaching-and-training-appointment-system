/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 8.0.17 : Database - xsap_dev
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`xsap_dev` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `xsap_dev`;

/*Table structure for table `t_consume_record` */

DROP TABLE IF EXISTS `t_consume_record`;

CREATE TABLE `t_consume_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `operate_type` varchar(10) DEFAULT NULL COMMENT '操作类型',
  `card_count_change` int(10) unsigned DEFAULT NULL COMMENT '卡次变化',
  `card_day_change` int(10) unsigned DEFAULT NULL COMMENT '有效天数变化',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作员',
  `note` varchar(255) DEFAULT NULL,
  `member_id` bigint(20) unsigned DEFAULT NULL COMMENT '会员id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `version` int(10) unsigned DEFAULT '1' COMMENT '版本',
  PRIMARY KEY (`id`),
  KEY `fk_consume_member_id` (`member_id`),
  CONSTRAINT `fk_consume_member_id` FOREIGN KEY (`member_id`) REFERENCES `t_member` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='消费记录';

/*Data for the table `t_consume_record` */

insert  into `t_consume_record`(`id`,`operate_type`,`card_count_change`,`card_day_change`,`operator`,`note`,`member_id`,`create_time`,`last_modify_time`,`version`) values 
(1,'首次绑卡',10,2,'企鹅',NULL,1,NULL,NULL,NULL),
(2,'扣款',5,3,'海鸥',NULL,1,NULL,NULL,NULL),
(3,'扣款',3,3,'海豚',NULL,2,NULL,NULL,NULL),
(4,'扣款',4,4,'海豚',NULL,NULL,NULL,NULL,1),
(5,'扣款',5,5,'海鸥',NULL,NULL,NULL,NULL,1),
(6,'扣款',6,6,'企鹅',NULL,NULL,NULL,NULL,1),
(7,'扣款',7,7,'海鸥',NULL,NULL,NULL,NULL,1),
(8,'扣款',8,8,'企鹅',NULL,NULL,NULL,NULL,1),
(9,'扣款',9,9,'企鹅',NULL,NULL,NULL,NULL,1);

/*Table structure for table `t_course` */

DROP TABLE IF EXISTS `t_course`;

CREATE TABLE `t_course` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `duration` int(10) unsigned DEFAULT NULL COMMENT '课程时长',
  `contains` int(10) unsigned DEFAULT NULL COMMENT '上课人数',
  `color` varchar(10) DEFAULT NULL COMMENT '卡片颜色',
  `introduce` varchar(255) DEFAULT NULL COMMENT '课程介绍',
  `times_cost` int(10) unsigned DEFAULT NULL COMMENT '每节课程需花费的次数',
  `limit_sex` varchar(6) DEFAULT NULL COMMENT '限制性别',
  `limit_age` int(10) unsigned DEFAULT NULL COMMENT '限制年龄',
  `limit_counts` int(10) unsigned DEFAULT NULL COMMENT '限制预约次数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `version` int(10) unsigned DEFAULT '1' COMMENT '版本',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='课程表';

/*Data for the table `t_course` */

insert  into `t_course`(`id`,`name`,`duration`,`contains`,`color`,`introduce`,`times_cost`,`limit_sex`,`limit_age`,`limit_counts`,`create_time`,`last_modify_time`,`version`) values 
(1,'历史',30,9,'blue','数学课',1,'男',6,NULL,'2020-09-09 09:47:42',NULL,2),
(2,'ds',30,9,'red','地理课',2,'女',7,NULL,NULL,NULL,1),
(3,'英语',34,4,'green','英语课',3,'男',8,NULL,NULL,NULL,NULL),
(4,'数学',45,6,'blue','数学课',1,'男',6,NULL,'2020-09-11 10:51:11',NULL,1),
(5,'数学2',45,5,'pink','数学课',2,'男',23,2,NULL,NULL,1),
(6,'数学2',45,5,'pink','数学课',2,'男',23,2,NULL,NULL,1),
(7,'数学2',45,5,'pink','数学课',2,'男',23,2,NULL,NULL,1),
(8,'数学2',45,5,'pink','数学课',2,'男',23,2,NULL,NULL,1),
(9,'数学2',45,5,'pink','数学课',2,'男',23,2,NULL,NULL,1),
(10,'语文',45,5,'pink','语文课',2,'男',23,2,NULL,NULL,NULL),
(11,'数学',45,5,'pink','数学课',2,'男',23,2,NULL,NULL,NULL),
(12,'数学',45,5,'pink','数学课',2,'男',23,2,NULL,NULL,NULL),
(13,'数学',45,5,'pink','数学课',2,'男',23,2,NULL,NULL,NULL),
(14,'数学',45,5,'pink','数学课',2,'男',23,2,NULL,NULL,NULL);

/*Table structure for table `t_course_card` */

DROP TABLE IF EXISTS `t_course_card`;

CREATE TABLE `t_course_card` (
  `card_id` bigint(20) unsigned NOT NULL COMMENT '会员卡id',
  `course_id` bigint(20) unsigned NOT NULL COMMENT '课程id',
  PRIMARY KEY (`card_id`,`course_id`),
  KEY `fk_course_course_id` (`course_id`),
  CONSTRAINT `fk_course_card_id` FOREIGN KEY (`card_id`) REFERENCES `t_member_card` (`id`),
  CONSTRAINT `fk_course_course_id` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='中间表：课程-会员卡';

/*Data for the table `t_course_card` */

insert  into `t_course_card`(`card_id`,`course_id`) values 
(1,1),
(3,1),
(1,2),
(2,3),
(3,3);

/*Table structure for table `t_employee` */

DROP TABLE IF EXISTS `t_employee`;

CREATE TABLE `t_employee` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `sex` varchar(6) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `introduce` varchar(255) DEFAULT NULL COMMENT '介绍',
  `note` varchar(255) DEFAULT NULL,
  `role_name` varchar(50) DEFAULT NULL COMMENT '操作角色名',
  `role_password` varchar(100) DEFAULT NULL COMMENT '操作角色密码',
  `role_type` tinyint(1) unsigned DEFAULT '0' COMMENT '操作角色类型，1，超级管理员；0，普通管理员',
  `is_deleted` tinyint(1) unsigned DEFAULT '0' COMMENT '逻辑删除，1有效，0无效',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `version` int(10) unsigned DEFAULT '1' COMMENT '版本',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='员工表';

/*Data for the table `t_employee` */

insert  into `t_employee`(`id`,`name`,`phone`,`sex`,`birthday`,`introduce`,`note`,`role_name`,`role_password`,`role_type`,`is_deleted`,`create_time`,`last_modify_time`,`version`) values 
(1,'张老师','12345123','女','2020-09-07','教数学','。。。','admin','123',1,NULL,NULL,NULL,NULL),
(2,'李老','467453','男','2020-09-09','地理','，，，','user','123',0,NULL,NULL,NULL,NULL),
(3,NULL,NULL,NULL,NULL,NULL,NULL,'test','111',NULL,NULL,NULL,NULL,NULL),
(4,'魏老','53459445','男',NULL,'教体育','note-2','user','123',0,0,NULL,NULL,1),
(5,'魏老','53459445','男',NULL,'教体育','note-3','user','123',0,0,NULL,NULL,1),
(6,'魏老','53459445','男',NULL,'教体育','note-6','user','123',0,0,NULL,NULL,1),
(7,'魏老','53459445','男',NULL,'教体育','note-7','user','123',0,0,NULL,NULL,1),
(8,'魏老','53459445','男',NULL,'教体育','note-8','user','123',0,0,NULL,NULL,1),
(9,'魏老','53459445','男',NULL,'教体育','note-9','user','123',0,0,NULL,NULL,1);

/*Table structure for table `t_global_reservation_set` */

DROP TABLE IF EXISTS `t_global_reservation_set`;

CREATE TABLE `t_global_reservation_set` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `start_time` int(10) unsigned DEFAULT NULL COMMENT '预约开始时间，按天算',
  `end_time` datetime DEFAULT NULL COMMENT '预约截止时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '预约取消时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `version` int(10) unsigned DEFAULT '1' COMMENT '版本',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='全局预约设置表';

/*Data for the table `t_global_reservation_set` */

insert  into `t_global_reservation_set`(`id`,`start_time`,`end_time`,`cancel_time`,`create_time`,`last_modify_time`,`version`) values 
(1,10,'2020-09-09 16:26:20','2020-09-08 16:26:23',NULL,NULL,NULL),
(2,10,NULL,NULL,NULL,NULL,1),
(3,10,NULL,NULL,NULL,NULL,1),
(4,10,NULL,NULL,NULL,NULL,1),
(5,10,NULL,NULL,NULL,NULL,1),
(6,10,NULL,NULL,NULL,NULL,1),
(7,10,NULL,NULL,NULL,NULL,1),
(8,10,NULL,NULL,NULL,NULL,1),
(9,10,NULL,NULL,NULL,NULL,1);

/*Table structure for table `t_member` */

DROP TABLE IF EXISTS `t_member`;

CREATE TABLE `t_member` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `sex` varchar(6) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '用户头像路径',
  `is_deleted` tinyint(1) unsigned DEFAULT '0' COMMENT '用户的逻辑删除，1有效，0无效',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `version` int(10) unsigned DEFAULT '1' COMMENT '版本',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='会员表';

/*Data for the table `t_member` */

insert  into `t_member`(`id`,`name`,`sex`,`phone`,`birthday`,`note`,`avatar_url`,`is_deleted`,`create_time`,`last_modify_time`,`version`) values 
(1,'lisi','男','9876543','2020-09-09','人1号','/user/img',NULL,NULL,NULL,NULL),
(2,'shun','女','1345678','2020-09-26','人2号','/user/small/img',NULL,NULL,NULL,NULL),
(3,'hui','女','345665432','2020-09-15','人3号','/user/img',NULL,NULL,NULL,NULL);

/*Table structure for table `t_member_bind_record` */

DROP TABLE IF EXISTS `t_member_bind_record`;

CREATE TABLE `t_member_bind_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) unsigned DEFAULT NULL,
  `card_id` bigint(20) unsigned DEFAULT NULL,
  `add_count` int(10) unsigned DEFAULT NULL COMMENT '充值可使用次数',
  `valid_day` int(10) unsigned DEFAULT NULL COMMENT '有效期，按天算',
  `received_money` decimal(10,2) unsigned DEFAULT NULL COMMENT '实收金额',
  `note` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `version` int(10) unsigned DEFAULT '1' COMMENT '版本',
  PRIMARY KEY (`id`),
  KEY `fk_bind_member_id` (`member_id`),
  KEY `fk_bind_card_id` (`card_id`),
  CONSTRAINT `fk_bind_card_id` FOREIGN KEY (`card_id`) REFERENCES `t_member_card` (`id`),
  CONSTRAINT `fk_bind_member_id` FOREIGN KEY (`member_id`) REFERENCES `t_member` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='中间表：会员绑定记录';

/*Data for the table `t_member_bind_record` */

insert  into `t_member_bind_record`(`id`,`member_id`,`card_id`,`add_count`,`valid_day`,`received_money`,`note`,`create_time`,`last_modify_time`,`version`) values 
(1,1,1,33,33,500.00,'绑定1',NULL,NULL,NULL),
(2,1,3,5,5,400.00,'绑定2',NULL,NULL,NULL),
(3,2,1,65,6,300.00,'绑定3',NULL,NULL,NULL),
(4,3,2,55,55,329.00,'绑定4',NULL,NULL,NULL),
(5,NULL,NULL,5,5,55.00,'bind-5',NULL,NULL,1),
(7,NULL,NULL,7,7,77.00,'bind-7',NULL,NULL,1),
(8,NULL,NULL,8,8,88.00,'bind-8',NULL,NULL,1),
(9,NULL,NULL,9,9,99.00,'绑定9',NULL,NULL,NULL),
(10,NULL,NULL,10,10,100.00,'绑定10',NULL,NULL,NULL),
(11,NULL,NULL,11,11,111.00,'绑定11',NULL,NULL,NULL),
(12,NULL,NULL,2,2,22.20,'绑定++','2020-09-11 17:22:24',NULL,1);

/*Table structure for table `t_member_card` */

DROP TABLE IF EXISTS `t_member_card`;

CREATE TABLE `t_member_card` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `price` decimal(10,2) unsigned DEFAULT NULL,
  `desc` varchar(255) DEFAULT NULL COMMENT '描述信息',
  `note` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `type` varchar(10) DEFAULT NULL COMMENT '会员卡类型',
  `total_count` int(10) unsigned DEFAULT NULL COMMENT '总可用次数',
  `total_day` int(10) unsigned DEFAULT NULL COMMENT '总可用天数',
  `status` tinyint(1) unsigned DEFAULT '0' COMMENT '激活状态，1激活，0非激活',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `version` int(10) unsigned DEFAULT '1' COMMENT '版本',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='会员卡表';

/*Data for the table `t_member_card` */

insert  into `t_member_card`(`id`,`name`,`price`,`desc`,`note`,`type`,`total_count`,`total_day`,`status`,`create_time`,`last_modify_time`,`version`) values 
(1,'一对一',240.00,'12课时','卡1','无限次',10,2,1,NULL,NULL,NULL),
(2,'6人小班',100.00,'30课时','卡2','100次',14,4,0,NULL,NULL,NULL),
(3,'15人大班',99.00,'32课时','卡3','99次',32,4,1,NULL,NULL,NULL),
(4,'12人',333.33,'18课时','note-4','无限次',NULL,NULL,0,NULL,NULL,1),
(5,'12人',333.33,'18课时','note-4','无限次',NULL,NULL,0,NULL,NULL,1),
(6,'12人',333.33,'18课时','note-6','无限次',NULL,NULL,0,NULL,NULL,1),
(7,'12人',333.33,'18课时','note-7','无限次',NULL,NULL,0,NULL,NULL,1),
(8,'12人',333.33,'18课时','note-8','无限次',NULL,NULL,0,NULL,NULL,1),
(9,'12人',333.33,'18课时','note-9','无限次',NULL,NULL,0,NULL,NULL,1);

/*Table structure for table `t_member_log` */

DROP TABLE IF EXISTS `t_member_log`;

CREATE TABLE `t_member_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(10) DEFAULT NULL COMMENT '操作类型',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作员名称',
  `member_id` bigint(20) unsigned DEFAULT NULL COMMENT '会员id',
  `create_time` datetime DEFAULT NULL,
  `last_modify_time` datetime DEFAULT NULL,
  `version` int(10) unsigned DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `fk_log_member_id` (`member_id`),
  CONSTRAINT `fk_log_member_id` FOREIGN KEY (`member_id`) REFERENCES `t_member` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='操作记录';

/*Data for the table `t_member_log` */

insert  into `t_member_log`(`id`,`type`,`operator`,`member_id`,`create_time`,`last_modify_time`,`version`) values 
(1,'充值','企鹅',1,NULL,NULL,NULL),
(2,'充值','企鹅',1,NULL,NULL,NULL),
(3,'充值','海豚',2,NULL,NULL,NULL),
(4,'扣费','企鹅',3,NULL,NULL,NULL),
(5,'扣费','海鸥',NULL,NULL,NULL,1),
(6,'扣费','海鸥',NULL,NULL,NULL,1),
(7,'扣费','海鸥',NULL,NULL,NULL,1),
(8,'扣费','海鸥',NULL,NULL,NULL,1),
(9,'充值','海豚',NULL,NULL,NULL,1);

/*Table structure for table `t_recharge_record` */

DROP TABLE IF EXISTS `t_recharge_record`;

CREATE TABLE `t_recharge_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `add_count` int(10) unsigned DEFAULT NULL COMMENT '充值可用次数',
  `add_day` int(10) unsigned DEFAULT NULL COMMENT '延长有效天数',
  `received_money` decimal(10,2) unsigned DEFAULT NULL COMMENT '实收金额',
  `note` varchar(255) DEFAULT NULL,
  `member_id` bigint(20) unsigned DEFAULT NULL COMMENT '会员id',
  `create_time` datetime DEFAULT NULL,
  `last_modify_time` datetime DEFAULT NULL,
  `version` int(10) unsigned DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `fk_charge_member_id` (`member_id`),
  CONSTRAINT `fk_charge_member_id` FOREIGN KEY (`member_id`) REFERENCES `t_member` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='充值记录';

/*Data for the table `t_recharge_record` */

insert  into `t_recharge_record`(`id`,`add_count`,`add_day`,`received_money`,`note`,`member_id`,`create_time`,`last_modify_time`,`version`) values 
(1,3,3,33.00,NULL,1,NULL,NULL,NULL),
(2,2,2,22.00,NULL,2,NULL,NULL,NULL),
(3,1,1,11.00,NULL,3,NULL,NULL,NULL),
(4,4,4,44.00,NULL,2,NULL,NULL,NULL),
(5,5,5,55.00,'note-5',NULL,NULL,NULL,1),
(6,6,6,66.00,'note-6',NULL,NULL,NULL,1),
(7,7,7,77.00,'note-7',NULL,NULL,NULL,1),
(8,8,8,88.00,'note-8',NULL,NULL,NULL,1),
(9,9,9,99.00,'note-9',NULL,NULL,NULL,1);

/*Table structure for table `t_reservation_record` */

DROP TABLE IF EXISTS `t_reservation_record`;

CREATE TABLE `t_reservation_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `order_nums` int(10) unsigned DEFAULT NULL COMMENT '预约人数',
  `status` tinyint(1) unsigned DEFAULT '0' COMMENT '预约状态，1有效，0无效',
  `comment` varchar(255) DEFAULT NULL COMMENT '教师评语',
  `note` varchar(255) DEFAULT NULL,
  `operator` varchar(50) DEFAULT NULL COMMENT '操作员',
  `member_id` bigint(20) unsigned DEFAULT NULL COMMENT '会员id',
  `schedule_id` bigint(20) unsigned DEFAULT NULL COMMENT '排课记录id',
  `create_time` datetime DEFAULT NULL,
  `last_modify_time` datetime DEFAULT NULL,
  `version` int(10) unsigned DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `fk_reserve_member_id` (`member_id`),
  KEY `fk_reserve_schedule_id` (`schedule_id`),
  CONSTRAINT `fk_reserve_member_id` FOREIGN KEY (`member_id`) REFERENCES `t_member` (`id`),
  CONSTRAINT `fk_reserve_schedule_id` FOREIGN KEY (`schedule_id`) REFERENCES `t_schedule_record` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='预约记录';

/*Data for the table `t_reservation_record` */

insert  into `t_reservation_record`(`id`,`order_nums`,`status`,`comment`,`note`,`operator`,`member_id`,`schedule_id`,`create_time`,`last_modify_time`,`version`) values 
(1,12,1,NULL,NULL,'海豚',1,1,NULL,NULL,NULL),
(2,11,0,NULL,NULL,'企鹅',1,2,NULL,NULL,NULL),
(3,7,1,NULL,NULL,'企鹅',2,3,NULL,NULL,NULL),
(4,4,0,NULL,NULL,'海豚',3,1,NULL,NULL,NULL),
(5,5,0,'不错','note-5','海鸥',NULL,NULL,NULL,NULL,1),
(6,6,0,'不错','note-6','海鸥',NULL,NULL,NULL,NULL,1),
(7,7,0,'不错','note-7','海鸥',NULL,NULL,NULL,NULL,1),
(8,8,0,'不错','note-8','海鸥',NULL,NULL,NULL,NULL,1),
(9,9,0,'不错','note-9','海鸥',NULL,NULL,NULL,NULL,1);

/*Table structure for table `t_schedule_record` */

DROP TABLE IF EXISTS `t_schedule_record`;

CREATE TABLE `t_schedule_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `course_id` bigint(20) unsigned DEFAULT NULL COMMENT '课程号',
  `teacher_id` bigint(20) unsigned DEFAULT NULL COMMENT '教师号',
  `start_date` date DEFAULT NULL COMMENT '上课日期',
  `class_time` time DEFAULT NULL COMMENT '上课时间',
  `limit_sex` varchar(6) DEFAULT NULL COMMENT '限制性别',
  `limit_age` int(10) unsigned DEFAULT NULL COMMENT '限制年龄',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `version` int(10) unsigned DEFAULT '1' COMMENT '版本',
  PRIMARY KEY (`id`),
  UNIQUE KEY `only_course_teach` (`course_id`,`teacher_id`,`class_time`),
  KEY `fk_sche_teacher_id` (`teacher_id`),
  CONSTRAINT `fk_sche_course_id` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`id`),
  CONSTRAINT `fk_sche_teacher_id` FOREIGN KEY (`teacher_id`) REFERENCES `t_employee` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='中间表：排课计划表';

/*Data for the table `t_schedule_record` */

insert  into `t_schedule_record`(`id`,`course_id`,`teacher_id`,`start_date`,`class_time`,`limit_sex`,`limit_age`,`create_time`,`last_modify_time`,`version`) values 
(1,1,1,'2020-09-08','09:00:45',NULL,NULL,NULL,NULL,NULL),
(2,3,1,'2020-09-04','09:00:33',NULL,NULL,NULL,NULL,NULL),
(3,2,2,'2020-09-08','09:00:44',NULL,NULL,NULL,NULL,NULL),
(4,1,2,'2020-09-09','14:00:55',NULL,NULL,NULL,NULL,NULL),
(5,1,2,'2020-09-09','15:30:00',NULL,NULL,NULL,NULL,NULL),
(6,NULL,NULL,'2020-09-11','14:40:00','男',8,NULL,NULL,1),
(7,NULL,NULL,'2020-09-02','14:30:00','男',7,NULL,NULL,1),
(8,NULL,NULL,'2020-09-12','14:10:00','男',6,NULL,NULL,1),
(9,NULL,NULL,'2020-09-14','11:40:00','女',9,NULL,NULL,1);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
