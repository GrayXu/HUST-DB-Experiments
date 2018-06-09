/*
SQLyog Professional v12.09 (64 bit)
MySQL - 8.0.11 : Database - lab3
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`lab3` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */;

USE `lab3`;

/*Table structure for table `car` */

DROP TABLE IF EXISTS `car`;

CREATE TABLE `car` (
  `license` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `brand` varchar(11) NOT NULL,
  `cost` int(11) NOT NULL COMMENT '租金',
  `status` int(11) NOT NULL DEFAULT '5' COMMENT '0为不在库中，1-5代表车况',
  `pledge` int(11) NOT NULL COMMENT '押金',
  PRIMARY KEY (`license`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `car` */

insert  into `car`(`license`,`brand`,`cost`,`status`,`pledge`) values ('鄂A15634','雪佛兰科鲁兹',200,5,2000),('鄂A55564','奔驰C200',4000,5,40000),('鄂A88888','奥迪A6',5000,5,50000),('鄂A89625','福特',200,4,2000),('闽A11234','大众',300,5,5000),('闽A11768','吉利',300,5,5000),('闽A12344','大众',250,4,4000),('闽A22227','马自达',300,4,5000),('闽A29934','标致',300,5,5000),('闽A73434','日产',300,5,5000),('闽A78787','起亚',300,5,5000),('闽A78887','长安',300,5,5500),('闽A89734','夏利',300,5,5000);

/*Table structure for table `customer` */

DROP TABLE IF EXISTS `customer`;

CREATE TABLE `customer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `age` int(11) DEFAULT NULL,
  `member` varchar(31) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `customer` */

insert  into `customer`(`id`,`name`,`age`,`member`) values (1,'张三',28,'N'),(2,'李四',44,'Y'),(3,'刘五',26,'Y'),(4,'赵六',23,'Y'),(5,'王七',30,'N');

/*Table structure for table `info` */

DROP TABLE IF EXISTS `info`;

CREATE TABLE `info` (
  `infoid` int(11) NOT NULL AUTO_INCREMENT,
  `license` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `customerid` int(11) NOT NULL,
  `stuffid` int(11) NOT NULL,
  `time` date NOT NULL COMMENT '20180526',
  `event` int(11) NOT NULL COMMENT '1:损坏并维修 2:罚款 3:借车 4:还车',
  `moychange` int(11) DEFAULT '0' COMMENT '这个事件中钱的流水情况',
  `detailevent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '详细事件情况',
  PRIMARY KEY (`infoid`),
  KEY `customerid` (`customerid`),
  KEY `stuffid` (`stuffid`),
  KEY `license` (`license`),
  CONSTRAINT `info_ibfk_2` FOREIGN KEY (`customerid`) REFERENCES `customer` (`id`),
  CONSTRAINT `info_ibfk_3` FOREIGN KEY (`stuffid`) REFERENCES `stuff` (`id`),
  CONSTRAINT `info_ibfk_4` FOREIGN KEY (`license`) REFERENCES `car` (`license`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `info` */

insert  into `info`(`infoid`,`license`,`customerid`,`stuffid`,`time`,`event`,`moychange`,`detailevent`) values (1,'鄂A88888',1,1,'2018-05-07',3,2200,NULL),(3,'闽A11234',2,1,'2018-05-07',3,2200,NULL),(6,'鄂A88888',1,9,'2018-05-08',4,-2000,NULL),(7,'鄂A88888',1,9,'2018-05-08',1,1000,'尾灯损坏'),(9,'闽A22227',3,1,'2018-04-10',3,5300,NULL),(10,'闽A11234',5,11,'2017-12-01',3,-200,NULL);

/*Table structure for table `stuff` */

DROP TABLE IF EXISTS `stuff`;

CREATE TABLE `stuff` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `age` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `stuff` */

insert  into `stuff`(`id`,`name`,`age`) values (1,'小张',25),(5,'小刘',23),(9,'小王',29),(10,'陈星',NULL),(11,'张天',22);

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `author` int(11) NOT NULL,
  `customerid` int(11) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `users` */

insert  into `users`(`name`,`password`,`author`,`customerid`) values ('gray','gray',1,NULL),('hover','hover',2,NULL),('user1','user1',3,1),('user2','user2',3,2),('user3','user3',3,3);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
