package ru.kpsug;

import static org.junit.Assert.*;

import java.sql.SQLException;


import ru.kpsug.db.DBOperator;

public class createDb {
    public static void main(String[] args) {
        DBOperator db_con =  new DBOperator(null);
        try {
            db_con.connect();
            assertTrue(db_con.BuildDatabase());
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
