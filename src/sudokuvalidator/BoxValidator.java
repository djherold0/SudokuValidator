/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sudokuvalidator;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * This will be a thread that checks the validity of one of the 3x3 boxes in the puzzle
 * @author Dan Herold
 * @version 2015.10.26
 */
public class BoxValidator implements Callable {

    //the numbers
    private int[][] box;
    //the numbers in the box
    private ArrayList<Integer> nums;

    /**
     * Thread that validates a box of a sudoku puzzle
     * @param box the 3x3 box to validate
     * @param completion the array of results
     * @param boxNum the place in completion to place results
     */
    public BoxValidator(int[][] box) {
        nums = new ArrayList();
        this.box = box;
    }

    @Override
    public Integer call() {
        //check each number
        for (int i = 0; i < box.length; i++) {
            for (int j = 0; j < box[i].length; j++) {
                //number has  been used already or is not 1-9
                //box is invalid
                if (nums.contains(box[i][j]) || box[i][j] < 1 || box[i][j] > 9) {
//                    synchronized(completion){
//                        completion[boxNum] = 0;
//                    }
                    return 0;
                }
                //add number to list of numbers
                nums.add(box[i][j]);
            }
        }
        //box is valid
//        synchronized(completion){
//            completion[boxNum] = 1;
//        }
        return 1;
    }
}
