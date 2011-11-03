/*
 * MySQLAdapter.java
 *
 * Copyright (c) 2011 Vladimir V..
 *
 * This file is part of QueryTool.
 *
 * QueryTool is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * QueryTool is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * QueryTool. If not, see <http ://www.gnu.org/licenses/>.
 */
package org.querytool.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MySQLAdapter {

    private static MySQLAdapter instance;
    private Connection connection;
    private String host;
    private String database;
    private String username;
    private String password;
    private final String DRIVER = "com.mysql.jdbc.Driver";
    private final String URL_PRIFEX = "jdbc:mysql://";
    
    private String currentDatabase = "";
    private String connectionInfo = "";
    private String connectionMessage = "No message";
    private String queryMessage = "No message";
    
    private MySQLAdapter() { }
    
    public static MySQLAdapter getInstance() { if (instance != null) return instance; else return instance = new MySQLAdapter(); }
    
    private boolean openConnection() {
        boolean opened = false;
        try {
            Class.forName(DRIVER);
        } catch(ClassNotFoundException ex) { connectionMessage = ex.getMessage(); };

        try {
            connection = DriverManager.getConnection(URL_PRIFEX + host +"/"+ database, username, password);
            if (isConnected()) {
                getCurrentDatabase();
                getCurrentUser();
            }
            opened = true;
        } catch(SQLException ex) { connectionMessage = ex.getMessage(); };

        return opened;
    }

    public boolean connect(String host, String port, String username, String password) {
        if (connection != null) disconnect();
        this.host = host +":"+ port;
        this.database = "";
        this.username = username;
        this.password = password;
        
        return openConnection();
    }
    
    public boolean reconnect() {
        if (connection != null) disconnect();
        
        return openConnection();
    }
    
    public boolean isConnected() {
        boolean connected = false;
        try {
            if (connection != null) connected = connection.isValid(1000);
        } catch (SQLException ex) { connectionMessage = ex.getMessage(); };
        
        return connected;
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException ex) { connectionMessage = ex.getMessage(); }
        }
    }
    
    public String getHost() { return host == null ? "None" : host; }
    
    public String getActiveDatabase() { return currentDatabase; }
    
    public String getConnectionInfo() { return connectionInfo; }

    public String getConnectionMessage() { return connectionMessage; }

    public String getQueryMessage() { return queryMessage; }
    
    public TreeMap<String, ArrayList> getMetaData() {
        TreeMap<String, ArrayList> map = new TreeMap<String, ArrayList>();
        try {
            ResultSet rs = connection.getMetaData().getCatalogs();
            
            while (rs.next()) {
                ArrayList tableList = new ArrayList();
                ResultSet trs = connection.getMetaData().getTables(rs.getString(1), null, null, null);
                while (trs.next()) {
                    tableList.add(trs.getString("TABLE_NAME"));
                }
                map.put(rs.getString(1), tableList);
                trs.close();
            }
            rs.close();
            
        } catch (SQLException ex) { connectionMessage = ex.getMessage(); }
        
        return map;
    }
    
    private ResultSet exec(String query) {
        ResultSet result = null;
        try {
            if (isConnected()) {
                PreparedStatement ps = connection.prepareStatement(query);
                if (ps.execute()) result = ps.getResultSet();
            }
        } catch (SQLException ex) { queryMessage = ex.getMessage(); }

        return result;
    }
    
    public ResultSet execQuery(String query) {
        queryMessage = "No message";
        ResultSet rs = exec(query);
        getCurrentDatabase();
        
        return rs;
    }
    
    private String getCurrentDatabase() {
        String dbName = "No DB selected";
        if (isConnected()) {
            ResultSet rs = exec("select database()");
            try {
                if (rs.first() && rs.getString(1) != null) { dbName = rs.getString(1); }
            }
            catch (SQLException ex) { connectionMessage = ex.getMessage(); }
            finally {
                try {
                    rs.close();
                } catch (SQLException ex) { connectionMessage = ex.getMessage(); }
            }
        }
        currentDatabase = dbName;
        
        return dbName;
    }
    
    private String getCurrentUser() {
        String userName = "Not connected";
        if (isConnected()) {
            try {
                userName = connection.getMetaData().getUserName();
            } catch (SQLException ex) { connectionMessage = ex.getMessage(); }
        }
        connectionInfo = userName;
        
        return userName;
    }
    
    public void switchDB(String dbName) {
        if (isConnected() && dbName != null && !dbName.isEmpty()) {
            exec("use "+ dbName);
            getCurrentDatabase();
        }
    }

    public List<String> collectWordsForDB(String dbName) {
        List<String> words = new ArrayList<String>();
        try {
            ResultSet rs = connection.getMetaData().getTables(currentDatabase, null, null, null);
            while (rs.next()) {
                words.add(rs.getString("TABLE_NAME"));
            }
            
        } catch (SQLException ex) { System.err.println(ex.getMessage()); }
        
        return words;
    }
}
