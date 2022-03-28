package BrainfuckInterpreter;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.IOException;

public class BfInterpreter {
	public static void bfInterpret(String script, InputStream in, PrintStream out) throws Engine.NoInputException, IOException{
		Engine en = new Engine(script, in, out);
		en.interpret();
	}

	public static String bfInterpret(String script, String input) throws Engine.NoInputException, IOException{
		StringEngine en = new StringEngine(script, input);
		return en.interpret();
	}
}
