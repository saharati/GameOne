CREATE TABLE IF NOT EXISTS `announcements` (
  `order` tinyint(1) unsigned NOT NULL,
  `msg` varchar(25) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`order`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;