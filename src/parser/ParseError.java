package parser;

/**
 * Represents one bracket error found by the parser.
 */
public class ParseError
{
    private final int lineNumber;   // line in the file where the bad bracket is
    private final char bracket;     // the bracket itself, e.g. ']'

    public ParseError( int lineNumber, char bracket )
    {
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