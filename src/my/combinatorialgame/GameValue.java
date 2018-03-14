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

package my.combinatorialgame;

/**
 * GameValue is a class which holds information about a rational, finite
 * value for a solved combinatorial game. It is used to keep track of rational 
 * numbers, and simplify them as necessary.
 * @author Madeleine Bulkow
 */
public class GameValue {
    private long num;
    private long den;
    
    /**
     * Initializes the GameValue object with the appropriate numerator
     * and denominator. 
     * @param num     the numerator of the game value
     * @param den     the denominator of the game value
     */
    public GameValue(long num, long den){
        this.num = num;
        this.den = den;
        simplify();
    }
    
    /**
     * Creates a new GameValue representing the sum of several combinatorial
     * games played simultaneously.
     * @param games     an array of GameValue objects
     */
    public GameValue(Iterable<GameValue> games){
        num = 0;
        den = 1;
        for(GameValue game: games){
            num = num * game.den + game.num * den;
            den *= game.den;
            simplify();
        }
    }
    
    /**
     * Returns the denominator of the current GameValue.
     * @return A positive integer, the denominator.
     */
    public long getDen(){
        return den;
    }
    
    /**
     * Returns the numerator of the current GameValue.
     * @return An integer, the numerator.
     */
    public long getNum(){
        return num;
    }
    
    /**
     * Simplifies the fraction numerator/denominator contained in GameValue.
     */
    public final void simplify(){
        long gcd = gcd(Math.abs(num), den);
        num /= gcd;
        den /= gcd;
    }
    
    /**
     * Finds the greatest common divisor of two positive integers.
     * @param x A nonnegative integer.
     * @param y A nonnegative integer.
     * @return The greatest common divisor of x and y.
     */
    public final long gcd(long x, long y){
        if(x >= y && y != 0){
            long r = x%y;
            if(r == 0){
                return y;
            }
            return gcd(y, r);
        }
        else if(y == 0){
            return x;
        }
        return gcd(y,x);
    }
    
    /**
     * Updates the current GameValue object to reflect a new value. Simplifies
     * the fraction if necessary.
     * @param num 
     * @param den 
     */
    public void update(long num, long den){
        if(den > 0){
            this.num = num;
            this.den = den;
        }
        else if(den < 0){
            this.num = -num;
            this.den = -den;
        }
        else{
            this.num = Integer.MAX_VALUE;
            if(num > 0){
                this.den = 1;
            }
            else{
                this.den = -1;
            }
        }
        simplify();
    }
    
    /**
     * Makes a string containing the current GameValue.
     * @return      A string containing "numerator/denominator".
     */
    public String printValue(){
        return Long.toString(num) + "/" + Long.toString(den);
    }
    
}
