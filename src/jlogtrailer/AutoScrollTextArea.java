/* 
Copyright Paul James Mutton, 2001-2004, http://www.jibble.org/

This file is part of JLogTailer.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

$Author: pjm2 $
$Id: AutoScrollTextArea.java,v 1.2 2004/02/01 13:21:17 pjm2 Exp $

*/

package jlogtrailer;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * JLogTailer - A log tailer utility written in Java.
 * Copyright Paul James Mutton, 2002.
 * 
 * Enhancements (C) Alex T. Ramos, 2018.
 * 
 * @author Paul James Mutton, http://www.jibble.org/
 * @version 2.0
 */
public class AutoScrollTextArea extends JScrollPane {
    
    private static final int DEFAULT_FONT_SIZE = 16;
    private JTextPane textPane = new JTextPane(new DefaultStyledDocument());
    private SimpleAttributeSet attributeSet = new SimpleAttributeSet();
    
	public AutoScrollTextArea() {
        super();
        textPane.setEditable(false);
        this.setFontSize(DEFAULT_FONT_SIZE);
        this.setViewportView(textPane);
    }
    
    public void setFontSize(int size) {
    	textPane.setFont(new Font("Monospaced", Font.PLAIN, size));
    }
    
    private void scrollToBottom() {
        textPane.setCaretPosition(textPane.getDocument().getLength());
    }
    
    public JTextPane getTextPane() {
        return textPane;
    }
    
    public void append(String str) throws BadLocationException {
        textPane.getDocument().insertString(textPane.getDocument().getLength(), str, attributeSet);
        scrollToBottom();
    }
    
    public Document getDocument() {
        return textPane.getDocument();
    }
    
    public SimpleAttributeSet getSimpleAttributeSet() {
        return attributeSet;
    }
}