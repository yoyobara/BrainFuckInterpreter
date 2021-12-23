import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class Test {
    public static void main(String[] args) throws IOException, Engine1.NoInputException {
        File f = new File("out.txt");
        Engine1 en = new Engine1("+++++ +++++ [>+++++ +++++ < -]>.", System.in, null);
        en.interpret();
    }
}
