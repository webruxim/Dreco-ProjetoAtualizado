package com.example.meusamigos2;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBExecute extends AsyncTask<String, Void, ResultSet> {

    private Connection connection;
    private String query;
    private String queryTipo;

    public DBExecute(Connection connection, String query, String pQueryTipo) {
        this.connection = connection;
        this.query = query;
        this.queryTipo = pQueryTipo;
    }

    @Override
    public ResultSet doInBackground(String... strings) {
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            if (queryTipo.equals("SELECT")) {
                resultSet = connection.prepareStatement(query).executeQuery();
            } else {
                /** se não então pode ser (insert, update, delete) */
                connection.createStatement().executeUpdate(query);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //connection.close();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return resultSet;
    }
}