package tryurl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnector {
    private String host = "127.0.0.1";
    private String port = "5432";
    private String db_name = "kp_index";
    private String user = "postgres";
    private String password = "postgres";
    private final String field_size = "2000";

    private void parseConf(String config) throws IOException{
        BufferedReader reader = new BufferedReader (new InputStreamReader(new FileInputStream(config)));
        String line = null;
        while((line = reader.readLine()) != null){
            int split_index = line.indexOf("=");
            String key = line.substring(0, split_index);
            String value = line.substring(split_index + 1);
            switch(key){
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
                default: break;
            }
        }
    }

    // null if from hardcode constants
    public DBConnector(String conf_path) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        if (conf_path != null) {
            try {
                parseConf(conf_path);
            } catch (IOException excp) {
            }
        }
    }

    private Connection connect = null;
    private Statement statement  = null;
    public void connect() throws SQLException 
    {
        connect = DriverManager.getConnection("jdbc:postgresql://" + host + ":"
                + port + "/" + db_name, user, password);
    }
    
    public void DropDatabase() throws SQLException{
        if(statement == null){
            statement = connect.createStatement();
        }
        String query_drop_scheme = "DROP SCHEMA public cascade";
        String create_scheme = "create schema public";
        try{
            statement.execute(query_drop_scheme);
            statement.execute(create_scheme);
        }catch(SQLException excp){
            
        }
    }
    
    private void checkStatement() throws SQLException{
        if(statement == null){
            statement = connect.createStatement();
        }
    }
    public void BuildDatabase() throws SQLException{
        checkStatement();
        DropDatabase();
        String main_query = " create table films(id integer PRIMARY KEY,  "
                + "actors varchar(" + field_size + ")," 
                + "purposes varchar(" + field_size + "),"
                + "links varchar(" + field_size + "),"
                + "annotation varchar(" + field_size + "),"
                + "rating varchar(" + field_size + "),"
                + "name varchar(" + field_size + "))";
        try{
            statement.execute(main_query);
        }catch(SQLException excp){
            
        }
    }
    
    public void InsertFilm(Film film) throws SQLException{
        checkStatement();
        String main_query = "insert into films (id, "
                + "purposes,"
                + " links, "
                + "annotation, "
                + "rating,"
                + " name)  VALUES (" + film.getId() + ","
                + " '" + film.getPurposes() + "',"
                + " '" + film.getSuggestion_links() + "', "
                + "'" + film.getAnnotation() + "', "
                + "'" + film.getRating() + "', "
                + "'" + film.getName() + "')";
        System.out.println(main_query);
        try{
            statement.execute(main_query);
        } catch(SQLException excp){
            System.out.println("ff");
        }
    }

    public boolean deleteFilmFromId(String id) throws SQLException{
        checkStatement();
        String main_query = "delete from films where id=" + id;
        try{
            statement.execute(main_query);
        } catch(SQLException excp){
            return false;
        }
        return true;
    }
    public void closeAll() throws SQLException {
        if (connect != null) {
            connect.close();
        }
    }
}
