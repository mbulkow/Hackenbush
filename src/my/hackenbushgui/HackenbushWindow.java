 /*
 * The MIT License
 *
 * Copyright 2018 Madeleine Bulkow.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package my.hackenbushgui;

import my.hackenbush.Hackentree;
import my.hackenbush.Hackenbush;
import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class launches a window which allows a user to create Red-Blue 
 * Hackenbush games by entering appropriate adjacency matrices.
 * If the game is a tree, it will also display the value of the current game.
 * Further interactivity is sadly not yet enabled.
 * @author Madeleine Bulkow
 */
public class HackenbushWindow implements ActionListener{
    
    private int width;
    private int height;
    private int border = 20;
    private Hackenbush bush;
    private HackenbushDisplayPanel hackenbushDisplay;

    int size = 3;
    int[][] redEdges = {{0,1,0},{1,0,0},{0,0,0}};
    int[][] blueEdges = {{0,0,0},{0,0,1},{0,1,0}};
    
    private JTextArea redText;
    private JTextArea blueText;
    public JLabel message;
    
    /**
     * Constructor
     * 
     * @param width The desired width for the window, in pixels.
     * @param height The desired height for the window, in pixels.
     */
    public HackenbushWindow(int width, int height){
        this.width = Math.abs(width);
        this.height = Math.abs(height);
        design();    
    }
    
    /**
     * Performs an action based on corresponding button press.
     * @param e The action it is responding to.
     */
    @Override
    public void actionPerformed(ActionEvent e){
        if("updateGraph".equals(e.getActionCommand())){
            updateGraph();
        }
    }
    
    /**
     * Creates the window needed to display the Hackenbush game, controls, and
     * messages.
     */
    private void design(){
        bush = new Hackenbush(size, redEdges, blueEdges);
        hackenbushDisplay = new HackenbushDisplayPanel(
                bush, width/2, height - 2 * border);        
        
        JPanel controlPanel = designControlPanel();
        
        JPanel container = new JPanel(new GridLayout(1,2));
        container.add(controlPanel);
        container.add(hackenbushDisplay);
        
        JFrame j = new JFrame();
        j.setSize(width + border,height + border);
        j.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        j.setVisible(true); 
        j.add(container);
    }
    
    /**
     * Creates a control panel allowing users to update the current game.
     * @return JPanel to display in HackenbushWindow.
     */
    private JPanel designControlPanel(){
        JLabel redLabel = new JLabel("Enter adjacency matrix for red edges:");
        redText = new JTextArea(edgesToText(redEdges),10,20);
        redText.setLineWrap(true);
        
        JLabel blueLabel = new JLabel("Enter adjacency matrix for blue edges:");
        blueText = new JTextArea(edgesToText(blueEdges),10,20);
        blueText.setLineWrap(true);
        
        Button updateGraph = new Button("Update graph");
        updateGraph.setActionCommand("updateGraph");
        updateGraph.addActionListener(this);
        
        message = new JLabel("Messages appear here.");
        
        JPanel controlPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 0.05;
        c.gridx = 0;
        c.gridy = 0;        
        controlPanel.add(redLabel,c);
        c.weighty = 0.3;
        c.gridx = 0;
        c.gridy = 1;
        controlPanel.add(redText,c);
        c.weighty = 0.05;
        c.gridx = 0;
        c.gridy = 2; 
        controlPanel.add(blueLabel,c);
        c.weighty = 0.3;
        c.gridx = 0;
        c.gridy = 3; 
        controlPanel.add(blueText,c);
        c.weighty = 0.1;
        c.gridx = 0;
        c.gridy = 4;
        controlPanel.add(updateGraph,c);
        c.gridx = 0;
        c.gridy = 5;
        controlPanel.add(message,c);
        
        return controlPanel;
    }
    
    /**
     * Converts a square, two-dimensional integer array into easily readable 
     * text.
     * @param edges A square, two-dimensional integer array.
     * @return A string, representing either the matrix or an appropriate error
     * message.
     */
    private String edgesToText(int[][] edges){
        String edgeText = "";
        size = edges.length;
        if(size == 0) return "";
        if(edges[0].length != size) return "Matrix not square.";
        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                edgeText += edges[i][j];
                if(j < size - 1){
                    edgeText += " ";
                }
            }
            if(i<edges.length - 1){
                edgeText += "\n";
            }
        }
        return edgeText;
    }
    
    /**
     * Prints information about the current state of the game, including the 
     * game's current value if the current graph is a rooted tree.
     */
    private void printState(){
        if(bush.isTree()){
            Hackentree hackentree = new Hackentree(bush);
            String value = hackentree.gameState('r');
            message.setText("Current game value is "+value);
        }
        else{
            String gameState = bush.gameState('r');
            message.setText(gameState);
        }
    }    
    
    /**
     * Converts space and new-line separated text into a square matrix.
     * @param text A square matrix of integers with space separated elements and 
     * new-line characters indicating a change in rows.
     * @return Corresponding matrix.
     */
    private int[][] textToEdges(String text){
        int currentSize = 1;
        int  k = 0;
        while(k < text.length() && text.charAt(k) != '\n'){
            if(text.charAt(k) == ' '){
                currentSize++;
            }
            k++;
        }
        int[][] edges = new int[currentSize][currentSize];
        int row = 0;
        int col = 0;
        String currentElt = "";
        char currentChar;
        for(int i=0; i<text.length(); i++){
            currentChar = text.charAt(i);
            if(row >= currentSize || col >= currentSize){
                message.setText("Improper input.");
                return null;
            }
            if(currentChar == '\n'){
                if(currentElt.isEmpty()){
                    message.setText("All elements must be nonempty.");
                    return null;
                }
                if(col < currentSize-1){
                    message.setText("Matrix must be square.");
                    return null;
                }
                edges[row][col] = Integer.parseInt(currentElt);
                currentElt = "";
                row ++;
                col = 0;
            }
            else if(currentChar == ' '){
                if(currentElt.isEmpty()){
                    message.setText("All elements must be nonempty.");
                    return null;
                }
                edges[row][col] = Integer.parseInt(currentElt);
                currentElt = "";
                col ++;
            }
            else{
                if(Character.digit(currentChar, 10)<0){
                    message.setText("All entries must be non-negative integers.");
                    return null;
                }
                currentElt += currentChar;
            }
        }
        if(!currentElt.isEmpty()){
            edges[row][col] = Integer.parseInt(currentElt);
        }
        return edges;
    }
    
    /**
     * Uses the current contents of the text fields to update the graph 
     * displaying the current game. Updates message with either a relevant error
     * message or with a current description of the state of the game.
     */
    public void updateGraph(){
        redEdges = textToEdges(redText.getText());
        blueEdges = textToEdges(blueText.getText());
        if(redEdges == null || blueEdges == null) return;
        size = redEdges.length;
        if(size != blueEdges.length){
            message.setText("Matrices must be the same size.");
        }
        else{
            bush = new Hackenbush(size, redEdges, blueEdges);
            hackenbushDisplay.hackenbush = bush;
            hackenbushDisplay.repaint();
        }
        redText.setText(edgesToText(bush.getEdges('r')));
        blueText.setText(edgesToText(bush.getEdges('b')));
        printState();
    }
}
