USE soccer_project;

DROP TABLE IF EXISTS game;
DROP TABLE IF EXISTS admin;
DROP TABLE IF EXISTS parent;
DROP TABLE IF EXISTS player;
DROP TABLE IF EXISTS team;
DROP TABLE IF EXISTS ageGroup;
DROP TABLE IF EXISTS manager;
DROP TABLE IF EXISTS coach;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
userId INT AUTO_INCREMENT,
role ENUM('parent', 'manager', 'coach', 'admin') NOT NULL,
PRIMARY KEY (userId)
);


CREATE TABLE coach (
    coachId INT PRIMARY KEY,
    coachName VARCHAR(100) NOT NULL,
    phoneNumber VARCHAR(10) NOT NULL,
    email VARCHAR(100) NOT NULL,
    FOREIGN KEY (coachId)
        REFERENCES user (userId)
        ON UPDATE CASCADE ON DELETE CASCADE
);



CREATE TABLE manager (
    managerId INT PRIMARY KEY,
    managerName VARCHAR(100) NOT NULL,
    phoneNumber VARCHAR(10) NOT NULL,
    email VARCHAR(100) NOT NULL,
    FOREIGN KEY (managerId)
        REFERENCES user (userId)
); 



CREATE TABLE ageGroup (
	ageGroupYear INT PRIMARY KEY
);



CREATE TABLE team (
    teamName VARCHAR(50) PRIMARY KEY,
    teamRank VARCHAR(1) NOT NULL, /* says which level the team is within its own age group; ex: the A team vs the B team (A is higher) */
    ageGroup INT NOT NULL,
    league VARCHAR(100) NOT NULL,
    coach INT NULL,
    manager INT NOT NULL,
    FOREIGN KEY (coach) 
		REFERENCES coach (coachId),
	FOREIGN KEY (manager)
		REFERENCES manager (managerId),
	FOREIGN KEY (ageGroup)
		REFERENCES ageGroup (ageGroupYear)
);



CREATE TABLE parent (
    parentId INT PRIMARY KEY,
    parentName VARCHAR(100) NOT NULL,
    phoneNumber VARCHAR(10) NOT NULL,
    email VARCHAR(100) NOT NULL,
    volunteerHours INT NULL,
    FOREIGN KEY (parentId)
        REFERENCES user (userId)
);



CREATE TABLE player (
    playerId INT AUTO_INCREMENT,
    playerName VARCHAR(100) NOT NULL,
	jerseyNum INT NOT NULL,
    birthday DATE NOT NULL,
    team VARCHAR(50),
    parentId INT NOT NULL,
    PRIMARY KEY (playerId),
    FOREIGN KEY (team)
        REFERENCES team (teamName)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
	FOREIGN KEY (parentId)
		REFERENCES parent (parentId)
);



CREATE TABLE admin (
    adminId INT PRIMARY KEY,
    adminName VARCHAR(100),
    phoneNumber INT NOT NULL,
    email VARCHAR(100) NOT NULL,
    FOREIGN KEY (adminId)
        REFERENCES user (userId)
);



CREATE TABLE game (
	gameId INT AUTO_INCREMENT,
    teamName VARCHAR(50) NOT NULL,
    opponentName VARCHAR(50) NOT NULL,
	location ENUM('home', 'away') NOT NULL,
    gameDate DATE NOT NULL,
    gameTime TIME NOT NULL,
    PRIMARY KEY (gameId),
    FOREIGN KEY (teamName) 
		REFERENCES team (teamName)
);

