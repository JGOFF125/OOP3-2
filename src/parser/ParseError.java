package parser;

/**
 * Represents one bracket error found by the {@link Parser}.
 * <p>
 * Each error records the line number where the problem bracket appears
 * and the bracket itself. Errors are created by
 * {@link Parser#finalEvaluation()} after parsing is complete, sorted by
 * line number, and turned into messages with {@link #toString()}.
 * </p>
 * <p>
 * A {@code ParseError} cannot be changed after it is created.
 * </p>
 */
public class ParseError
{
    /** The line in the file where the problem bracket appears, starting at 1. */
    private final int lineNumber;

    /** The bracket that caused the error, e.g. {@code ']'}. */
    private final char bracket;

    /**
     * Creates an error for the given bracket on the given line.
     *
     * @param lineNumber  the line in the file where the bracket appears,
     *                    starting at 1
     * @param bracket     the bracket that caused the error
     */
    public ParseError( int lineNumber, char bracket )
    {
        this.lineNumber = lineNumber;
        this.bracket = bracket;
    }

    /**
     * Returns the line number where this error occurred.
     *
     * @return the line in the file where the problem bracket appears
     */
    public int getLineNumber() { return lineNumber; }

    /**
     * Returns the bracket that caused this error.
     *
     * @return the problem bracket, e.g. {@code ']'}
     */
    public char getBracket()   { return bracket; }

    /**
     * Returns this error as a message ready to print, in the format
     * {@code Line 4: Syntax error on "]".}
     *
     * @return the formatted error message
     */
    @Override
    public String toString()
    {
        return "Line " + lineNumber + ": Syntax error on \"" + bracket + "\".";
    }
}
