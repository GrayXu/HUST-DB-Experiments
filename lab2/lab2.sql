#建表
/*Table structure for table `actin` */
CREATE TABLE `actin` (
  `ACTID` INT(11) NOT NULL,
  `FID` INT(11) NOT NULL,
  `ISLEADING` VARCHAR(1) DEFAULT NULL,
  `GRADE` INT(11) DEFAULT NULL,
  PRIMARY KEY (`ACTID`,`FID`),
  KEY `FID` (`FID`),
  CONSTRAINT `actin_ibfk_1` FOREIGN KEY (`FID`) REFERENCES `film` (`fid`),
  CONSTRAINT `actin_ibfk_2` FOREIGN KEY (`ACTID`) REFERENCES `actor` (`actid`)
);
/*Data for the table `actin` */
INSERT  INTO `actin`(`ACTID`,`FID`,`ISLEADING`,`GRADE`) VALUES (1,1,'Y',99),(1,2,'Y',98),(2,1,'Y',10),(3,5,'Y',99);

/*Table structure for table `actor` */

CREATE TABLE `actor` (
  `ACTID` INT(11) NOT NULL,
  `ANAME` VARCHAR(30) DEFAULT NULL,
  `SEX` VARCHAR(2) DEFAULT NULL,
  `BYEAR` INT(11) DEFAULT NULL,
  PRIMARY KEY (`ACTID`)
);
/*Data for the table `actor` */
INSERT  INTO `actor`(`ACTID`,`ANAME`,`SEX`,`BYEAR`) VALUES (1,'男主角','男',1997),(2,'大魔王','男',1980),(3,'女主角','女',1996),(4,'路人甲','男',1983);

/*Table structure for table `film` */

CREATE TABLE `film` (
  `FID` INT(11) NOT NULL,
  `FNAME` VARCHAR(255) DEFAULT NULL,
  `FTYPE` VARCHAR(255) DEFAULT NULL,
  `DNAME` VARCHAR(255) DEFAULT NULL,
  `FLENGTH` INT(11) DEFAULT NULL,
  `IS3D` VARCHAR(255) DEFAULT NULL,
  `GRADE` INT(11) DEFAULT NULL,
  PRIMARY KEY (`FID`)
);

/*Data for the table `film` */
INSERT  INTO `film`(`FID`,`FNAME`,`FTYPE`,`DNAME`,`FLENGTH`,`IS3D`,`GRADE`) VALUES (1,'战狼','动作片','吴宇森',120,'Y',10),(2,'战狼2','警匪片','导演1',12,'Y',91),(3,'战狼3','枪战片 ','导演1',50,'N',80),(4,'战狼4','科幻片','吴宇森',79,'Y',NULL),(5,'星球大战','科幻','导演2',NULL,NULL,89);

/*Table structure for table `showt` */

CREATE TABLE `showt` (
  `FID` INT(11) NOT NULL,
  `TID` INT(11) NOT NULL,
  `PRICEC` INT(11) DEFAULT NULL,
  `SYEAR` INT(11) DEFAULT NULL,
  `SMONTH` INT(11) DEFAULT NULL,
  PRIMARY KEY (`FID`,`TID`),
  KEY `TID` (`TID`),
  CONSTRAINT `showt_ibfk_1` FOREIGN KEY (`TID`) REFERENCES `theater` (`tid`),
  CONSTRAINT `showt_ibfk_2` FOREIGN KEY (`FID`) REFERENCES `film` (`fid`)
);

/*Data for the table `showt` */
INSERT  INTO `showt`(`FID`,`TID`,`PRICEC`,`SYEAR`,`SMONTH`) VALUES (1,1,10,2017,1),(1,2,15,2017,11),(1,3,1,2018,11),(1,4,NULL,2017,2),(2,1,11,2017,10),(2,2,10,2017,11),(2,3,12,2016,12),(4,1,NULL,2018,NULL),(4,2,NULL,2018,NULL),(4,3,NULL,2018,NULL),(4,4,NULL,2000,1);


/*Table structure for table `theater` */
CREATE TABLE `theater` (
  `TID` INT(11) NOT NULL,
  `TNAME` VARCHAR(20) DEFAULT NULL,
  `TAREA` VARCHAR(20) DEFAULT NULL,
  `ADDRESS` VARCHAR(30) DEFAULT NULL,
  PRIMARY KEY (`TID`)
);

/*Data for the table `theater` */
INSERT  INTO `theater`(`TID`,`TNAME`,`TAREA`,`ADDRESS`) VALUES (1,'沁苑415','洪山区','洪山区1'),(2,'天河影院','江汉区','江汉区1'),(3,'环球影院','武昌区','武昌区1'),(4,'金逸影院','洪山区','洪山区2');

#2.2
#1)增删改
INSERT INTO film (FID,FNAME)
VALUES (8,'测试电影');

UPDATE film
SET FNAME='修改电影名字'
WHERE FID=8;

DELETE FROM film
WHERE FID=8;

#2) 
CREATE TABLE YOUNG_ACTOR
SELECT * FROM actor
WHERE BYEAR > 1989 AND BYEAR < 2000

#3) 
SELECT * FROM YOUNG_ACTOR INTO OUTFILE  'test.txt';

#4) 观察实验
CREATE TABLE test(
	a INT,
	b INT
);
INSERT INTO test (a,b) VALUES (1,1);
INSERT INTO test (a,b) VALUES (1,1);
UPDATE test SET a=2 WHERE a=1;
#均发生了修改
SELECT * FROM test;
DROP TABLE test;

#5) 创建视图
CREATE VIEW view_test AS
SELECT actor.ACTID, actor.ANAME, actor.BYEAR, COUNT(*),MAX(film.GRADE)
FROM actor, actin, film
WHERE actor.ACTID = actin.ACTID AND film.FID = actin.FID AND actin.ISLEADING = 'Y' AND actor.BYEAR>1979 AND actor.BYEAR<1990
GROUP BY actor.ACTID
SELECT * FROM view_test;

DROP VIEW view_test;



#6) 触发器
DELIMITER $$

CREATE
    TRIGGER tri1 BEFORE INSERT
    ON film
    FOR EACH ROW BEGIN
	IF(new.DNAME = '周星驰') THEN
	SET new.FTYPE = '喜剧片';
	END IF;
    END$$

DELIMITER ;


#2.3查询
#1）
SELECT showt.SYEAR, showt.SMONTH,theater.TNAME
FROM (film INNER JOIN showt ON film.FID=showt.FID) INNER JOIN theater ON showt.TID=theater.TID
WHERE film.FNAME = '战狼' AND theater.TAREA ='洪山区' AND showt.SYEAR = 2017
ORDER BY showt.SMONTH

#2）
SELECT * FROM film
WHERE FID NOT IN (
SELECT film.FID
FROM film,actor,actin
WHERE film.FID = actin.FID AND actin.ACTID = actor.ACTID)
ORDER BY FTYPE ASC, GRADE DESC

#3)
SELECT FID,FNAME,DNAME FROM film
WHERE FID NOT IN (
SELECT showt.FID
FROM showt,film
WHERE showt.FID = film.FID AND showt.SYEAR = 2017)

#4)
SELECT FID FROM(
SELECT FID , COUNT(TID) AS b FROM showt
GROUP BY FID) AS a
WHERE b IN (SELECT COUNT(*) FROM theater);

#5)
SELECT FID,FNAME,DNAME,GRADE FROM film
WHERE GRADE > 89
UNION
SELECT FID,FNAME,DNAME,GRADE FROM film
WHERE GRADE < 80;

#6)
SELECT DNAME, MAX(GRADE),MIN(GRADE)
FROM film
GROUP BY DNAME

#7)
SELECT DNAME,COUNT(DNAME)
FROM film
GROUP BY DNAME HAVING COUNT(DNAME) >= 2

#8)
SELECT DNAME,COUNT(DNAME),AVG(GRADE)
FROM film
WHERE DNAME IN (
SELECT DNAME FROM (SELECT DNAME FROM film WHERE GRADE >= 80) AS a
GROUP BY DNAME HAVING COUNT(DNAME) >= 2
) GROUP BY DNAME;

#9)
SELECT film.DNAME, actor.ACTID, actor.ANAME
FROM actin, actor, film
WHERE actin.ACTID = actor.ACTID AND actin.FID = film.FID AND film.DNAME IN(
SELECT DNAME
FROM film
GROUP BY DNAME HAVING COUNT(DNAME) >= 2);

#10)
SELECT ACTID, FID, AVG(actin.GRADE)
FROM actin
WHERE ISLEADING = 'Y'
GROUP BY ACTID

#11)
SELECT * FROM(
SELECT film.FNAME, showt.SYEAR, showt.SMONTH
FROM showt,film
WHERE showt.FID = film.FID AND film.GRADE > 90
ORDER BY showt.SYEAR ASC, showt.SMONTH ASC) AS a LIMIT 1;

#12)
SELECT * FROM(
SELECT film.FNAME,showt.TID, showt.SYEAR, showt.SMONTH
FROM showt,film
WHERE showt.FID = film.FID AND film.GRADE > 90
ORDER BY showt.SYEAR ASC, showt.SMONTH ASC) AS a LIMIT 1;

#13)
SELECT FID,COUNT(FID)
FROM showt
GROUP BY FID

#14)
SELECT DISTINCT DNAME	
FROM film 
WHERE FTYPE IN ('动作片','警匪片','枪战片')

#15)
SELECT film.FID, film.FNAME, theater.TNAME, showt.SYEAR, showt.SMONTH
FROM film,showt,theater
WHERE film.FID = showt.FID AND showt.TID = theater.TID AND LOCATE('战狼',film.FNAME) <> 0
ORDER BY film.FNAME ASC

#16)
SELECT a.TID 
FROM (SELECT TID,SYEAR,SMONTH FROM showt
WHERE FID = 1) AS a,(SELECT TID,SYEAR,SMONTH FROM showt
WHERE FID = 2) AS b
WHERE a.SYEAR = b.SYEAR AND a.SMONTH = b.SMONTH


#17)
SELECT actor.ACTID, actor.ANAME
FROM actin,actor
WHERE actin.ACTID = actor.ACTID AND actin.ACTID NOT IN (
SELECT ACTID
FROM actin,film
WHERE actin.FID = film.FID AND film.GRADE < 85)

#18)
SELECT actor.ANAME 
FROM actor,actin,film
WHERE actor.ACTID = actin.ACTID AND actin.FID = film.FID AND film.DNAME = '吴宇森';

#19）
SELECT actor.ACTID,actor.ANAME,film.FNAME
FROM actor
LEFT JOIN actin
ON actor.ACTID = actin.ACTID
LEFT JOIN film
ON film.FID = actin.FID

#20)
SELECT FID,FNAME
FROM film
WHERE GRADE IS NULL AND FID IN (
SELECT FID
FROM showt
GROUP BY FID HAVING COUNT(FID)>3);

#3.2.5
EXPLAIN SELECT FID,FNAME
FROM film
WHERE GRADE IS NULL AND FID IN (
SELECT FID
FROM showt
GROUP BY FID HAVING COUNT(FID)>3);

#3.2.6
ALTER TABLE film ADD premiere DATE;
UPDATE FILM SET premiere=DATE_FORMAT("2018-7-7","%y-%m-%d") WHERE FID=1;
SELECT * FROM FILM WHERE MONTH(premiere)-MONTH(NOW())=1;

DELIMITER $$
CREATE FUNCTION getNumByActorYear(actidIn INT,yearIn INT) 
RETURNS INT 
BEGIN
	RETURN (
		SELECT COUNT(*) FROM `ACTOR` 
		JOIN `ACTIN`  
		ON `ACTOR`.`ACTID`=`ACTIN`.`ACTID`  
		JOIN `FILM` 
		ON `ACTIN`.`FID`=`FILM`.`FID` AND YEAR(`premiere`)=yearnum 
		WHERE `ACTOR`.`ACTID`=actid
	); 
END$$
DELIMITER ;#not work


CREATE USER gray@localhost IDENTIFIED BY '1569348'
GRANT ALL ON lab2.* TO gray@localhost;
SELECT HOST,USER,authentication_string FROM mysql.user

source C:\Users\Dell\Desktop\1.sql