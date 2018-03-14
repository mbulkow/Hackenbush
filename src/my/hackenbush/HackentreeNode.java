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
import my.combinatorialgame.GameValue;

/**
 * The class HackentreeNode recursively defines a tree in Red-Blue Hackenbush
 * by holding a vertex's identifying data, the color of the stem used to reach 
 * that vertex, and lists of its red and blue children.
 * 
 * It also contains a field for, and methods to obtain, the value of the game 
 * represented by the tree.
 * 
 * @author Madeleine Bulkow
 * @param <T> The type of data held in each node.
 */
public class HackentreeNode<T> {
    private T data;
    private HackentreeNode<T> parent;
    private char stemColor;
    private LinkedList<HackentreeNode<T>> redChildren;
    private LinkedList<HackentreeNode<T>> blueChildren;
    private GameValue gameValue;
    
    /**
     * Constructor for root node
     * 
     * @param data Identifying information about the node.
     */
    public HackentreeNode(T data){
        this.data = data;
        this.stemColor = 'e';
    }
    
    /**
     * Constructor for non-root node
     * 
     * @param data Identifying information about the node.
     * @param parent A HackentreeNode<T> object representing the current node's
     * parent (i.e. the node directly supporting it)
     * @param stemColor A character representing the color of the stem: 'r' for 
     * red or 'b' for blue.
     */
    public HackentreeNode(T data, HackentreeNode<T> parent, char stemColor){
        this(data);
        this.parent = parent;
        this.stemColor = stemColor;
    }
    
    /**
     * Constructs and adds a child to the current node's either red or blue 
     * children.
     * @param childData Identifying information about the child node.
     * @param color A character representing the connection between the current
     * node and the child: 'r' for red or 'b' for blue.
     * @return The child we just added.
     */
    public HackentreeNode<T> addChild(T childData, char color){
        HackentreeNode<T> child = new HackentreeNode<>(childData, this, color);
        if(color == 'r'){
            if(redChildren == null){
                redChildren = new LinkedList<>();
            }
            redChildren.add(child);
        }
        else{
            if(blueChildren == null){
                blueChildren = new LinkedList<>();
            }
            blueChildren.add(child);
        }
        return child;
    }
    
    /**
     * Returns the data contained in the current node.
     * @return the data...
     */
    public T getData(){
        return data;
    }
    
    /**
     * Returns a list of children connected by the specified color.
     * @param color A character representing which set of children you want
     * to retrieve: 'r' for red or 'b' for blue.
     * @return A list of children.
     */
    public LinkedList<HackentreeNode<T>> listChildren(char color){
        if(color == 'r'){
            return redChildren;
        }
        else{
            return blueChildren;
        }
    }
    
    /**
     * Finds the value of the game represented, consisting of the game rooted at
     * the node and the stem it sits on.
     * @return A GameValue for the represented tree.
     */
    private GameValue findGameValue(){
        if(gameValue == null){
            gameValue = new GameValue(0,1);
        }
        if(redChildren == null && blueChildren == null){
            if(stemColor == 'r') gameValue.update(-1,1);
            else if(stemColor == 'b') gameValue.update(1,1);
            else{
                gameValue.update(0,1);
            }
        }
        else{
            GameValue childrenValue = new GameValue(getChildValues());
            if(stemColor == 'r'){
                int n = 1;
                while(childrenValue.getNum() >= (n - 1)*childrenValue.getDen()){
                    n++;
                }
                long newDen = (long) Math.pow(2, n-1);
                long newNum = childrenValue.getNum() - n * childrenValue.getDen();
                newDen *= childrenValue.getDen();
                gameValue.update(newNum, newDen);
            }
            else if(stemColor == 'b'){
                int n = 1;
                while(childrenValue.getNum() <= (1 - n)*childrenValue.getDen()){
                    n++;
                }
                long newDen = (long) myTwoPow(n - 1);
                long newNum = childrenValue.getNum() + n * childrenValue.getDen();
                newDen *= childrenValue.getDen();
                gameValue.update(newNum, newDen);
            }
            else{
                gameValue = childrenValue;
            }
        }
        return gameValue;
    }
    
    /**
     * Finds the value of each game rooted at the node.
     * @return A list of GameValues, corresponding to each child game.
     */
    public LinkedList<GameValue> getChildValues(){
        LinkedList<GameValue> values = new LinkedList<>();
        if(redChildren != null){
            for(HackentreeNode<T> redChild: redChildren){
                values.add(redChild.findGameValue());
            }
        }
        if(blueChildren != null){
            for(HackentreeNode<T> blueChild: blueChildren){
                values.add(blueChild.findGameValue());
            }
        }
        return values;
    }
    
    /**
     * Returns the color of the stem the node sits on: 'r' for red, 'b' for 
     * blue, or 'e' for empty (in case of a root node).
     * @return Character corresponding to stem color supporting the current node.
     */
    public char getStemColor(){
        return stemColor;
    }
    
    /**
     * Returns the parent of the current node, if it exists.
     * @return HackentreeNode<T> or null
     */
    public HackentreeNode<T> getParent(){
        return parent;
    }
    
    /**
     * A utility function to quickly take powers of two.
     * @param n A nonnegative integer to which to raise two.
     * @return A long integer, two raised to the nth power.
     */
    private long myTwoPow(int n){
        if(n == 0){
            return 1;
        }
        else if(n == 1){
            return 2;
        }
        else{
            int k = n/2;
            int r = n - 2*k;
            long bigPow = myTwoPow(k);
            long littlePow = myTwoPow(r);
            return bigPow*bigPow*littlePow;
        }
    }
    
    /**
     * Finds the current value of the tree and returns it in the form of a 
     * string.
     * @return A string containing the current value of the tree.
     */
    public String printGameValue(){
        findGameValue();
        return gameValue.printValue();
    }
}
