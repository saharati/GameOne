CREATE TABLE IF NOT EXISTS `announcements` (
  `order` tinyint(1) unsigned NOT NULL,
  `msg` varchar(25) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`order`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT IGNORE INTO `announcements` VALUES ('1', 'Welcome to GameOne!');
INSERT IGNORE INTO `announcements` VALUES ('2', 'This is example announce!');
INSERT IGNORE INTO `announcements` VALUES ('3', 'Admins can edit it.');