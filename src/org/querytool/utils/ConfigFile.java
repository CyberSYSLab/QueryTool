/*
 * ConfigFile.java
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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigFile {

    public static String dbHost = "";
    public static String dbPort = "";
    public static String dbUserName = "";
    public static String dbPassword = "";
    
    public boolean  loadConfig() {
        Properties configFile = new Properties();
        boolean loaded = false;

        try {
            configFile.load(new FileInputStream("querytool.conf"));
            dbHost = configFile.getProperty("db_host", "localhost");
            dbPort = configFile.getProperty("db_port", "3306");
            dbUserName = configFile.getProperty("db_username", "root");
            dbPassword = configFile.getProperty("db_password", "");
            loaded = true;
        } catch (IOException ex) { System.out.println(ex.getMessage()); }
        
        return loaded;
    }
    
}
