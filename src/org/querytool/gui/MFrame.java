/*
 * MFrame.java
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
package org.querytool.gui;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.KeyStroke;
import javax.swing.text.Position;
import javax.swing.tree.TreePath;
import org.querytool.utils.*;

public class MFrame extends javax.swing.JFrame {

    private Timer appTimer = new Timer();
    private TimerTask connectionTimerTask = null;
    
    private final String OPENCONN_KEY = "OPENCONN";
    private final KeyStroke openConnHotkey = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK, false);
    private final String RECONN_KEY = "RECONN";
    private final KeyStroke reConnHotkey = KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK, false);
    private final String SELQUERY_KEY = "SELQUERY";
    private final KeyStroke selQueryHotkey = KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.CTRL_MASK, false);
    private final String SELRESULT_KEY = "SELRESULT";
    private final KeyStroke selResultHotkey = KeyStroke.getKeyStroke(KeyEvent.VK_2, KeyEvent.CTRL_MASK, false);
    private final String SELMETA_KEY = "SELMETA";
    private final KeyStroke selMetaHotkey = KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.CTRL_MASK, false);
    private final String EXECQUERY_KEY = "EXECQUERY";
    private final KeyStroke execQueryHotkey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK, false);
    private final String SHOWHELP_KEY = "SHOWHELP";
    private final KeyStroke showHelpHotkey = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false);
    
    private final Color focusColor = Color.decode("#AADDAA");
    private final Color noFocusColor = Color.LIGHT_GRAY;
    
    private QueryTableModel queryTableModel = new QueryTableModel();
    private MetaTreeModel metaTreeModel = new MetaTreeModel();
    private MetaTreeCellRenderer metaCellRenderer = new MetaTreeCellRenderer();

    public MFrame() {
        initComponents();
        
        queryResultTable.setModel(queryTableModel);
        metaTree.setCellRenderer(metaCellRenderer);
        
        URL url = ClassLoader.getSystemResource("resources/querytool_128.png");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(url);
        setIconImage(img);
        
        AppHotKey hotkeyManager = AppHotKey.getInstance();
        hotkeyManager.getInputMap().put(openConnHotkey, OPENCONN_KEY);
        hotkeyManager.getActionMap().put(OPENCONN_KEY, openConnAction);

        hotkeyManager.getInputMap().put(reConnHotkey, RECONN_KEY);
        hotkeyManager.getActionMap().put(RECONN_KEY, reConnAction);

        hotkeyManager.getInputMap().put(selQueryHotkey, SELQUERY_KEY);
        hotkeyManager.getActionMap().put(SELQUERY_KEY, selQueryAction);
        hotkeyManager.getInputMap().put(selResultHotkey, SELRESULT_KEY);
        hotkeyManager.getActionMap().put(SELRESULT_KEY, selResultAction);
        hotkeyManager.getInputMap().put(selMetaHotkey, SELMETA_KEY);
        hotkeyManager.getActionMap().put(SELMETA_KEY, selMetaAction);

        hotkeyManager.getInputMap().put(execQueryHotkey, EXECQUERY_KEY);
        hotkeyManager.getActionMap().put(EXECQUERY_KEY, execQueryAction);

        hotkeyManager.getInputMap().put(showHelpHotkey, SHOWHELP_KEY);
        hotkeyManager.getActionMap().put(SHOWHELP_KEY, showHelpAction);

        setSyntax();
        hideTreeRoot();

        connectionTimerTask = new CheckConnectionTask();
        appTimer.schedule(connectionTimerTask, new Date(), 1000);
    }

    private void setSyntax() {
        queryCode.setDocument(new SQLDocument());
    }
    
    class CheckConnectionTask extends TimerTask {
        @Override public void run() {
            String activeDB = "No DB selected";
            String conInfo = "Not connected";
            String conMsg = "No message";
            boolean connected = MySQLAdapter.getInstance().isConnected();

            if (connected) {
                activeDB = MySQLAdapter.getInstance().getActiveDatabase();
                conInfo = MySQLAdapter.getInstance().getConnectionInfo();
                conMsg = MySQLAdapter.getInstance().getQueryMessage();
            }

            queryCode.setEnabled(connected);
            queryResultTable.setEnabled(connected);
            metaTree.setEnabled(connected);

            if (!taActiveDB.getText().equalsIgnoreCase("DB: "+ activeDB)) {
                taActiveDB.setText("DB: "+ activeDB);
                taActiveDB.setCaretPosition(0);
                if (metaTree.getRowCount() > 0) {
                    TreePath dbPath = null;
                    TreePath tp = metaTree.getNextMatch(activeDB, 0, Position.Bias.Forward);
                    while (tp != null) {
                        if (tp.getLastPathComponent().toString().equalsIgnoreCase(activeDB)) {
                            dbPath = tp;
                            tp = null;
                        } else {
                            tp = metaTree.getNextMatch(activeDB, metaTree.getRowForPath(tp) +1, Position.Bias.Forward);
                        }
                    }
                    if (dbPath != null) {
                        metaCellRenderer.setHighLightName(activeDB);
                        for (int i = metaTree.getRowCount() -1; i >= 0; i--) metaTree.collapseRow(i);
                        metaTree.setSelectionPath(dbPath);
                        metaTree.expandPath(dbPath);
                        metaTree.scrollPathToVisible(dbPath);
                        metaTreeModel.nodeChanged(metaTreeModel.findDB(activeDB));
                        metaTree.treeDidChange();
                    }
                }
            }

            taMessage.setText(conMsg);
            taMessage.setCaretPosition(0);
            setTitle("QueryTool ["+ conInfo +"]");
        }
    }
    
    private void hideTreeRoot() {
        metaTree.setRootVisible(true);
        metaTree.expandRow(0);
        metaTree.setRootVisible(false);
    }
    
    private void loadDBMeta() {
        metaTreeModel.loadMeta(MySQLAdapter.getInstance().getMetaData());
        metaTree.setModel(metaTreeModel);
        hideTreeRoot();
    }

    private final AbstractAction openConnAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            setEnabled(false);
            try {
                OpenConnection dialog = new OpenConnection(null, true);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
                loadDBMeta();
                queryCode.grabFocus();

            } catch (Exception exc) { System.err.println(exc.getLocalizedMessage()); }
            setEnabled(true);
        }
    };
    
    private final AbstractAction reConnAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            setEnabled(false);
            if (MySQLAdapter.getInstance().isConnected()) {
                loadDBMeta();
            } else {
                MySQLAdapter.getInstance().reconnect();
                loadDBMeta();
            }
            setEnabled(true);
        }
    };

    private final AbstractAction selQueryAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) { if (queryCode.isFocusable()) { queryCode.requestFocus(); }; }
    };

    private final AbstractAction selResultAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) { if (queryResultTable.isFocusable()) { queryResultTable.requestFocus(); }; }
    };

    private final AbstractAction selMetaAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (metaTree.isFocusable()) {
                metaTree.requestFocus();
                if (metaTree.getSelectionCount() == 0 && metaTree.getRowCount() > 0) {
                    metaTree.setSelectionRow(0);
                }
            }
        }
    };

    private final AbstractAction execQueryAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    queryTableModel.loadData(MySQLAdapter.getInstance().execQuery(queryCode.getText()));
                    TableTools.autoResizeColWidth(queryResultTable);
                }
            });
        }
    };
    
    private final AbstractAction showHelpAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            HelpDialog help = new HelpDialog(null, true);
            help.setLocationRelativeTo(null);
            help.setVisible(true);
        }
    };

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paneActiveDB = new javax.swing.JPanel();
        spActiveDB = new javax.swing.JScrollPane();
        taActiveDB = new javax.swing.JTextArea();
        spMetaTree = new javax.swing.JScrollPane();
        metaTree = new javax.swing.JTree();
        spResultTable = new javax.swing.JScrollPane();
        queryResultTable = new javax.swing.JTable();
        spQueryCode = new javax.swing.JScrollPane();
        queryCode = new javax.swing.JTextPane();
        paneMessage = new javax.swing.JPanel();
        spMessage = new javax.swing.JScrollPane();
        taMessage = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("QueryTool");

        paneActiveDB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        paneActiveDB.setForeground(new java.awt.Color(204, 204, 204));

        spActiveDB.setBorder(null);
        spActiveDB.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spActiveDB.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        spActiveDB.setHorizontalScrollBar(null);

        taActiveDB.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        taActiveDB.setColumns(20);
        taActiveDB.setEditable(false);
        taActiveDB.setForeground(new java.awt.Color(153, 153, 153));
        taActiveDB.setRows(5);
        taActiveDB.setTabSize(4);
        taActiveDB.setText("Not connected...");
        taActiveDB.setAutoscrolls(false);
        taActiveDB.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 3, 0, 0));
        taActiveDB.setFocusable(false);
        spActiveDB.setViewportView(taActiveDB);

        javax.swing.GroupLayout paneActiveDBLayout = new javax.swing.GroupLayout(paneActiveDB);
        paneActiveDB.setLayout(paneActiveDBLayout);
        paneActiveDBLayout.setHorizontalGroup(
            paneActiveDBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spActiveDB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        paneActiveDBLayout.setVerticalGroup(
            paneActiveDBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spActiveDB, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        spMetaTree.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));
        spMetaTree.setForeground(new java.awt.Color(204, 204, 204));

        metaTree.setModel(new org.querytool.utils.MetaTreeModel());
        metaTree.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                metaTreeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                metaTreeFocusLost(evt);
            }
        });
        metaTree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                metaTreeKeyPressed(evt);
            }
        });
        spMetaTree.setViewportView(metaTree);

        spResultTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));
        spResultTable.setForeground(new java.awt.Color(204, 204, 204));

        queryResultTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        queryResultTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        queryResultTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                queryResultTableFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                queryResultTableFocusLost(evt);
            }
        });
        spResultTable.setViewportView(queryResultTable);

        spQueryCode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));

        queryCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                queryCodeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                queryCodeFocusLost(evt);
            }
        });
        spQueryCode.setViewportView(queryCode);

        paneMessage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        spMessage.setBorder(null);
        spMessage.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spMessage.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        taMessage.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        taMessage.setColumns(20);
        taMessage.setEditable(false);
        taMessage.setForeground(new java.awt.Color(153, 153, 153));
        taMessage.setRows(5);
        taMessage.setText("No message...");
        taMessage.setAutoscrolls(false);
        taMessage.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 3, 0, 0));
        taMessage.setFocusable(false);
        spMessage.setViewportView(taMessage);

        javax.swing.GroupLayout paneMessageLayout = new javax.swing.GroupLayout(paneMessage);
        paneMessage.setLayout(paneMessageLayout);
        paneMessageLayout.setHorizontalGroup(
            paneMessageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spMessage, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        paneMessageLayout.setVerticalGroup(
            paneMessageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spMessage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spMetaTree, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .addComponent(paneActiveDB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spResultTable, javax.swing.GroupLayout.DEFAULT_SIZE, 628, Short.MAX_VALUE)
                    .addComponent(paneMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addComponent(spQueryCode)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spResultTable, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(spMetaTree, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(paneMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(paneActiveDB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spQueryCode, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void queryCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_queryCodeFocusGained
        spQueryCode.setBorder(BorderFactory.createLineBorder(focusColor, 2));
    }//GEN-LAST:event_queryCodeFocusGained

    private void queryCodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_queryCodeFocusLost
        spQueryCode.setBorder(BorderFactory.createLineBorder(noFocusColor, 2));
    }//GEN-LAST:event_queryCodeFocusLost

    private void metaTreeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_metaTreeFocusGained
        spMetaTree.setBorder(BorderFactory.createLineBorder(focusColor, 2));
    }//GEN-LAST:event_metaTreeFocusGained

    private void metaTreeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_metaTreeFocusLost
        spMetaTree.setBorder(BorderFactory.createLineBorder(noFocusColor, 2));
    }//GEN-LAST:event_metaTreeFocusLost

    private void queryResultTableFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_queryResultTableFocusGained
        spResultTable.setBorder(BorderFactory.createLineBorder(focusColor, 2));
    }//GEN-LAST:event_queryResultTableFocusGained

    private void queryResultTableFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_queryResultTableFocusLost
        spResultTable.setBorder(BorderFactory.createLineBorder(noFocusColor, 2));
    }//GEN-LAST:event_queryResultTableFocusLost

    private void metaTreeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_metaTreeKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            TreePath tp = metaTree.getSelectionPath();
            if (tp != null && tp.getPath().length == 2) {
                String name = tp.getLastPathComponent().toString();
                MySQLAdapter.getInstance().switchDB(name);
            }
        }
    }//GEN-LAST:event_metaTreeKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree metaTree;
    private javax.swing.JPanel paneActiveDB;
    private javax.swing.JPanel paneMessage;
    private javax.swing.JTextPane queryCode;
    private javax.swing.JTable queryResultTable;
    private javax.swing.JScrollPane spActiveDB;
    private javax.swing.JScrollPane spMessage;
    private javax.swing.JScrollPane spMetaTree;
    private javax.swing.JScrollPane spQueryCode;
    private javax.swing.JScrollPane spResultTable;
    private javax.swing.JTextArea taActiveDB;
    private javax.swing.JTextArea taMessage;
    // End of variables declaration//GEN-END:variables
}
