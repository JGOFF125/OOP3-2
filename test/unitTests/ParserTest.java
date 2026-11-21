package unitTests;

import static org.junit.Assert.*;
import org.junit.Test;
import parser.Parser;

public class ParserTest
{
    /** Runs the given lines through a new Parser (numbered from 1) and returns the errors. */
    private String[] parse( String... lines )
    {
        Parser p = new Parser();
        for ( int i = 0; i < lines.length; i++ )
        {
            p.processLine( lines[i], i + 1 );
        }
        return p.finalEvaluation();
    }

    // ---- Correct input: no errors ----

    @Test
    public void testCorrectNesting()
    {
        assertEquals( 0, parse( "( [ ] )" ).length );
        assertEquals( 0, parse( "{ [ ( ) ] }" ).length );
    }

    @Test
    public void testNonBracketsIgnored()
    {
        assertEquals( 0, parse( "int x = 5; // hello" ).length );
    }

    @Test
    public void testPairSpanningLines()
    {
        assertEquals( 0, parse( "{", "  ( )", "}" ).length );
    }

    // ---- The spec's incorrect examples ----

    @Test
    public void testSpecExampleCrossed()
    {
        String[] errors = parse( "( [ ) ]" );
        assertEquals( 1, errors.length );
        assertEquals( "Line 1: Syntax error on \"[\".", errors[0] );
    }

    @Test
    public void testSpecExampleDeepCrossed()
    {
        String[] errors = parse( "{ [ ( ] ) }" );
        assertEquals( 1, errors.length );
        assertEquals( "Line 1: Syntax error on \"(\".", errors[0] );
    }

    // ---- Missing / extra brackets ----

    @Test
    public void testExtraCloserAlone()
    {
        assertEquals( 1, parse( "]" ).length );
    }

    @Test
    public void testExtraCloserInside()
    {
        String[] errors = parse( "( ] )" );
        assertEquals( 1, errors.length );
        assertEquals( "Line 1: Syntax error on \"]\".", errors[0] );
    }

    @Test
    public void testMissingCloser()
    {
        assertEquals( 1, parse( "( ( )" ).length );
    }

    @Test
    public void testCloserBeforeOpener()
    {
        assertEquals( 1, parse( "( ] [ )" ).length );
    }

    // ---- Line numbers and ordering ----

    @Test
    public void testUnclosedReportsOriginalLine()
    {
        String[] errors = parse( "{", "( )", "x = 1;" );
        assertEquals( 1, errors.length );
        assertEquals( "Line 1: Syntax error on \"{\".", errors[0] );
    }

    @Test
    public void testErrorsSortedByLine()
    {
        // The line-2 error is found mid-parse, but the line-1 error only at EOF.
        String[] errors = parse( "{", "( [ ) ]" );
        assertEquals( 2, errors.length );
        assertEquals( "Line 1: Syntax error on \"{\".", errors[0] );
        assertEquals( "Line 2: Syntax error on \"[\".", errors[1] );
    }

    @Test
    public void testEmptyFile()
    {
        assertEquals( 0, parse().length );
    }
}