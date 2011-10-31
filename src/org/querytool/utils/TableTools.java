/*
 * TableTools.java
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

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class TableTools {

    public static void autoResizeColWidth(JTable table) {
        int margin = 5;
        int limit = 300;

        for (int colIdx = 0; colIdx < table.getColumnCount(); colIdx++) {
            int width = 0;
            DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
            TableColumn col = colModel.getColumn(colIdx);

            TableCellRenderer renderer = col.getHeaderRenderer();
            if (renderer == null) { renderer = table.getTableHeader().getDefaultRenderer(); }
            Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, -1, colIdx);
            width = comp.getPreferredSize().width;

            for (int rosIdx = 0; rosIdx < table.getRowCount(); rosIdx++) {
                comp = table.getCellRenderer(rosIdx, colIdx).getTableCellRendererComponent(table, table.getValueAt(rosIdx, colIdx),
                                                                                                  false, false, rosIdx, colIdx);
                width = Math.max(width, comp.getPreferredSize().width);
                if (width >= limit) { break; }
            }

            width += 2 * margin;
            if (width > limit) { width = limit; }
            col.setPreferredWidth(width);
        }

    }
}
