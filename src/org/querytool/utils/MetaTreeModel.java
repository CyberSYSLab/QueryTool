/*
 * MetaTreeModel.java
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

import java.util.ArrayList;
import java.util.TreeMap;
import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class MetaTreeModel extends DefaultTreeModel {

    public MetaTreeModel(TreeNode root, boolean asksAllowsChildren) { super(root, asksAllowsChildren); }

    public MetaTreeModel(TreeNode root) { super(root); }
    
    public MetaTreeModel() {
        super(null);
        super.root = rootNode;
    }

    protected EventListenerList listeners = new EventListenerList();
    
    private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Databases");
        
    public void loadMeta(TreeMap<String, ArrayList> map) {
        rootNode.removeAllChildren();
        for (String key : map.keySet()) {
            DefaultMutableTreeNode dbNode = new DefaultMutableTreeNode(key);
            if (!map.get(key).isEmpty()) {
                for (Object val : map.get(key).toArray()) {
                    dbNode.add(new DefaultMutableTreeNode(val));
                }
            }
            rootNode.add(dbNode);
        }
    }
    
    @Override
    public Object getRoot() { return rootNode; }

}