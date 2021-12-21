import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException, Engine1.NoInputException {
        Engine1 en = new Engine1("+++++ +++++ [>+++++ +++++ < -]>.", System.in, System.out);
        en.interpret();
    }
}
