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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * This class launches a window which allows a user to create Red-Blue 
 * Hackenbush games by entering appropriate adjacency matrices.
 * If the game is a tree, it will also display the value of the current game.
 * Further interactivity is sadly not yet enabled.
 * @author Madeleine Bulkow
 */
public class HackenbushWindow implements ActionListener{
    
    private final int width;
    private final int height;
    private final int border = 20;
    private Hackenbush bush;
    private HackenbushDisplayPanel hackenbushDisplay;

    int size = 3;
    int[][] redEdges = {{0,1,0},{1,0,0},{0,0,0}};
    int[][] blueEdges = {{0,0,0},{0,0,1},{0,1,0}};
    
    private JTextArea redText;
    private JTextArea blueText;
    public JTextArea messages;
    private final String[] colorStrings = {"Red", "Blue"};
    private JComboBox moveColor;
    private JComboBox edgeStart;
    private JComboBox edgeEnd;
    private Button move;
    
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
     * Performs an action based on corresponding button press or comboBox
     * selection.
     * @param e The action it is responding to.
     */
    @Override
    public void actionPerformed(ActionEvent e){
        // TODO switch?
        if("createGame".equals(e.getActionCommand())){
            createNewGameData();
            updateGUI();
        }
        else if("updateStartChoices".equals(e.getActionCommand())){
            updateStartChoices();
            updateEndChoices();
        }
        else if("updateEndChoices".equals(e.getActionCommand())){
            updateEndChoices();
        }
        else if("move".equals(e.getActionCommand())){
            userMove();
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
        hackenbushDisplay.setBorder(
                BorderFactory.createLineBorder(Color.black));
        
        JPanel controlPanel = designControlPanel();
        
        JPanel container = new JPanel(new GridLayout(1,2));
        container.add(controlPanel);
        container.add(hackenbushDisplay);
        
        JFrame j = new JFrame("Hackenbush"); //TODO
        j.setSize(width + border,height + border);
        j.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        j.setVisible(true); 
        j.add(container);
    }
    
    
    // TODO this is only called once, does it really need its own function?
    /**
     * Creates a control panel allowing users to update the current game.
     * @return JPanel to display in HackenbushWindow.
     */
    private JPanel designControlPanel(){
        JLabel redLabel = new JLabel("Red edges:");
        redText = new JTextArea(edgesToText(redEdges),12,20); //TODO
        redText.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JLabel blueLabel = new JLabel("Blue edges:");
        blueText = new JTextArea(edgesToText(blueEdges),12,20); //TODO
        blueText.setBorder(BorderFactory.createLineBorder(Color.black));
        
        Button updateGraph = new Button("Create game");
        updateGraph.setActionCommand("createGame");
        updateGraph.addActionListener(this);
        
        JLabel makeMove = new JLabel("Make a move: ");
        
        moveColor = new JComboBox(colorStrings);
        moveColor.setActionCommand("updateStartChoices");
        moveColor.addActionListener(this);
        
        edgeStart = new JComboBox();
        edgeStart.setActionCommand("updateEndChoices");
        edgeStart.addActionListener(this);
        
        edgeEnd = new JComboBox();
        
        move = new Button("Move");
        move.setActionCommand("move");
        move.addActionListener(this);
        
        updateStartChoices();
        updateEndChoices();
        
        messages = new JTextArea("Messages appear here.",4, 30); //TODO
        messages.setEditable(false);
        
        JScrollPane messageScroll = new JScrollPane(messages, 
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        JPanel controlPanel = new JPanel(new GridBagLayout());
        
        // TODO yiiiiikes
        
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = .2;
        c.weighty = 0.3;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;        
        controlPanel.add(redLabel,c);
        c.weightx = 0.8;
        c.gridwidth = 4;
        c.gridx = 1;
        c.gridy = 0;
        controlPanel.add(redText,c);
        c.weightx = 0.2;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 2; 
        controlPanel.add(blueLabel,c);
        c.weightx = 0.8;
        c.gridwidth = 4;
        c.gridx = 1;
        c.gridy = 2; 
        controlPanel.add(blueText,c);
        c.weightx = 0.2;
        c.weighty = 0.1;
        c.gridwidth = 1;
        c.gridx = 4;
        c.gridy = 4;
        controlPanel.add(updateGraph,c);
        c.gridx = 0;
        c.gridy = 5;
        controlPanel.add(makeMove,c);
        c.gridx = 1;
        c.gridy = 5;
        controlPanel.add(moveColor,c);
        c.gridx = 2;
        c.gridy = 5;
        controlPanel.add(edgeStart,c);
        c.gridx = 3;
        c.gridy = 5;
        controlPanel.add(edgeEnd,c);
        c.gridx = 4;
        c.gridy = 5;
        controlPanel.add(move,c);
        c.gridwidth = 5;
        c.gridx = 0;
        c.gridy = 6;
        controlPanel.add(messageScroll,c);
        
        return controlPanel;
    }
    
    /**
     * Given a color, finds all nodes at which that color can move.
     * @param color A character representing the current color; 'r' for red or 
     * 'b' for blue.
     * @return An array of available nodes (represented by Integers between 0 
     * and size-1, inclusive).
     */
    private Integer[] availableMoves(char color){
        LinkedList<Integer> availableMoves = new LinkedList<>();
        int[][] edges = bush.getEdges(color);
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(edges[i][j] > 0){
                    availableMoves.add(i);
                    break;
                }
            }
        }
        return availableMoves.toArray(new Integer[availableMoves.size()]);
    }
    
    /**
     * Given a color and node, finds all color-adjacent moves.
     * @param color A character representing the current color; 'r' for red or 
     * 'b' for blue.
     * @return An array of available nodes (represented by Integers between 0 
     * and size-1, inclusive).
     */
    private Integer[] availableMovesAtNode(char color, int node){
        LinkedList<Integer> availableMoves = new LinkedList<>();
        int[][] edges = bush.getEdges(color);
        for(int i = 0; i < size; i++){
            if(edges[node][i]>0){
                availableMoves.add(i);
            }
        }
        return availableMoves.toArray(new Integer[availableMoves.size()]);
    }
    
    /**
     * Updates data to reflect current GUI selections.
     */
    private void createNewGameData(){
        redEdges = textToEdges(redText.getText());
        blueEdges = textToEdges(blueText.getText());
        if(redEdges == null || blueEdges == null) return;
        size = redEdges.length;
        if(size != blueEdges.length){
            messages.setText("Matrices must be the same size.");
        }
        else{
            bush = new Hackenbush(size, redEdges, blueEdges);
            hackenbushDisplay.hackenbush = bush;
        }
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
     * Makes a move on the Hackenbush object based on current selections.
     * Updates GUI accordingly.
     */
    private void userMove(){
        // TODO protect against bad selections
        if(moveColor.getSelectedItem() == null ||
                edgeStart.getSelectedItem() == null ||
                edgeEnd.getSelectedItem() == null){
            messages.setText(messages.getText() + "\n Bad move.");
            return;
        }
        char color  = stringToColor((String) moveColor.getSelectedItem());
        int start = (int) edgeStart.getSelectedItem();
        int end = (int) edgeEnd.getSelectedItem();
        String moveMessage = bush.move(color, start, end);
        messages.setText(messages.getText() + "\n" + moveMessage);
        updateGUI();
    }
    
    /**
     * Prints information about the current state of the game, including the 
     * game's current value if the current graph is a rooted tree.
     */
    private void printState(){
        if(bush.isTree()){
            Hackentree hackentree = new Hackentree(bush);
            String gameState = hackentree.gameState('r');
            messages.setText(messages.getText() + "\n" + gameState);
        }
        else{
            String gameState = bush.gameState('r');
            messages.setText(messages.getText() + "\n" + gameState);
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
                messages.setText("Improper input.");
                return null;
            }
            if(currentChar == '\n'){
                if(currentElt.isEmpty()){
                    messages.setText("All elements must be nonempty.");
                    return null;
                }
                if(col < currentSize-1){
                    messages.setText("Matrix must be square.");
                    return null;
                }
                edges[row][col] = Integer.parseInt(currentElt);
                currentElt = "";
                row ++;
                col = 0;
            }
            else if(currentChar == ' '){
                if(currentElt.isEmpty()){
                    messages.setText("All elements must be nonempty.");
                    return null;
                }
                edges[row][col] = Integer.parseInt(currentElt);
                currentElt = "";
                col ++;
            }
            else{
                if(Character.digit(currentChar, 10)<0){
                    messages.setText("All entries must be non-negative "
                            + "integers.");
                    return null;
                }
                currentElt += currentChar;
            }
        }
        if(!currentElt.isEmpty()){
            edges[row][col] = Integer.parseInt(currentElt);
        }
        messages.setText("Game created.");
        return edges;
    }
    
    /**
     * Converts a string describing the name of a color to the associated 
     * character (i.e. the lowercase version of the first letter)
     * @param colorString A string containing the color name.
     * @return The first letter of the color name, in lowercase.
     */
    private char stringToColor(String colorString){
        if(colorString.isEmpty()) return ' ';
        return Character.toLowerCase(colorString.charAt(0));
    }
    
    /**
     * Updates the ending node choices available in edgeEnd.
     */
    private void updateEndChoices(){
        // TODO keep previous selection if valid
        char startColor = stringToColor((String) moveColor.getSelectedItem());
        edgeEnd.removeAllItems();
        if(edgeStart.getItemCount() == 0){
            return;
        }
        int startPt = (int) edgeStart.getSelectedItem();
        Integer[] endingPts = availableMovesAtNode(startColor, startPt);
        for(Integer point: endingPts){
            edgeEnd.addItem(point);
        }
        if(endingPts.length > 0){
            edgeEnd.setSelectedIndex(0);
        }
        edgeEnd.setEnabled(endingPts.length != 0);
        move.setEnabled(endingPts.length != 0);
    }
    
    /**
     * Updates the graph and text to reflect current data.
     */
    private void updateGUI(){
        hackenbushDisplay.repaint();
        redText.setText(edgesToText(bush.getEdges('r')));
        blueText.setText(edgesToText(bush.getEdges('b')));
        updateStartChoices();
        updateEndChoices();
        printState();
    }
    
    /**
     * Updates the list of starting nodes available in edgeStart.
     */
    private void updateStartChoices(){
        // TODO keep previous selection if valid
        char startColor = stringToColor((String) moveColor.getSelectedItem());
        Integer[] startingPts = availableMoves(startColor);
        edgeStart.removeAllItems();
        for(Integer point: startingPts){
            edgeStart.addItem(point);
        }
        boolean haveChoices = (startingPts.length > 0);
        if(haveChoices){
            edgeStart.setSelectedIndex(0);
        }
        edgeStart.setEnabled(haveChoices);
        edgeEnd.setEnabled(haveChoices);
        move.setEnabled(haveChoices);
    }
}
