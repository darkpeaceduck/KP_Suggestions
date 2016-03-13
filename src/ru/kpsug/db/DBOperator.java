package ru.kpsug.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import ru.kpsug.utils.ParseUtils;

public class DBOperator {
    private static final String HOST_DEFAULT = "127.0.0.1";
    private static final String PORT_DEFAULT = "5432";
    private static final String DB_NAME_DEFAULT = "kp_index";
    private static final String USER_DEFAULT = "postgres";
    private static final String PASSWORD_DEFAULT = "postgres";
    private static final int FIELD_SIZE = 5000;
    private static final String FILMS_TABLE = "films";
    
    private String host = HOST_DEFAULT;
    private String port = PORT_DEFAULT;
    private String dbName = DB_NAME_DEFAULT;
    private String user = USER_DEFAULT;
    private String password = PASSWORD_DEFAULT;

    volatile private Connection connect = null;

    private void parseConf(String config) throws IOException {
        Map<String, String> values = ParseUtils.parseConfig(config);
        for (Entry<String, String> pair : values.entrySet()) {
            String key = pair.getKey();
            String value = pair.getValue();
            switch (key) {
            case "host":
                host = value;
                break;
            case "port":
                port = value;
                break;
            case "db_name":
                dbName = value;
                break;
            case "user":
                user = value;
                break;
            case "password":
                password = value;
                break;
            default:
                break;
            }
        }
    }
    
    public DBOperator(String conf_path) {
        if (conf_path != null) {
            try {
                parseConf(conf_path);
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
    }

    public void connect() throws SQLException {
        connect = DriverManager.getConnection("jdbc:postgresql://" + host + ":"
                + port + "/" + dbName, user, password);
    }

    public void closeAll() throws SQLException {
        if (connect != null) {
            connect.close();
        }
    }

    public PreparedStatement newPreparedStatement(String request) throws SQLException{
    	return connect.prepareStatement(request);
    }

    public void dropDatabase() throws SQLException {
        String queryDropScheme = "DROP SCHEMA public cascade";
        String createScheme = "create schema public";
        
        newPreparedStatement(queryDropScheme).executeUpdate();
        newPreparedStatement(createScheme).executeUpdate();
    }

    public void buildDatabase() throws SQLException{
    	dropDatabase();
    
        String queryFormat = 
        		" create table ? "
        		+ "(id integer PRIMARY KEY,"
        		+ "actors varchar(?),"
        		+ "purposes varchar(?),"
                + "links varchar(?),"
                + "annotation varchar(?),"
                + "rating varchar(?),"
                + "name varchar(?))";
        
        
        PreparedStatement statement = newPreparedStatement(queryFormat);
        statement.setString(1, FILMS_TABLE);
        for (int i = 2; i <= 7; i++) {
        	statement.setInt(i, FIELD_SIZE);
        }
        statement.executeUpdate();
    }

    private String writeList(List<String> arr) {
        return JSONValue.toJSONString(arr);
    }

    private String writePurposes(Map<String, List<String>> purposes) {
        return JSONValue.toJSONString(purposes);
    }
    
    private String checkIdValue(String value) throws SQLException{
		if(!value.matches("\\d*")){
			throw new SQLException();
		}
		return value;
	}
	
	private String checkFieldValue(String value) throws SQLException{
		if(!value.matches("[^']*")){
			throw new SQLException();
		}
		return value;
	}

    public void insertFilm(Film film) throws SQLException{
       
        String queryFormat = "insert into ? "
        		+ "(id, "
        		+ "purposes, "
        		+ "links, "
        		+ "annotation, "
        		+ "rating, "
        		+ "name, "
        		+ "actors)  "
        		+ "SELECT ?, "
        		+ "'?', "
        		+ "'?', "
        		+ "'?', "
        		+ "'?', "
        		+ "'?', "
        		+ "'?' " 
                + " WHERE NOT EXISTS (  SELECT id FROM ? "
                + " WHERE id = ?);";
        
        PreparedStatement statement = newPreparedStatement(queryFormat);
        statement.setString(1, FILMS_TABLE);
        statement.setString(2, checkIdValue(film.getId()));
		statement.setString(3, checkFieldValue(writePurposes(film.getPurposes()))); 
		statement.setString(4, checkFieldValue(writeList(film.getSuggestion_links())));
		statement.setString(5, checkFieldValue(film.getAnnotation()));
		statement.setString(6, checkFieldValue(film.getRating()));
		statement.setString(7, checkFieldValue(film.getName()));
		statement.setString(8, checkFieldValue(writeList(film.getActors())));
		statement.setString(9, FILMS_TABLE);
		statement.setString(10, checkIdValue(film.getId()));
        
        statement.executeUpdate();
    }

    public void updateFilm(Film film) throws SQLException {
        String queryFormat = 
        		"UPDATE ? "
        		+ "SET id=?, "
        		+ "purposes='?', "
        		+ "links='?', "
        		+ "annotation='?', "
        		+ "rating='?', "
        		+ "name='?', "
        		+ "actors='?' "
        		+ "WHERE id=?";
        PreparedStatement statement = newPreparedStatement(queryFormat);
       
        statement.setString(1, FILMS_TABLE);
        statement.setString(2, checkIdValue(film.getId()));
		statement.setString(3, checkFieldValue(writePurposes(film.getPurposes()))); 
		statement.setString(4, checkFieldValue(writeList(film.getSuggestion_links())));
		statement.setString(5, checkFieldValue(film.getAnnotation()));
		statement.setString(6, checkFieldValue(film.getRating()));
		statement.setString(7, checkFieldValue(film.getName()));
		statement.setString(8, checkFieldValue(writeList(film.getActors())));
		statement.setString(9, checkIdValue(film.getId()));
		
        statement.executeUpdate();
    }
    
    public void insertWithUpdate(Film film) throws SQLException {
        updateFilm(film);
        insertFilm(film);
    }

    public void deleteFilmFromId(String id) throws SQLException{
        String queryFormat = "delete from ? "
        					+ "where id=?";
        
        PreparedStatement statement = newPreparedStatement(queryFormat);
        
        statement.setString(1, FILMS_TABLE);
        statement.setString(2, checkIdValue(id));
        
        statement.executeUpdate();
    }

    private List<String> parseListString(String s) {
        try {
            return (List<String>) (ParseUtils.getJSONParser()).parse(s,
                    ParseUtils.getContainerFactory());
        } catch (ParseException e) {
            return new ArrayList<String>();
        }
    }

    private Map<String, List<String>> parsePurposes(String s) {
        try {
            return (Map<String, List<String>>) (ParseUtils
                    .getJSONParser()).parse(s,
                    ParseUtils.getContainerFactory());
        } catch (ParseException e) {
            return new TreeMap<String, List<String>>();
        }
    }
    
    private String getResultSetField(ResultSet set, String field) throws SQLException{
    	String value = set.getString(field);
    	if(value == null){
    		return "";
    	}
    	return value;
    }
    
    public static class FilmNotFoundException extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = 6034087387014737521L;
    	
    }

    public synchronized Film selectFilm(String id) throws SQLException, FilmNotFoundException {
    	connect.setAutoCommit(false);
        String queryFormat = "select * from ? "
        					+ "where id=?";
        PreparedStatement statement = newPreparedStatement(queryFormat);
        
        statement.setString(1, FILMS_TABLE);
        statement.setString(2, checkIdValue(id));
        
        ResultSet set = statement.executeQuery();
        connect.commit();
        
        if(set == null){
        	throw new FilmNotFoundException();
        }

        if(!set.next()){
        	throw new FilmNotFoundException();
        }
        
        Film film = new Film();
        film.setId(getResultSetField(set, "id"));
        film.setName(getResultSetField(set, "name"));
        film.setRating(getResultSetField(set, "rating"));
        film.setAnnotation(getResultSetField(set, "annotation"));
        film.setSuggestion_links(parseListString(getResultSetField(set, "links")));
        film.setActors(parseListString(getResultSetField(set, "actors")));
        film.setPurposes(parsePurposes(getResultSetField(set, "purposes")));
        return film;
    }
}
