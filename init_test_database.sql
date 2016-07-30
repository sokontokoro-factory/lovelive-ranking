CREATE DATABASE IF NOT EXISTS LOVELIVE_GAME_DB;

CREATE TABLE USER (
  `ID` bigint(20) NOT NULL,
  `ADMIN` char(1) NOT NULL,
  `CREATE_DATE` bigint(20) NOT NULL,
  `DELETED` char(1) NOT NULL,
  `FAVORITE_ID` int(11) DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  `UPDATE_DATE` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE SCORE (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `COUNT` int(11) NOT NULL,
  `CREATE_DATE` bigint(20) NOT NULL,
  `FINAL_DATE` bigint(20) NOT NULL,
  `GAME_ID` int(11) NOT NULL,
  `POINT` int(11) NOT NULL,
  `UPDATE_DATE` bigint(20) DEFAULT NULL,
  `USER_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_SCORE_USER_ID` (`USER_ID`),
  CONSTRAINT `FK_SCORE_USER_ID` FOREIGN KEY (`USER_ID`) REFERENCES USER (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE GAME_LOG (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CLIENT_IP` varchar(255) DEFAULT NULL,
  `GAME_ID` int(11) NOT NULL,
  `LOCALE` varchar(255) DEFAULT NULL,
  `PLAY_DATE` bigint(20) NOT NULL,
  `POINT` int(11) NOT NULL,
  `SESSION_ID` varchar(255) DEFAULT NULL,
  `START_SESSION_DATE` bigint(20) DEFAULT NULL,
  `USER_AGENT` varchar(255) DEFAULT NULL,
  `USER_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_GAME_LOG_USER_ID` (`USER_ID`),
  CONSTRAINT `FK_GAME_LOG_USER_ID` FOREIGN KEY (`USER_ID`) REFERENCES USER (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;