/*
 * SQLDocument.java
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
import java.util.HashMap;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;

public class SQLDocument extends DefaultStyledDocument {

    private Element rootElement;
    private boolean multiLineComment;
    private MutableAttributeSet normal;
    private MutableAttributeSet keyword;
    private MutableAttributeSet metaword;
    private MutableAttributeSet comment;
    private MutableAttributeSet quote;
    private HashMap<String, Object> keyWords = new HashMap<String, Object>();
    private HashMap<String, Object> metaWords = new HashMap<String, Object>();
    
    private String lastError = "";

    public SQLDocument() {
        
        rootElement = getDefaultRootElement();
        putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");

        normal = new SimpleAttributeSet();
        StyleConstants.setForeground(normal, Color.BLACK);
        comment = new SimpleAttributeSet();
        StyleConstants.setForeground(comment, Color.GRAY);
        keyword = new SimpleAttributeSet();
        StyleConstants.setForeground(keyword, Color.BLUE);
        metaword = new SimpleAttributeSet();
        StyleConstants.setForeground(metaword, Color.decode("#999966"));
        quote = new SimpleAttributeSet();
        StyleConstants.setForeground(quote, Color.RED);
    }
    
    public void loadKeyWords(List<String> keyWords) {
        this.keyWords.clear();
        for (String word : keyWords) { this.keyWords.put(word.toUpperCase(), new Object()); }
    }

    public void loadMetaWords(List<String> metaWords) {
        this.metaWords.clear();
        for (String word : metaWords) { this.metaWords.put(word.toUpperCase(), new Object()); }
    }
    
    /**
     * Override to apply syntax highlighting after the document has been updated
     */
    @Override
    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
        if (str.equals("{")) str = addMatchingBrace(offset);
        str = str.replaceAll("\t", "    ");
        super.insertString(offset, str, a);
        processChangedLines(offset, str.length());
    }

    /**
     * Override to apply syntax highlighting after the document has been updated
     */
    @Override
    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);
        processChangedLines(offset, 0);
    }

    /**
     * Determine how many lines have been changed,
     * then apply highlighting to each line
     */
    private void processChangedLines(int offset, int length) throws BadLocationException {
        String content = getText(0, getLength());
        int startLine = rootElement.getElementIndex(offset);
        int endLine = rootElement.getElementIndex(offset + length);
        setMultiLineComment(commentLinesBefore(content, startLine));
        for (int i = startLine; i <= endLine; i++) applyHighlighting(content, i);
        if (isMultiLineComment())
            commentLinesAfter(content, endLine);
        else
            highlightLinesAfter(content, endLine);
    }

    /**
     * Highlight lines when a multi line comment is still 'open'
     * (ie. matching end delimiter has not yet been encountered)
     */
    private boolean commentLinesBefore(String content, int line) {
        int offset = rootElement.getElement(line).getStartOffset();
        int startDelimiter = lastIndexOf(content, getStartDelimiter(), offset - 2);
        if (startDelimiter < 0) return false;
        int endDelimiter = indexOf(content, getEndDelimiter(), startDelimiter);
        if (endDelimiter < offset & endDelimiter != -1) return false;
        setCharacterAttributes(startDelimiter, offset - startDelimiter + 1, comment, false);
        return true;
    }

    /**
     * Highlight comment lines to matching end delimiter
     */
    private void commentLinesAfter(String content, int line) {
        int offset = rootElement.getElement(line).getEndOffset();
        int endDelimiter = indexOf(content, getEndDelimiter(), offset);
        if (endDelimiter < 0) return;
        int startDelimiter = lastIndexOf(content, getStartDelimiter(), endDelimiter);
        if (startDelimiter < 0 || startDelimiter <= offset) {
            setCharacterAttributes(offset, endDelimiter - offset + 1, comment, false);
        }
    }

    /**
     * Highlight lines to start or end delimiter
     */
    private void highlightLinesAfter(String content, int line) throws BadLocationException {
        int offset = rootElement.getElement(line).getEndOffset();
        int startDelimiter = indexOf(content, getStartDelimiter(), offset);
        int endDelimiter = indexOf(content, getEndDelimiter(), offset);
        if (startDelimiter < 0) startDelimiter = content.length();
        if (endDelimiter < 0) endDelimiter = content.length();
        int delimiter = Math.min(startDelimiter, endDelimiter);
        if (delimiter < offset) return;
        int endLine = rootElement.getElementIndex(delimiter);
        for (int i = line + 1; i < endLine; i++) {
            Element branch = rootElement.getElement(i);
            Element leaf = getCharacterElement(branch.getStartOffset());
            AttributeSet as = leaf.getAttributes();
            if (as.isEqual(comment)) applyHighlighting(content, i);
        }
    }

    /**
     * Parse the line to determine the appropriate highlighting
     */
    private void applyHighlighting(String content, int line) throws BadLocationException {
        int startOffset = rootElement.getElement(line).getStartOffset();
        int endOffset = rootElement.getElement(line).getEndOffset() - 1;
        int lineLength = endOffset - startOffset;
        int contentLength = content.length();
        if (endOffset >= contentLength) endOffset = contentLength - 1;
        if (endingMultiLineComment(content, startOffset, endOffset)
                || isMultiLineComment()
                || startingMultiLineComment(content, startOffset, endOffset)) {
            
            setCharacterAttributes(startOffset, endOffset - startOffset + 1, comment, false);
            return;
        }
        setCharacterAttributes(startOffset, lineLength, normal, true);
        int index = content.indexOf(getSingleLineDelimiter(), startOffset);
        if ((index > -1) && (index < endOffset)) {
            setCharacterAttributes(index, endOffset - index + 1, comment, false);
            endOffset = index - 1;
        }
        checkForTokens(content, startOffset, endOffset);
    }

    /**
     * Does this line contain the start delimiter
     */
    private boolean startingMultiLineComment(String content, int startOffset, int endOffset) throws BadLocationException {
        int index = indexOf(content, getStartDelimiter(), startOffset);
        if ((index < 0) || (index > endOffset))
            return false;
        else {
            setMultiLineComment(true);
            return true;
        }
    }

    /**
     * Does this line contain the end delimiter
     */
    private boolean endingMultiLineComment(String content, int startOffset, int endOffset) throws BadLocationException {
        int index = indexOf(content, getEndDelimiter(), startOffset);
        if ((index < 0) || (index > endOffset))
            return false;
        else {
            setMultiLineComment(false);
            return true;
        }
    }

    /**
     * We have found a start delimiter
     * and are still searching for the end delimiter
     */
    private boolean isMultiLineComment() { return multiLineComment; }

    private void setMultiLineComment(boolean value) { multiLineComment = value; }

    /**
     * Parse the line for tokens to highlight
     */
    private void checkForTokens(String content, int startOffset, int endOffset) {
        while (startOffset <= endOffset) {
            while (isDelimiter(content.substring(startOffset, startOffset + 1))) {
                if (startOffset < endOffset)
                    startOffset++;
                else
                    return;
            }
            if (isQuoteDelimiter(content.substring(startOffset, startOffset + 1)))
                startOffset = getQuoteToken(content, startOffset, endOffset);
            else
                startOffset = getOtherToken(content, startOffset, endOffset);
        }
    }

    /**
     * Parse the line to get the quotes and highlight it
     */
    private int getQuoteToken(String content, int startOffset, int endOffset) {
        String quoteDelimiter = content.substring(startOffset, startOffset + 1);
        String escapeString = getEscapeString(quoteDelimiter);
        int index;
        int endOfQuote = startOffset;
        index = content.indexOf(escapeString, endOfQuote + 1);
        while ((index > -1) && (index < endOffset)) {
            endOfQuote = index + 1;
            index = content.indexOf(escapeString, endOfQuote);
        }
        index = content.indexOf(quoteDelimiter, endOfQuote + 1);
        if ((index < 0) || (index > endOffset))
            endOfQuote = endOffset;
        else
            endOfQuote = index;
        setCharacterAttributes(startOffset, endOfQuote - startOffset + 1, quote, false);
        return endOfQuote + 1;
    }

    private int getOtherToken(String content, int startOffset, int endOffset) {
        int endOfToken = startOffset + 1;
        while (endOfToken <= endOffset) {
            if (isDelimiter(content.substring(endOfToken, endOfToken + 1))) break;
            endOfToken++;
        }
        String token = content.substring(startOffset, endOfToken);
        if (isKeyword(token)) setCharacterAttributes(startOffset, endOfToken - startOffset, keyword, false);
        if (isMetaword(token)) setCharacterAttributes(startOffset, endOfToken - startOffset, metaword, false);
        return endOfToken + 1;
    }

    /**
     * This updates the colored text and prepares for undo event
     */
    @Override
    protected void fireInsertUpdate(DocumentEvent evt) {
        super.fireInsertUpdate(evt);
        try {
            processChangedLines(evt.getOffset(), evt.getLength());
        } catch (BadLocationException ex) { lastError = ex.getMessage(); }
    }

    /**
     * This updates the colored text and does the undo operation
     */
    @Override
    protected void fireRemoveUpdate(DocumentEvent evt) {
        super.fireRemoveUpdate(evt);
        try {
            processChangedLines(evt.getOffset(), evt.getLength());
        } catch (BadLocationException ex) { lastError = ex.getMessage(); }
    }

    /**
     * Assume the needle will the found at the start/end of the line
     */
    private int indexOf(String content, String needle, int offset) {
        int index;
        while ((index = content.indexOf(needle, offset)) != -1) {
            String text = getLine(content, index).trim();
            if (text.startsWith(needle) || text.endsWith(needle))
                break;
            else
                offset = index + 1;
        }
        return index;
    }

    /**
     * Assume the needle will the found at the start/end of the line
     */
    private int lastIndexOf(String content, String needle, int offset) {
        int index;
        while ((index = content.lastIndexOf(needle, offset)) != -1) {
            String text = getLine(content, index).trim();
            if (text.startsWith(needle) || text.endsWith(needle))
                break;
            else
                offset = index - 1;
        }
        return index;
    }

    private String getLine(String content, int offset) {
        int line = rootElement.getElementIndex(offset);
        Element lineElement = rootElement.getElement(line);
        int start = lineElement.getStartOffset();
        int end = lineElement.getEndOffset();
        return content.substring(start, end - 1);
    }

    /**
     * Override for other languages
     */
    protected boolean isDelimiter(String character) {
        String operands = ";:{}()[]+-/%<=>!&|^~*";
        if (Character.isWhitespace(character.charAt(0)) || operands.indexOf(character) != -1)
            return true;
        else
            return false;
    }

    /**
     * Override for other languages
     */
    protected boolean isQuoteDelimiter(String character) {
        String quoteDelimiters = "\"'";
        if (quoteDelimiters.indexOf(character) < 0)
            return false;
        else
            return true;
    }

    /**
     * Override for other languages
     */
    protected boolean isKeyword(String token) {
        Object o = keyWords.get(token.toUpperCase());
        return o == null ? false : true;
    }

    /**
     * Override for other languages
     */
    protected boolean isMetaword(String token) {
        Object o = metaWords.get(token.toUpperCase());
        return o == null ? false : true;
    }
    
    /**
     * Override for other languages
     */
    protected String getStartDelimiter() { return "/*"; }

    /**
     * Override for other languages
     */
    protected String getEndDelimiter() { return "*/"; }

    /**
     * Override for other languages
     */
    protected String getSingleLineDelimiter() { return "--"; }

    /**
     * Override for other languages
     */
    protected String getEscapeString(String quoteDelimiter) { return "\\" + quoteDelimiter; }

    /**
     * Overide bracket matching for other languages
     */
    protected String addMatchingBrace(int offset) throws BadLocationException {
        StringBuilder whiteSpace = new StringBuilder();
        int line = rootElement.getElementIndex(offset);
        int i = rootElement.getElement(line).getStartOffset();
        while (true) {
            String temp = getText(i, 1);
            if (temp.equals(" ") || temp.equals("\t")) {
                whiteSpace.append(temp);
                i++;
            } else
                break;
        }
        return "{\n" + whiteSpace.toString() + whiteSpace.toString() + "\n" + whiteSpace.toString() + "}";
    }
}
