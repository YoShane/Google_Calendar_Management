-- --------------------------------------------------------
-- 主機:                           127.0.0.1
-- 服務器版本:                        10.3.6-MariaDB - mariadb.org binary distribution
-- 服務器操作系統:                      Win64
-- HeidiSQL 版本:                  9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- 導出 calendar_app 的資料庫結構
CREATE DATABASE IF NOT EXISTS `calendar_app` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `calendar_app`;

-- 導出  表 calendar_app.advisor_usergroup 結構
CREATE TABLE IF NOT EXISTS `advisor_usergroup` (
  `a_Id` int(11) NOT NULL AUTO_INCREMENT,
  `a_Uid` int(11) DEFAULT 0,
  `a_Gid` int(11) DEFAULT 0,
  PRIMARY KEY (`a_Id`),
  KEY `FK_advise_usergroup_user` (`a_Uid`),
  KEY `FK_advise_usergroup_group` (`a_Gid`),
  CONSTRAINT `FK_advise_usergroup_group` FOREIGN KEY (`a_Gid`) REFERENCES `group_list` (`g_Id`),
  CONSTRAINT `FK_advise_usergroup_user` FOREIGN KEY (`a_Uid`) REFERENCES `user_list` (`u_Id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- 資料導出被取消選擇。
-- 導出  表 calendar_app.group_list 結構
CREATE TABLE IF NOT EXISTS `group_list` (
  `g_Id` int(11) NOT NULL AUTO_INCREMENT,
  `g_Name` varchar(50) NOT NULL DEFAULT '0',
  PRIMARY KEY (`g_Id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- 資料導出被取消選擇。
-- 導出  表 calendar_app.user_list 結構
CREATE TABLE IF NOT EXISTS `user_list` (
  `u_Id` int(11) NOT NULL AUTO_INCREMENT,
  `u_Name` varchar(10) DEFAULT '0',
  `u_Email` varchar(50) DEFAULT '0',
  `u_Phone` varchar(15) DEFAULT '',
  PRIMARY KEY (`u_Id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- 資料導出被取消選擇。
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
