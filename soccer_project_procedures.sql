USE soccer_project;

-- CREATE PROCEDURES --

-- to register a new player:
DROP PROCEDURE IF EXISTS register_player;
DELIMITER $$
CREATE PROCEDURE register_player(IN playerName VARCHAR(100), jerseyNum INT, birthday DATE, teamName VARCHAR(50), parentId INT)
	BEGIN
    
    INSERT INTO player(playerName, jerseyNum, birthday, team, parentId)
    VALUES (playerName, jerseyNum, birthday, teamName, parentId);
    
    
    END $$
DELIMITER ;


CALL register_player('lizzy hwang', 21, '2002-10-05', 'SASC 00G RED', 1);
CALL register_player('justin hwang', 7, '2004-12-19', 'SASC 03B RED', 1);
CALL register_player('sami lukpat', 8, '2002-11-20', 'SASC 00G RED', 2);
CALL register_player('christina fojas', 3, '2002-01-11', 'SASC 00G RED', 4);
SELECT * from player;
ALTER TABLE player AUTO_INCREMENT = 3;
DELETE FROM player WHERE playerId > 2;

-- to create a new game:
DROP PROCEDURE IF EXISTS create_game;
DELIMITER $$
CREATE PROCEDURE create_game(IN teamName VARCHAR(50), opponentName VARCHAR(50), location ENUM('home', 'away'), gameDate DATE, gameTime TIME)
	BEGIN
    
    INSERT INTO game(teamName, opponentName, location, gameDate, gameTime)
    VALUES (teamName, opponentName, location, gameDate, gameTime);
    
    END $$
DELIMITER ;

SELECT * FROM game;
DELETE FROM game WHERE gameId > 0;
ALTER TABLE game AUTO_INCREMENT = 1;
CALL create_game('SASC 00G RED', 'SC BREAKERS 02G BLUE', 'home', '2020-03-25', '11:30:00');
CALL create_game('SASC 07G RED', 'PALO ALTO 07G BLUE', 'away', '2020-03-25', '15:30:00');
CALL create_game('SASC 02B BLACK', 'SANTA CLARA 02B WHITE', 'home', '2020-02-27', '18:00:00');
CALL create_game('SASC 02B BLACK', 'PAJARO VALLEY 02B BLACK', 'away', '2020-04-02', '10:45:00');
CALL create_game('SASC 02B BLACK', 'MVLA 02B GREEN', 'home', '2020-02-27', '10:00:00');
CALL create_game('SASC 07G RED', 'WILLOW GLEN 07G BLUE', 'home', '2020-03-25', '8:30:00');






-- READ PROCEDURES

-- to read contact information given a coach's name
DROP PROCEDURE IF EXISTS get_coach_contact;
DELIMITER $$
CREATE PROCEDURE get_coach_contact(IN name VARCHAR(50))
	BEGIN
    
    SELECT coachName, phoneNumber, email FROM coach
    WHERE coachName = name;
    
    END $$
DELIMITER ;

SELECT * FROM coach;
CALL get_coach_contact('tim yordan');



-- to get a list of the players registered for the given team
DROP PROCEDURE IF EXISTS get_team_players;
DELIMITER $$
CREATE PROCEDURE get_team_players(IN name VARCHAR(50))
	BEGIN
    
    SELECT playerName, jerseyNum, birthday FROM player JOIN team ON player.team = team.teamName
    WHERE teamName = name;
    
    END $$
DELIMITER ;

SELECT * FROM TEAM;
CALL get_team_players('SASC 00G RED');



-- to read contact information given a parent's name
DROP PROCEDURE IF EXISTS get_parent_contact;
DELIMITER $$
CREATE PROCEDURE get_parent_contact(IN name VARCHAR(50))
	BEGIN
    
    SELECT parentName, phoneNumber, email FROM parent
    WHERE parentName = name;
    
    END $$
DELIMITER ;

SELECT * FROM parent;
CALL get_parent_contact('dave hwang');



-- returns the list of all teams in the club
DROP PROCEDURE IF EXISTS get_teams;
DELIMITER $$
CREATE PROCEDURE get_teams()
	BEGIN
    
    SELECT * FROM team;
    
    END $$
DELIMITER ;

SELECT * FROM team;
CALL get_teams();


-- returns a list of all the games scheduled for a specific team, in order by date then time.
DROP PROCEDURE IF EXISTS season_schedule;
DELIMITER $$
CREATE PROCEDURE season_schedule(IN team VARCHAR(50))
	BEGIN
    
    SELECT * FROM game 
    WHERE teamName = team
    ORDER BY DATE(gameDate), TIME(gameTime);
    
    END $$
DELIMITER ;

SELECT * FROM game;
CALL season_schedule('SASC 07G RED');
CALL season_schedule('SASC 02B BLACK');


-- UPDATE PROCEDURES

-- to update parent phone number
DROP PROCEDURE IF EXISTS update_parent_phone;
DELIMITER $$
CREATE PROCEDURE update_parent_phone(IN id INT, newNumber VARCHAR(11))
	BEGIN
    
    UPDATE parent
    SET phoneNumber = newNumber
    WHERE parentId = id;
    
    END $$
DELIMITER ;

SELECT * FROM parent;
CALL update_parent_phone(1, '1111111111');
CALL update_parent_phone(1, '4086224139');


-- to update the time of a game
DROP PROCEDURE IF EXISTS update_game_time;
DELIMITER $$
CREATE PROCEDURE update_game_time(IN id INT, newTime VARCHAR(50))
	BEGIN
    
    UPDATE game
    SET gameTime = newTime
    WHERE gameId = id;
    
    END $$
DELIMITER ;

SELECT * FROM game;
CALL update_game_time(1, '11:30:00');
CALL update_game_time(1, '11:45:00');

-- to update which team a player is currently on
DROP PROCEDURE IF EXISTS update_player_team;
DELIMITER $$
CREATE PROCEDURE update_player_team(IN id INT, newTeam VARCHAR(50))
	BEGIN
    
    UPDATE player
    SET team = newTeam
    WHERE playerId = id;
    
    END $$
DELIMITER ;

SELECT * FROM player;
CALL update_player_team(4, 'SASC 00G RED');


-- to update coach phone number
DROP PROCEDURE IF EXISTS update_coach_phone;
DELIMITER $$
CREATE PROCEDURE update_coach_phone(IN id INT, newNumber VARCHAR(11))
	BEGIN
    
    UPDATE coach
    SET phoneNumber = newNumber
    WHERE coachId = id;
    
    END $$
DELIMITER ;

SELECT * FROM coach;
CALL update_coach_phone(9, '2222222222');
CALL update_coach_phone(9, '4089191624');


-- DELETE PROCEDURES

-- to delete a player from the player table
DROP PROCEDURE IF EXISTS delete_player;
DELIMITER $$
CREATE PROCEDURE delete_player(IN id INT)
	BEGIN
    
    DELETE FROM player
    WHERE playerId = id;
    
    END $$
DELIMITER ;

SELECT * FROM player;
CALL delete_player(1);

-- to delete a game from the game table
DROP PROCEDURE IF EXISTS delete_game;
DELIMITER $$
CREATE PROCEDURE delete_game(IN id INT)
	BEGIN
    
    DELETE FROM game
    WHERE gameId = id;
    
    END $$
DELIMITER ;

SELECT * FROM game;
CALL delete_game(1);

-- to delete a coach from the coach table
DROP PROCEDURE IF EXISTS delete_coach;
DELIMITER $$
CREATE PROCEDURE delete_coach(IN id INT)
	BEGIN
    
    DELETE FROM coach
    WHERE coachId = id;
    
    END $$
DELIMITER ;

SELECT * FROM coach;
CALL delete_coach(13);



-- interesting queries

-- to see which age group has the most players
DROP PROCEDURE IF EXISTS biggest_age_group;
DELIMITER $$
CREATE PROCEDURE biggest_age_group()
	BEGIN
    
    SELECT YEAR(birthday) as 'year', COUNT(*) FROM player
    GROUP BY YEAR(birthday)
    ORDER BY COUNT(*) DESC LIMIT 1;
    
    END $$
DELIMITER ;

CALL biggest_age_group();


-- too see which month has the most games scheduled
DROP PROCEDURE IF EXISTS month_with_most_games;
DELIMITER $$
CREATE PROCEDURE month_with_most_games()
	BEGIN
    
    SELECT MONTH(gameDate) as 'month', COUNT(*) FROM game
    GROUP BY MONTH(gameDate)
    ORDER BY COUNT(*) DESC LIMIT 1;
    
    END $$
DELIMITER ;

CALL month_with_most_games();
select * from game;


/*
-- to convert a number into a month name
DROP PROCEDURE IF EXISTS month_name;
DELIMITER $$
CREATE PROCEDURE month_name(IN month INT)
	BEGIN
    
    SELECT SUBSTRING('JAN FEB MAR APR MAY JUN JUL AUG SEP OCT NOV DEC ', (month * 4) - 3, 3) as 'month';
    
    END $$
DELIMITER ;

CALL month_name(3);
*/


-- returns a parent/child roster for a given team
DROP PROCEDURE IF EXISTS parent_child_team_roster;
DELIMITER $$
CREATE PROCEDURE parent_child_team_roster(IN team VARCHAR(50))
	BEGIN
    
    SELECT playerName as 'player', parentName as 'parent', phoneNumber, email FROM
    parent JOIN
    (SELECT * FROM player WHERE player.team = team) p
    ON parent.parentId = p.parentId;
    
    END $$
DELIMITER ;

CALL parent_child_team_roster('SASC 00G RED');
select * from parent;