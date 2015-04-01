/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.*;

/**
 *
 * @author EliFriedman
 */
public class LobbyHandler extends ResponseHandler {

    private GameHandler gamehandler;
    public static final String NEW_GAME_STRING = "new_game";
    public static final String LOGIN_STRING = "login";
    public static final String NAME_STRING = "name";
    public static final String PWD_STRING = "password";
    public static final String ADD_PLAYER_STRING = "addplayer";
    public static final String AUTH_STRING = "authentication";
    public static final String USER_COOKIE = "user_cookie";
    public static final String REQ_GAMELIST_STRING = "gamelist_request";

    public LobbyHandler(GameHandler g) {
        this.gamehandler = g;
    }

    @Override
    public void handleRequest(JSONObject jsonMap, HttpExchange he) {
        boolean sent = false;
        createTables();
        if (jsonMap.has(LOGIN_STRING)) {
//          format of login_string {login: {name: ______, password: _____}}
//          therefore we need to unpack to JSONObject
            JSONObject login = jsonMap.getJSONObject(LOGIN_STRING);
            String name = login.getString(NAME_STRING);
            String pwd = login.getString(PWD_STRING);
            System.out.println("lobbyHandler received: " + name + ", " + pwd);
            JSONObject ret = new JSONObject();
            if (name.equals("Eli") && pwd.equals("b")) {
                ret.put(AUTH_STRING, true);
            } else {
                ret.put(AUTH_STRING,false);
            }
            this.sendJSON(ret, he);
            sent = true;
        }
        if (!sent) this.sendJSON(new JSONObject(), he);
    }
	
	private Connection getConnection(){
		
		Connection con = null;
		try{
			//Class.forName("com.mysql.jdbc.Driver").newInstance();
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("got here.");
			con = DriverManager.getConnection("jdbc:mysql://localhost/setgame", "root", "betamobile");
			if(con.isClosed()){
				System.out.println("mySQL is closed");
			}
		}catch(Exception e){
			System.out.println("could not connect to mySQL");
			System.err.println(e);

		}
		return con;
	}
	 
	private void createTables(){
		Connection c = getConnection();
		Statement stmt =null;
		ResultSet rs = null;
		String sql = null;
		
		try{
		stmt = c.createStatement();
		sql = "DROP TABLE IF EXISTS Users; "//	 +			" DROP TABLE Games IF EXISTS;"
			//+ "DROP TABLE IF EXISTS Games; "
				;
		stmt.execute(sql);
		sql = "CREATE TABLE Users (" +
				"uid int NOT NULL AUTO_INCREMENT," +
				"name TEXT," +
				"password TEXT," +
				"ssn CHAR(10)," +//don't need it, but if they give us it, why wouldn't we store it.
				"PRIMARY KEY(uid)" +
				");";
	    
	    stmt.execute(sql);
	    sql = "";
	    stmt.close();
		}catch(Exception e){
			System.err.print(e);
		}
	}
	
}
