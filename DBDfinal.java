
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

class JavaMySql {

  // The name of the MySQL account to use
  private String userName = "yourName";

  // The password for the MySQL account
  private String password = "yourPassword!";

  // The name of the computer running MySQL
  private final String serverName = "yourServerName";

  // The port of the MySQL server (default is 3306)
  private final int portNumber = 3306;

  // The name of the database we are testing with
  private final String dbName = "soccer_project";
  
  private static Scanner userInput = new Scanner(System.in).useDelimiter("\\n");


  // Get a new database connection
  // @return
  // @throws SQLException
  public Connection getConnection() throws SQLException {
    Connection conn = null;
    Properties connectionProps = new Properties();
    connectionProps.put("user", this.userName);
    connectionProps.put("password", this.password);

    conn = DriverManager.getConnection("jdbc:mysql://"
        + this.serverName + ":" + this.portNumber + "/" + this.dbName + 
        "?characterEncoding=UTF-8&useSSL=false",
        connectionProps);

    return conn;
  }
  /**
   * Run a SQL command which does not return a recordset:
   * CREATE/INSERT/UPDATE/DELETE/DROP/etc.
   * 
   * @throws SQLException If something goes wrong
   */
  public boolean executeUpdate(Connection conn, String command) throws SQLException {
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      stmt.executeUpdate(command); // This will throw a SQLException if it fails
      return true;
    }
    finally {

      // This will run whether we throw an exception or not
      if (stmt != null) {
        stmt.close();
      }
    }
  }

  /**
   * Connect to MySQL and do some stuff.
   */
  public void run() {

    // Connect to MySQL
    Connection conn = null;
    try {
      conn = this.getConnection();
      System.out.println("Connected to database");
      System.out.println("Please enter your User ID:");
      String userId = userInput.nextLine();
      ArrayList<String> validUsers = this.getUserIds(conn);
      // this ensures that the inputted userId is valid
      while (!validUsers.contains(userId)) {
        System.out.println("The given User ID is not valid");
        System.out.println("Please enter your User ID:");
        userId = userInput.nextLine();
      }
      ArrayList<String> validEnums = this.getUserEnums(conn);
      int indexOfUser = validUsers.indexOf(userId);
      String userEnum = validEnums.get(indexOfUser);
      System.out.println("You have been identified as a: " + userEnum);
      boolean exit = false;
      if (userEnum.equals("parent")) {
        while (!exit) {
          System.out.println("Hello! Please select an option from the menu:");
          ArrayList<String> options = new ArrayList<String>(
              Arrays.asList("[1] Register new player", "[2] Obtain coach contact information",
                  "[3] Update phone number", "[4] Remove player", "[5] Exit"));
          for (String option : options) {
            System.out.println(option);
          }
          String userChoice = userInput.next();
          if (userChoice.equals("1")) {
            // allows the parent to register a new player
            System.out.println("In order to register a new player,");
            System.out.println("you must provide the following information:");
            System.out.println(" - Name\n - Desired Jersey Number\n - Birthday\n - Team Name");
            System.out.println("Do you wish to continue? Y/N");
            userChoice = userInput.next();
            if (userChoice.equalsIgnoreCase("Y")) {
              boolean correct = false;
              while (!correct) {
                System.out.println("Please provide the player's full name:");
                String playerName = userInput.next();
                System.out.println("Please provide the desired jersey number:");
                int jerseyNumber = userInput.nextInt();
                System.out.println("Please provide the year of birth as a four-digit number:");
                String yob = userInput.next();
                System.out.println("Please provide the month of birth as a two-digit number:");
                String mob = userInput.next();
                System.out.println("Please provide the date of birth as a two-digit number:");
                String dob = userInput.next();
                String birthDate = yob + "-" + mob + "-" + dob;
                System.out.println("Please provide the team name:");
                String teamName = userInput.next();
                ArrayList<String> teams = this.getTeams(conn);
                while (!teams.contains(teamName)) {
                  System.out.println("Invalid team name");
                  System.out.println("Please select a team from the following list");
                  for (String team : teams) {
                    System.out.println(" - " + team);
                  }
                  teamName = userInput.next();
                }
                System.out.println("Is the following information correct? Y/N");
                System.out.println("Player's name: " + playerName);
                System.out.println("Desired jersey number: " + jerseyNumber);
                System.out.println("Date of birth: " + birthDate);
                System.out.println("Team name: " + teamName);
                userChoice = userInput.next();
                if (userChoice.equalsIgnoreCase("Y")) {
                  this.registerPlayer(conn, playerName, jerseyNumber, birthDate, teamName,
                      Integer.valueOf(userId));
                  System.out.println("Your player has been registered!");
                  correct = true;
                }
              }
            }
          }
          if (userChoice.equals("2")) {
            // allows the parent to obtain coach contact information
            System.out.println("This is our current list of coaches:");
            ArrayList<String> coaches = this.getCoachNames(conn);
            for (String coach : coaches) {
              System.out.println(" - " + coach);
            }
            System.out.println("Please select a coach:");
            userChoice = userInput.next();
            while (!coaches.contains(userChoice)) {
              System.out.println("Invalid coach");
              System.out.println("Please select a coach:");
              userChoice = userInput.next();
            }
            ArrayList<String> contactInfo = this
                .resultSetToList(this.getCoachContact(conn, userChoice));
            for (String element : contactInfo) {
              System.out.println(element);
            }
          }
          if (userChoice.equals("3")) {
            // allows the parent to update their phone number
            System.out.println("Please enter your new phone number:");
            userChoice = userInput.next();
            this.updateParentPhone(conn, Integer.valueOf(userId), userChoice);
            System.out.println("Your phone number has been updated");
          }
          if (userChoice.equals("4")) {
            // allows the parent to remove their player from the database
            ArrayList<String> players = this.getParentsPlayers(conn, Integer.valueOf(userId));
            if (!players.isEmpty()) {
              System.out.println("These are your registered players:");
              for (String player : players) {
                System.out.println(" - " + player);
              }
              System.out.println("Which one would you like to remove?");
              String playerName = userInput.next();
              while (!players.contains(playerName)) {
                System.out.println("Invalid name");
                System.out.println("Please try again");
                playerName = userInput.next();
              }
              this.removePlayer(conn, playerName);
              System.out.println("This player has been removed");
            }
            else {
              System.out.println("This action is not supported");
              System.out.println("You have no players to remove");
            }
          }
          if (userChoice.equals("5")) {
            exit = true;
          }
        }
      }
      if (userEnum.equals("coach")) {
        while (!exit) {
          System.out.println("Hello! Please select an option from the menu:");
          ArrayList<String> options = new ArrayList<String>(
              Arrays.asList("[1] Obtain list of players", "[2] Update phone number", "[3] Exit"));
          for (String option : options) {
            System.out.println(option);
          }
          String userChoice = userInput.next();
          if (userChoice.equals("1")) {
            // allows the coach to obtain a list of their players
            System.out.println("Please select a team from the following:");
            ArrayList<String> teams = this.getTeams(conn);
            for (String team : teams) {
              System.out.println(" - " + team);
            }
            userChoice = userInput.next();
            while (!teams.contains(userChoice)) {
              System.out.println("Invalid team");
              System.out.println("Please try again:");
              userChoice = userInput.next();
            }
            ArrayList<String> players = this.getTeamPlayers(conn, userChoice);
            if (players.isEmpty()) {
              System.out.println("This team is currently empty");
            }
            else {
              for (String player : players) {
                System.out.println(" - " + player);
              }
            }
          }
          if (userChoice.equals("2")) {
            // allows the coach to update their phone number
            System.out.println("Please enter your new phone number:");
            userChoice = userInput.next();
            this.updateCoachPhone(conn, Integer.valueOf(userId), userChoice);
            System.out.println("Your phone number has been updated");
          }
          if (userChoice.equals("3")) {
            exit = true;
          }
        }
      }
      if (userEnum.equals("admin")) {
        while (!exit) {
          System.out.println("Hello! Please select an option from the menu:");
          ArrayList<String> options = new ArrayList<String>(
              Arrays.asList("[1] Obtain list of team names", "[2] Change player's current team",
                  "[3] Remove coach", "[4] See which age group has the most players",
                  "[5] See which month has the most games scheduled", "[6] Exit"));
          for (String option : options) {
            System.out.println(option);
          }
          String userChoice = userInput.next();
          if (userChoice.equals("1")) {
            // allows the admin to obtain a list of all the team names
            ArrayList<String> teams = this.getTeams(conn);
            for (String team : teams) {
              System.out.println(" - " + team);
            }
          }
          if (userChoice.equals("2")) {
            // allows the admin to change a player's current team
            System.out.println("Please select a player to modify:");
            String playerName = userInput.next();
            ArrayList<String> allPlayers = this.getAllPlayers(conn);
            while (!allPlayers.contains(playerName)) {
              System.out.println("Invalid player name");
              System.out.println("Please select a player from the following to modify:");
              for (String player : allPlayers) {
                System.out.println(" - " + player);
              }
              playerName = userInput.next();
            }
            System.out.println("Please select a team to migrate this player to:");
            String teamName = userInput.next();
            ArrayList<String> teams = this.getTeams(conn);
            while (!teams.contains(teamName)) {
              System.out.println("Invalid team name");
              System.out
                  .println("Please select a team from the following to migrate this player to:");
              for (String team : teams) {
                System.out.println(" - " + team);
              }
              teamName = userInput.next();
            }
            this.updatePlayerTeam(conn, playerName, teamName);
            System.out.println("This player's team has been modified");
          }
          if (userChoice.equals("3")) {
            // allows the admin to remove a coach from their team
            boolean correct = false;
            while (!correct) {
              System.out.println("Please select a coach from the following:");
              ArrayList<String> coaches = this.getCoachNames(conn);
              for (String coach : coaches) {
                System.out.println(" - " + coach);
              }
              userChoice = userInput.next();
              if (coaches.contains(userChoice)) {
                correct = true;
                this.removeCoach(conn, userChoice);
                System.out.println("This coach has been removed");
              }
              else {
                System.out.println("Invalid coach name");
                correct = false;
              }
            }
          }
          if (userChoice.equals("4")) {
            System.out.println("Age Group - Number of players - ");
            ArrayList<String> results = this.resultSetToList(this.biggestAgeGroup(conn));
            for (String element : results) {
              System.out.println(element);
            }
          }
          if (userChoice.equals("5")) {
            System.out.println("Month - Number of games scheduled -");
            System.out.println(this.monthWithMostGames(conn));
          }
          if (userChoice.equals("6")) {
            exit = true;
          }
        }
      }
      if (userEnum.equals("manager")) {
        while (!exit) {
          System.out.println("Hello! Please select an option from the menu:");
          ArrayList<String> options = new ArrayList<String>(
              Arrays.asList("[1] Create a new match", "[2] Obtain list of a team's players",
                  "[3] Obtain a parent's contact information", "[4] Update match time",
                  "[5] Withdraw from match", "[6] Obtain a team's season schedule",
                  "[7] Obtain a team's contact information", "[8] Exit"));
          for (String option : options) {
            System.out.println(option);
          }
          String userChoice = userInput.next();
          if (userChoice.equals("1")) {
            // allows the manager to create a new match
            boolean correct = false;
            while (!correct) {
              System.out.println("Please enter your team's name:");
              ArrayList<String> validTeams = this.getValidManagerTeams(conn,
                  Integer.valueOf(userId));
              String teamName = userInput.next();
              while (!validTeams.contains(teamName)) {
                System.out.println("Invalid team name");
                System.out.println("Please select a team from the following:");
                for (String team : validTeams) {
                  System.out.println(" - " + team);
                }
                teamName = userInput.next();
              }
              System.out.println("Please enter the opponent's team name:");
              String opponentTeamName = userInput.next();
              System.out.println("Home game? Y/N");
              String home = userInput.next();
              while (!home.equalsIgnoreCase("Y") && !home.equalsIgnoreCase("N")) {
                System.out.println("Invalid input");
                System.out.println("Home game? Y/N");
                home = userInput.next();
              }
              if (home.equalsIgnoreCase("Y")) {
                home = "home";
              }
              else if (home.equalsIgnoreCase("N")) {
                home = "away";
              }
              System.out.println("Please enter the year of the match as a four-digit number:");
              String year = userInput.next();
              System.out.println("Please enter the month of the match as a two-digit number:");
              String month = userInput.next();
              System.out.println("Please enter the date of the match as a two-digit number:");
              String date = userInput.next();
              String matchDate = year + "-" + month + "-" + date;
              System.out
                  .println("Please enter the starting hour of the match as a two-digit number");
              System.out.println("Use 24-hour time:");
              String hour = userInput.next();
              System.out
                  .println("Please enter the starting minute of the match as a two-digit number");
              System.out.println("Use 24-hour time:");
              String minute = userInput.next();
              String time = hour + ":" + minute + ":00";
              System.out.println("The following match will be created:");
              System.out.println("Team name: " + teamName);
              System.out.println("Opponent team: " + opponentTeamName);
              System.out.println("Location: " + home);
              System.out.println("Match date: " + matchDate);
              System.out.println("Match time: " + time);
              System.out.println("Is this correct? Y/N");
              String correctInformation = userInput.next();
              if (correctInformation.equalsIgnoreCase("Y")) {
                this.createGame(conn, teamName, opponentTeamName, home, matchDate, time);
                System.out.println("This match has been added");
                correct = true;
              }
            }
          }
          if (userChoice.equals("2")) {
            // allows the manager to obtain a list of all players
            System.out.println("Please select a team from the following:");
            ArrayList<String> teams = this.getTeams(conn);
            for (String team : teams) {
              System.out.println(" - " + team);
            }
            userChoice = userInput.next();
            while (!teams.contains(userChoice)) {
              System.out.println("Invalid team");
              System.out.println("Please try again:");
              userChoice = userInput.next();
            }
            ArrayList<String> players = this.getTeamPlayers(conn, userChoice);
            if (players.isEmpty()) {
              System.out.println("This team is currently empty");
            }
            else {
              for (String player : players) {
                System.out.println(" - " + player);
              }
            }
          }
          if (userChoice.equals("3")) {
            // allows the manager to obtain a parent's contact information
            System.out.println("Please select a parent from the following:");
            ArrayList<String> parents = this.getParents(conn);
            for (String parent : parents) {
              System.out.println(" - " + parent);
            }
            userChoice = userInput.next();
            while (!parents.contains(userChoice)) {
              System.out.println("Invalid parent");
              System.out.println("Please select a valid parent");
              userChoice = userInput.next();
            }
            ArrayList<String> contactInfo = this
                .resultSetToList(this.getParentContact(conn, userChoice));
            for (String element : contactInfo) {
              System.out.println(element);
            }
          }
          if (userChoice.equals("4")) {
            // allows the manager to update a match's time
            System.out.println("Select a match to update from the following");
            System.out.println("In order to select a match, enter the Match ID");
            System.out.println("The Match ID is the first element of each row");
            ArrayList<String> validMatches = this
                .resultSetToList(this.getValidManagerMatches(conn, Integer.valueOf(userId)));
            for (String match : validMatches) {
              System.out.println(match);
            }
            int matchId = userInput.nextInt();
            boolean correct = false;
            while (!correct) {
              System.out
                  .println("Please enter the new starting hour of the match as a two-digit number");
              System.out.println("Use 24-hour time:");
              String hour = userInput.next();
              System.out.println(
                  "Please enter the new starting minute of the match as a two-digit number");
              System.out.println("Use 24-hour time:");
              String minute = userInput.next();
              String newMatchTime = hour + ":" + minute + ":00";
              System.out.println("Is this the correct time? Y/N");
              System.out.println(newMatchTime);
              userChoice = userInput.next();
              if (userChoice.equalsIgnoreCase("Y")) {
                this.updateGameTime(conn, matchId, newMatchTime);
                correct = true;
              }
            }
            System.out.println("This match's start time has been updated");
          }
          if (userChoice.equals("5")) {
            // allows the manager to withdraw their team from a match
            System.out.println("Select a match to remove from the following");
            System.out.println("In order to select a match, enter the Match ID");
            System.out.println("The Match ID is the first element of each row");
            ArrayList<String> validMatches = this
                .resultSetToList(this.getValidManagerMatches(conn, Integer.valueOf(userId)));
            for (String match : validMatches) {
              System.out.println(match);
            }
            int matchId = userInput.nextInt();
            boolean correct = false;
            while (!correct) {
              System.out.println("Is this the correct Match ID? Y/N");
              System.out.println(matchId);
              userChoice = userInput.next();
              if (userChoice.equalsIgnoreCase("Y")) {
                this.removeGame(conn, matchId);
                correct = true;
              }
            }
            System.out.println("This match has been withdrawn");
          }
          if (userChoice.equals("6")) {
            // allows the manager to obtain any team's season schedule
            System.out.println("Please select a team:");
            ArrayList<String> teams = this.getTeams(conn);
            userChoice = userInput.next();
            while (!teams.contains(userChoice)) {
              System.out.println("Invalid team name");
              System.out.println("Please select a team from the following:");
              for (String team : teams) {
                System.out.println(" - " + team);
              }
              userChoice = userInput.next();
            }
            ArrayList<String> schedule = this
                .resultSetToList(this.getSeasonSchedule(conn, userChoice));
            for (String match : schedule) {
              System.out.println(match);
            }
          }
          if (userChoice.equals("7")) {
            // allows the manager to obtain a given team's list of player's and each's
            // parent's contact information
            System.out.println("Please select a team:");
            ArrayList<String> teams = this.getTeams(conn);
            userChoice = userInput.next();
            while (!teams.contains(userChoice)) {
              System.out.println("Invalid team name");
              System.out.println("Please select a team from the following:");
              for (String team : teams) {
                System.out.println(" - " + team);
              }
              userChoice = userInput.next();
            }
            ArrayList<String> teamPlayers = this.getTeamPlayers(conn, userChoice);
            if (teamPlayers.isEmpty()) {
              System.out.println("This team current has no players");
            }
            else {
              ArrayList<String> players = this.resultSetToList(this.getParentChildTeamRoster(conn, userChoice));
              for (String element : players) {
                System.out.println(element);
              }
            }
          }
          if (userChoice.equals("8")) {
            exit = true;
          }
        }
      }
      System.out.println("Thank you for using the team's database!");
      System.out.println("Goodbye");
      conn.close();
    }
    catch (SQLException e) {
      System.out.println("ERROR: Could not connect to the database");
      e.printStackTrace();
      return;
    }
//
//    // Create a table
//    try {
//        String createString =
//              "CREATE TABLE " + this.tableName + " ( " +
//              "ID INTEGER NOT NULL, " +
//              "NAME varchar(40) NOT NULL, " +
//              "STREET varchar(40) NOT NULL, " +
//              "CITY varchar(20) NOT NULL, " +
//              "STATE char(2) NOT NULL, " +
//              "ZIP char(5), " +
//              "PRIMARY KEY (ID))";
//      this.executeUpdate(conn, createString);
//      System.out.println("Created a table");
//      } catch (SQLException e) {
//      System.out.println("ERROR: Could not create the table");
//      e.printStackTrace();
//      return;
//    }
//    
//    // Drop the table
//    try {
//        String dropString = "DROP TABLE " + this.tableName;
//      this.executeUpdate(conn, dropString);
//      System.out.println("Dropped the table");
//      } catch (SQLException e) {
//      System.out.println("ERROR: Could not drop the table");
//      e.printStackTrace();
//      return;
//    }
  }

  public ArrayList<String> getUserIds(Connection con) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("select userId from user");
      ResultSet rs = ps.executeQuery();
      ArrayList<String> users = new ArrayList<String>();
      while (rs.next()) {
        users.add(rs.getString(1).toLowerCase());
      }
      rs.close();
      ps.close();
      return users;
    }
    catch (SQLException e) {
      System.out.println("Error: Could not query 'select userId from user'");
      e.printStackTrace();
      return null;
    }
  }

  public ArrayList<String> getUserEnums(Connection con) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("select role from user");
      ResultSet rs = ps.executeQuery();
      ArrayList<String> users = new ArrayList<String>();
      while (rs.next()) {
        users.add(rs.getString(1).toLowerCase());
      }
      rs.close();
      ps.close();
      return users;
    }
    catch (SQLException e) {
      System.out.println("Error: Could not query 'select role from user'");
      e.printStackTrace();
      return null;
    }
  }

  public ArrayList<String> getCoachNames(Connection con) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("select coachName from coach");
      ResultSet rs = ps.executeQuery();
      ArrayList<String> coaches = new ArrayList<String>();
      while (rs.next()) {
        coaches.add(rs.getString(1).toLowerCase());
      }
      rs.close();
      ps.close();
      return coaches;
    }
    catch (SQLException e) {
      System.out.println("Error: Could not query 'select coachName from coach'");
      e.printStackTrace();
      return null;
    }
  }

  public ArrayList<String> resultSetToList(ResultSet rs) throws SQLException {
    ResultSetMetaData rsmd = rs.getMetaData();
    ArrayList<String> output = new ArrayList<String>();
    while (rs.next()) {
      String currentRow = "";
      for (int columnNo = rsmd.getColumnCount(); columnNo >= 1; columnNo--) {
        currentRow = rs.getString(columnNo) + " - " + currentRow;
      }
      output.add(currentRow);
    }
    return output;
  }

  public String listToString(ArrayList<String> array) throws SQLException {
    String output = "";
    for (String element : array) {
      output = output + "\n" + element;
    }
    return output;
  }

  public void registerPlayer(Connection con, String playerName, int jerseyNumber, String birthDate,
      String teamName, int parentId) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("call register_player(?, ?, ?, ?, ?)");
      ps.setString(1, playerName);
      ps.setInt(2, jerseyNumber);
      ps.setString(3, birthDate);
      ps.setString(4, teamName);
      ps.setInt(5, parentId);
      ps.executeQuery();
      ps.close();
    }
    catch (SQLException e) {
      System.out.println("Error: Could not call register_player(" + playerName + ", " + jerseyNumber
          + ", " + birthDate + ", " + teamName + ", " + parentId + ")");
      e.printStackTrace();
    }
  }

  public void removePlayer(Connection con, String playerName) throws SQLException {
    PreparedStatement ps = con
        .prepareStatement("select playerId from player where playerName = (?)");
    ps.setString(1, playerName);
    ResultSet rs = ps.executeQuery();
    rs.next();
    int playerId = rs.getInt(1);
    PreparedStatement ps2 = con.prepareStatement("call delete_player(?)");
    ps2.setInt(1, playerId);
    ps2.executeQuery();
    rs.close();
    ps.close();
    ps2.close();
  }

  public void removeCoach(Connection con, String coachName) throws SQLException {
    PreparedStatement ps = con.prepareStatement("select coachId from coach where coachName = (?)");
    ps.setString(1, coachName);
    ResultSet rs = ps.executeQuery();
    rs.next();
    int coachId = rs.getInt(1);
    PreparedStatement ps2 = con.prepareStatement("call delete_coach(?)");
    ps2.setInt(1, coachId);
    ps2.executeQuery();
    ps2.close();
  }

  public ArrayList<String> getParentsPlayers(Connection con, int parentId) throws SQLException {
    try {
      PreparedStatement ps = con
          .prepareStatement("select playerName from player where parentId = (?)");
      ps.setInt(1, parentId);
      ResultSet rs = ps.executeQuery();
      ArrayList<String> children = new ArrayList<String>();
      while (rs.next()) {
        children.add(rs.getString(1));
      }
      rs.close();
      ps.close();
      return children;
    }
    catch (SQLException e) {
      System.out.println("Error: Could not query 'select playerName from player where parentId = "
          + parentId + "'");
      e.printStackTrace();
      return null;
    }
  }

  public ArrayList<String> getTeams(Connection con) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("select teamName from team");
      ResultSet rs = ps.executeQuery();
      ArrayList<String> teams = new ArrayList<String>();
      while (rs.next()) {
        teams.add(rs.getString(1));
      }
      rs.close();
      ps.close();
      return teams;
    }
    catch (SQLException e) {
      System.out.println("Error: Could not query 'select teamName from team'");
      e.printStackTrace();
      return null;
    }
  }

  public ArrayList<String> getParents(Connection con) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("select parentName from parent");
      ResultSet rs = ps.executeQuery();
      ArrayList<String> parents = new ArrayList<String>();
      while (rs.next()) {
        parents.add(rs.getString(1));
      }
      rs.close();
      ps.close();
      return parents;
    }
    catch (SQLException e) {
      System.out.println("Error: Could not query 'select parentName from parent'");
      e.printStackTrace();
      return null;
    }
  }

  public ResultSet getParentContact(Connection con, String parentName) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("call get_parent_contact(?)");
      ps.setString(1, parentName);
      ResultSet rs = ps.executeQuery();
      return rs;
    }
    catch (SQLException e) {
      System.out.println("Error: Could not call get_parent_contact(" + parentName + ")");
      e.printStackTrace();
      return null;
    }
  }

  public ResultSet getCoachContact(Connection con, String coachName) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("call get_coach_contact(?)");
      ps.setString(1, coachName);
      ResultSet rs = ps.executeQuery();
      return rs;
    }
    catch (SQLException e) {
      System.out.println("Error: Could not call get_coach_contact(" + coachName + ")");
      e.printStackTrace();
      return null;
    }
  }

  public ArrayList<String> getValidManagerTeams(Connection con, int managerId) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("select teamName from team where manager = (?)");
      ps.setInt(1, managerId);
      ResultSet rs = ps.executeQuery();
      ArrayList<String> validTeams = new ArrayList<String>();
      while (rs.next()) {
        validTeams.add(rs.getString(1));
      }
      return validTeams;
    }
    catch (SQLException e) {
      System.out.println(
          "Error: Could not query 'select teamname from team where manager = " + managerId + "'");
      e.printStackTrace();
      return null;
    }
  }

  public ResultSet getValidManagerMatches(Connection con, int managerId) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement(
          "select game.gameId, game.teamName, game.opponentName, game.location, game.gameDate, game.gameTime\n"
              + " from game join team on game.teamName = team.teamName where team.manager = (?)");
      ps.setInt(1, managerId);
      ResultSet rs = ps.executeQuery();
      return rs;
    }
    catch (SQLException e) {
      System.out.println(
          "Error: Could not query 'select game.gameId, game.teamName, game.opponentName, game.location, game.gameDate, game.gameTime from game join team on game.teamName = team.teamName where team.manager = "
              + managerId + "'");
      e.printStackTrace();
      return null;
    }
  }

  public void updateParentPhone(Connection con, int parentId, String newNumber)
      throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("call update_parent_phone(?, ?)");
      ps.setInt(1, parentId);
      ps.setString(2, newNumber);
      ps.executeQuery();
      ps.close();
    }
    catch (SQLException e) {
      System.out.println(
          "Error: Could not call update_parent_phone(" + parentId + ", " + newNumber + ")");
      e.printStackTrace();
    }
  }

  public void updateCoachPhone(Connection con, int coachId, String newNumber) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("call update_coach_phone(?, ?)");
      ps.setInt(1, coachId);
      ps.setString(2, newNumber);
      ps.executeQuery();
      ps.close();
    }
    catch (SQLException e) {
      System.out
          .println("Error: Could not call update_coach_phone(" + coachId + ", " + newNumber + ")");
      e.printStackTrace();
    }
  }

  public ArrayList<String> getTeamPlayers(Connection con, String teamName) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("call get_team_players(?)");
      ps.setString(1, teamName);
      ResultSet rs = ps.executeQuery();
      ArrayList<String> players = new ArrayList<String>();
      while (rs.next()) {
        players.add(rs.getString(1));
      }
      rs.close();
      ps.close();
      return players;
    }
    catch (SQLException e) {
      System.out.println("Error: Could not call get_team_players(" + teamName + ")");
      e.printStackTrace();
      return null;
    }
  }

  public ArrayList<String> getAllPlayers(Connection con) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("select playerName from player");
      ResultSet rs = ps.executeQuery();
      ArrayList<String> allPlayers = new ArrayList<String>();
      while (rs.next()) {
        allPlayers.add(rs.getString(1));
      }
      rs.close();
      ps.close();
      return allPlayers;
    }
    catch (SQLException e) {
      System.out.println("Error: Could not query 'select playerName from player'");
      e.printStackTrace();
      return null;
    }
  }

  public ResultSet getSeasonSchedule(Connection con, String teamName) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("call season_schedule(?)");
      ps.setString(1, teamName);
      ResultSet rs = ps.executeQuery();
      return rs;
    }
    catch (SQLException e) {
      System.out.println("Error: Could not call season_schedule(" + teamName + ")");
      e.printStackTrace();
      return null;
    }
  }

  public void updatePlayerTeam(Connection con, String playerName, String teamName)
      throws SQLException {
    PreparedStatement ps = con
        .prepareStatement("select playerId from player where playerName = (?)");
    ps.setString(1, playerName);
    ResultSet rs = ps.executeQuery();
    rs.next();
    int playerId = rs.getInt(1);
    PreparedStatement ps2 = con.prepareStatement("call update_player_team(?, ?)");
    ps2.setInt(1, playerId);
    ps2.setString(2, teamName);
    ps2.executeQuery();
    rs.close();
    ps.close();
    ps2.close();
  }

  public void createGame(Connection con, String teamName, String opponentTeam, String location,
      String gameDate, String gameTime) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("call create_game(?, ?, ?, ?, ?)");
      ps.setString(1, teamName);
      ps.setString(2, opponentTeam);
      ps.setString(3, location);
      ps.setString(4, gameDate);
      ps.setString(5, gameTime);
      ps.executeQuery();
      ps.close();
    }
    catch (SQLException e) {
      System.out.println("Error: Could not call create_game(" + teamName + ", " + opponentTeam
          + ", " + location + ", " + gameDate + ", " + gameTime + ")");
      e.printStackTrace();
    }
  }

  public void updateGameTime(Connection con, int matchId, String newMatchTime) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("call update_game_time(?, ?)");
      ps.setInt(1, matchId);
      ps.setString(2, newMatchTime);
      ps.executeQuery();
      ps.close();
    }
    catch (SQLException e) {
      System.out
          .println("Error: Could not call update_game_time(" + matchId + ", " + newMatchTime + ")");
      e.printStackTrace();
    }
  }

  public void removeGame(Connection con, int matchId) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("call delete_game(?)");
      ps.setInt(1, matchId);
      ps.executeQuery();
      ps.close();
    }
    catch (SQLException e) {
      System.out.println("Error: Could not call delete_game(" + matchId + ")");
      e.printStackTrace();
    }
  }

  public ResultSet biggestAgeGroup(Connection con) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("call biggest_age_group()");
      ResultSet rs = ps.executeQuery();
      return rs;
    }
    catch (SQLException e) {
      System.out.println("Error: Could not call biggest_age_group()");
      e.printStackTrace();
      return null;
    }
  }

  public String monthWithMostGames(Connection con) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("call month_with_most_games()");
      ResultSet rs = ps.executeQuery();
      rs.next();
      int monthNumber = rs.getInt(1);
      int gameCount = rs.getInt(2);
      if (monthNumber == 1) {
        String month = "JAN";
        String result = month + " - " + gameCount + " - ";
        return result;
      }
      else if (monthNumber == 2) {
        String month = "FEB";
        String result = month + " - " + gameCount + " - ";
        return result;
      }
      else if (monthNumber == 3) {
        String month = "MAR";
        String result = month + " - " + gameCount + " - ";
        return result;
      }
      else if (monthNumber == 4) {
        String month = "APR";
        String result = month + " - " + gameCount + " - ";
        return result;
      }
      else if (monthNumber == 5) {
        String month = "MAY";
        String result = month + " - " + gameCount + " - ";
        return result;
      }
      else if (monthNumber == 6) {
        String month = "JUN";
        String result = month + " - " + gameCount + " - ";
        return result;
      }
      else if (monthNumber == 7) {
        String month = "JUL";
        String result = month + " - " + gameCount + " - ";
        return result;
      }
      else if (monthNumber == 8) {
        String month = "AUG";
        String result = month + " - " + gameCount + " - ";
        return result;
      }
      else if (monthNumber == 9) {
        String month = "SEP";
        String result = month + " - " + gameCount + " - ";
        return result;
      }
      else if (monthNumber == 10) {
        String month = "OCT";
        String result = month + " - " + gameCount + " - ";
        return result;
      }
      else if (monthNumber == 11) {
        String month = "NOV";
        String result = month + " - " + gameCount + " - ";
        return result;
      }
      else {
        String month = "DEC";
        String result = month + " - " + gameCount + " - ";
        return result;
      }
    }
    catch (SQLException e) {
      System.out.println("Error: Could not call month_with_most_games()");
      e.printStackTrace();
      return "";
    }
  }
  
  public ResultSet getParentChildTeamRoster(Connection con, String teamName) throws SQLException {
    try {
      PreparedStatement ps = con.prepareStatement("call parent_child_team_roster(?)");
      ps.setString(1, teamName);
      ResultSet rs = ps.executeQuery();
      return rs;
    }
    catch (SQLException e) {
      System.out.println("Error: Could not call parent_child_team_roster(" + teamName + ")");
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Connect to the DB and do some stuff
   * 
   * @param args
   */
  public static void main(String[] args) {
    JavaMySql app = new JavaMySql();
    System.out.println("Welcome to the team's database!");

    app.run();
  }
}
