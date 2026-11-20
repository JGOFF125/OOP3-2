package parser;

public class ParseError {
	private final int lineNumber;
	private final char bracket;
	

	public ParseError(int lineNumber, char bracket) {
		this.lineNumber = lineNumber;
		this.bracket = bracket;
	}
	
	public int getLineNumber() { return lineNumber; }
    public char getBracket()   { return bracket; }

    @Override
    public String toString()
    {
        return "Line " + lineNumber + ": Syntax error on \"" + bracket + "\".";
    }
}
