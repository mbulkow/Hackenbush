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

package my.hackenbush;

import java.util.LinkedList;

/**
 * The Hackenbush class holds a generic Red-Blue Hackenbush game, represented
 * by two (necessarily symmetric) adjacency matrices, one for red sticks and 
 * one for blue sticks. The vertex at index 0 is treated as "ground" for the 
 * purposes of edge deletion.
 * Note that this means all Hackenbush games represented by this class will
 * necessarily appear connected at the ground. However, since connecting nodes 
 * at the ground level does not affect the value of a game, any Red-Blue 
 * Hackenbush game can be represented by an equivalent game in this class.
 * @author Madeleine Bulkow
 */
public class Hackenbush {
    
    /**
     * The number of nodes in the hackenbush game.
     */
    protected int size;
    
    /**
     * An adjacency matrix for red edges. Must be symmetric, contain only 
     * nonnegative integers.
     */
    protected int[][] redGraph;
    
    /**
     * An adjacency matrix for blue edges. Must be symmetric, contain only
     * nonnegative integers.
     */
    protected int[][] blueGraph;
    
    /**
     * An array containing the height of each node (i.e the length of the
     * shortest path to the ground). If the node is disconnected, its height
     * is listed as size+1.
     */
    protected int[] nodeHeights;
    
    /**
     * The maximum height among nodes that are still connected to the ground.
     * Should be nonnegative and less than size.
     */
    protected int maxHeight;
    
    /**
     * An array of linked lists, of length maxHeight, where the ith list 
     * contains the indices of all nodes of height i.
     */
    protected LinkedList<Integer>[] nodesByHeight;
    /*
    TODO Figure out whether an ArrayList would work better. Optimize usage for
    chosen data structure.
    */
    
    /**
     * Constructor.
     * 
     * @param size A nonnegative integer corresponding to the number of nodes
     * in the Hackenbush.
     * @param redEdges A symmetric size by size array of nonnegative integers,
     * corresponding to the red edges of the Hackenbush.
     * @param blueEdges A symmetric size by size array of nonnegative integers,
     * corresponding to the blue edges of the Hackenbush.
     */
    public Hackenbush(int size, int[][] redEdges, 
            int[][] blueEdges){
        if(size < 0) {
            throw new IllegalArgumentException("Size must be nonnegative.");
        }
        this.size = size;
        nodesByHeight = new LinkedList[size];
        if(size != redEdges.length || size != blueEdges.length){
            throw new IllegalArgumentException("Unequal sizes");
        }
        for(int i = 0; i < size; i++){
            if(redEdges[i].length != size 
                    || blueEdges[i].length != size){
                throw new IllegalArgumentException("Unequal sizes");
            }
        }
        for(int i = 0; i < size; i++){
            for(int j = i; j < size; j ++){
                if(redEdges[i][j] != redEdges[j][i]
                        || blueEdges[i][j] != blueEdges[j][i]){
                    throw new IllegalArgumentException("Not symmetric.");
                }
                if(redEdges[i][j]<0 || blueEdges[i][j]<0){
                    throw new IllegalArgumentException(
                            "Edge numbers must be positive");
                }
            }
        }
        redGraph = redEdges;
        blueGraph = blueEdges;
        calculateHeights();
        cleanUpDetached();
    }
    
    /**
     * Calculates the height (the length of the shortest path to the ground) of
     * each node in the Hackenbush. If the node is not connected to the ground,
     * its height will be size + 1.
     * Updates nodeHeights and nodesByHeight.
     */
    private void calculateHeights(){
        /*
        Peforms a breadth-first search of the graph, starting at the root
        to calculate the height of each node. It is stored both in nodeHeights,
        an array of integers, and in nodesByHeight, an array of lists.
        */
        nodeHeights = new int[size];
        for(int i = 0; i<size; i++){
            nodeHeights[i] = size + 1;
            nodesByHeight[i] = new LinkedList<>();
        }
        int currentHeight;
        LinkedList<Integer> nextNodes = new LinkedList();
        nextNodes.add(0);
        nodeHeights[0] = 0;
        nodesByHeight[0].add(0);
        while(nextNodes.size() > 0){
            int currentNode = nextNodes.removeFirst();
            currentHeight = nodeHeights[currentNode];
            if(currentHeight > maxHeight){
                maxHeight = currentHeight;
            }
            for(int i = currentNode; i < size; i++){
                if((redGraph[currentNode][i]>0 || blueGraph[currentNode][i]>0) 
                        && nodeHeights[i] == size + 1){
                    nextNodes.add(i);
                    nodeHeights[i] = currentHeight + 1;
                    nodesByHeight[currentHeight+1].add(i);
                }
            }
        }
    }
    
    /**
     * Removes all edges that are not connected to the ground.
     * Updates nodeHeights, nodesByHeight, redEdges, and blueEdges.
     * @return The number of red edges and blue edges lost.
     */
    private int[] cleanUpDetached(){
        calculateHeights();
        int redEdgesLost = 0;
        int blueEdgesLost = 0;
        for(int i = 0; i<size; i++){
            if(nodeHeights[i] == size + 1){
                for(int j = 0; j<size; j++){
                    redEdgesLost += redGraph[i][j];
                    redGraph[i][j] = 0;
                    redGraph[j][i] = 0;
                    blueEdgesLost += blueGraph[i][j];
                    blueGraph[i][j] = 0;
                    blueGraph[j][i] = 0;
                }
            }
        }
        return new int[]{redEdgesLost, blueEdgesLost};
    };
    
    /**
     * Creates a shallow copy of a two-dimensional integer array.
     * @param matrix The matrix to be copied.
     * @return A shallow copy of the matrix.
     */
    protected int[][] cloneMatrix(int[][] matrix){
        int height = matrix.length;
        int[][] newMatrix = new int[height][];
        for(int i = 0; i < height; i++){
            newMatrix[i] = matrix[i].clone();
        }
        return newMatrix;
    }
    
    /**
     * Keeps track of whether the game has been won yet.
     * @param lastMove
     * @return 
     */
    public String gameState(char lastMove){
        boolean anyRed = false;
        boolean anyBlue = false;
        for(int i=0; i<size; i++){
            for(int j = i; j<size; j++){
                if(redGraph[i][j]>0){
                    anyRed = true;
                }
                if(blueGraph[i][j]>0){
                    anyBlue = true;
                }
                if(anyRed && anyBlue){
                    return "The game is not yet won.";
                }
            }
        }
        if(anyRed){
            return "Red has won.";
        }
        if(anyBlue || lastMove == 'b'){
            return "Blue has won.";
        }
        return "Red has won";
    }
    
    /**
     * Produces a shallow copy of one of the adjacency matrices for the
     * current Hackenbush game.
     * @param color The character 'b' for Blue or 'r' for Red.
     * @return The red edges, blue edges, or null if a different color.
     */
    public int[][] getEdges(char color){
        if(color == 'b'){
            return cloneMatrix(blueGraph);
        }
        else if(color == 'r'){
            return cloneMatrix(redGraph);
        }
        return null;
    }
    
    /**
     * Returns an array of the current heights of each node, where height is
     * the length of the shortest path from the node to the ground, or (size + 
     * 1) if no path exists.
     * @return Array of heights.
     */
    public int[] getHeights(){
        return nodeHeights;
    }
    
    /**
     * Creates and returns an array of pairs of integers, to be used for 
     * graphing the current Hackenbush.
     * @param canvasWidth An integer number of pixels.
     * @param canvasHeight An integer number of pixels.
     * @return A size by 2 array of integers, specifying coordinates for 
     * graphing.
     */
    public int[][] getNodeCoords(int canvasWidth, int canvasHeight){
        int[][] nodeCoords = new int[size][2];
        if(size == 1){
            nodeCoords[0][0] = canvasWidth/2;
            nodeCoords[0][1] = canvasHeight;
            return nodeCoords;
        }
        int nodesAtHeightI;
        double xCoord;
        double yCoord;
        for(int i = 0; i<=maxHeight; i++){
            nodesAtHeightI = nodesByHeight[i].size();
            yCoord = canvasHeight - canvasHeight*i/(1.5*maxHeight);
            for(int j = 0; j<nodesAtHeightI; j++){
                xCoord = canvasWidth * ((j + 0.5) / (nodesAtHeightI));
                nodeCoords[nodesByHeight[i].get(j)][0] = (int) xCoord;
                nodeCoords[nodesByHeight[i].get(j)][1] = (int) yCoord;
            }
        }
        return nodeCoords;
    }
    
    /**
     * Returns the number of nodes in the current Hackenbush (connected and
     * disconnected).
     * @return A nonnegative integer number of nodes.
     */
    public int getSize(){
        return size;
    }
    
    /**
     * Evaluates the current graph (the union of red and blue edges) 
     * to see if it consists of a rooted tree. Here a tree is a connected graph,
     * including the vertex at index 0, which contains no cycles. Singleton
     * vertices are allowed to be disconnected.
     * @return "true" if the graph is a tree, "false" otherwise
     */
    public boolean isTree(){
        /*
        Peforms a breadth-first search of the graph to determine that each node
        is hit at most once, ensuring there are no cycles in the graph.
        */
        if(size == 0){
            return false;
        }
        boolean[] nodesHit = new boolean[size];
        LinkedList<Integer> nextNodes = new LinkedList();
        nextNodes.add(0);
        nodesHit[0] = true;
        while(nextNodes.size() > 0){
            int currentNode = nextNodes.removeFirst();
            for(int i = currentNode; i < size; i++){
                if((redGraph[currentNode][i] + 
                        blueGraph[currentNode][i] == 1)){
                    if(!nodesHit[i]) {
                        nextNodes.add(i);
                        nodesHit[i] = true;
                    }
                    else return false;
                }
                else if((redGraph[currentNode][i] + 
                        blueGraph[currentNode][i] > 1)){
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Allows an attempted move by the player of the associated color at one of
     * the edges between vertex i and vertex j. Returns a message about the 
     * number of edges removed. If no edge of the correct color exists at this 
     * location, or edges have not been initialized, an appropriate error 
     * message is returned.
     * @param color 'r' for red or 'b' for blue
     * @param i An integer between 0 and (size - 1) inclusive, representing a 
     * vertex of the Hackenbush graph.
     * @param j An integer between 0 and (size - 1) inclusive, representing a 
     * vertex of the Hackenbush graph.
     * @return A message about the number of edges removed or an error message
     * if invalid input was received.
     */
    public String move(char color, int i, int j){
        if(redGraph == null || blueGraph == null){
            return "The Hackenbush has not been appropriatedly initialized.";
        }
        int redEdgesLost = 0;
        int blueEdgesLost = 0;
        if(color == 'r'){
            if(redGraph[i][j] == 0){
                return "Not a valid move.";
            }
            redEdgesLost += 1;
            redGraph[i][j] -= 1;
            if(i != j){
                redGraph[j][i] -= 1;
            }
        }
        else if(color == 'b'){
            if(blueGraph[i][j] == 0){
                return "Not a valid move.";
            }
            blueEdgesLost += 1;
            blueGraph[i][j] -= 1;
            if(i != j){
                blueGraph[j][i] -= 1;
            }
        }
        int[] edgesLost = cleanUpDetached();
        redEdgesLost += edgesLost[0];
        blueEdgesLost += edgesLost[1];
        return "This move removed " + redEdgesLost + " red sticks and " + 
                blueEdgesLost + " blue sticks. " + gameState(color);
    };
}
