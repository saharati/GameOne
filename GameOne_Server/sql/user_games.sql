CREATE TABLE IF NOT EXISTS `user_games` (
  `userId` tinyint(3) unsigned NOT NULL,
  `gameId` tinyint(2) unsigned NOT NULL,
  `score` int(11) NOT NULL,
  `wins` smallint(5) unsigned NOT NULL,
  `loses` smallint(5) unsigned NOT NULL,
  PRIMARY KEY (`userId`,`gameId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;