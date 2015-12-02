package ru.kpsug;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;

import ru.kpsug.db.DBOperator;

public class createDb {

    @Test
    public void test() {
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
