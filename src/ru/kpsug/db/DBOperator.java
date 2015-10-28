package ru.kpsug.db;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ru.kpsug.utils.ConfigParser;

public class DBOperator {
    private String host = "127.0.0.1";
    private String port = "5432";
    private String db_name = "kp_index";
    private String user = "postgres";
    private String password = "postgres";
    private static final String field_size = "5000";
    private static final String films_table = "films";
    
    volatile private Connection connect = null;
    volatile private Statement statement = null;

    private void parseConf(String config) throws IOException {
        Map<String, String> values = ConfigParser.parseConfig(config);
        for(Entry<String, String> pair : values.entrySet()){
            String key = pair.getKey()  ;
            String value = pair.getValue();
            switch (key) {
            case "host":
                host = value;
                break;
            case "port":
                port = value;
                break;
            case "db_name":
                db_name = value;
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

    // null if from hardcode constants
    public DBOperator(String conf_path)  {
        if (conf_path != null) {
            try {
                parseConf(conf_path);
            } catch (IOException excp) {
            }
        }
    }


    public void connect() throws SQLException {
        connect = DriverManager.getConnection("jdbc:postgresql://" + host + ":"
                + port + "/" + db_name, user, password);
    }

    public void closeAll() throws SQLException {
        if (connect != null) {
            connect.close();
        }
    }


    public synchronized boolean executeUpdate(String query) {
        if (statement == null) {
            try {
                statement = connect.createStatement();
            } catch (SQLException excp) {
                return false;
            }
        }
        try {
            statement.execute(query);
        } catch (SQLException excp) {
            return false;
        }
        return true;
    }

    // returns null if failed
    public synchronized ResultSet executeQuery(String query) {
        if (statement == null) {
            try {
                statement = connect.createStatement();
            } catch (SQLException excp) {
                return null;
            }
        }
        try {
            ResultSet ret = statement.executeQuery(query);
            return ret;
        } catch (SQLException excp) {
            return null;
        }
    }

    public boolean DropDatabase() {
        String query_drop_scheme = "DROP SCHEMA public cascade";
        String create_scheme = "create schema public";
        return executeUpdate(query_drop_scheme) && executeUpdate(create_scheme);
    }

    public boolean BuildDatabase() {
        if (!DropDatabase()) {
            return false;
        }
        String main_query = " create table " + films_table
                + " (id integer PRIMARY KEY,  " + "actors varchar("
                + field_size + ")," + "purposes varchar(" + field_size + "),"
                + "links varchar(" + field_size + ")," + "annotation varchar("
                + field_size + ")," + "rating varchar(" + field_size + "),"
                + "name varchar(" + field_size + "))";
        return executeUpdate(main_query);
    }
    
    
    
    private String writeArrayList(ArrayList<String> arr){
        return JSONValue.toJSONString(arr);
    }

    private String writePurposes(TreeMap<String, ArrayList<String>> purposes){
        return JSONValue.toJSONString(purposes);
    }
    // insert film if not exists
    public boolean InsertFilm(Film film) {
        String main_query = "insert into " + films_table + " (id, "
                + "purposes," + " links, " + "annotation, " + "rating,"
                + " name , actors)  SELECT " + film.getId() + "," + " '"
                + writePurposes(film.getPurposes()) + "'," + " '" + writeArrayList(film.getSuggestion_links())
                + "', " + "'" + film.getAnnotation() + "', " + "'"
                + film.getRating() + "', " + "'" + film.getName() + "', '"
                + writeArrayList(film.getActors()) + "' WHERE NOT EXISTS (  SELECT id FROM "
                + films_table + " WHERE id = " + film.getId() + ");";
        return executeUpdate(main_query);
    }

    public boolean deleteFilmFromId(String id) {
        String main_query = "delete from " + films_table + " where id=" + id;
        return executeUpdate(main_query);
    }

    // returns null if failed
    private String getResultSetValue(ResultSet set, String param) {
        try {
            String result = set.getString(param);
            return result;
        } catch (SQLException excp) {
            return null;
        }
    }
    
    private ArrayList<String> parseArrayListString(String s){
        try {
            return (ArrayList<String>)(ConfigParser.getJSONParser()).parse(s, ConfigParser.getContainerFactory());
        } catch (ParseException e) {
            return new ArrayList<String>();
        }
    }
    
    private TreeMap<String, ArrayList<String>> parsePurposes(String s){
        try {
            return (TreeMap<String, ArrayList<String>>)(ConfigParser.getJSONParser()).parse(s, ConfigParser.getContainerFactory());
        } catch (ParseException e) {
            return new TreeMap<String, ArrayList<String>>();
        }
    }

    public Film selectFilm(String id) {
        String main_query = "select * from " + films_table + " where id=" + id;
        ResultSet set = executeQuery(main_query);
        if (set == null) {
            return null;
        }
        try {
            set.next();
        } catch (SQLException e) {
            return null;
        }
        Film film = new Film();
        String value = "";
        if((value = getResultSetValue(set, "id")) == null){
            return null;
        }
        film.setId(value);
        if((value = getResultSetValue(set, "name")) == null){
            return null;
        }
        film.setName(value);
        if((value = getResultSetValue(set, "rating")) == null){
            return null;
        }
        film.setRating(value);
        if((value = getResultSetValue(set, "annotation")) == null){
            return null;
        }
        film.setAnnotation(value);
        if((value = getResultSetValue(set, "links")) == null){
            return null;
        }
        film.setSuggestion_links(parseArrayListString(value));
        if((value = getResultSetValue(set, "actors")) == null){
            return null;
        }
        film.setActors(parseArrayListString(value));
        if((value = getResultSetValue(set, "purposes")) == null){
            return null;
        }
        film.setPurposes(parsePurposes(value));
        return film;
    }
}
