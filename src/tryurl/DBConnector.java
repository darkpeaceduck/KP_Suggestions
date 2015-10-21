package tryurl;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnector {
    //null if from hardcode constants
    String url = "127.0.0.1";
    String port = "5542";
    public DBConnector(String conf_path) {
         if(conf_path != null){
             Connection db= DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/kp_index", "postgres", "postgres");
             db.close(); 
         }
    }
}
