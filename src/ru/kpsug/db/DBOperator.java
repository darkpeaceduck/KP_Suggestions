package ru.kpsug.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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

import ru.kpsug.utils.MyParseUtils;

public class DBOperator {
    private static final String HOST_DEFAULT = "127.0.0.1";
    private static final String PORT_DEFAULT = "5432";
    private static final String DB_NAME_DEFAULT = "kp_index";
    private static final String USER_DEFAULT = "postgres";
    private static final String PASSWORD_DEFAULT = "postgres";
    private static final String FIELD_SIZE = "5000";
    private static final String FILMS_TABLE = "films";
    
    private String host = HOST_DEFAULT;
    private String port = PORT_DEFAULT;
    private String dbName = DB_NAME_DEFAULT;
    private String user = USER_DEFAULT;
    private String password = PASSWORD_DEFAULT;

    volatile private Connection connect = null;
    volatile private Statement statement = null;

    private void parseConf(String config) throws IOException {
        Map<String, String> values = MyParseUtils.parseConfig(config);
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

    public synchronized void executeUpdate(String query) throws SQLException{
        if (statement == null) {
            statement = connect.createStatement();
        }
        statement.execute(query);
    }

    public synchronized ResultSet executeQuery(String query) throws SQLException{
        if (statement == null) {
            statement = connect.createStatement();
        }
        return statement.executeQuery(query);
    }

    public void DropDatabase() throws SQLException {
        String queryDropScheme = "DROP SCHEMA public cascade";
        String createScheme = "create schema public";
        
        executeUpdate(queryDropScheme);
        executeUpdate(createScheme);
    }

    public void BuildDatabase() throws SQLException{
    
        String queryFormat = 
        		" create table %s "
        		+ "(id integer PRIMARY KEY,"
        		+ "actors varchar(%s),"
        		+ "purposes varchar(%s),"
                + "links varchar(%s),"
                + "annotation varchar(%s),"
                + "rating varchar(%s),"
                + "name varchar(%s))";
        
        DropDatabase();
        executeUpdate(String.format(queryFormat, 
        		FILMS_TABLE, 
        		
        		FIELD_SIZE, 
        		FIELD_SIZE,
        		FIELD_SIZE, 
        		FIELD_SIZE,
        		FIELD_SIZE, 
        		FIELD_SIZE));
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

    public void InsertFilm(Film film) throws SQLException{
       
        String queryFormat = "insert into %s "
        		+ "(id, purposes, links, annotation, rating, name, actors)  "
        		+ "SELECT %s, '%s', '%s', '%s', '%s', '%s', '%s' " 
                + " WHERE NOT EXISTS (  SELECT id FROM %s "
                + " WHERE id = %s);";
        
        executeUpdate(String.format(queryFormat, 
        		FILMS_TABLE,
        		
        		checkIdValue(film.getId()), 
        		checkFieldValue(writePurposes(film.getPurposes())), 
        		checkFieldValue(writeList(film.getSuggestion_links())),
				checkFieldValue(film.getAnnotation()), 
				checkFieldValue(film.getRating()),
				checkFieldValue(film.getName()), 
				checkFieldValue(writeList(film.getActors())),
        		
        		FILMS_TABLE,
        		
        		checkIdValue(film.getId())));
    }

    public void UpdateFilm(Film film) throws SQLException {
        String queryFormat = 
        		"UPDATE %s "
        		+ "SET id=%s, "
        		+ "purposes='%s', "
        		+ "links='%s', "
        		+ "annotation='%s', "
        		+ "rating='%s', "
        		+ "name='%s', "
        		+ "actors='%s' "
        		+ "WHERE id=%s";
        executeUpdate(String.format(queryFormat, 
        		FILMS_TABLE,
        		
        		checkIdValue(film.getId()),
        		checkFieldValue(writePurposes(film.getPurposes())),
				checkFieldValue(writeList(film.getSuggestion_links())),
				checkFieldValue(film.getAnnotation()),
				checkFieldValue(film.getRating()),
				checkFieldValue(film.getName()),
				checkFieldValue(writeList(film.getActors())),
        		
				checkIdValue(film.getId()) ));
    }
    
    public void InsertWithUpdate(Film film) throws SQLException {
        UpdateFilm(film);
        InsertFilm(film);
    }

    public void deleteFilmFromId(String id) throws SQLException{
        String queryFormat = "delete from %s "
        					+ "where id=%s";
        
        executeUpdate(String.format(queryFormat,
        		FILMS_TABLE, 
        		checkIdValue(id)));
    }

    private List<String> parseListString(String s) {
        try {
            return (List<String>) (MyParseUtils.getJSONParser()).parse(s,
                    MyParseUtils.getContainerFactory());
        } catch (ParseException e) {
            return new ArrayList<String>();
        }
    }

    private Map<String, List<String>> parsePurposes(String s) {
        try {
            return (Map<String, List<String>>) (MyParseUtils
                    .getJSONParser()).parse(s,
                    MyParseUtils.getContainerFactory());
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
        String queryFormat = "select * from %s "
        					+ "where id=%s";
        ResultSet set = executeQuery(String.format(queryFormat,
					FILMS_TABLE, 
					checkIdValue(id)) );
        
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
