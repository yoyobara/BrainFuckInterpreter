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
        // script
        char[] scriptArr = script.toCharArray();

        if (!checkScript(scriptArr)){
            System.err.println("the script is not interpretable.");
            System.exit(5);
        }

        Integer scriptPtr = 0;

        // cells
        int[] cells = new int[N_CELLS];
        Integer cellPtr = 0;

        // brackets counter
        int loopCounter = 0;

        

    }

    /**
     * returns the value of the current pointed cell.
     * 
     * @param cellPtr the cells pointer
     * @param cells the cells array
     */
    private static int getCurrent(Integer cellPtr, int[] cells){
        return cells[cellPtr];
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
            out.write(getCurrent(cellPtr, cells));
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