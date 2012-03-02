/*
 * ResultTableCellRenderer.java
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

import java.awt.Color;
import java.awt.Component;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ResultTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, vColIndex);
        
        if (isSelected) {
            setBackground(Color.decode("#dddddd"));
        } else {
            setBackground(Color.decode("#ffffff"));
        }

        if (value != null) {
            setForeground(Color.black);
            if (value instanceof Number) {
                setHorizontalAlignment(RIGHT);
                if (isSelected) {
                    setBackground(Color.decode("#ccffcc"));
                } else {
                    setBackground(Color.decode("#f1fff1"));
                }
            }
            if (value instanceof Date) {
                if (isSelected) {
                    setBackground(Color.decode("#ffffcc"));
                } else {
                    setBackground(Color.decode("#fffff1"));
                }
            }
        } else {
            setForeground(Color.lightGray);
            setText("<NULL>");
        }
        
        return this;
    }

    @Override
    public void validate() {}
    @Override
    public void revalidate() {}
    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {}
    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
}