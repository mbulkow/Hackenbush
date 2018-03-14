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

import my.hackenbush.Hackenbush;


import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;

/**
 * The class HackenbushDisplayPanel is an extension of JPanel with the ability 
 * to visually represent Red-Blue Hackenbush games placed into the field 
 * "hackenbush".
 * @author Madeleine Bulkow
 */
public class HackenbushDisplayPanel extends JPanel{
    
    public Hackenbush hackenbush;
    public int width;
    public int height;
    public int pointSize = 4;
    public int loopSize = 8;
    public int minAngle = 5;
    
    /**
     * Constructor
     * 
     * @param hackenbush The Red-Blue Hackenbush game to display.
     * @param width The width of the display area in pixels.
     * @param height The height of the display area in pixels.
     */
    public HackenbushDisplayPanel(Hackenbush hackenbush, int width, int height){
        this.hackenbush = hackenbush;
        this.width = width;
        this.height = height;
        this.setSize(width,height);
        
    }
    
    /**
     * Draws an arc starting at the given start coordinates, moving through 
     * the given number of degrees, and ending at the given end coordinates.
     * Note: currently can only draw arcs with angle magnitude less than or 
     * equal to 180 degrees.
     * @param xStart An integer: the x coordinate for the start of the arc.
     * @param yStart An integer: the y coordinate for the start of the arc.
     * @param xEnd An integer: the x coordinate for the end of the arc.
     * @param yEnd An integer: the y coordinate for the end of the arc.
     * @param arcAngle An integer between -180 and 180.
     * @param g 
     */
    private void myDrawArc(int xStart, int yStart, int xEnd, int yEnd, 
            int arcAngle, Graphics g){
        if(arcAngle % 180 != 0){
            arcAngle = arcAngle % 180;
        }
        if(Math.abs(arcAngle) <= minAngle){
            g.drawLine(xStart, yStart, xEnd, yEnd);
            return;
        }
        int slopeX = xEnd - xStart;
        int slopeY = yEnd - yStart;
        int xMidpoint = (xEnd + xStart) / 2;
        int yMidpoint = (yEnd + yStart) / 2;
        double distance = Math.sqrt(slopeX * slopeX + slopeY * slopeY);
        double alpha = Math.toRadians(arcAngle / 2.0);
        double radius = Math.abs(distance / (2.0 * Math.sin(alpha)));
        double secantToCenter = Math.sqrt(radius * radius - distance * distance 
                / 4.0);
        double xCenter = xMidpoint + (secantToCenter * slopeY) / distance;
        double yCenter = yMidpoint - (secantToCenter * slopeX) / distance;
        if(arcAngle < 0){
            xCenter = xMidpoint - (secantToCenter * slopeY) / distance;
            yCenter = yMidpoint + (secantToCenter * slopeX) / distance;
        }
        int xUpperLeft = (int) (xCenter - radius);
        int yUpperLeft = (int) (yCenter - radius);
        double sinStartAngle = (yCenter - yStart) / radius;
        if(sinStartAngle < -1) sinStartAngle = -1;
        else if(sinStartAngle > 1) sinStartAngle = 1;
        int startAngle = (int) Math.toDegrees(Math.asin(sinStartAngle));
        if(xCenter >= xStart){
            startAngle = 180 - startAngle;
        }
        g.drawArc(xUpperLeft, yUpperLeft, 2 * (int) radius, 2 * (int) radius, 
                startAngle, arcAngle);
    }
    
    /**
     * {@inheritDoc}
     * @param g Graphics object.
     */
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.clearRect(0, 0, width, height);
        int[][] nodeCoords = hackenbush.getNodeCoords(width, height);
        
        // Draw the ground.
        g.drawLine(0, height, width, height);
        
        int[][] redEdges = hackenbush.getEdges('r');
        int[][] blueEdges = hackenbush.getEdges('b');
        
        for(int i = 0; i<hackenbush.getSize(); i++){
            for(int j = i; j<hackenbush.getSize(); j++){
                int redIJEdges = redEdges[i][j];
                int blueIJEdges = blueEdges[i][j];
                if(redIJEdges + blueIJEdges > 0){
                    int xCoordStart = nodeCoords[i][0];
                    int yCoordStart = nodeCoords[i][1];
                    // Draw any loops.
                    if(i==j){
                        g.setColor(Color.RED);
                        for(int k=0; k<redIJEdges; k++){
                            g.drawOval(xCoordStart, yCoordStart - (k + 1) * 
                                    loopSize, 2*(k + 1)*loopSize, 
                                    2*(k + 1)*loopSize);
                        }
                        g.setColor(Color.BLUE);
                        for(int k=0; k<blueIJEdges; k++){
                            g.drawOval(xCoordStart - 2 * (k + 1) * loopSize, 
                                    yCoordStart - (k + 1)*loopSize,
                                    2*(k + 1)*loopSize, 2*(k + 1)*loopSize);
                        }
                    }
                    else{
                        // Draw edges from one node to the next.
                        // To handle multiple edges, draw arcing lines
                        // when necessary.
                        int xCoordEnd = nodeCoords[j][0];
                        int yCoordEnd = nodeCoords[j][1];
                        double eachAngle = 360/(redIJEdges + blueIJEdges + 1.0);
                        int k = 0;
                        while(k < redIJEdges){
                            g.setColor(Color.RED);
                            myDrawArc(xCoordStart, yCoordStart, xCoordEnd, 
                                    yCoordEnd, 
                                    -180 + (int)((k + 1) * eachAngle), g);
                            k++;
                        }
                        while(k < redIJEdges + blueIJEdges){
                            g.setColor(Color.BLUE);
                            myDrawArc(xCoordStart, yCoordStart, xCoordEnd, 
                                    yCoordEnd, 
                                    -180 + (int) ((k + 1) *eachAngle) , g);
                            k++;
                        }
                    }
                }
            }
        }
        // Draw all the nodes.
        g.setColor(Color.BLACK);
        for(int[] nodeCoordPair: nodeCoords){
            g.drawOval(nodeCoordPair[0] - pointSize / 2,
                    nodeCoordPair[1] - pointSize / 2, pointSize, pointSize);
            g.fillOval(nodeCoordPair[0] - pointSize / 2,
                    nodeCoordPair[1] - pointSize / 2, pointSize, pointSize);
        }
    }
}
