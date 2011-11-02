/*
 * QueryTableModel.java
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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

public class QueryTableModel extends AbstractTableModel implements TableModelListener {

    private ArrayList<String> columnFields = new ArrayList<String>();
    private Object[][] data = null;
    private ArrayList<String> columnTypes = new ArrayList<String>();

    private String lastError = "";
    
    public QueryTableModel() { }
    
    public String getLastError() { return lastError; }
    
    public void loadData(ResultSet resultSet) {
        data = null;
        columnFields.clear();
        columnTypes.clear();
        try {
            if (resultSet != null && !resultSet.isClosed() && resultSet.next()) {
                ArrayList rowData = new ArrayList();
                ArrayList queryData = new ArrayList();
                ResultSetMetaData meta = resultSet.getMetaData();
                resultSet.beforeFirst();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    columnFields.add(meta.getColumnName(i));
                    columnTypes.add(meta.getColumnClassName(i));
                }
                while (resultSet.next()) {
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        rowData.add(resultSet.getObject(i));
                    }
                    queryData.add(rowData.toArray());
                    rowData.clear();
                }
                resultSet.close();
                data = new Object[queryData.size()][];
                for (int i = 0; i < queryData.size(); i++) { data[i] = (Object[]) queryData.get(i); }
            }
        } catch (SQLException ex) { lastError = ex.getMessage();
        } finally { fireTableStructureChanged(); }
    }
    
    @Override
    public int getRowCount() { if (data != null) return data.length; else return 0; }

    @Override
    public int getColumnCount() { return columnFields.size(); }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) { if (data != null) return data[rowIndex][columnIndex]; else return null; }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Class type = "".getClass();
        if (data != null && data.length > 0) {
            try {
                type = Class.forName(columnTypes.get(columnIndex));
            } catch (ClassNotFoundException ex) { lastError = ex.getMessage();
            } finally { return type; }
        }
        
        return type;
    }

    @Override
    public String getColumnName(int column) { return columnFields.get(column); }

    @Override
    public void tableChanged(TableModelEvent e) { }
    
};