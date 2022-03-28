package BrainfuckInterpreter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class StringEngine {
	private String output = "";
	private final char[] input;
	private int inputPtr = 0;
	
	private Engine en;

	public StringEngine(String script, String input_str) {
		this.input = input_str.toCharArray();

		PrintStream out = new PrintStream(new OutputStream() {
			@Override
			public void write(int i) {
				output += (char) i;
			}
		});

		InputStream in = new InputStream() {
			@Override
			public int read() {
				if (inputPtr == input.length) return -1;
				return input[inputPtr++];
			}
		};

		this.en = new Engine(script, in, out);
	}
	
	public String interpret() throws IOException, Engine.NoInputException{
		this.en.interpret();
		return this.output;
	}
}
