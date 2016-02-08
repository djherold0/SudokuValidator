/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sudokuvalidator;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * This will be a thread that checks the validity of rows in the puzzle
 * @author Dan Herold  
 * @version 2015.10.26
 */
public class ColumnValidator implements Callable {

    //the numbers seen already
    private ArrayList<Integer> nums;
    //the column
    private int[] col;
    //shared space to indicate vslid/invalid row
    int[] completion;
    //row number to write to completion array
    private int colNum;

    /**
     * Thread that validates a column of a sudoku puzzle
     * @param col the column of the puzzle to validate
     * @param completion the results of validations
     * @param colNum the spot to place results
     */
    public ColumnValidator(int[] col) {
        nums = new ArrayList();
        this.col = col;
//        this.colNum = colNum;
//        this.completion = completion;
    }

    /**
     * validate the column and place the results in the completion array
     */
    @Override
    public Integer call() {
        //go through the row
        for (int i = 0; i < col.length; i++) {
            //check whether the current number in the column has already been seen
            //or if the number is not 1-9
            if (nums.contains(col[i]) || col[i] < 1 || col[i] > 9) {
                //solution is not valid
//                synchronized(completion){
//                    completion[colNum] = 0;
//                }
//                completion[colNum] = 0;
                return 0;
            }
            //add numbers to the list of numbers in the arraylist
            nums.add(col[i]);
        }
        //if this code is reached, then there are no repeating numbers in the row
        //and all the numbers are 1-9, the column is valid
//        synchronized(completion){
//            completion[colNum] = 1;
//        }
//        completion[colNum] = 1;
        return 1;
    }
}
