import java.sql.*;
import java.util.Scanner;
import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.Math;
import java.util.HashMap;

public class LoadDriver {

	public static boolean isLoggedIn;
	public static String currentUsername;
	public static int currentID;

	static Connection getDBConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/mydb", "root", "mynewpassword");
			
			p("[Success connecting to SQL]");

			return con;
		} catch (Exception e) {
			p("Error connecting to DB! -> " + e);
			return null;
        }
	}

	static void userLogin(Connection conn) {
		if(isLoggedIn) {
			p(" You are already logged in as " + currentUsername + ".");
			return;
		}

		Scanner myObj = new Scanner(System.in);
		System.out.print(" Enter your username: ");
		String username = myObj.nextLine();
		
		try {
			String stat = "SELECT username, id FROM Account WHERE username = '" + username + "'";
			PreparedStatement execStat = conn.prepareStatement(stat);
			ResultSet rs = execStat.executeQuery(); 

			if(rs.next()) {
				isLoggedIn = true;
				currentUsername = rs.getString("username");
				currentID = rs.getInt("id");
				p("\n You are now logged in as " + username + ". \n");
			} else {
				p(" Sorry, that username doesn't exist. Use 'CreateAccount' to create a new account.");
			}
		} catch (Exception e) {
			p(" Error while logging in -> " + e);
		}
	}

	static void checkIsLoggedIn() {
		if (isLoggedIn) {
			p(" You are currently logged in as " + currentUsername + "(ID=" + currentID + ")");
		} else {
			p(" You are not logged in. To Login, type 'Login' or 'Create Account'.");
		}
	}

	static void logOut() {
		if(isLoggedIn) {
			p(" Logged out of account " + currentUsername);
			isLoggedIn = false;
			currentUsername = null;
			currentID = -1;
		} else {
			p(" You already are logged out.");
		}
	}

	static int generateID() {
		Random rand = new Random();
  		int upperBound = 1000000;
  		int r = rand.nextInt(upperBound);
  		return r;
	}

	static void p(String s) {
		System.out.println(s);
	}




	// ========================== Accounts ======================




	static void createAccountTable(Connection conn) {
		try {
			String stat = "CREATE TABLE Account(id int, username varchar(40) NOT NULL UNIQUE, firstName varchar(40), lastName varchar(40), dob date, address varchar(100), creditCard varchar(16), occupation varchar(100), sin int, isHost bool, PRIMARY KEY(id));";
			PreparedStatement createStat = conn.prepareStatement(stat);
			createStat.executeUpdate();
		} catch (Exception e) {
			p("Error while creating tables! -> " + e);
		}
	}

	static void userCreateAccount(Connection conn) {
		try {
			if (isLoggedIn) {
				p(" You are already logged in! To create an account, please logout first.");
				return;
			}

			p("\n\n ===============  Creating Account  ===================\n");
			Scanner myObj = new Scanner(System.in);
			p("\n You are now creating a new account! Please enter the following information:\n");

			System.out.print(" Enter a username: ");
			String username = myObj.nextLine();

			System.out.print(" Enter your first name: ");
			String firstName = myObj.nextLine();

			System.out.print(" Enter your last name: ");
			String lastName = myObj.nextLine();

			System.out.print(" Enter your date of birth [YYYY-MM-DD]: ");
			String dob = myObj.nextLine();
			Date dobParsed = new SimpleDateFormat("yyyy-MM-dd").parse(dob);
			Date dateLimit = new SimpleDateFormat("yyyy-MM-dd").parse("2004-10-08");
			if(dobParsed.compareTo(dateLimit) > 0) {
				p(" Sorry, but you must be over 18 to register.");
				return;
			}

			System.out.print(" What is your home address? ");
			String address = myObj.nextLine();

			System.out.print(" Please enter your 16 digit credit card number: ");
			String creditCard = myObj.nextLine();
			if(creditCard.length() != 16) {
				p(" Make sure you are using the 16-digit number on your card.");
				return;
			}

			System.out.print(" Please enter your job title: ");
			String occupation = myObj.nextLine();

			System.out.print(" Please enter your social insurance number: ");
			String s = myObj.nextLine();
			int sin = Integer.valueOf(s);

			System.out.print(" Is this account for hosting or renting? [H/R] ");
			String hr = myObj.nextLine();
			Boolean isHost = hr.equals("H") ? true : false;

			int id = generateID();

			insertIntoAccountTable(conn, id, username, firstName, lastName, dob, address, creditCard, occupation, sin, isHost);
		} catch (Exception e) {
			p(" There was an issue creating your account: " + e);
		}
	}

	static void populateAccountTable(Connection conn) {
		try {
			// Hosts
			insertIntoAccountTable(conn, 7809, "GWashington5", "George", "Washington", "1732-02-22", "1600 Pennsylvania Ave", "1776177617761776", "President", 8920, true);
			insertIntoAccountTable(conn, 39007, "Bernie", "Bernard", "Sanders", "1946-11-20", "34 Forest Drive Burlington", "2017201820152016", "Senator", 8009, true);
			insertIntoAccountTable(conn, 74823, "Ronald1984", "Ronald", "Reagen", "1911-02-06", "15 24th Avenue Tampica", "7654890754350987", "President", 89723, true);
			insertIntoAccountTable(conn, 82398, "FDR", "Franklin", "Roosevelt", "1882-01-30", "124 57th St New York City", "7075435096548987", "President", 00261, true);
			insertIntoAccountTable(conn, 21283, "Teddy", "Theodore", "Roosevelt", "1858-10-27", "14 3rd St New York City", "9090767632321679", "President", 61998, true);
			insertIntoAccountTable(conn, 33033, "JFK", "John", "Kennedy", "1917-05-29", "13 Warren St Brooklyn", "1096548987075435", "President", 994261, true);
			insertIntoAccountTable(conn, 83621, "JCarter", "Jimmy", "Carter", "1984-04-22", "33 RR 3, Plains", "7777888866665555", "President", 112398, true);
			insertIntoAccountTable(conn, 9912, "LBJ", "Lyndon", "Johnson", "1908-08-17", "16 Jefferson Blvd Dallas", "7109654898075435", "President", 9900061, true);

			// Renters
			insertIntoAccountTable(conn, 89637, "JT2015", "Justin", "Trudeau", "1971-12-25", "24 Sussex Drive", "2015201620172018", "Prime Minister", 8119, false);
			insertIntoAccountTable(conn, 90300, "TheRock", "Dwayne", "Johnson", "1972-05-02", "34 13 Ave Hollywood California", "0987654320987654", "Wrestler", 2639, false);
			insertIntoAccountTable(conn, 84031, "AbeLinc", "Abraham", "Lincoln", "1823-01-09", "23 45th Street Chicago", "0987698765454320", "President", 9039, false);
			insertIntoAccountTable(conn, 84000, "Shaq", "Shaquille", "ONeal", "1971-10-19", "12 Mountain blvd Los Angeles", "0976545432087698", "NBA Player", 39787, false);
			insertIntoAccountTable(conn, 56832, "MJ", "Michael", "Jordan", "1962-10-19", "45 Ocean Road Malibu", "3208769809765454", "NBA Player", 39787, false);
			insertIntoAccountTable(conn, 72971, "Oiler97", "Connor", "McDavid", "1997-01-13", "29 Forest Crescent Edmonton", "9070009865781113", "NHL Player", 97873, false);
			insertIntoAccountTable(conn, 77129, "GoldenGoal", "Sydney", "Crosby", "1987-08-07", "12 13th St Halifax", "0700098965781113", "NHL Player", 97871, false);
		} catch (Exception e) {
			p("Error while populating tables! -> " + e);
		}
	}

	static void deleteAccount(Connection conn, int id) {
		try {
			String stat = "DELETE FROM Account WHERE id=" + id;
			PreparedStatement deleteStat = conn.prepareStatement(stat);
			deleteStat.executeUpdate();
			p("\n [Account successfully deleted.]\n");
		} catch (Exception e) {
			p("Error while deleting account! -> " + e);
		}
	}

	static void deleteAccountListings(Connection conn, int hid) {
		try {
			String stat = "DELETE FROM Listing WHERE hid=" + hid;
			PreparedStatement deleteStat = conn.prepareStatement(stat);
			deleteStat.executeUpdate();
			p("\n [Listings successfully deleted.]\n");
		} catch (Exception e) {
			p("Error while deleting account listings! -> " + e);
		}
	}

	static void deleteAccountReservations(Connection conn, int id) {
		try {
			String stat = "DELETE FROM Availability WHERE hid=" + id;
			PreparedStatement deleteStat = conn.prepareStatement(stat);
			deleteStat.executeUpdate();

			String stat2 = "UPDATE Availability SET rid=NULL WHERE rid=" + id;
			PreparedStatement deleteStat2 = conn.prepareStatement(stat2);
			deleteStat2.executeUpdate();
			p("\n [Reservations successfully deleted.]\n");
		} catch (Exception e) {
			p("Error while deleting account listings! -> " + e);
		}
	}

	static void userDeleteAccount(Connection conn) {
		if(!isLoggedIn) {
			p(" You must be logged in to delete your account.");
			return;
		}
		Scanner myObj = new Scanner(System.in);
		System.out.print(" You are about to delete your account! Type 'CONFIRM' to delete your account: ");
		String confirm = myObj.nextLine();
		if(confirm.equals("CONFIRM")) {
			deleteAccountReservations(conn, currentID);
			deleteAccountListings(conn, currentID);
			deleteAccount(conn, currentID);
			isLoggedIn = false;
			currentID = -1;
			currentUsername = null;
		} else {
			p(" Keyword does not match 'CONFIRM', account not deleted.");
		}
	}


	static void insertIntoAccountTable(Connection conn, int id, String username, String firstName, String lastName, String dob, String address, String creditCard, String occupation, int sin, boolean isHost) {
		try {
			String updatStr = "INSERT INTO Account(id, username, firstName, lastName, dob, address, creditCard, occupation, sin, isHost) VALUES(?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement insertAccount = conn.prepareStatement(updatStr);
			insertAccount.setInt(1, id);
			insertAccount.setString(2, username);
			insertAccount.setString(3, firstName);
			insertAccount.setString(4, lastName);
			insertAccount.setDate(5, java.sql.Date.valueOf(dob));
			insertAccount.setString(6, address);
			insertAccount.setString(7, creditCard);
			insertAccount.setString(8, occupation);
			insertAccount.setInt(9, sin);
			insertAccount.setBoolean(10, isHost);

			int r = insertAccount.executeUpdate();
			if(r == 1) {
				isLoggedIn = true;
				currentUsername = username;
				currentID = id;
			}
			p("\n Welcome, " + username + "! Your account has been successfully created, and you are now logged in.\n\n");
		} catch (Exception e) {
			p("Error inserting into Account table! -> " + e);
		}
	}

	static void viewAccounts(Connection conn) {
		try {
			PreparedStatement execStat = conn.prepareStatement("SELECT * FROM Account");
			ResultSet rs = execStat.executeQuery(); 
		       
			while (rs.next()) {
				int id  = rs.getInt("id");
				String username = rs.getString("username");
				String firstName = rs.getString("firstName");
				String lastName = rs.getString("lastName");
				Date dob = rs.getDate("dob");
				String address = rs.getString("address");
				String creditCard = rs.getString("creditCard");
				String occupation = rs.getString("occupation");
				int sin = rs.getInt("sin");
				boolean isHost = rs.getBoolean("isHost");

				p("\n =====   User Overview for " + username + " (UserID " + id + "):   =====");
				p(" " + firstName + " " + lastName + ", " + occupation);
				p(" Born " + dob);
				p(" Address: " + address);
				p(" Credit Card Number: " + creditCard);
				p(" Social Insurance Number: " + sin);
				p(" Is a host? " + isHost + "\n");
			}
    	} catch (Exception e) {
    		p("Error retrieving user data -> " + e);
    	}
	}



    // ===========================   Listings ==========================




	static void createListingTable(Connection conn) {
		try {
			String stat = "CREATE TABLE Listing(lid int, hid int REFERENCES Account(id), type varchar(20), streetAddress " +
				"varchar(100), postalCode varchar(6), city varchar(30), country varchar(30), longitude float, latitude float, " +
				"numBeds int, numBath int, wifi bool, kitchen bool, parking bool, PRIMARY KEY(lid));";
			PreparedStatement createStat = conn.prepareStatement(stat);
			createStat.executeUpdate();
		} catch (Exception e) {
			p("Error while creating tables! -> " + e);
		}
	}

	static void userDeleteListing(Connection conn) {
		if (!isLoggedIn) {
			p(" You must be logged to delete a listing.");
			return;
		}
		Scanner myObj = new Scanner(System.in);
		System.out.print(" Enter the ID of the listing to be deleted: ");
		int delLID = Integer.valueOf(myObj.nextLine());

		try {
			String stat = "SELECT lid FROM Listing WHERE lid = '" + delLID + "' AND hid = '" + currentID + "'";
			PreparedStatement execStat = conn.prepareStatement(stat);
			ResultSet rs = execStat.executeQuery(); 

			if(rs.next() == true) {
				deleteListing(conn, delLID);
			} else {
				p(" Sorry, you are not the host of that listing. You can only delete your own listings.");
			}
		} catch (Exception e) {
			p(" Error while logging in -> " + e);
		}
	}

	static void userCreateListing(Connection conn) {
		if (!isLoggedIn) {
			p(" You must be logged in to create a new listing.");
			return;
		}

		// Check if the user is a host
		try {
			String stat = "SELECT isHost FROM Account WHERE id=" + currentID;
			PreparedStatement execStat = conn.prepareStatement(stat);             
			ResultSet rs = execStat.executeQuery(); 

			rs.next();
			Boolean isHost = rs.getBoolean("isHost");
			if(!isHost) {
				p(" Sorry, only hosts can create listings.");
				return;
			}

		} catch (Exception e) {
			p("Error while checking for for renter-host correlation -> " + e);
		}
		Scanner myObj = new Scanner(System.in);

		p("\n ===================================");
		p("\n You are now creating a new listing! Please enter the following information:\n");

		System.out.print(" What is the type of the listing? [Room/Apartment/House] ");
		String type = myObj.nextLine();
		if(!type.equals("Room") && !type.equals("Apartment") && !type.equals("House")) {
			p(" Please set the type of listing to [Room/Apartment/House].");
			return;
		}

		System.out.print(" What is the street address of the listing? ");
		String streetAddress = myObj.nextLine();

		System.out.print(" What city is this listing located in? ");
		String city = myObj.nextLine();

		System.out.print(" What country is this listing located in? ");
		String country = myObj.nextLine();

		System.out.print(" What is the postal code of the listing? ");
		String postalCode = myObj.nextLine();

		System.out.print(" Please enter the longitude of listing location. ");
		String lo = myObj.nextLine();
		double longitude = Double.valueOf(lo);

		System.out.print(" Please enter the latitude of listing location. ");
		String la = myObj.nextLine();
		double latitude = Double.valueOf(la);

		System.out.print(" How many bedrooms are available? ");
		String nbed = myObj.nextLine();
		int numBeds = Integer.valueOf(nbed);

		System.out.print(" How many bathrooms are available? ");
		String nbath = myObj.nextLine();
		int numBaths = Integer.valueOf(nbath);

		System.out.print(" Is there Wifi? [Y/N] ");
		String wifiStr = myObj.nextLine();
		Boolean wifi = wifiStr.equals("Y");

		System.out.print(" Is there a kitchen? [Y/N] ");
		String kitchenStr = myObj.nextLine();
		Boolean kitchen = kitchenStr.equals("Y");

		System.out.print(" Is there parking? [Y/N] ");
		String parkingStr = myObj.nextLine();
		Boolean parking = parkingStr.equals("Y");

		int hid = currentID;
		int lid = generateID();

		try {
			insertIntoListingsTable(conn, lid, hid, type, streetAddress, postalCode, city, country, longitude, latitude, numBeds, numBaths, wifi, kitchen, parking);
			p("\n Your Listing has been successfully created!\n\n");
		} catch (Exception e) {
			p(" There was an issue creating your listing: " + e);
		}
		
		System.out.print(" If you want to start adding prices and dates for reservations, type 'NEXT'. \n");
		System.out.print(" If you want to leave this until later, type 'DONE' ");

		String confirm = myObj.nextLine();
		if (confirm.equals("NEXT")) {
			userCreateAvailability(conn, lid);
		} else {
			p(" \n Thank you! Your listing is now in the database. When you are ready to make it available to rent, use the command 'CreateAvailability'.");
		}
	}

	static void populateListingTable(Connection conn) {
		try {
			//Washington
			insertIntoListingsTable(conn, 67812, 7809, "Room", "400 57th Avenue", "G0B3J1", "New York City", "United States", -74.0194, 40.7320, 1, 1, false, false, false);
	    	insertIntoListingsTable(conn, 9134, 7809, "Apartment", "312 Yonge Street", "M2U9L1", "Toronto", "Canada", -79.41112, 43.65743, 2, 2, true, true, false);
	    	insertIntoListingsTable(conn, 23900, 7809, "House", "1603 Bloor Street", "M2N7K1", "Toronto", "Canada", -79.4372, 43.6589, 4, 3, true, true, true);
	    	//Bernie
	    	insertIntoListingsTable(conn, 290123, 39007, "House", "45 High Street", "K4V7L0", "Vancouver", "Canada", -123.8234, 49.2812, 2, 1, false, true, true);
	    	insertIntoListingsTable(conn, 110233, 39007, "Apartment", "3 Church Street", "M2U9L1", "Toronto", "Canada", -79.4372, 43.6009, 3, 1, true, true, true);
	    	insertIntoListingsTable(conn, 23901, 39007, "Room", "120 22nd Avenue", "O8IH9E", "London", "UK", 0.1223, 51.5289, 1, 1, true, false, false);
	    	//Ronald Reagan
	    	insertIntoListingsTable(conn, 839345, 74823, "Apartment", "23 Eglinton Avenue E", "M2N7K1", "Toronto", "Canada", -79.430, 43.7189, 2, 1, true, true, false);
	    	insertIntoListingsTable(conn, 999345, 74823, "Room", "129 Wall Street", "G0R2L1", "New York City", "United States", -74.770, 40.8189, 1, 0, false, false, true);
	    	//FDR 
	    	insertIntoListingsTable(conn, 899345, 82398, "Apartment", "22 Eglinton Avenue E", "M2N7K1", "Toronto", "Canada", -79.432, 43.7190, 2, 1, true, true, false);
	    	//Teddy
	    	insertIntoListingsTable(conn, 213743, 21283, "House", "11 Bayside Drive", "K4N7K1", "Vancouver", "Canada", -123.772, 49.2210, 4, 3, true, true, true);
	    	insertIntoListingsTable(conn, 218883, 21283, "Apartment", "88 University Ave", "K4V7L0", "Vancouver", "Canada", -123.722, 48.9810, 2, 2, false, true, false);
	    	insertIntoListingsTable(conn, 611374, 21283, "Apartment", "44 Deer Park Ave", "M2N7K4", "Toronto", "Canada", -79.472, 43.6710, 1, 1, true, true, false);
	    	//JFK
	    	insertIntoListingsTable(conn, 219743, 33033, "House", "11 Bayside Drive", "K4N7K1", "Vancouver", "Canada", -123.772, 49.2210, 4, 3, true, true, true);
	    	insertIntoListingsTable(conn, 219883, 33033, "House", "13 Privet drive", "O8R3G4", "London", "UK", 0.422, 51.6810, 3, 2, true, true, true);
	    	insertIntoListingsTable(conn, 619374, 33033, "Room", "2 Buckingham St", "O8R3G4", "London", "UK", 0.3720, 51.6210, 1, 1, true, true, false);
	    	//Carter
	    	insertIntoListingsTable(conn, 975836, 83621, "House", "14 Bayside Drive", "K4N7K1", "Vancouver", "Canada", -123.779, 49.2219, 2, 2, false, true, true);
	    	insertIntoListingsTable(conn, 980328, 83621, "Apartment", "452 Greenway Road", "K4N8R2", "Vancouver", "Canada", -123.113, 49.2819, 2, 2, true, true, false);
	    	insertIntoListingsTable(conn, 378237, 83621, "Apartment", "453 Greenway Road", "K4N8R2", "Vancouver", "Canada", -123.117, 49.2829, 2, 2, true, true, false);
	    	// LBJ
	    	insertIntoListingsTable(conn, 558877, 9912, "House", "32 Sheppard Ave East", "M2N7K4", "Toronto", "Canada", -79.4107, 43.7617, 3, 1, true, true, true);
		} catch (Exception e) {
			p("Error while creating tables! -> " + e);
		}
	}

	static void deleteTable(Connection conn, String tableName) {
		try {
			PreparedStatement clearStat = conn.prepareStatement("DROP TABLE " + tableName);
			clearStat.executeUpdate();
		} catch (Exception e) {
			p("Error while clearing table! -> " + e);
		}
	}

	static void userDeleteTable(Connection conn) {
		Scanner myObj = new Scanner(System.in);
		System.out.print(" Enter the name of the table to delete: ");
		String tableToDelete = myObj.nextLine();
		deleteTable(conn, tableToDelete);
		p(" [Table deleted]");
	}

	static void deleteListing(Connection conn, int lid) {
		try {
			String stat = "DELETE FROM Listing WHERE lid=" + lid;
			PreparedStatement deleteStat = conn.prepareStatement(stat);
			deleteStat.executeUpdate();
			p("\n [Listing successfully deleted.]\n");
		} catch (Exception e) {
			p("Error while deleting Listing! -> " + e);
		}
	}

	static void viewListingsHistory(Connection conn) {
		if(!isLoggedIn) {
			p(" You must be logged in to see a listing's rental history.");
			return;
		}
		try {
			Scanner myObj = new Scanner(System.in);
			
			System.out.print(" Enter listing code: ");
			String lidStr = myObj.nextLine();
			int lid = Integer.valueOf(lidStr);

			String stat = "SELECT * FROM Availability WHERE lid=" + lid + " AND rid IS NOT NULL AND hid=" + currentID + " ORDER BY day DESC";
			PreparedStatement execStat = conn.prepareStatement(stat);             
			ResultSet rs = execStat.executeQuery(); 

			p("\n");
			while(rs.next()) {
				Date day = rs.getDate("day");
				int rid = rs.getInt("rid");
				float price = rs.getFloat("price");
				p(" Rented for $" + price + " for the night of " + day + " (Renter code " + rid + ")"); 
			}
			p("\n");
		} catch (Exception e) {
			p(" Error while getting listing history -> " + e);
		}
	}

	static void clearTable(Connection conn, String tableName) {
		try {
			PreparedStatement clearStat = conn.prepareStatement("DELETE FROM " + tableName);
			clearStat.executeUpdate();
		} catch (Exception e) {
			p("Error while clearing table! -> " + e);
		}
	}

	static void userClearTable(Connection conn) {
		Scanner myObj = new Scanner(System.in);
		System.out.print(" Enter the name of the table to clear: ");
		String tableToClear = myObj.nextLine();
		clearTable(conn, tableToClear);
		p(" [Table cleared]");
	}

	static void insertIntoListingsTable(Connection conn, int lid, int hid, String type, String streetAddress, String postalCode, String city, String country, double longitude, double latitude, int numBeds, int numBath, boolean wifi, boolean kitchen, boolean parking) {
		try {
			String updatStr = "INSERT INTO Listing(lid, hid, type, streetAddress, postalCode, city, country, longitude, latitude, numBeds, numBath, wifi, kitchen, parking) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement insertListing = conn.prepareStatement(updatStr);
			insertListing.setInt(1, lid);
			insertListing.setInt(2, hid);
			insertListing.setString(3, type);
			insertListing.setString(4, streetAddress);
			insertListing.setString(5, postalCode);
			insertListing.setString(6, city);
			insertListing.setString(7, country);
			insertListing.setDouble(8, longitude);
			insertListing.setDouble(9, latitude);
			insertListing.setInt(10, numBeds);
			insertListing.setInt(11, numBath);
			insertListing.setBoolean(12, wifi);
			insertListing.setBoolean(13, kitchen);
			insertListing.setBoolean(14, parking);

			insertListing.executeUpdate();
		} catch (Exception e) {
			p("Error inserting into Listing table! -> " + e);
		}
	}

	static void viewListings(Connection conn) {
		try {
			if(!isLoggedIn) {
				p(" You must be logged in to view your listings.");
				return;
			}

			// Check if the user is a host
			try {
				String stat = "SELECT isHost FROM Account WHERE id=" + currentID;
				PreparedStatement execStat = conn.prepareStatement(stat);             
				ResultSet rs = execStat.executeQuery(); 

				rs.next();
				Boolean isHost = rs.getBoolean("isHost");
				if(!isHost) {
					p(" Sorry, only hosts can view their listings.");
					return;
				}

			} catch (Exception e) {
				p("Error while checking for if user is host -> " + e);
			}
			PreparedStatement execStat = conn.prepareStatement("SELECT * FROM Listing WHERE hid=" + currentID);
			ResultSet rs = execStat.executeQuery(); 
		       
			while (rs.next()) {
				int lid  = rs.getInt("lid");
				String type = rs.getString("type");
				String streetAddress = rs.getString("streetAddress");
				String postalCode = rs.getString("postalCode");
				String city = rs.getString("city");
				String country = rs.getString("country");
				Float longitude = rs.getFloat("longitude");
				Float latitude = rs.getFloat("latitude");
				int numBeds = rs.getInt("numBeds");
				int numBath = rs.getInt("numBath");
				boolean wifi = rs.getBoolean("wifi");
				boolean kitchen = rs.getBoolean("kitchen");
				boolean parking = rs.getBoolean("parking");

				p("\n =====  " + type + " at " + streetAddress + ", " + city + ", " + country + " | Listing code " + lid + " =====");
				p(" Postal Code:  " + postalCode + " | [" + latitude + ", " + longitude + "]");
				p(" Wifi" + (wifi ? " provided," : " not provided," ) + " kitchen" + (kitchen ? " available," : " not available," ) + (parking ? " access to parking. " : " no access to parking. " ));
				p(" " + numBeds + " Bedrooms and " + numBath + " Bathrooms available.\n");
			}
    	} catch (Exception e) {
    		p("Error retrieving user data-> " + e);
    	}
	}



	// ===================== Reservation functions ===============



	static void createAvailabilityTable(Connection conn) {
		try {
			String stat = "CREATE TABLE Availability(aid int PRIMARY KEY, lid int REFERENCES Listing(lid), hid int REFERENCES Account(id), rid int REFERENCES Account(id), price float, day date);";
			PreparedStatement createStat = conn.prepareStatement(stat);
			createStat.executeUpdate();
		} catch (Exception e) {
			p("Error while creating availability table! -> " + e);
		}
	}

	static void populateAvailabilityTable(Connection conn) {
		try {
			//Gwashington
			insertAvailability(conn, 129890, 67812, 7809, 84000, 49.99, "2023-03-03");
			insertAvailability(conn, 217217, 67812, 7809, 84000, 49.99, "2023-03-04");
			insertAvailability(conn, 217351, 67812, 7809, -1, 59.99, "2023-03-05");
			insertAvailability(conn, 333309, 67812, 7809, -1, 59.99, "2023-03-06");
			insertAvailability(conn, 559372, 9134, 7809, -1, 119.95, "2022-11-06");
			insertAvailability(conn, 827654, 23900, 7809, 77129, 399.99, "2023-03-15");
			insertAvailability(conn, 193742, 23900, 7809, 77129, 399.99, "2023-03-16");
			insertAvailability(conn, 671033, 23900, 7809, 77129, 399.99, "2023-03-17");
			insertAvailability(conn, 173917, 23900, 7809, -1, 399.99, "2023-03-18");
			insertAvailability(conn, 378888, 23900, 7809, -1, 399.99, "2023-03-19");
			insertAvailability(conn, 937233, 23900, 7809, -1, 399.99, "2023-03-20");
			//Bernie
			insertAvailability(conn, 391082, 290123, 39007, -1, 349.99, "2023-03-03");
			insertAvailability(conn, 849271, 110233, 39007, -1, 139.99, "2023-12-12");
			insertAvailability(conn, 111341, 110233, 39007, -1, 159.99, "2024-01-25");
			insertAvailability(conn, 184027, 110233, 39007, -1, 159.99, "2024-01-26");
			insertAvailability(conn, 992239, 23901, 39007, 7809, 39.99, "2021-07-29");
			insertAvailability(conn, 108238, 23901, 39007, 7809, 39.99, "2021-07-30");
			insertAvailability(conn, 649271, 23901, 39007, 7809, 39.99, "2021-07-31");
			insertAvailability(conn, 193027, 23901, 39007, 7809, 39.99, "2021-08-01");
			//FDR
			insertAvailability(conn, 679876, 899345, 82398, 56832, 200.00, "2023-10-01");
			insertAvailability(conn, 702841, 899345, 82398, -1, 210.00, "2023-10-02");
			insertAvailability(conn, 982519, 899345, 82398, -1, 220.00, "2023-10-03");
			insertAvailability(conn, 324870, 899345, 82398, -1, 230.00, "2023-10-04");
			insertAvailability(conn, 274142, 899345, 82398, -1, 230.00, "2023-10-05");
			insertAvailability(conn, 128732, 899345, 82398, -1, 230.00, "2023-10-06");
			insertAvailability(conn, 239864, 899345, 82398, -1, 230.00, "2023-10-07");
			//Teddy
			insertAvailability(conn, 823989, 213743, 21283, 84000, 599.99, "2022-12-25");
			insertAvailability(conn, 987311, 218883, 21283, -1, 450.00, "2022-12-25");
			insertAvailability(conn, 154809, 611374, 21283, -1, 204.99, "2022-12-25");
			//JFK
			insertAvailability(conn, 914799, 219883, 33033, -1, 388.88, "2023-02-16");
			insertAvailability(conn, 886254, 219883, 33033, -1, 388.88, "2023-02-17");
			insertAvailability(conn, 943780, 219883, 33033, 84000, 399.99, "2023-03-16");
			insertAvailability(conn, 900021, 219883, 33033, 84000, 399.99, "2023-03-17");
			insertAvailability(conn, 371949, 619374, 33033, 56832, 99.99, "2020-03-01");
			//Carter
			insertAvailability(conn, 991122, 975836, 83621, 84031, 199.99, "2021-03-03");
			insertAvailability(conn, 982371, 975836, 83621, 84031, 199.99, "2021-03-04");
			insertAvailability(conn, 128721, 975836, 83621, 84031, 179.99, "2021-03-05");
			insertAvailability(conn, 983798, 975836, 83621, 84031, 179.99, "2021-03-06");
			insertAvailability(conn, 897123, 980328, 83621, -1, 69.99, "2022-10-15");
			insertAvailability(conn, 719876, 980328, 83621, -1, 89.99, "2023-10-15");
			insertAvailability(conn, 713336, 378237, 83621, -1, 89.99, "2023-10-15");
			//LBJ
			insertAvailability(conn, 34781, 558877, 9912, -1, 550.00, "2023-07-01");
			insertAvailability(conn, 291772, 558877, 9912, 90300, 550.00, "2023-07-02");
		} catch (Exception e) {
			p(" Error while populating availability table -> " + e);
		}
	}

	static void insertAvailability(Connection conn, int aid, int lid, int hid, int rid, double price, String day) {
		try {
			String updatStr = "INSERT INTO Availability(aid, lid, hid, rid, price, day) VALUES(?,?,?,?,?,?)";
			PreparedStatement insertAvailable = conn.prepareStatement(updatStr);
			insertAvailable.setInt(1, aid);
			insertAvailable.setInt(2, lid);
			insertAvailable.setInt(3, hid);
			if (rid == -1) {
	            insertAvailable.setNull(4, Types.INTEGER);
	        }
	        else {
	            insertAvailable.setInt(4, rid);
	        }
			insertAvailable.setDouble(5, price);
			insertAvailable.setDate(6, java.sql.Date.valueOf(day));

			insertAvailable.executeUpdate();
			p(" Your listing is available for $" + price + " on " + day + "!\n");
		} catch (Exception e) {
			p(" Error inserting into Availablility table! -> " + e);
		}
	}

	static void updateAvailability(Connection conn, int aid, int rid) {
		// UPDATE table_name SET column1 = value1, column2 = value2, ... WHERE condition;
		try {
			String updatStr;
			if (rid == -1) {
				updatStr = "UPDATE Availability SET rid=NULL WHERE aid=" + aid;
			} else {
				updatStr = "UPDATE Availability SET rid=" + rid + " WHERE aid=" + aid;
			}
			PreparedStatement updateAvailable = conn.prepareStatement(updatStr);     // NEED A CASE WHERE RID = -1, THEN SET TO NULL.
			updateAvailable.executeUpdate();
			p(" [Reservation updated]\n");
		} catch (Exception e) {
			p("Error updating Availablility table! -> " + e);
		}
	}

	static void updatePrice(Connection conn, int aid, double price) {
		// UPDATE table_name SET column1 = value1, column2 = value2, ... WHERE condition;
		try {
			String updatStr = "UPDATE Availability SET price=" + price + " WHERE rid IS NULL AND aid=" + aid ;
			PreparedStatement updatePrice = conn.prepareStatement(updatStr);
			updatePrice.executeUpdate();
			p("\n The price has been updated to $" + price + "!\n");
		} catch (Exception e) {
			p("Error updating price on Availablility table! -> " + e);
		}
	}

	static void userUpdatePrice(Connection conn) {
		if (!isLoggedIn) {
			p(" You must be logged in to update the price of a listing.");
			return;
		}
		Scanner myObj = new Scanner(System.in);

		p("\n ===================================");
		p("\n You are now updating the price of an availability! Please enter the following information:\n");

		System.out.print(" What is the reservation code? ");
		String aidStr = myObj.nextLine();
		int aid = Integer.valueOf(aidStr);

		try {
			String stat = "SELECT price, day FROM Availability WHERE rid IS NULL AND hid = " + currentID + " AND aid = " + aid;
			PreparedStatement execStat = conn.prepareStatement(stat);
			ResultSet rs = execStat.executeQuery(); 

			if(rs.next() == false) {
				p(" Sorry, you are unable to change the price of this listing.");
				return;
			} else {
				float price = rs.getFloat("price");
				Date day = rs.getDate("day"); 
				p(" \n The current listing is priced at at $" + price + " for the night of " + day + ".\n");
			}
		} catch (Exception e) {
			p("Error while checking for listing ownership -> " + e);
		}

		System.out.print(" Please enter the new price: ");
		String newPriceStr = myObj.nextLine();
		double newPrice = Double.valueOf(newPriceStr);

		try {
			updatePrice(conn, aid, newPrice);
		} catch (Exception e) {
			p("There was an issue updating the price! -> " + e);
		}
	}

	static double suggestPrice(Connection conn, int lid) {
		try {
			String stat = "SELECT type, numBeds, numBath, wifi, kitchen, parking FROM Listing WHERE lid = " + lid;
			PreparedStatement execStat = conn.prepareStatement(stat);
			ResultSet rs = execStat.executeQuery(); 
			String type = "apartment";
			int numBeds = 1;
			int numBaths = 1;
			boolean wifi = false;
			boolean parking = false;
			boolean kitchen = false;

			if(rs.next()) {
				type = rs.getString("type");
				numBeds = rs.getInt("numBeds");
				numBaths = rs.getInt("numBath");
				wifi = rs.getBoolean("wifi");
				kitchen = rs.getBoolean("kitchen");
				parking = rs.getBoolean("parking");
			}

			PreparedStatement execAvgStat = conn.prepareStatement("SELECT AVG(price) as averagePrice FROM (Availability NATURAL JOIN Listing) where type='" + type + "'");
			ResultSet rsAvg = execAvgStat.executeQuery();

			double avgPrice = 300;
			if(rsAvg.next()) {
				avgPrice = rsAvg.getDouble("averagePrice");
			}

			if(type.toLowerCase().equals("room")) {
				return avgPrice;
			}
			double suggestedPrice = (avgPrice - 200) + 20*(numBeds) + 25*(numBaths) + (wifi ? 10 : 0) + (parking ? 20 : 0) + (kitchen ? 25 : 0);
			if(!(wifi && kitchen && parking)) {
				double potentialGain = (wifi ? 10 : 0) + (parking ? 20 : 0) + (kitchen ? 25 : 0);
				p(" Tip! You can expect to charge up to an additional $" + potentialGain + " if a kitchen, parking, and wifi is available in your listing.");
			}
			return suggestedPrice;
		} catch (Exception e) {
			p(" Error while calculating suggested price -> " + e);
		}
		return 300;
	}

	public static List<String> getDaysBetweenDates(String startdate, String enddate){
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");      
		    Date day1 = formatter.parse(startdate); 
		    Date day2 = formatter.parse(enddate); 

		    List<Date> dates = new ArrayList<Date>();
		    Calendar calendar = new GregorianCalendar();
		    calendar.setTime(day1);

		    while (calendar.getTime().before(day2)) {
		        Date result = calendar.getTime();
		        dates.add(result);
		        calendar.add(Calendar.DATE, 1);
		    }
		   	Date result = calendar.getTime();
		    dates.add(result);
		    calendar.add(Calendar.DATE, 1);
		    
		    List<String> dateStrs = new ArrayList<String>();
		   	for(int i=0; i < dates.size(); i++) {
				String dateAsStr = formatter.format(dates.get(i));
				dateStrs.add(dateAsStr);
			}
			return dateStrs;
		} catch (Exception e) {
			p(" Error generating date range -> " + e);
		}
		return null;
	}

	static void userCreateAvailability(Connection conn, int suppliedlid) {
		if (!isLoggedIn) {
			p(" You must be logged in the create an availability.");
			return;
		}
		Scanner myObj = new Scanner(System.in);

		p("\n ===================================");
		p("\n You are now creating an availability! Please enter the following information:\n");

		int lid;

		if(suppliedlid != -1) {
			lid = suppliedlid;
		} else {
			System.out.print(" What is the listing code? ");
			String lidStr = myObj.nextLine();
			lid = Integer.valueOf(lidStr);

			try {
				String stat = "SELECT lid FROM Listing WHERE lid = '" + lid + "' AND hid = '" + currentID + "'";
				PreparedStatement execStat = conn.prepareStatement(stat);
				ResultSet rs = execStat.executeQuery(); 

				if(rs.next() == false) {
					p(" Sorry, but you are only able to make available listings that are your own.");
					return;
				}
			} catch (Exception e) {
				p("Error while checking for listing ownership -> " + e);
			}
		}

		System.out.print(" What is first day the listing is available? [YYYY-MM-DD]: ");
		String startDay = myObj.nextLine();

		System.out.print(" What is last day the listing is available? [YYYY-MM-DD]: ");
		String lastDay = myObj.nextLine();

		// ========== ToolKit ================

		System.out.print(" [MyBnB ToolKit] Would you like a suggested price for you listing? (Leave blank to skip) ");
		String toolKitConfirm = myObj.nextLine();

		if(!toolKitConfirm.equals("")) {
			p(" The suggested price for your listing is $" + (int)suggestPrice(conn, lid) + ".");
		}

		// ===================================

		System.out.print(" What is the price? ");
		String priceStr = myObj.nextLine();
		double price = Double.valueOf(priceStr);

		int hid = currentID;
		int rid = -1;

		try {
			List<String> dates = new ArrayList<String>();
			dates = getDaysBetweenDates(startDay, lastDay);
			for(int i = 0; i < dates.size(); i++) {
				int aid = generateID();
				String day = dates.get(i);

				String stat = "SELECT lid FROM Availability WHERE lid = '" + lid + "' AND day = '" + day + "'";
				PreparedStatement execStat = conn.prepareStatement(stat);
				ResultSet rs = execStat.executeQuery(); 

				if(rs.next() == false) {
					insertAvailability(conn, aid, lid, hid, rid, price, day);
				} else {
					p(" The listing is already available on " + day + ". If you would like to change it's price, use 'UpdatePrice'. \n");
				}
			}
		} catch (Exception e) {
			p(" There was an issue creating your reservation: " + e);
		}
	}

	static void userReserveListing(Connection conn) {
		if (!isLoggedIn) {
			p(" You must be logged in to create a reservation.");
			return;
		}
		Scanner myObj = new Scanner(System.in);

		p("\n ===================================");
		p("\n You are now creating an reservation! Please enter the following information:\n");

		System.out.print(" What is the reservation code? ");
		String aidStr = myObj.nextLine();
		int aid = Integer.valueOf(aidStr);

		try {
			String stat = "SELECT price FROM Availability WHERE rid IS NULL AND aid = " + aid;
			PreparedStatement execStat = conn.prepareStatement(stat);
			ResultSet rs = execStat.executeQuery(); 

			if(rs.next() == false) {
				p(" Sorry, but this time and location are not currently available.");
				return;
			} else {
				float price = rs.getFloat("price");
				p(" \n Your reservation has been purchased at $" + price + "!\n");
			}
		} catch (Exception e) {
			p("Error while checking for listing ownership -> " + e);
		}

		int rid = currentID;

		try {
			updateAvailability(conn, aid, rid);
		} catch (Exception e) {
			p(" There was an issue creating your reservation: " + e);
		}
	}

	static void deleteAvailability(Connection conn, int aid) {
		try {
			String stat = "DELETE FROM Availability WHERE aid=" + aid;
			PreparedStatement deleteStat = conn.prepareStatement(stat);
			deleteStat.executeUpdate();
			p("\n [Availability successfully deleted.]\n");
		} catch (Exception e) {
			p("Error while deleting Availability! -> " + e);
		}
	}

	static void hostRemoveAvailability(Connection conn) {
		if (!isLoggedIn) {
			p(" You must be logged in to cancel an availability.");
			return;
		}
		// Check if the user is a host
		try {
			String stat = "SELECT isHost FROM Account WHERE id=" + currentID;
			PreparedStatement execStat = conn.prepareStatement(stat);             
			ResultSet rs = execStat.executeQuery(); 

			rs.next();
			Boolean isHost = rs.getBoolean("isHost");
			if(!isHost) {
				p(" Sorry, only hosts can change availability of their listings.");
				return;
			}

		} catch (Exception e) {
			p("Error while checking for if user is host -> " + e);
		}
		Scanner myObj = new Scanner(System.in);

		p("\n ===================================");
		p("\n You are now cancelling an availability. Please enter the following information:\n");

		System.out.print(" What is the reservation code? ");
		String aidStr = myObj.nextLine();
		int aid = Integer.valueOf(aidStr);
		int hid = -1;
		String dayForCancel = "";

		try {
			String stat = "SELECT day, hid FROM Availability WHERE rid IS NULL AND hid = " + currentID + " AND aid = " + aid;
			PreparedStatement execStat = conn.prepareStatement(stat);
			ResultSet rs = execStat.executeQuery(); 

			if(rs.next() == false) {
				p(" Sorry, but you are unable to change the availability for this reservaiton code.");
				return;
			} else {
				String date_string = "08-08-2022";
      			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");      
       			Date currentDay = formatter.parse(date_string); 

				Date day = rs.getDate("day");
				dayForCancel = "" + day + "";
				hid = rs.getInt("hid");
				if(day.compareTo(currentDay) < 0) {
					p(" Sorry, but the reservation you have given has already taken place.");
					return;
				}
			}
		} catch (Exception e) {
			p("Error while checking for reservation -> " + e);
		}

		try {
			deleteAvailability(conn, aid);
		} catch (Exception e) {
			p(" There was an issue deleting your availability: " + e);
		}
	}

	static void userDeleteReservation(Connection conn) {
		if (!isLoggedIn) {
			p(" You must be logged in to cancel a reservation.");
			return;
		}
		Scanner myObj = new Scanner(System.in);

		p("\n ===================================");
		p("\n You are now cancelling a reservation. Please enter the following information:\n");

		System.out.print(" What is the reservation code? ");
		String aidStr = myObj.nextLine();
		int aid = Integer.valueOf(aidStr);
		int hid = -1;
		String dayForCancel = "";

		try {
			String stat = "SELECT day, hid FROM Availability WHERE rid = " + currentID + " AND aid = " + aid;
			PreparedStatement execStat = conn.prepareStatement(stat);
			ResultSet rs = execStat.executeQuery(); 

			if(rs.next() == false) {
				p(" Sorry, but the reservation code you provided did not match any of your booked reservations.");
				return;
			} else {
				String date_string = "08-08-2022";
      			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");      
       			Date currentDay = formatter.parse(date_string); 

				Date day = rs.getDate("day");
				dayForCancel = "" + day + "";
				hid = rs.getInt("hid");
				if(day.compareTo(currentDay) < 0) {
					p(" Sorry, but the reservation you have given has already taken place.");
					return;
				}
			}
		} catch (Exception e) {
			p("Error while checking for reservation ownership -> " + e);
		}

		try {
			insertIntoCancellationTable(conn, generateID(), hid, currentID, dayForCancel);
			updateAvailability(conn, aid, -1);
		} catch (Exception e) {
			p(" There was an issue deleting your reservation: " + e);
		}
	}


	static void viewAvailibleListings(Connection conn) {
		try {			
			searchWithFilters(conn, "", -1, -1, -1, -1, 9999999, -1, -1, false, false, false, true, "", "");
    	} catch (Exception e) {
    		p("Error viewing reservation data-> " + e);
    	}
	}

	static void viewListingDetails(Connection conn) {

		Scanner myObj = new Scanner(System.in);

		System.out.print(" Please enter the listing code: ");
		String lidStr = myObj.nextLine();
		int lid = Integer.valueOf(lidStr);

		// First display the details like postal code, latitude, longitude, etc.
		try {
			String stat = "SELECT * FROM ((SELECT * FROM Availability INNER JOIN Account ON Account.id=Availability.hid) AS T INNER JOIN Listing ON T.lid=Listing.lid) WHERE T.rid IS NULL AND Listing.lid = " + lid;
			PreparedStatement execStat = conn.prepareStatement(stat);
			ResultSet rs = execStat.executeQuery(); 

			if(rs.next() == false) {
				p(" Sorry, but that listing is not currently available.");
				return;
			} else {
				String type = rs.getString("Listing.type");
				String streetAddress = rs.getString("Listing.streetAddress");
				String postalCode = rs.getString("Listing.postalCode");
				String city = rs.getString("Listing.city");
				String country = rs.getString("Listing.country");
				Float longitude = rs.getFloat("Listing.longitude");
				Float latitude = rs.getFloat("Listing.latitude");
				int numBeds = rs.getInt("Listing.numBeds");
				int numBath = rs.getInt("Listing.numBath");
				boolean wifi = rs.getBoolean("Listing.wifi");
				boolean kitchen = rs.getBoolean("Listing.kitchen");
				boolean parking = rs.getBoolean("Listing.parking");
				String host = rs.getString("T.username");
				
				p("\n =====  Displaying Details for " + type + " at " + streetAddress + ", " + city + ", " + country + "  =====\n");
				p(" Listing code " + lid + " Hosted by " + host);
				p(" Postal Code " + postalCode + " - [" + latitude + ", " + longitude + "]");
				p(" Wifi" + (wifi ? " provided," : " not provided," ) + " kitchen" + (kitchen ? " available," : " not available," ) + (parking ? " access to parking. " : " no access to parking. " ));
				p(" " + numBeds + " Bedroom(s) and " + numBath + " Bathroom(s) available.\n");
			}
    	} catch (Exception e) {
    		p("Error viewing reservation data-> " + e);
    	}

    	// Now get all dates + prices
    	try {
			PreparedStatement execStat = conn.prepareStatement("SELECT * FROM Availability WHERE rid IS NULL AND lid = " + lid + " ORDER BY day");
			ResultSet rs = execStat.executeQuery(); 

			p(" ------- Dates and prices: -------\n");
			while(rs.next()) {
				Float price = rs.getFloat("price");
				Date day = rs.getDate("day");
				int aid = rs.getInt("aid");

				p(" " + day + " - $" + price + " | Reservation code " + aid + "\n");
			}
			p(" ---------------------------------\n");
    	} catch (Exception e) {
    		p("Error viewing reservation data-> " + e);
    	}
	}

	static void viewReservations(Connection conn) {
		// Could probably merge this function with the one above it
		if (!isLoggedIn) {
			p(" You must be logged in to view your reservations.");
			return;
		}
		try {
			p("\n ========== Your Reservations ============ ");
			String stat = "SELECT Availability.aid, Availability.rid, Availability.lid, Availability.price, Availability.day, " +
				"Listing.streetAddress, Listing.city, Listing.country " +
				"FROM (Availability INNER JOIN Listing ON Availability.lid=Listing.lid) WHERE Availability.rid=" + currentID + " ORDER BY Availability.day";
			PreparedStatement execStat = conn.prepareStatement(stat);
			ResultSet rs = execStat.executeQuery(); 
		       
			while (rs.next()) {
				String address  = rs.getString("Listing.streetAddress");
				String city = rs.getString("Listing.city");
				String country = rs.getString("Listing.country");
				int aid = rs.getInt("Availability.aid");
				int lid = rs.getInt("Availability.lid");
				Float price = rs.getFloat("price");
				Date day = rs.getDate("Availability.day");

				p("\n Renting " + address + ", " + city +  ", " + country + " | Listing code " + lid + " / Reservation code " + aid);
				p(" $" + price + " for the night of " + day);
			}

			// Check if the user is a host
			try {
				String checkHostStat = "SELECT isHost FROM Account WHERE id=" + currentID;
				PreparedStatement execCheckStat = conn.prepareStatement(checkHostStat);             
				rs = execCheckStat.executeQuery(); 

				rs.next();
				Boolean isHost = rs.getBoolean("isHost");
				if(!isHost) {
					p("\n");
					return;
				}

			} catch (Exception e) {
				p("Error while checking for if user is host -> " + e);
			}
			p("\n ========== All stays in your Listings ============  \n");

			String statHost = "SELECT Availability.aid, Availability.rid, Availability.lid, Availability.price, Availability.day, " +
				"Listing.streetAddress, Listing.city, Listing.country " +
				"FROM (Availability INNER JOIN Listing ON Availability.lid=Listing.lid) WHERE Availability.rid IS NOT NULL AND Availability.hid=" + currentID + " ORDER BY Availability.day DESC";
			PreparedStatement execStatHost = conn.prepareStatement(statHost);
			rs = execStatHost.executeQuery(); 
		       
			while (rs.next()) {
				String address  = rs.getString("Listing.streetAddress");
				String city = rs.getString("Listing.city");
				String country = rs.getString("Listing.country");
				int aid = rs.getInt("Availability.aid");
				int lid = rs.getInt("Availability.lid");
				int rid = rs.getInt("Availability.rid");
				Float price = rs.getFloat("price");
				Date day = rs.getDate("Availability.day");

				String statUsername = "SELECT username FROM Account WHERE id=" + rid;
				PreparedStatement execStatUsername = conn.prepareStatement(statUsername);
				ResultSet rs1 = execStatUsername.executeQuery();

				String username = "";
				if(rs1.next()) {
					username = rs1.getString("username");
				}

				p("\n Hosting " + address + ", " + city +  ", " + country + " | Listing code " + lid + " / Reservation code " + aid);
				p(" Reserved by " + username);
				p(" $" + price + " for the night of " + day);
			}
			p("\n");
    	} catch (Exception e) {
    		p("Error viewing reservation data-> " + e);
    	}
	}



	// ===================== Seaching-related functions ===================


	static void userSearchByAddress(Connection conn) {
		Scanner myObj = new Scanner(System.in);
		System.out.print(" Enter the name of the country you'd like to search in: ");
		String country = myObj.nextLine();

		System.out.print(" Enter the name of the city you'd like to search in: ");
		String city = myObj.nextLine();

		System.out.print(" Enter the address to search: ");
		String address = myObj.nextLine();

		searchByAddress(conn, address, city, country);
	}

	static void searchByAddress(Connection conn, String addressInput, String cityInput, String countryInput) {
		try {
			String stat = buildQuery(true, "", -1, -1, -1, -1, -1, -1, -1, false, false, false, true, "", "") + " WHERE streetAddress = '" + addressInput + "'";
			PreparedStatement execStat = conn.prepareStatement(stat);
			ResultSet rs = execStat.executeQuery(); 

			p("\n\n ========= Displaying all listings that match " + addressInput + ", " + cityInput + ", " + countryInput + " ==========");
			p(" ===  Use 'ViewListingDetails' [VLD] to see prices, dates, and details.  ===\n\n");
		       
			while (rs.next()) {
				String address  = rs.getString("streetAddress");
				String city  = rs.getString("city");
				String country = rs.getString("country");
				String postalCode = rs.getString("postalCode");
				Float lo1 = rs.getFloat("longitude");
				Float la1 = rs.getFloat("latitude");
				Float price = rs.getFloat("price");
				int lid = rs.getInt("lid");

				p("\n " + address + ", " + city + ", " + country + " | Listing code " + lid);
				p(" Postal code " + postalCode + " [" + la1 + ", " + lo1 + "]");
				p(" Starting at $" + price);
			}
			p("\n\n");
    	} catch (Exception e) {
    		p("Error viewing reservation data-> " + e);
    	}
	}

	static String buildQuery(Boolean base, String pc, float longitude, float latitude, int distance, int minPrice, int maxPrice, int numBeds, int numBaths, boolean wifi, boolean kitchen, boolean parking, boolean isAsc, String startDate, String endDate) {
		if(base) {
			return "SELECT type, streetAddress, city, country, postalCode, longitude, latitude, " + 
			"numBeds, numBath, day, lid, rid, min(price) as price FROM " +
			"(Listing NATURAL JOIN (SELECT * FROM Availability WHERE rid IS NULL) AS F)";
		}

		String distanceMeasure = "";
		if(latitude != -1) {
			distanceMeasure = ", (SQRT((POWER(longitude - " + longitude + ", 2)) + (POWER(latitude - " + latitude +", 2)))*85) as distance";
		}

		String stat = "SELECT type, streetAddress, city, country, postalCode, longitude, latitude, " + 
			"numBeds, numBath, day, lid, rid, min(price) as price" + distanceMeasure + " FROM " +
			"(Listing NATURAL JOIN (SELECT * FROM Availability WHERE rid IS NULL) AS F) WHERE lid > 0";

		if(!pc.equals("") && pc.length() > 1) {
			stat = stat + " AND postalCode LIKE '" + pc.substring(0,2) + "%'";
		}
		if(minPrice > -1) {
			stat = stat + " AND price > " + minPrice;
		}
		if(maxPrice < 9999999) {
			stat = stat + " AND price < " + maxPrice;
		}
		if(numBeds > -1) {
			stat = stat + " AND numBeds = " + numBeds;
		}
		if(numBaths > -1) {
			stat = stat + " AND numBath = " + numBaths;
		}
		if(wifi) {
			stat = stat + " AND wifi = " + 1;
		}
		if(kitchen) {
			stat = stat + " AND kitchen = " + 1;
		}
		if(parking) {
			stat = stat + " AND parking = " + 1;
		}
		if(!startDate.equals("")) {
			stat = stat + " AND day>'" + startDate + "'";
		}
		if(!endDate.equals("")) {
			stat = stat + " AND day<'" + endDate + "'";
		}
		stat = stat + " GROUP BY lid";

		if(latitude != -1) {
			stat = stat + " ORDER BY distance";
		} else if (isAsc) {
			stat = stat + " ORDER BY price ASC";
		} else {
			stat = stat + " ORDER BY price DESC";
		}

		return stat;
	}

	static void searchWithFilters(Connection conn, String pc, float lo2, float la2, int distance, int minPrice, int maxPrice, int numBeds, int numBaths, boolean wifi, boolean kitchen, boolean parking, boolean isAsc, String date1, String date2) {
		try {
			String stat = buildQuery(false, pc, lo2, la2, distance, minPrice, maxPrice, numBeds, numBaths, wifi, kitchen, parking, isAsc, date1, date2);
			PreparedStatement execStat = conn.prepareStatement(stat);
			ResultSet rs = execStat.executeQuery(); 

			p("\n\n ========= Displaying all listings with at least one availability ==========");
			p(" ===  Use 'ViewListingDetails' [VLD] to see prices, dates, and details.  ===\n\n");

			while (rs.next()) {
				String address  = rs.getString("streetAddress");
				String city  = rs.getString("city");
				String country = rs.getString("country");
				String postalCode = rs.getString("postalCode");
				Float lo1 = rs.getFloat("longitude");
				Float la1 = rs.getFloat("latitude");
				Float price = rs.getFloat("price");
				int lid = rs.getInt("lid");

				Boolean passesDistance = (la2 == -1) || distance > rs.getFloat("distance");

				if(passesDistance) {
					p("\n " + address + ", " + city + ", " + country + " | Listing code " + lid);
					p(" Postal code " + postalCode + " [" + la1 + ", " + lo1 + "]");
					if(la2 != -1) {
						Float distanceToCoords = rs.getFloat("distance");
						p(" " + Math.round(distanceToCoords) + "Km from search.");
					}
					p(" Starting at $" + price);
				}
			}
			p("\n\n");
    	} catch (Exception e) {
    		p("Error viewing reservation data-> " + e);
    	}
	}

	static void userSearchWithFilters(Connection conn) {
		p("\n\n =======  Setting Filters for Searching ========\n");

		Scanner myObj = new Scanner(System.in);
		System.out.print(" Enter a postal code to search nearby (Leave blank to skip): ");
		String nearPostalCode = myObj.nextLine();

		// location based on postal code
		System.out.print(" Would you like to search for listings using geographical coordinates? This will sort the results by distance. \n (Leave blank to skip, or type any input to confirm): ");
		String confirmCoords = myObj.nextLine();
		float longitude = -1;
		float latitude = -1;
		int distance = -1;

		// Location based on latitude/longitude
		if(!confirmCoords.equals("")) {
			System.out.print(" Please enter a latitude: ");
			String latitudeStr = myObj.nextLine();
			latitude = Float.valueOf(latitudeStr);

			System.out.print(" Please enter a longitude: ");
			String longitudeStr = myObj.nextLine();
			longitude = Float.valueOf(longitudeStr);

			System.out.print(" Please enter a radius, in kilometers (Leave blank for default distance of 10km): ");
			String distanceStr = myObj.nextLine();
			if(distanceStr.equals("")) {
				distance = 10;
			} else {
				distance = Integer.valueOf(distanceStr);
			}
			if(distance < 0) {
				p(" Sorry, but the distance must be greater than zero.");
				return;
			}
		}

		// Mininum Price
		System.out.print(" Enter a lower bound for price (Leave blank to omit): ");
		String minPriceStr = myObj.nextLine();
		int minPrice = 0;
		if(!minPriceStr.equals("")) {
			minPrice = Integer.valueOf(minPriceStr);
		}

		// Maximum price
		System.out.print(" Enter an upper bound for price (Leave blank to omit): ");
		String maxPriceStr = myObj.nextLine();
		int maxPrice = 9999999;
		if(!maxPriceStr.equals("")) {
			maxPrice = Integer.valueOf(maxPriceStr);
		}

		if(maxPrice < minPrice || maxPrice < 0) {
			p(" Sorry, but the price range entered is invalid. Make sure the mininum price is non-negative and below the max price.");
			return;
		}

		// Number of beds
		System.out.print(" Enter the number of beds (Leave blank to omit): ");
		String numBedStr = myObj.nextLine();
		int numBeds = -1;
		if(!numBedStr.equals("")) {
			numBeds = Integer.valueOf(numBedStr);
		}

		// Number of baths
		System.out.print(" Enter the number of baths (Leave blank to omit): ");
		String numBathStr = myObj.nextLine();
		int numBaths = -1;
		if(!numBathStr.equals("")) {
			numBaths = Integer.valueOf(numBathStr);
		}

		// Wifi
		System.out.print(" Should wifi be provided? (Leave blank to omit): ");
		String wifiStr = myObj.nextLine();
		boolean wifi = !wifiStr.equals("");

		// Kitchen
		System.out.print(" Should a kitchen be available? (Leave blank to omit): ");
		String kitchenStr = myObj.nextLine();
		boolean kitchen = !kitchenStr.equals("");

		// Parking
		System.out.print(" Should parking be available? (Leave blank to omit): ");
		String parkingStr = myObj.nextLine();
		boolean parking = !parkingStr.equals("");

		// Date range
		System.out.print(" Date range start [YYYY-MM-DD]: ");
		String date1 = myObj.nextLine();
		System.out.print(" Date range end [YYYY-MM-DD]: ");
		String date2 = myObj.nextLine();

		// Asc or Desc
		String aOrD = "";
		if(confirmCoords.equals("")) {
			System.out.print(" Would you like the prices to be ascending or decending? \n [Type anything for descending/leave blank for default ascending] ");
			aOrD = myObj.nextLine();
		}
		
		try {
			searchWithFilters(conn, nearPostalCode, longitude, latitude, distance, minPrice, maxPrice, numBeds, numBaths, wifi, kitchen, parking, aOrD.equals(""), date1, date2);
		} catch(Exception e) {
			p(" Error while searching with filters -> " + e);
		}
	}


	// ====================   Feedback   ========================




	static void createUserFeedbackTable(Connection conn) {
		try {
			String stat = "CREATE TABLE UserFeedback(ucid int PRIMARY KEY, lid int REFERENCES Listing(lid), rid int REFERENCES Account(id), rating int, comment varchar(500));";
			PreparedStatement createStat = conn.prepareStatement(stat);
			createStat.executeUpdate();
			p(" [Successfully created UserFeedback table]");
		} catch (Exception e) {
			p("Error while creating user Feedback table! -> " + e);
		}
	}

	static void createHostFeedbackTable(Connection conn) {
		try {
			String stat = "CREATE TABLE HostFeedback(hcid int PRIMARY KEY, rid int REFERENCES Account(id), hid int REFERENCES Account(id), rating int, comment varchar(500));";
			PreparedStatement createStat = conn.prepareStatement(stat);
			createStat.executeUpdate();
			p(" [Successfully created HostFeedback table]");
		} catch (Exception e) {
			p("Error while creating host Feedback table! -> " + e);
		}
	}

	static void populateFeedback(Connection conn) {
		try {
			insertUserFeedback(conn, 739162, 619374, 56832, 5, "The host and place were great! I loved the kitchen and the wifi was really fast.");
			insertUserFeedback(conn, 739213, 619374, 56832, 4, "This place was really nice. It was close to downtown, had good wifi, and the host was really cool.");
			insertUserFeedback(conn, 897234, 23901, 7809, 2, "I didn't like this place very much, it was noisy. However, it was close to the airport and bicycle rentals.");
			insertUserFeedback(conn, 892347, 975836, 84031, 4, "The host, Jimmy, was awesome and the listing was great for children, especially the big garden.");
			insertHostFeedback(conn, 929872, 7809, 39007, 4, "George was a great guest, he kept the place very clean. Thanks!");
			insertHostFeedback(conn, 929992, 84000, 39007, 5, "Shaq is an awesome guest. I'm glad he can fit in the bed.");
			insertHostFeedback(conn, 929871, 84000, 39007, 4, "He is always a great guest, but he has cancelled on me a few times.");
			insertHostFeedback(conn, 823897, 84031, 83621, 5, "Abe is an amazing guest!");
		} catch (Exception e) {
			p(" Error populating feedback tables -> " + e);
		}
	}

	static void insertUserFeedback(Connection conn, int ucid, int lid, int rid, int rating, String comment) {
		try {
			String updatStr = "INSERT INTO UserFeedback(ucid, lid, rid, rating, comment) VALUES(?,?,?,?,?)";
			PreparedStatement insertFeedback = conn.prepareStatement(updatStr);
			insertFeedback.setInt(1, ucid);
			insertFeedback.setInt(2, lid);
			insertFeedback.setInt(3, rid);
			insertFeedback.setInt(4, rating);
			if (comment.equals("")) {
	            insertFeedback.setNull(5, java.sql.Types.VARCHAR);
	        }
	        else {
	            insertFeedback.setString(5, comment);
	        }

			insertFeedback.executeUpdate();
			p("\n Thank you for submitting your feedback!\n");
		} catch (Exception e) {
			p("Error inserting into UserFeedback table! -> " + e);
		}
	}

	static void insertHostFeedback(Connection conn, int hcid, int rid, int hid, int rating, String comment) {
		try {
			String updatStr = "INSERT INTO HostFeedback(hcid, rid, hid, rating, comment) VALUES(?,?,?,?,?)";
			PreparedStatement insertFeedback = conn.prepareStatement(updatStr);
			insertFeedback.setInt(1, hcid);
			insertFeedback.setInt(2, rid);
			insertFeedback.setInt(3, hid);
			insertFeedback.setInt(4, rating);
			if (comment.equals("")) {
	            insertFeedback.setNull(5, java.sql.Types.VARCHAR);
	        }
	        else {
	            insertFeedback.setString(5, comment);
	        }

			insertFeedback.executeUpdate();
			p("\n Thank you for submitting your feedback!\n");
		} catch (Exception e) {
			p("Error inserting into HostFeedback table! -> " + e);
		}
	}

	static void userListingFeedback(Connection conn) {
		if (!isLoggedIn) {
			p(" You must be logged in to provide feedback on a listing.");
			return;
		}
		p("\n ===================================");
		p("\n You are now giving feedback on a Listing! Please enter the following information:\n");

		Scanner myObj = new Scanner(System.in);

		System.out.print(" What is the reservation code for the listing at which you stayed? ");
		String aidStr = myObj.nextLine();
		int aid = Integer.valueOf(aidStr);

		int lid = -1;
		try {
			String stat = "SELECT Availability.lid FROM (Availability INNER JOIN Listing ON Availability.lid=Listing.lid) WHERE Availability.aid=" + aid + " AND Availability.rid=" + currentID;
			PreparedStatement execStat = conn.prepareStatement(stat);             
			ResultSet rs = execStat.executeQuery(); 

			if(rs.next() == false) {
				p(" Sorry, but you can't give feedback on a listing at which you haven't stayed.");
				return;
			} else {
				lid = rs.getInt("Availability.lid");
			}
		} catch (Exception e) {
			p("Error while checking for listing ownership -> " + e);
		}

		System.out.print(" Please rate the experience you had with the host and listing from 0-5: ");
		String ratingStr = myObj.nextLine();
		int rating = Integer.valueOf(ratingStr);

		if(rating > 5 || rating < 0) {
			p("Please give a rating from 0 to 5. You provided a rating of " + rating + ".");
			return;
		}

		System.out.print(" Optionally, leave a comment about your experience with the listing and host: ");
		String comment = myObj.nextLine();

		int rid = currentID;
		int ucid = generateID();

		try {
			insertUserFeedback(conn, ucid, lid, rid, rating, comment);
		} catch (Exception e) {
			p(" There was an issue creating your feedback: " + e);
		}
	}

	static void userRenterFeedback(Connection conn) {
		if (!isLoggedIn) {
			p(" You must be logged in to provide feedback on a renter.");
			return;
		}
		p("\n ===================================");
		p("\n You are now giving feedback on a renter! Please enter the following information:\n");

		Scanner myObj = new Scanner(System.in);

		System.out.print(" What is the username of the account you want to rate? ");
		String username = myObj.nextLine();

		int rid = -1;
		try {
			String stat = "SELECT Availability.rid FROM (Availability INNER JOIN Account ON Availability.rid=Account.id) WHERE Availability.hid=" + currentID + " AND Account.username='" + username + "'";
			PreparedStatement execStat = conn.prepareStatement(stat);             
			ResultSet rs = execStat.executeQuery(); 

			if(rs.next() == false) {
				p(" Sorry, but you can't give feedback on a renter which hasn't stayed in one of your listings.");
				return;
			} else {
				rid = rs.getInt("Availability.rid");
			}
		} catch (Exception e) {
			p("Error while checking for for renter-host correlation -> " + e);
		}

		System.out.print(" Please rate the experience you had with the renter from 0-5: ");
		String ratingStr = myObj.nextLine();
		int rating = Integer.valueOf(ratingStr);

		if(rating > 5 || rating < 0) {
			p("Please give a rating from 0 to 5. You provided a rating of " + rating + ".");
			return;
		}

		System.out.print(" Optionally, leave a comment about your experience with the renter: ");
		String comment = myObj.nextLine();

		int hid = currentID;
		int hcid = generateID();

		try {
			insertHostFeedback(conn, hcid, rid, hid, rating, comment);
		} catch (Exception e) {
			p(" There was an issue creating your feedback: " + e);
		}

	}

	static void viewListingFeedback(Connection conn) {
		Scanner myObj = new Scanner(System.in);
		System.out.print("\n Enter the location ID of the listing for which you would like to read feedback: ");
		String lidStr = myObj.nextLine();
		int lid = Integer.valueOf(lidStr);

		try {
			String stat = "SELECT streetAddress, city, country FROM Listing WHERE lid=" + lid;
			PreparedStatement execStat1 = conn.prepareStatement(stat);
			ResultSet rs1 = execStat1.executeQuery();

			if(rs1.next()) {
				String address = rs1.getString("streetAddress");
				String city = rs1.getString("city");
				String country = rs1.getString("country");

				p("\n === Viewing comments for " + address + ", " + city + ", " + country + " ===\n");
			} else {
				p(" Sorry, that listing cannot be found. ");
			}

			stat = "SELECT comment, rating FROM UserFeedback WHERE lid=" + lid;
			PreparedStatement execStat2 = conn.prepareStatement(stat);
			ResultSet rs2 = execStat2.executeQuery(); 
		     
		    p("\n");  
			while (rs2.next()) {
				String comment = rs2.getString("comment");
				int rating = rs2.getInt("rating");

				p(" " + rating + "/5 - " + comment);
			}
			p("\n");
		} catch (Exception e) {
			p("Error while searching for user comments! -> " + e);
		}
	}

	static void viewHostFeedback(Connection conn) {
		Scanner myObj = new Scanner(System.in);
		System.out.print("\n Enter the username of the host for which you would like to read feedback: ");
		String username = myObj.nextLine();

		try {
			String stat = "SELECT UserFeedback.comment, UserFeedback.rating FROM ((UserFeedback INNER JOIN Listing ON UserFeedback.lid=Listing.lid) INNER JOIN Account ON Account.id=Listing.hid) WHERE Account.username='" + username + "'";
			PreparedStatement execStat = conn.prepareStatement(stat);
			ResultSet rs = execStat.executeQuery(); 
		     
		    p("\n === Viewing feedback for host " + username + " ===");  
			while (rs.next()) {
				String comment = rs.getString("UserFeedback.comment");
				int rating = rs.getInt("UserFeedback.rating");

				p(" " + rating + "/5 - " + comment);
			}
			p("\n");
		} catch (Exception e) {
			p("Error while searching for host comments! -> " + e);
		}
	}

	static void viewRenterFeedback(Connection conn) {
		Scanner myObj = new Scanner(System.in);
		System.out.print("\n Enter the username of the renter for which you would like to read feedback: ");
		String username = myObj.nextLine();

		try {
			String stat = "SELECT HostFeedback.comment, HostFeedback.rating FROM (HostFeedback INNER JOIN Account ON HostFeedback.rid=Account.id) WHERE Account.username='" + username + "'";
			PreparedStatement execStat = conn.prepareStatement(stat);
			ResultSet rs = execStat.executeQuery(); 
		     
		    p("\n === Viewing feedback for renter " + username + " ===");  
			while (rs.next()) {
				String comment = rs.getString("HostFeedback.comment");
				int rating = rs.getInt("HostFeedback.rating");

				p(" " + rating + "/5 - " + comment);
			}
			p("\n");
		} catch (Exception e) {
			p("Error while searching for renter comments! -> " + e);
		}
	}



	// =====================  Report Functions ====================




	static void numBookingsReport(Connection conn) {
		try {
			Scanner myObj = new Scanner(System.in);
			System.out.print("\n Would you like to run the report using a city name or ZIP code? [CITY/ZIP]: ");
			String reportChoice = myObj.nextLine();

			System.out.print("\n Start date [YYYY-MM-DD]: ");
			String startDate = myObj.nextLine();

			System.out.print("\n End date [YYYY-MM-DD]: ");
			String endDate = myObj.nextLine();
			
			if(reportChoice.equals("CITY")) {
				System.out.print("\n What city is this report for? ");
				String city = myObj.nextLine();

				String stat = "SELECT count(*) AS total FROM (Listing INNER JOIN Availability ON Listing.lid=Availability.lid) " + 
					"WHERE Availability.rid IS NOT NULL AND Availability.day <= '" + endDate + "' AND Availability.day >= '" + startDate + "' AND Listing.city='" + city + "'";

				PreparedStatement execStat = conn.prepareStatement(stat);
				ResultSet rs = execStat.executeQuery(); 
			     
				while (rs.next()) {
					int count = rs.getInt("total");
					p("\n There were " + count + " bookings in " + city + " during the specified time range.");
				}
				p("\n");
			} else if (reportChoice.equals("ZIP")) {
				System.out.print("\n What postal code is this report for? ");
				String postalCode = myObj.nextLine();

				String stat = "SELECT count(*) AS total FROM (Listing INNER JOIN Availability ON Listing.lid=Availability.lid) " + 
					"WHERE Availability.rid IS NOT NULL AND Availability.day <= '" + endDate + "' AND Availability.day >= '" + startDate + "' AND Listing.postalCode='" + postalCode + "'";

				PreparedStatement execStat = conn.prepareStatement(stat);
				ResultSet rs = execStat.executeQuery(); 
			     
				while (rs.next()) {
					int count = rs.getInt("total");
					p("\n There were " + count + " bookings in " + postalCode + " during the specified time range.");
				}
				p("\n");
			}
		} catch (Exception e) {
			p(" Error while generating report -> " + e);
		}
	}

	static void numListingsReport(Connection conn) {
		try {
			Scanner myObj = new Scanner(System.in);
			System.out.print("\n Please enter the country in which to run the report: ");
			String country = myObj.nextLine();

			System.out.print("\n Please enter the city in which to run the report (Leave blank to omit): ");
			String city = myObj.nextLine();

			System.out.print("\n Please enter the postal code in which to run the report (Leave blank to omit): ");
			String postalCode = myObj.nextLine();
			
			String stat = "SELECT count(*) AS total FROM Listing " + 
				"WHERE country = '"+ country + "'";

			if(!city.equals("")) {
				stat = stat + " AND city='" + city + "'";
			}
			if(!postalCode.equals("")) {
				stat = stat + " AND postalCode='" + postalCode + "'";
			}

			PreparedStatement execStat = conn.prepareStatement(stat);
			ResultSet rs = execStat.executeQuery(); 
		     
			while (rs.next()) {
				int count = rs.getInt("total");
				p("\n There are " + count + " listings in the specified location.");
			}
			p("\n");
		} catch (Exception e) {
			p(" Error while generating report -> " + e);
		}
	}

	static void hostNumberOfListings(Connection conn) {
		try {
			Scanner myObj = new Scanner(System.in);
			System.out.print("\n Would you like the report to be run on cities or countries? [CITY/COUNTRY]: ");
			String locChoice = myObj.nextLine().toLowerCase();

			if(!locChoice.equals("country") && !locChoice.equals("city")) {
				p(" Please select either 'CITY' or 'COUNTRY'.");
				return;
			}

			PreparedStatement execLocationStat = conn.prepareStatement("SELECT DISTINCT " + locChoice + " FROM Listing");
			ResultSet rs1 = execLocationStat.executeQuery();

			while(rs1.next()) {
				String location = rs1.getString(locChoice);
				p("\n ===== " + location + " =====\n");

				String stat = "SELECT count(*) as total, username, city, country FROM (Listing INNER JOIN Account ON Listing.hid=Account.id) WHERE " + locChoice + "='" + location + "' GROUP BY hid ORDER BY total DESC LIMIT 3";
				PreparedStatement execStat = conn.prepareStatement(stat);
				ResultSet rs = execStat.executeQuery();
				while(rs.next()) {
					String username = rs.getString("username");
					int total = rs.getInt("total");
					p(" Host " + username + " - " + total + " listing(s)");
				}
				p("\n");
			}			
		} catch (Exception e) {
			p(" Error while generating report -> " + e);
		}
	}

	static void reportCommericalHosts(Connection conn) {
		try {
			Scanner myObj = new Scanner(System.in);
			System.out.print("\n Would you like the report to be run on cities or countries? [CITY/COUNTRY]: ");
			String locChoice = myObj.nextLine();

			if(locChoice.equals("COUNTRY")) {
				String stat = "SELECT DISTINCT Listing.country, Listing.hid, Account.username FROM (Listing INNER JOIN Account ON Listing.hid=Account.id) ORDER BY Listing.country";

				PreparedStatement execStat = conn.prepareStatement(stat);
				ResultSet rs = execStat.executeQuery();
			     
				while (rs.next()) {
					String country = rs.getString("Listing.country");
					String hid = rs.getString("Listing.hid");
					String username = rs.getString("Account.username");

					String totalStat = "SELECT count(*) as totalListings FROM Listing WHERE country='" + country + "'";
					PreparedStatement execTotalStat = conn.prepareStatement(totalStat);
					ResultSet rs1 = execTotalStat.executeQuery();
					int listingTotal = 1;
					while(rs1.next()) {
						listingTotal = rs1.getInt("totalListings");
					}
					String countryStat = "SELECT count(*) as total FROM Listing WHERE country='" + country + "' AND hid=" + hid;
					PreparedStatement execCountryStat = conn.prepareStatement(countryStat);
					ResultSet rs2 = execCountryStat.executeQuery();
					while(rs2.next()) {
						int total = rs2.getInt("total");
						double percentageOwned = (double)total / (double)listingTotal;
						if (percentageOwned > 0.1) {
							p(" " + country + " | User " + username + " owns " + ((percentageOwned)*100) + "% of listings.");
						}
					}
				}
			} else {
				String stat = "SELECT DISTINCT Listing.country, Listing.city, Listing.hid, Account.username FROM (Listing INNER JOIN Account ON Listing.hid=Account.id) ORDER BY Listing.country";

				PreparedStatement execStat = conn.prepareStatement(stat);
				ResultSet rs = execStat.executeQuery();
			     
				while (rs.next()) {
					String country = rs.getString("Listing.country");
					String city = rs.getString("Listing.city");
					String hid = rs.getString("Listing.hid");
					String username = rs.getString("Account.username");

					String totalStat = "SELECT count(*) as totalListings FROM Listing WHERE country='" + country + "' AND city='" + city + "'";
					PreparedStatement execTotalStat = conn.prepareStatement(totalStat);
					ResultSet rs1 = execTotalStat.executeQuery();
					int listingTotal = 1;
					while(rs1.next()) {
						listingTotal = rs1.getInt("totalListings");
					}
					String countryStat = "SELECT count(*) as total FROM Listing WHERE city='" + city + "' AND country='" + country + "' AND hid=" + hid;
					PreparedStatement execCountryStat = conn.prepareStatement(countryStat);
					ResultSet rs2 = execCountryStat.executeQuery();
					while(rs2.next()) {
						int total = rs2.getInt("total");
						double percentageOwned = (double)total / (double)listingTotal;
						if (percentageOwned > 0.1) {
							p(" " + city + ", " + country + " | User " + username + " owns " + percentageOwned*100 + "% of listings.");
						}
					}
				}
			}
			p("\n");
		} catch (Exception e) {
			p(" Error while generating report -> " + e);
		}
	}

	static void rankRenters(Connection conn) {
		try {
			Scanner myObj = new Scanner(System.in);
			System.out.print("\n Start date [YYYY-MM-DD]: ");
			String startDate = myObj.nextLine();

			System.out.print("\n End date [YYYY-MM-DD]: ");
			String endDate = myObj.nextLine();

			System.out.print("\n Enter a city to search in (Leave blank to omit): ");
			String city = myObj.nextLine();


			String stat;
			if(city.equals("")) {
				stat = "SELECT rid, username, count(*) as total FROM " +
				"(SELECT * FROM (Availability INNER JOIN Account ON Availability.rid=Account.id) " + 
				"WHERE day<'" + endDate + "' AND day>'" + startDate + "') AS T GROUP BY rid ORDER BY count(*) desc";

				PreparedStatement execStat = conn.prepareStatement(stat);
				ResultSet rs = execStat.executeQuery(); 

				p("\n === Ranking users by number of bookings: ===\n");
			     
				while (rs.next()) {
					int total = rs.getInt("total");
					String username = rs.getString("username");
					p(" User " + username + " - " + total + " bookings.");
				}
			} else {
				stat = "SELECT rid, count(*) as total FROM " +
				"(SELECT * FROM (Availability NATURAL JOIN Listing) " + 
				"WHERE rid IS NOT NULL AND city='" + city + "' AND day<'" + endDate + "' AND day>'" + startDate + "') AS T GROUP BY rid ORDER BY count(*) desc";

				PreparedStatement execStat = conn.prepareStatement(stat);
				ResultSet rs = execStat.executeQuery(); 

				p("\n === Ranking users with at least two bookings in " + city + ": ===\n");
			     
				while (rs.next()) {
					int total = rs.getInt("total");
					int rid = rs.getInt("rid");

					String statUsername = "SELECT username FROM Account WHERE id=" + rid;
					PreparedStatement execUserStat = conn.prepareStatement(statUsername);
					ResultSet rs1 = execUserStat.executeQuery(); 
			     
					while (rs1.next()) {
						String username = rs1.getString("username");
						if(total > 1) {
							p(" User " + username + " - " + total + " bookings.");
						}
					}
				}
			}

			p("\n");
		} catch (Exception e) {
			p(" Error while generating report -> " + e);
		}
	}

	static void createCancellationTable(Connection conn) {
		try {
			String stat = "CREATE TABLE Cancellation(cid int, hid int REFERENCES Account(id), rid int REFERENCES Account(id), day Date, PRIMARY KEY(cid));";
			PreparedStatement createStat = conn.prepareStatement(stat);
			createStat.executeUpdate();
		} catch (Exception e) {
			p("Error while creating tables! -> " + e);
		}
	}

	static void populateCancellationTable(Connection conn) {
		try {
			insertIntoCancellationTable(conn, 782612, 39007, 84000, "2023-03-03");
			insertIntoCancellationTable(conn, 267612, 39007, 84000, "2023-03-12");
			insertIntoCancellationTable(conn, 988612, 39007, 84000, "2023-03-25");
			insertIntoCancellationTable(conn, 788882, 39007, 84000, "2023-03-26");
			insertIntoCancellationTable(conn, 927111, 7809, 77129, "2023-03-18");
			insertIntoCancellationTable(conn, 927222, 7809, 77129, "2023-03-19");
			insertIntoCancellationTable(conn, 127489, 39007, 56832, "2021-07-29");
		} catch (Exception e) {
			p(" Error while populating Cancellation table.");
		}
	}

	static void insertIntoCancellationTable(Connection conn, int cid, int hid, int rid, String day) {
		try {
			String updatStr = "INSERT INTO Cancellation(cid, hid, rid, day) VALUES(?,?,?,?)";
			PreparedStatement insertCancallation = conn.prepareStatement(updatStr);

			insertCancallation.setInt(1, cid);
			insertCancallation.setInt(2, hid);
			insertCancallation.setInt(3, rid);
			insertCancallation.setDate(4, java.sql.Date.valueOf(day));
			insertCancallation.executeUpdate();
			p(" [Cancellation recorded.]");
		} catch (Exception e) {
			p("Error inserting into Account table! -> " + e);
		}
	}

	static void reportCancellations(Connection conn) {
		try {
			Scanner myObj = new Scanner(System.in);
			System.out.print("\n Please enter the year [YYYY]: ");
			String year = myObj.nextLine();

			String startDate = year + "-01-01";
			String endDate = year + "-12-31";
			
			String statUsers = "SELECT username, rid, count(*) as total FROM " + 
				"(Cancellation INNER JOIN Account ON Cancellation.rid=Account.id) WHERE day<'" + endDate + "' AND day>'" + startDate + "' GROUP BY rid ORDER BY count(*) desc";

			PreparedStatement execStatUsers = conn.prepareStatement(statUsers);
			ResultSet rs1 = execStatUsers.executeQuery(); 
		     
		    p("\n ==== Renters with most cancellations ====\n");
			while (rs1.next()) {
				String username = rs1.getString("username");
				int total = rs1.getInt("total");
				p(" User " + username + " with " + total + " cancellations");
			}
			String statHosts = "SELECT username, hid, count(*) as total FROM " + 
				"(Cancellation INNER JOIN Account ON Cancellation.hid=Account.id) WHERE day<'" + endDate + "' AND day>'" + startDate + "' GROUP BY hid ORDER BY count(*) desc";

			PreparedStatement execStatHosts = conn.prepareStatement(statHosts);
			ResultSet rs2 = execStatHosts.executeQuery(); 
		     
		    p("\n ==== Hosts recieving the most cancellations ====\n");
			while (rs2.next()) {
				String username = rs2.getString("username");
				int total = rs2.getInt("total");
				p(" Host " + username + " with " + total + " cancellations");
			}
			p("\n");
		} catch (Exception e) {
			p(" Error while generating report -> " + e);
		}
	}

	static void getCommentNouns(Connection conn) {
		try {
			String stat = "SELECT Listing.lid, Listing.streetAddress, Listing.city, UserFeedback.comment FROM (Listing INNER JOIN UserFeedback ON UserFeedback.lid=Listing.lid)";
			PreparedStatement execStat = conn.prepareStatement(stat);
			ResultSet rs = execStat.executeQuery(); 

			HashMap<String, List<String>> commentHashmap = new HashMap<String, List<String>>();
			List<String> commonNouns = new ArrayList<>();
			commonNouns = Arrays.asList("wifi", "pets", "kitchen", "closet", "airport", "bike", "bicycle", "highway", "downtown", "uptown", "condo", "garden", "tv", "netflix", "children", "heating", "air conditioning", "parking");

			while (rs.next()) {
				int lid  = rs.getInt("Listing.lid");
				String streetAddress = rs.getString("Listing.streetAddress");
				String city = rs.getString("Listing.city");
				String comment = rs.getString("UserFeedback.comment");
				String key = streetAddress + ", "+ city + " | Listing code " + lid;

				if(commentHashmap.get(key) == null) {
					commentHashmap.put(key, new ArrayList<String>());
				}

				for(int i = 0; i < commonNouns.size(); i++){
					String noun = commonNouns.get(i);
					if(comment.contains(noun) && !commentHashmap.get(key).contains(noun)) {
						commentHashmap.get(key).add(noun);
					} 
				}
			}
			p("\n");
			for (HashMap.Entry<String, List<String>> entry : commentHashmap.entrySet()) {
		    	p(" === Keywords for " + entry.getKey() + " ===");
		    	p(" " + entry.getValue());
		    	p("\n");
			}
    	} catch (Exception e) {
    		p("Error retrieving user data-> " + e);
    	}
	}




	// =====================  Terminal functions ================





	static void printHelp() {
		p("\n\n ===================================");
		p("\n Login - Login to your account");
		p(" IsLoggedIn - Check to see who is currently logged in");
		p(" Logout - Log out from your account");

		p("\n ViewMyListings [VML] - Display all listings that you are the host of.");
		p(" CreateListing [CL] - Create a new listing"); 
		p(" DeleteListing [DL] - Delete a listing"); 
		p(" ViewListingHistory [VLH] - View the log of reservations for a listing.");
		// p(" PopulateListings {a} - Add sample data to listings");
		//p(" CreateListingTable {a} - Create the listing table");

		//p("\n ViewAccounts [VA] {a} - Display all accounts");
		p("\n CreateAccount [CA] - Create a new account");
		p(" DeleteAccount [DA]  - Delete an account");                      
		//p(" PopulateAccounts {a} - Add sample data to accounts");					
		//p(" CreateAccountTable {a} - Create a new account table");

		p("\n ViewAvailableListings [VAL] - Dislay all available listings");
		p(" ViewListingDetails [VLD] - Display the price, available dates, and details of a specific listing.");
		p(" SearchByAddress [SBA] - Display all available listings with matching address");
		p(" SearchWithFilters [SWF] - Display listings based on given filters.");
		p(" ViewReservations [VR] - Display your upcoming and past reservations (Host or Renter)");
		p(" CreateReservation [CR] - Create a new reservation");
		p(" DeleteReservation [DR] - Cancel a reservation you've booked");   
		p(" CreateAvailability [CAV] - Create an availability for listing"); 
		p(" RemoveAvailability [RA] - Remove an availability for a listing.");
		p(" UpdatePrice [UP] - Update the price of an available listing.");          
		//p(" CreateAvailabilityTable {a} - Spawn table for availibilities");

		p("\n ListingFeedback [LF] - Create a comment and a rating on a listing you've booked.");
		p(" RenterFeedback [RF] - Create a comment and a rating on a renter you've had.");
		p(" ViewListingFeedback [VLF] - Display all the comments and ratings for a listing.");
		p(" ViewHostFeedback [VHF] - Display all the comments and ratings for a host.");
		p(" ViewRenterFeedback [VRF] - Display all the comments and ratings for a renter.");
		//p(" CreateUserFeedbackTable {a} - Spawn table for user feedback on listings.");
		//p(" CreateHostFeedbackTable {a} - Spawn table for host feedback on renters.");

		p("\n NumBookingsReport [NBR] - Provide the number of bookings in a date range by location.");
		p(" NumListingsReport [NLR] - Provide the number of listings per city, country or postal code.");
		p(" HostNumberOfListings [HNOL] - Rank top 3 hosts by number of listings per country or city.");
		p(" ReportCommericalHosts [RCH] - Provide all the hosts which make up more than 10% of a city/country's listings.");
		p(" RankRenters [RR] - Rank renters by number of bookings in a specified time period.");
		p(" ReportCancellations [RC] - View hosts and renters with most cancellations in a year.");
		p(" GetCommentNouns [GCN] - Return common nouns from comments on listings.");

		//p("\n ClearTable {a} - Remove all data from a table");
		//p(" DeleteTable {a} - Delete a table from the DB");
		// ClearAll - Clears all 6 tables
		// populate - populates all tables

		p("\n Quit - Quits the application \n");

		p(" ===================================\n\n");
	}


	static void parseUserInput(Connection conn, String userInput) {
		String input = userInput.toLowerCase();

		switch(input) {
			case ("help"):
				printHelp();
				break;

			case "login":
				userLogin(conn);
				break;
			case "isloggedin":
				checkIsLoggedIn();
				break;
			case "logout":
				logOut();
				break;

			// =========== Cases for Listings =========

			case "viewmylistings":
			case "vml":
				viewListings(conn);
				break;
			case "createlisting":
			case "cl":
				userCreateListing(conn);
				break;
			case "deletelisting":
			case "dl":
				userDeleteListing(conn);
				break;
			case "viewlistinghistory":
			case "vlh":
				viewListingsHistory(conn);
				break;
			case "populatelistings":
				populateListingTable(conn);
				break;
			case "createlistingtable":
				createListingTable(conn);
				break;

			// ========== Cases for Accounts ===========

			case "viewaccounts":
			case "va":
				viewAccounts(conn);
				break;
			case "createaccount":
			case "ca":
				userCreateAccount(conn);
				break;
			case "deleteaccount":
			case "da":
				userDeleteAccount(conn);
				break;
			case "populateaccounts":
				populateAccountTable(conn);
				break;
			case "createaccounttable":
				createAccountTable(conn);
				p(" [Account table created]");
				break;

			// ========  Cases for Availability ==========

			case "viewavailablelistings":
			case "val":
				viewAvailibleListings(conn);
				break;
			case "viewlistingdetails":
			case "vld":
				viewListingDetails(conn);
				break;
			case "searchbyaddress":
			case "sba":
				userSearchByAddress(conn);
				break;
			case "searchwithfilters":
			case "swf":
				userSearchWithFilters(conn);
				break;
			case "viewreservations":
			case "vr":
				viewReservations(conn);
				break;
			case "createreservation":
			case "cr":
				userReserveListing(conn);
				break;
			case "deletereservation":
			case "dr":
				userDeleteReservation(conn);
				break;
			case "createavailability":
			case "cav":
				userCreateAvailability(conn, -1);
				break;
			case "removeavailability":
			case "ra":
				hostRemoveAvailability(conn);
				break;
			case "updateprice":
			case "up":
				userUpdatePrice(conn);
				break;
			case "createavailabilitytable":
				createAvailabilityTable(conn);
				break;

			// ========== Cases for Feedback ============

			case "listingfeedback":
			case "lf":
				userListingFeedback(conn);
				break;
			case "renterfeedback":
			case "rf":
				userRenterFeedback(conn);
				break;
			case "viewlistingfeedback":
			case "vlf":
				viewListingFeedback(conn);
				break;
			case "viewhostfeedback":
			case "vhf":
				viewHostFeedback(conn);
				break;
			case "viewrenterfeedback":
			case "vrf":
				viewRenterFeedback(conn);
				break;
			case "createuserfeedbacktable":
				createUserFeedbackTable(conn);
				break;
			case "createhostfeedbacktable":
				createHostFeedbackTable(conn);
				break;

			// ========== Cases for Reports =================

			case "numbookingsreport":
			case "nbr":
				numBookingsReport(conn);
				break;
			case "numlistingsreport":
			case "nlr":
				numListingsReport(conn);
				break;
			case "hostnumberoflistings":
			case "hnol":
				hostNumberOfListings(conn);
				break;
			case "reportcommericalhosts":
			case "rch":
				reportCommericalHosts(conn);
				break;
			case "rankrenters":
			case "rr":
				rankRenters(conn);
				break;
			case "reportCancellations":
			case "rc":
				reportCancellations(conn);
				break;
			case "getcommentnouns":
			case "gcn":
				getCommentNouns(conn);
				break;
			case "cct":
				createCancellationTable(conn);
				break;

			// ========== Cases for other commands ==========

			case "cleartable":
				userClearTable(conn);
				break;
			case "deletetable":
				userDeleteTable(conn);
				break;
			case "populate":
				populateAccountTable(conn);
				populateListingTable(conn);
				populateAvailabilityTable(conn);
				populateFeedback(conn);
				populateCancellationTable(conn);
				break;
			case "clearall":
				clearTable(conn, "Cancellation");
				clearTable(conn, "HostFeedback");
				clearTable(conn, "UserFeedback");
				clearTable(conn, "Availability");
				clearTable(conn, "Listing");
				clearTable(conn, "Account");
				break;
			default:
				p(" Sorry, but '" + userInput + "' is not a recognized command. Type 'Help' for a full list of options.");
		}
	}




	// ====================== Driver ============================



    public static void main(String[] args) {
    	try {
    		Connection conn = getDBConnection();
    		isLoggedIn = false;
    		currentUsername = null;
    		currentID = -1;

    		p("Welcome to MyBnb!\n");
    		p("Type 'Login' or 'CreateAccount' to get started, or type 'Help' to see a full list of options.\n");
    		p("Type 'quit' when you want to exit.\n");

    		Scanner myObj = new Scanner(System.in);

    		while(true) {
    			System.out.print(">> ");
    			String userInput = myObj.nextLine();

    			if (userInput.toLowerCase().equals("quit")) {
    				break;
    			}
    			parseUserInput(conn, userInput);
    		}
    	} catch (Exception e) {
    		p("Application error. Please read -> " + e);
    	}
    }
}
