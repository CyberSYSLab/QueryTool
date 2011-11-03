/*
 * SQLDocumentListener.java
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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class SQLDocumentListener implements DocumentListener {

    private JTextPane textArea;
    private StyledDocument doc;
    private static final String COMMIT_ACTION = "commit";
    private static enum Mode { INSERT, COMPLETION };
    private final List<String> keyWords = new ArrayList<String>();
    private Mode mode = Mode.INSERT;
    
    public void setTextArea(JTextPane textArea, StyledDocument doc) {
        this.textArea = textArea;
        this.doc = doc;

        InputMap im = textArea.getInputMap();
        ActionMap am = textArea.getActionMap();
        im.put(KeyStroke.getKeyStroke("TAB"), COMMIT_ACTION);
        am.put(COMMIT_ACTION, new CommitAction());
    }
    
    public void loadKeyWords(List<String> keyWords) {
        this.keyWords.clear();
        for (String word : keyWords) this.keyWords.add(word.toLowerCase());
        Collections.sort(this.keyWords);
    }
    
    private class CommitAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent ev) {
            if (mode == Mode.COMPLETION) {
                try {
                    int pos = textArea.getSelectionEnd();
                    doc.insertString(pos, " ", null);
                    textArea.setCaretPosition(pos + 1);
                    mode = Mode.INSERT;
                } catch (BadLocationException ex) { System.err.println(ex.getMessage()); }
            } else { textArea.replaceSelection("\t"); }
        }
    }

    private class CompletionTask implements Runnable {
        String completion;
        int position;
        
        CompletionTask(String completion, int position) {
            this.completion = completion;
            this.position = position;
        }
        
        @Override
        public void run() {
            try {
                doc.insertString(position, completion, null);
                textArea.setCaretPosition(position + completion.length());
                textArea.moveCaretPosition(position);
                mode = Mode.COMPLETION;
            } catch (BadLocationException ex) { System.err.println(ex.getMessage()); }
        }
    }
    
    @Override
    public void insertUpdate(DocumentEvent ev) {
        
        if (ev.getLength() != 1) { return; }
        
        int pos = ev.getOffset();
        String content = null;
        try {
            content = textArea.getText(0, pos + 1);
        } catch (BadLocationException e) { System.err.println(e.getMessage()); }
        
        int w;
        for (w = pos; w >= 0; w--) {
            if (! Character.isLetter(content.charAt(w))) { break; }
        }
        
        if (pos - w < 2) { return; }
        
        String prefix = content.substring(w + 1).toLowerCase();
        int n = Collections.binarySearch(keyWords, prefix);
        if (n < 0 && -n <= keyWords.size()) {
            String match = keyWords.get(-n - 1);
            if (match.startsWith(prefix)) {
                String completion = match.substring(pos - w);
                SwingUtilities.invokeLater(new CompletionTask(completion, pos + 1));
            }
        } else { mode = Mode.INSERT; }
    }

    @Override
    public void removeUpdate(DocumentEvent e) { }

    @Override
    public void changedUpdate(DocumentEvent e) { }
    
}
