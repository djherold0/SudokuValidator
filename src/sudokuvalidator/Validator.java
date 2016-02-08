/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sudokuvalidator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * does all the hard work so the main class looks better
 *
 * @author Dan Herold
 * @version 2015.10.28
 */
public class Validator {

    //the sudoku puzzle
    private int[][] puzzle;
    //thread dispatcher
    private ExecutorService exec;
    //results of validations
    private int[] columns, rows, boxes;
    //lists of futures to get return values from Callables
    private ArrayList<Future<Integer>> colFutures, rowFutures, boxFutures;
    private BufferedReader read;

    public Validator(int[][] puzzle) {
        this.puzzle = puzzle;
        exec = Executors.newFixedThreadPool(15);
        columns = new int[9];
        rows = new int[9];
        boxes = new int[9];
        rowFutures = new ArrayList();
        colFutures = new ArrayList();
        boxFutures = new ArrayList();
    }

    public Validator(String fileName) {
        exec = Executors.newFixedThreadPool(15);
        puzzle = new int[9][9];
        columns = new int[9];
        rows = new int[9];
        boxes = new int[9];
        rowFutures = new ArrayList();
        colFutures = new ArrayList();
        boxFutures = new ArrayList();
        try {
            read = new BufferedReader(new FileReader(new File(fileName)));
            readInPuzzle();
        } catch (IOException ie) {
            System.err.println("There was an IO error during sudoku validator creation....");
        }
    }

    private void readInPuzzle() {
        try {
            String str = "";
            int helper = 0;
            while ((str = read.readLine()) != null) {
                String[] foo = str.split(" ");
                for (int i = 0; i < 9; i++) {
                    puzzle[helper][i] = Integer.parseInt(foo[i]);
                }
                helper++;
            }
            read.close();
        } catch (IOException ie) {
            System.err.println("There was an IO error during sudoku validator reading....");
        }
    }

    /**
     * Validate the sudoku puzzle solution
     *
     * @return true if the solution is valid, false if the solution is invalid
     */
    public boolean validatePuzzle() {
        //actually perform the validations
        performValidations();
        //check results
        if (checkRows()) {
            if (checkColumns()) {
                if (checkBoxes()) {
                    return true;
                }
                System.out.println("Box invalid");
                return false;
            }
            System.out.println("Column invalid");
            return false;
        }
        //something was invalid somewhere
        System.out.println("Row invalid");
        return false;
    }

    /**
     * Check results of row validations
     *
     * @return true if valid, false if invalid
     */
    private boolean checkRows() {
        for (int i = 0; i < rows.length; i++) {
            //0 = invalid
            if (rows[i] == 0) {
                System.out.println("Invalid row = " + i);
                return false;
            }
        }
        return true;
    }

    /**
     * Check results of column validations
     *
     * @return true if valid, false if invalid
     */
    private boolean checkColumns() {
        for (int i = 0; i < columns.length; i++) {
            //0 = invalid
            if (columns[i] == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check results of box validations
     *
     * @return true if valid, false if invalid
     */
    private boolean checkBoxes() {
        //check result for each box
        for (int i = 0; i < boxes.length; i++) {
            //if box[i] is 0, box is invalid
            if (boxes[i] == 0) {
                return false;
            }
        }
        //all entries were 1's
        return true;
    }

    /**
     * Get results of thread executions into appropriate arrays
     */
    private void performValidations() {
        validateRows();
        validateColumns();
        validateBoxes();
        //get return results into result arrays
        Iterator<Future<Integer>> it = colFutures.iterator();
        int i = 0;
        while (it.hasNext()) {
            try {
                //increment i after use
                columns[i++] = it.next().get();
            } catch (InterruptedException e) {
                System.out.println("Interrupetion in thread exectution for column " + i);
                e.printStackTrace();
            } catch (ExecutionException ee) {
                System.out.println("ExecutionException in thread exectution for column " + i);
                ee.printStackTrace();
            }
        }
        i = 0;
        it = rowFutures.iterator();
        //get return results into result arrays
        while (it.hasNext()) {
            try {
                rows[i++] = it.next().get();
            } catch (InterruptedException e) {
                System.out.println("Interrupetion in thread exectution for row " + i);
                e.printStackTrace();
            } catch (ExecutionException ee) {
                System.out.println("ExecutionException in thread exectution for row " + i);
                ee.printStackTrace();
            }
        }
        //get return results into result arrays
        it = boxFutures.iterator();
        i = 0;
        while (it.hasNext()) {
            try {
                boxes[i++] = it.next().get();
            } catch (InterruptedException e) {
                System.out.println("Interrupetion in thread exectution for box " + i);
                e.printStackTrace();
            } catch (ExecutionException ee) {
                System.out.println("ExecutionException in thread exectution for box " + i);
                ee.printStackTrace();
            }
        }
    }

    /**
     * Submit RowValidator threads to exec and add the futures to the list of
     * futures
     */
    private void validateRows() {
        for (int i = 0; i < 9; i++) {
            //create a thread to validate the row and submit it to the executor service
            //add the future returned by this to the futures list
            rowFutures.add(exec.submit(new RowValidator(puzzle[i])));
        }
    }

    /**
     * Submit ColumnValidator threads to exec and add the futures to the list of
     * futures
     */
    private void validateColumns() {
        for (int i = 0; i < 9; i++) {
            //place columns to validate in this array
            int[] tempCol = new int[9];
            for (int j = 0; j < 9; j++) {
                tempCol[j] = puzzle[j][i];
            }
            colFutures.add(exec.submit(new ColumnValidator(tempCol)));
        }
    }

    /**
     * Submit BoxValidator threads to exec and add the futures to the list of
     * futures
     */
    private void validateBoxes() {
        int[][] box1 = {
            {puzzle[0][0], puzzle[0][1], puzzle[0][2]},
            {puzzle[1][0], puzzle[1][1], puzzle[1][2]},
            {puzzle[2][0], puzzle[2][1], puzzle[2][2]}
        };
        int[][] box2 = {
            {puzzle[0][3], puzzle[0][4], puzzle[0][5]},
            {puzzle[1][3], puzzle[1][4], puzzle[1][5]},
            {puzzle[2][3], puzzle[2][4], puzzle[2][5]}
        };
        int[][] box3 = {
            {puzzle[0][6], puzzle[0][7], puzzle[0][8]},
            {puzzle[1][6], puzzle[1][7], puzzle[1][8]},
            {puzzle[2][6], puzzle[2][7], puzzle[2][8]}
        };
        int[][] box4 = {
            {puzzle[3][0], puzzle[3][1], puzzle[3][2]},
            {puzzle[4][0], puzzle[4][1], puzzle[4][2]},
            {puzzle[5][0], puzzle[5][1], puzzle[5][2]}
        };
        int[][] box5 = {
            {puzzle[3][3], puzzle[3][4], puzzle[3][5]},
            {puzzle[4][3], puzzle[4][4], puzzle[4][5]},
            {puzzle[5][3], puzzle[5][4], puzzle[5][5]}
        };
        int[][] box6 = {
            {puzzle[3][6], puzzle[3][7], puzzle[3][8]},
            {puzzle[4][6], puzzle[4][7], puzzle[4][8]},
            {puzzle[5][6], puzzle[5][7], puzzle[5][8]}
        };
        int[][] box7 = {
            {puzzle[6][0], puzzle[6][1], puzzle[6][2]},
            {puzzle[7][0], puzzle[7][1], puzzle[7][2]},
            {puzzle[8][0], puzzle[8][1], puzzle[8][2]}
        };
        int[][] box8 = {
            {puzzle[6][3], puzzle[6][4], puzzle[6][5]},
            {puzzle[7][3], puzzle[7][4], puzzle[7][5]},
            {puzzle[8][3], puzzle[8][4], puzzle[8][5]}
        };
        int[][] box9 = {
            {puzzle[6][6], puzzle[6][7], puzzle[6][8]},
            {puzzle[7][6], puzzle[7][7], puzzle[7][8]},
            {puzzle[8][6], puzzle[8][7], puzzle[8][8]}
        };
        int[][][] tempBoxes = {box1, box2, box3, box4, box5, box6, box7, box8, box9};
        for (int i = 0; i < tempBoxes.length; i++) {
            boxFutures.add(exec.submit(new BoxValidator(tempBoxes[i])));
        }
    }

    /**
     * toString()
     *
     * @return String with the validity of the puzzle solution as well as the
     * puzzle solution
     */
    @Override
    public String toString() {
        String str = "";
        if (validatePuzzle()) {
            str = "Valid solution: \r\n";
        } else {
            str = "Invalid solution: \r\n";
        }
        for (int i = 0; i < puzzle.length; i++) {
            for (int j = 0; j < puzzle[i].length; j++) {
                str += puzzle[i][j] + " ";
            }
            str += "\r\n";
        }
        return str;
    }
}
