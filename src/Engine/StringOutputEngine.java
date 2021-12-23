package Engine;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringOutputEngine extends Engine{

    BufferedInputStream input = null;
    String strInput;

    String output = "";

    public StringOutputEngine(String script, InputStream input) {
        super(script);
        this.input = new BufferedInputStream(input);
        
    }

    protected StringOutputEngine(String script, String input){
        super(script);
        this.strInput = input;
    }

    @Override
    protected void getchar() throws NoInputException, IOException {
        if (input == null){
            // string input
            if (strInput.isEmpty())
                throw new NoInputException("no more input!");

            char next = strInput.charAt(0);
            strInput = strInput.substring(1);

            cells[cellPtr] = next;

        } else {
            // inputStream input
            int read = input.read();
            if (read == -1) 
                throw new NoInputException("no more input!");

            cells[cellPtr] = read;
        }
    }

    @Override
    protected void putchar() {
        output += String.valueOf(getCurrentCell());
    }

    public String interpret() throws IOException, NoInputException {
        super._interpret();
        return output;
    }

}
