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
public class RowValidator implements Callable {

    //the numbers seen already
    private ArrayList<Integer> nums;
    //the row
    private int[] row;

    /**
     * Thread that validates a row of a sudoku puzzle
     * @param row the row of the puzzle to validate
     * @param completion the results of validations
     * @param rowNum the spot to place results
     */
    public RowValidator(int[] row) {
        nums = new ArrayList();
        this.row = row;
    }

    /**
     * validate the row and place the results in the completion array
     */
    @Override
    public Integer call() {
        //go through the row
        for (int i = 0; i < row.length; i++) {
            //check whether the current number in the row has already been seen
            //or if the number is not 1-9
            if (nums.contains(row[i]) || row[i] < 1 || row[i] > 9) {
                //solution is not valid
                return 0;
            }
            //add numbers to the list of numbers in the arraylist
            nums.add(row[i]);
        }
        //if this code is reached, then there are no repeating numbers in the row
        //and all the numbers are 1-9, the row is valid
        return 1;
    }
}
