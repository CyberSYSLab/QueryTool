/*
 * MetaTreeCellRenderer.java
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
import java.awt.Font;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class MetaTreeCellRenderer extends DefaultTreeCellRenderer {

    private String highLightName = "";
    
    public void setHighLightName(String highLightName) {
        this.highLightName = highLightName;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        if (value != null) {
            if (value instanceof DefaultMutableTreeNode) {
                if (value.toString().equalsIgnoreCase(highLightName)) {
                    setFont(getFont().deriveFont(Font.BOLD));
                    setForeground(Color.decode("#3333DD"));
                } else {
                    setForeground(Color.black);
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
            }
        }
        return this;
    }
    
}