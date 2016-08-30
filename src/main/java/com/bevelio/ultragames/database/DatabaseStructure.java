package com.bevelio.ultragames.database;

public class DatabaseStructure
{
/*	Players
 *  player_id, player_uuid, player_name, player_lowercase_name, killstreak, deaths, kills, wins, games, crystals (currency)
 *  
 *  Match
 *  match_id, match_name, match_start, match_end, match_winner
 *  
 *  Combat
 *  combat_id, killer_id, victim_id, killer_weapon, match_id,combat_timestamp
 */
	public final static String CREATE_PLAYER_TABLE 		= 	"CREATE TABLE IF NOT EXISTS Players (`player_id` INT NOT NULL AUTO_INCREMENT, `player_uuid` VARCHAR(32), `player_name` VARCHAR(18), `player_lowwercase_name` VARCHAR(18), `crystals` INT(11) NOT NULL default '0', `killstreak` INT(11) NOT NULL default '0', `kills` INT(11) NOT NULL default '0', `deaths` INT(11) NOT NULL default '0', `first_login` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (player_id), UNIQUE KEY (player_uuid));"
							 , CREATE_MATCH_TABLE 		= 	"CREATE TABLE IF NOT EXISTS Matches (`match_id` INT NOT NULL AUTO_INCREMENT, `match_name` VARCHAR(32), `match_start` TIMESTAMP, `match_end` TIMESTAMP,`match_winner` VARCHAR(32), PRIMARY KEY (match_id));"
							 , CREATE_COMBAT_TABLE 		= 	"CREATE TABLE IF NOT EXISTS Combat (`combat_id` INT NOT NULL AUTO_INCREMENT, `killer_id_or_reason` VARCHAR(64), `victim_id` INT, `match_id` INT, `killer_weapon` VARCHAR(32), `combat_timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (match_id));"
							 
							 , SELECT_PLAYER_BY_UUID	=	"SELECT * FROM Players WHERE `player_uuid`=?"
							 
							 , INSERT_NEW_PLAYER		=	"INSERT INTO Players (`player_uuid`, `player_name`, `player_lowwercase_name`) VALUES (?, ?, ?);"
							 , INSERT_NEW_MATCH			=	"INSERT INTO Matches (`match_name`, `match_start`, `match_end`, `match_winner`) VALUES (?, ?, ?, ?);"
							 , INSERT_NEWCOMBAT			=	"INSERT INTO Combat () VALUES (?, ?, ?);";
	
}
