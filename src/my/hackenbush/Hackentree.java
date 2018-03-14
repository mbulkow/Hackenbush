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

/**
 * A specific case of Hackenbush created under the assumption that the current
 * graph is a tree. Contains a data structure representing this tree,
 * and a method to print out the value of the current game.
 * @author Madeleine Bulkow
 */
public class Hackentree extends Hackenbush{
    
    public boolean isTree;
    private HackentreeNode<Integer> root;
    
    /**
     * Constructor
     * 
     * @param size A non-negative integer.
     * @param redEdges The entries of an adjacency matrix representing red
     * sticks.
     * @param blueEdges The entries of an adjacency matrix representing blue
     * sticks.
     */
    public Hackentree(int size, int[][] redEdges, int[][] blueEdges){
        super(size, redEdges, blueEdges);
        isTree = super.isTree();
        if(isTree){
            root = new HackentreeNode(0);
            generateSubtree(root);
        }
    }
    
    /**
     * Constructor
     * 
     * @param hackenbush A Hackenbush object.
     */
    public Hackentree(Hackenbush hackenbush){
        this(hackenbush.size,hackenbush.redGraph,hackenbush.blueGraph);
    }
    
    /**
     * Generates the part of the tree data structure representing all nodes
     * and edges rooted at the given element.
     * @param subtreeRoot 
     */
    private void generateSubtree(HackentreeNode<Integer> subtreeRoot){
        if(!isTree) return;
        int subtreeRootIndex = subtreeRoot.getData();
        HackentreeNode<Integer> currentChild;
        for(int i = 0; i<size; i++){
            if(redGraph[subtreeRootIndex][i] == 1 && 
                    nodeHeights[subtreeRootIndex] < nodeHeights[i]){
                currentChild = subtreeRoot.addChild(i,'r');
                generateSubtree(currentChild);
            }
            else if(blueGraph[subtreeRootIndex][i] == 1 && 
                    nodeHeights[subtreeRootIndex] < nodeHeights[i]){
                currentChild = subtreeRoot.addChild(i,'b');
                generateSubtree(currentChild);
            }
        }
    }
    
    /**
     * Returns information about the state of the current game, including the
     * game's value if applicable.
     * @param lastMove
     * @return 
     */
    @Override
    public String gameState(char lastMove){
        if(isTree){
            return root.printGameValue();
        }
        else{
            return super.gameState(lastMove);
        }
    }
}
