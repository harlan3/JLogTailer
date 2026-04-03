/* 
Copyright Paul James Mutton, 2001-2004, http://www.jibble.org/

This file is part of JLogTailer.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

$Author: pjm2 $
$Id: JLogTailerInternalFrame.java,v 1.2 2004/02/01 13:21:17 pjm2 Exp $

*/

package jlogtrailer;

import java.io.*;
import java.util.*;
import java.nio.file.*;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * JLogTailer - A log tailer utility written in Java.
 * Copyright Paul James Mutton, 2002.
 * 
 * Enhancements (C)2018 Alex T. Ramos
 * 
 * @author Paul James Mutton, http://www.jibble.org/
 * @version 2.0
 */
public class JLogTailerJFrame extends JFrame implements Runnable, Serializable {
    
    // Maximum number of lines that we shall display before removing earlier ones.
    private int maxLines = 10000;
    private int linesShown = 0;
    
    private boolean running = true;
    private int updateInterval = 100;
    private File logFile;
    private long filePointer;
    public AutoScrollTextArea autoScrollTextArea = new AutoScrollTextArea();
    
    public JLogTailerJFrame() {
        
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
    }
    
    public JLogTailerJFrame(File logFile, Rectangle bounds) throws IOException {
        
    	this.logFile = logFile;
        
        Container pane = this.getContentPane();
        pane.add(autoScrollTextArea, BorderLayout.CENTER);
        
        setBounds(bounds);
        setTitle(logFile.getName());
        
        // Allow tail logging of non-existant files.
        if (!logFile.exists() || logFile.isDirectory() || !logFile.canRead()) {
        	Files.createFile(logFile.toPath());
        }
        
        filePointer = logFile.length();
        
        this.appendMessage("Log tailing started on " + logFile.toString());
        this.setVisible(true);
    }
    
    // This is the method that contains all the actual log tailing stuff.
    // Note: I'm not particularly happy about the use of the readLine()
    // method call, as it may return a partial line if it reaches the
    // end of the file.  It might be worth investigating other alternatives
    // at another time.
    public void run() {
        try {
            while (running) {
                Thread.sleep(updateInterval);
                long len = logFile.length();
                if (len < filePointer) {
                    // Log must have been modified or deleted.
                    this.appendMessage("Log file was reset. Restarting logging from start of file.");
                    filePointer = len;
                }
                else if (len > filePointer) {
                    // File must have had something added to it!
                    RandomAccessFile raf = new RandomAccessFile(logFile, "r");
                    raf.seek(filePointer);
                    String line = null;
                    while ((line = raf.readLine()) != null) {
                        this.appendLine(line);
                    }
                    filePointer = raf.getFilePointer();
                    raf.close();
                }
            }
        }
        catch (Exception e) {
            this.appendMessage("Fatal error reading log file, log tailing has stopped.");
        }
    }
    
    public void appendLine(String line) {
        try {
        	
            JTextPane textPane = autoScrollTextArea.getTextPane();
            Document document = autoScrollTextArea.getDocument();
                      
            autoScrollTextArea.append(line + "\n");
            
            textPane.setDocument(document);
            if (++linesShown > maxLines) {
                // We must remove a line!
                int len = textPane.getText().indexOf('\n');
                document.remove(0, len);
                linesShown--;
            }
        }
        catch (BadLocationException e) {
            // But this'll never happen, right?
            throw new RuntimeException("Tried to add a new line to a bad place.");
        }
    }
    
    public void appendMessage(String message) {
    	
        SimpleAttributeSet attr = autoScrollTextArea.getSimpleAttributeSet();
        StyleConstants.setForeground(attr, Color.black);
        this.appendLine("[" + new Date().toString() + ", " + message + "]");
    }
}