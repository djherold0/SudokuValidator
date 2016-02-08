package sudokuvalidator;

public class SudokuValidator {

    public static void main(String[] args) {
        Validator results = new Validator(args[0]);
        if (results.validatePuzzle()) {
            System.out.println("This solution is valid... :)");
        } else {
            System.out.println("This solution is invalid... :(");
        }
        System.exit(0);
    }
}
