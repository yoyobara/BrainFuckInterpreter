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

    public static <InputStream> String solve(String script, InputStream in, OutputStream out){
        // script
        char[] scriptArr = script.toCharArray();
        Integer scriptPtr = 0;

        // cells
        int[] cells = new int[N_CELLS];
        Integer cellPtr = 0;

    }

    /**
     * moves the cell pointer one cell to the right
     * 
     * @param cellPtr the pointer itself
     */
    public static void nextCell(Integer cellPtr){
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
    public static void previousCell(Integer cellPtr){
        if (cellPtr == 0)
            cellPtr = N_CELLS;
        else
            cellPtr--;
    }
}