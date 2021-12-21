import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class Engine1 {

    // available commands
    public static final char INCREASE = '+';
    public static final char DECREASE = '-';
    public static final char NEXT = '>';
    public static final char PREVIOUS = '<';
    public static final char OPEN_LOOP = '[';
    public static final char CLOSE_LOOP = ']';
    public static final char PRINT = '.';
    public static final char GET = ',';

    // number of cells available
    public static final int N_CELLS = 20;

    // the script characters array.
    private char[] script;

    // the script array pointer
    private int scriptPtr;

    // the cells array
    private int[] cells;
    
    // the cells pointer
    private int cellPtr;

    // the OutputStream to output to.
    private OutputStream output;

    // the inputStream to get the input from.
    // buffered inputstream is used so we can read from it one character at a time.
    private BufferedInputStream input;

    /**
     * constructs the interpreter engine object.
     * 
     * @param script the script to be run
     * @param in the input stream to take input from
     * @param out the output stream for the program's output. 
     */
    private Engine1(char[] script, InputStream in, OutputStream out){
        this.output = out;
        this.input = new BufferedInputStream(in);

        this.cells = new int[N_CELLS];
        this.cellPtr = 0;

        this.script = script;
        this.scriptPtr = 0;
    }

    /**
     * constructs the interpreter engine object.
     * (using String script)
     * 
     * @param script the script to be run
     * @param in the input stream to take input from
     * @param out the output stream for the program's output. 
     */
    private Engine1(String script, InputStream in, OutputStream out){
        this(script.toCharArray(), in, out);
    }

        /**
     * check if the script is interpretable
     * by making sure every '[' has a matching ']'.
     * 
     * @return whether the script is interpretable or not.
     */
    public boolean checkScript(){
        int ptr = 0;
        int loopCounter = 0;

        while (ptr < script.length){
            switch (script[ptr]){
                case OPEN_LOOP:
                    loopCounter++;
                    break;

                case CLOSE_LOOP:
                    loopCounter--;
                    break;
            }
            ptr++;
        }

        return loopCounter == 0;
    }

    /**
     * @return the value of the current pointed cell.
     */
    private int getCurrentCell(){
        return cells[cellPtr];
    }

    /**
     * @return the current command to run.
     */
    private char getCurrentCmd(){
        return script[scriptPtr];
    }

    /**
     * moves the cell pointer one cell to the right
     */
    private void nextCell(){
        if (cellPtr == N_CELLS)
            cellPtr = 0;
        else
            cellPtr++;
    }

    /**
     * moves the cell pointer one cell to the left
     */
    private void previousCell(){
        if (cellPtr == 0)
            cellPtr = N_CELLS;
        else
            cellPtr--;
    }

    /**
     * increases the value of the current pointed cell by one.
     */
    private void increase(){
        cells[cellPtr]++;
    }

    /**
     * decreases the value of the current pointed cell by one.
     */
    private void decrease(){
        cells[cellPtr]--;
    }

    /**
     * call when at a starting of a loop.
     * make sure you call it exactly when the script pointer is on the '['.
     * 
     * if the current cell is zero, it will skip to after the corresponding end of the loop
     * elsewhere it will enter the loop.
     */
    private void startloop(){
        // if the current cell is 0, jump to the command after the corresponding close.
        if (getCurrentCell() == 0){
            findLoopBound();
            return;
        }

        // elsewhere enter the loop
        scriptPtr++;
    }

    /**
     * call when at an ending of a loop.
     * make sure you call it exactly when the script pointer is on the ']'.
     * 
     * if the current cell is zero, it will exit the loop and continue to the command after it
     * elsewhere it will return to the first command inside its loop.
     */
    private void endloop(){
        // if the current cell is 0, exit the loop and go to next command.
        if (getCurrentCell() == 0){
            scriptPtr++;
            return;
        }

        // elsewhere come back to the first command in the loop
        findLoopBound();
        scriptPtr++;        
    }

    /**
     * finds the other bound of a loop.
     * call when the script pointer is on the '[' or a ']' of a loop.
     * 
     * the method will finish when the script pointer is on the corresponding bound
     */
    private void findLoopBound(){
        int direction;
        char finish;
        char begin;

        // determine whether to search for a startloop or an endloop.
        switch (getCurrentCmd()){
            case OPEN_LOOP:
                direction = 1;
                begin = OPEN_LOOP;
                finish = CLOSE_LOOP;
                break;
        
            case CLOSE_LOOP:
                direction = -1;
                begin = CLOSE_LOOP;
                finish = OPEN_LOOP;
            default:
                throw new NotOnALoopException("not on a loop bound. make sure you call this method when you are on a loop bound.");
        }

        int counter = 0;
        boolean reached = false;
        
        // search
        while (!reached){
            scriptPtr += direction;

            char currentCmd = getCurrentCmd();
                if (currentCmd == begin){
                    counter++;
                    break;
                }
                else if (currentCmd == finish){
                    if (counter == 0) {
                        reached = true;
                    } else {
                        counter--;
                    }
                }
            }
        }

        /**
     * gets the next character from the input buffer, puts it's numeric value
     * in the current pointed cell.
     * 
     * might throw a NoInputException in case when the input buffer has reached its end
     * and the program tries to read more input.
     * 
     * in case of an IOException it will be printed and exit with code 2.
     */
    private void getchar(){
        try {
            int n = input.read();
            if (n == -1) throw new NoInputException("no more input!");
            
            cells[cellPtr] = n;

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    /**
     * gets the next character from the input buffer, puts it's numeric value
     * in the current pointed cell.
     * 
     * in case of an IOException it will be printed and exit with code 2.
     */
    private void putchar(){
        try {
            output.write(getCurrentCell());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    /**
     * Exception for the case when the program asks for input 
     * and the input has reached its end.
     */
    public static class NoInputException extends RuntimeException{
        public NoInputException(String errorMessage) {
            super(errorMessage);
        }
    }

    /**
     * exception for the case when trying to find the other bound of a loop
     * and the pointer is not on the other bound.
     */
    public static class NotOnALoopException extends RuntimeException{
        public NotOnALoopException(String errorMessage) {
            super(errorMessage);
        }
    }
}
