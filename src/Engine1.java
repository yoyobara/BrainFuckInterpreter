import java.io.*;

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
    private PrintStream output;

    // the inputStream to get the input from.
    // buffered inputstream is used so we can read from it one character at a time.
    private BufferedInputStream input;

    /**
     * constructs the interpreter engine object.
     * 
     * @param script the script to be run
     * @param in the input stream to take input from
     * @param out the print stream for the program's output. 
     */
    public Engine1(String script, InputStream in, PrintStream out){
        this.output = out;
        this.input = new BufferedInputStream(in);

        this.cells = new int[N_CELLS];
        this.cellPtr = 0;

        this.script = script.toCharArray();
        this.scriptPtr = 0;
    }

    /**
     * interprets the script given in the constructor.
     * 
     * output goes to the given outputStream.
     * input comes from the given InputStream. 
     * 
     * in case the script is not interpretable, exits with code 5.
     * 
     * @throws NoInputException no more input, and the program's trying to get more.
     * @throws IOException problems with input and output
     */
    public void interpret() throws IOException, NoInputException{
        // check script's interpretability
        if (!checkScript()){
            System.err.println("the script is not interpretable.");
            System.exit(5);
        }

        while(scriptPtr != script.length){
            System.out.println(cells[0] + " " + cells[1]);
            switch (getCurrentCmd()) {
                case INCREASE:
                    increase();
                    break;
                
                case DECREASE:
                    decrease();
                    break;
                
                case NEXT:
                    nextCell();
                    break;

                case PREVIOUS:
                    previousCell();
                    break;

                case OPEN_LOOP:
                    startloop();
                    break;
                
                case CLOSE_LOOP:
                    endloop();
                    break;

                case PRINT:
                    putchar();
                
                case GET:
                    getchar();
            }
            
            scriptPtr++;
        }
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
        if (cellPtr == N_CELLS - 1)
            cellPtr = 0;
        else
            cellPtr++;
    }

    /**
     * moves the cell pointer one cell to the left
     */
    private void previousCell(){
        if (cellPtr == 0)
            cellPtr = N_CELLS - 1;
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
        }
        // elsewhere enter the loop
    }

    /**
     * call when at an ending of a loop.
     * make sure you call it exactly when the script pointer is on the ']'.
     * 
     * if the current cell is zero, it will exit the loop and continue to the command after it
     * elsewhere it will return to the first command inside its loop.
     */
    private void endloop() {
        // if the current cell is 0, exit the loop and go to next command.
        if (getCurrentCell() == 0){
            return;
        }

        // elsewhere come back to the first command in the loop
        findLoopBound();
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
                break;
            
            default:
                throw new RuntimeException("called findLoopBound when not on the bound of a loop.");
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
     * @throws IOException problems with input and output
     * @throws NoInputException no more input, and the program's trying to get more.
     */
    private void getchar() throws NoInputException, IOException{
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
     * @throws IOException problems with input and output
     */
    private void putchar() throws IOException{
        output.println((char) getCurrentCell());
    }

    /**
     * Exception for the case when the program asks for input 
     * and the input has reached its end.
     */
    public static class NoInputException extends Exception{
        public NoInputException(String errorMessage) {
            super(errorMessage);
        }
    }
}
