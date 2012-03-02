/*
 * AppHotKey.java
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

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class AppHotKey extends EventQueue {

    private static final AppHotKey instance = new AppHotKey();
    private final InputMap keyStrokes = new InputMap();
    private final ActionMap actions = new ActionMap();

    public static AppHotKey getInstance() { return instance; }
    public InputMap getInputMap() { return keyStrokes; }
    public ActionMap getActionMap() { return actions; }

    static { Toolkit.getDefaultToolkit().getSystemEventQueue().push(instance); }

    private AppHotKey() { }

    @Override
    protected void dispatchEvent(AWTEvent event) {
        if (event instanceof KeyEvent) {
            KeyStroke ks = KeyStroke.getKeyStrokeForEvent((KeyEvent) event);

            String actionKey = (String) keyStrokes.get(ks);
            if (actionKey != null) {
                Action action = actions.get(actionKey);
                if (action != null && action.isEnabled()) {
                    action.actionPerformed(new ActionEvent(event.getSource(),
                            event.getID(), actionKey, ((KeyEvent) event).getModifiers()));
                    return;
                }
            }
        }
        super.dispatchEvent(event);
    }
    
    public void addKey(int keyCode, int modifiers, AbstractAction action) {
        addKey(KeyStroke.getKeyStroke(keyCode, modifiers, false), action);
    }
    
    public void addKey(KeyStroke key, AbstractAction action) {
        getInputMap().put(key, key.toString());
        getActionMap().put(key.toString(), action);
    }
}
