import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Engine - interprets the brainfuck itself.
 * can be used by many tools involving brainfuck.
 */
public class Engine {

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

    public static void solve(String script, BufferedInputStream in, OutputStream out){
        solve(script.toCharArray(), in, out);
    }

    public static void solve(char[] script, BufferedInputStream in, OutputStream out){
        // check script's interpretability
        if (!checkScript(script)){
            System.err.println("the script is not interpretable.");
            System.exit(5);
        }

        Integer scriptPtr = 0;

        // cells
        int[] cells = new int[N_CELLS];
        Integer cellPtr = 0;

        while(scriptPtr != script.length){
            switch (getCurrentCmd(scriptPtr, script)) {
                case INCREASE:
                    increase(cellPtr, cells);
                    break;
                
                case DECREASE:
                    decrease(cellPtr, cells);
                    break;
                
                case NEXT:
                    nextCell(cellPtr);
                    break;

                case PREVIOUS:
                    previousCell(cellPtr);
                    break;

                case OPEN_LOOP:
                    startloop(cellPtr, cells, scriptPtr, script);
                    break;
                
                case CLOSE_LOOP:
                    endloop(cellPtr, cells, scriptPtr, script);
                    break;

                case PRINT:
                    putchar(cellPtr, cells, out);
                
                case GET:
                    getchar(cellPtr, cells, in);
            }
            
            scriptPtr++;
        }
    }

    /**
     * returns the value of the current pointed cell.
     * 
     * @param cellPtr the cells pointer
     * @param cells the cells array
     */
    private static int getCurrentCell(Integer cellPtr, int[] cells){
        return cells[cellPtr];
    }

    /**
     * returns the current command to run.
     * 
     * @param scriptPtr the script pointer
     * @param script the script
     */
    private static char getCurrentCmd(Integer scriptPtr, char[] script){
        return script[scriptPtr];
    }

    /**
     * moves the cell pointer one cell to the right
     * 
     * @param cellPtr the pointer itself
     */
    private static void nextCell(Integer cellPtr){
        if (cellPtr == N_CELLS)
            cellPtr = 0;
        else
            cellPtr++;
    }

    /**
     * moves the cell pointer one cell to the left
     * 
     * @param cellPtr the pointer itself
     */
    private static void previousCell(Integer cellPtr){
        if (cellPtr == 0)
            cellPtr = N_CELLS;
        else
            cellPtr--;
    }

    /**
     * increases the value of the current pointed cell by one.
     * 
     * @param cellPtr the cells pointer
     * @param cells the cells array
     */
    private static void increase(Integer cellPtr, int[] cells){
        cells[cellPtr]++;
    }

    /**
     * decreases the value of the current pointed cell by one.
     * 
     * @param cellPtr the cells pointer
     * @param cells the cells array
     */
    private static void decrease(Integer cellPtr, int[] cells){
        cells[cellPtr]--;
    }

    /**
     * call when at a starting of a loop.
     * make sure you call it exactly when the script pointer is on the '['.
     * 
     * if the current cell is zero, it will skip to after the corresponding end of the loop
     * elsewhere it will enter the loop.
     * 
     * @param cellPtr the cells pointer
     * @param cells the cells array
     * @param scriptPtr the script pointer
     * @param script the script
     */
    private static void startloop(Integer cellPtr, int[] cells, Integer scriptPtr, char[] script){
        // if the current cell is 0, jump to the command after the corresponding close.
        if (getCurrentCell(cellPtr, cells) == 0){
            findLoopBound(cellPtr, cells, scriptPtr, script);
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
     * 
     * @param cellPtr the cells pointer
     * @param cells the cells array
     * @param scriptPtr the script pointer
     * @param script the script
     */
    private static void endloop(Integer cellPtr, int[] cells, Integer scriptPtr, char[] script){
        // if the current cell is 0, exit the loop and go to next command.
        if (getCurrentCell(cellPtr, cells) == 0){
            scriptPtr++;
            return;
        }

        // elsewhere come back to the first command in the loop
        findLoopBound(cellPtr, cells, scriptPtr, script);
        scriptPtr++;        
    }

    /**
     * finds the other bound of a loop.
     * call when the script pointer is on the '[' or a ']' of a loop.
     * 
     * the method will finish when the script pointer is on the corresponding bound
     * 
     * @param cellPtr the cells pointer
     * @param cells the cells array
     * @param scriptPtr the script pointer
     * @param script the script
     */
    private static void findLoopBound(Integer cellPtr, int[] cells, Integer scriptPtr, char[] script){
        int direction;
        char finish;
        char begin;

        // determine whether to search for a startloop or an endloop.
        switch (getCurrentCmd(scriptPtr, script)){
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

            char currentCmd = getCurrentCmd(scriptPtr, script);
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
     * 
     * @param cellPtr the cells pointer
     * @param cells the cells array
     * @param in the input stream to take the input from
     */
    private static void getchar(Integer cellPtr, int[] cells, BufferedInputStream in){
        try {
            int n = in.read();
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
     * 
     * @param cellPtr the cells pointer
     * @param cells the cells array
     * @param out the output stream used for the output
     */
    private static void putchar(Integer cellPtr, int[] cells, OutputStream out){
        try {
            out.write(getCurrentCell(cellPtr, cells));
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

    /**
     * check if the script is interpretable
     * by making sure every '[' has a matching ']'.
     * 
     * @param script the script charArray.
     * @return whether the script is interpretable or not.
     */
    public static boolean checkScript(char[] script){
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
}