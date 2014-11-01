CREATE TABLE `crowdofflinemess` (
  `index` int(11) NOT NULL AUTO_INCREMENT,
  `crowdid` int(11) NOT NULL,
  `userid` int(11) NOT NULL,
  `offlinemessid` int(11) NOT NULL,
  `readed` int(11) NOT NULL,
  PRIMARY KEY (`index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `friendship` (
  `index` int(11) NOT NULL AUTO_INCREMENT,
  `userid_a` int(11) NOT NULL,
  `userid_b` int(11) NOT NULL,
  PRIMARY KEY (`index`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

CREATE TABLE `offlinemessage` (
  `index` int(11) NOT NULL AUTO_INCREMENT,
  `message` varchar(255) NOT NULL,
  `fromuserid` int(11) NOT NULL,
  `touserid` int(11) NOT NULL,
  PRIMARY KEY (`index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
  `_id` int(20) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `_name` varchar(50) NOT NULL COMMENT '昵称',
  `_password` varchar(50) NOT NULL COMMENT '密码',
  `_email` varchar(50) NOT NULL COMMENT '邮箱',
  `_isOnline` int(20) NOT NULL DEFAULT '0' COMMENT '是否在线',
  `_img` int(20) DEFAULT '0' COMMENT '用户头像',
  `_time` varchar(50) NOT NULL COMMENT '用户注册时间',
  `iscrowd` int(11) DEFAULT NULL,
  PRIMARY KEY (`_id`,`_email`)
) ENGINE=InnoDB AUTO_INCREMENT=2059 DEFAULT CHARSET=utf8;
