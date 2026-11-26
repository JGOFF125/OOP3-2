package parser;

import utilities.Iterator;
import utilities.ListADT;
import utilities.QueueADT;
import utilities.StackADT;

import exceptions.EmptyQueueException;
import implementations.MyArrayList;
import implementations.MyQueue;
import implementations.MyStack;

/**
 * Checks a text file for correctly matched brackets: (), {}, [] and &lt;&gt;.
 * <p>
 * Lines are fed in one at a time with {@link #processLine(String, int)}.
 * After the last line, {@link #finalEvaluation()} returns every error found,
 * sorted by line number. Based on Kitty's parser algorithm, using
 * {@link MyStack} for open brackets and two {@link MyQueue}s for errors.
 * </p>
 * <p>
 * Each stack/queue entry is a String of the form {@code "lineNumber: bracket"},
 * because the ADTs only store Strings.
 * </p>
 * <p>
 * Use a new Parser for each file.
 * </p>
 */
public class Parser {

    /** Start a fresh check for one file */
    public Parser()
    {
    }
	
	/** Open brackets waiting for their closer, as "line: bracket" entries. */
	private StackADT stack = new MyStack();
	/** Openers skipped over by a mismatch, and closers that arrived with an empty stack. */
	private QueueADT errorQ = new MyQueue();
	/** Closers whose opener is nowhere in the stack. */
	private QueueADT extrasQ = new MyQueue();
	/** Confirmed errors, collected before sorting. */
	private ListADT errors = new MyArrayList();
	
	/*
	 *  ==========================
	 *  HELPER METHODS
	 *  ==========================
	 */
	
	/**
	 * Checks if the character is one of the open brackets
	 *
	 * @param c           the character to check
	 * 
	 * @return true if c is an opening bracket; false otherwise
	 */
	private static boolean isOpenBracket (char c) {
		return c=='(' || c=='{' || c=='[' || c=='<';
	}

	/**
	 * Checks if the character is one of the close brackets
	 *
	 * @param c           the character to check
	 * 
	 * @return true if c is a closing bracket; false otherwise
	 */
	private static boolean isCloseBracket (char c) {
		return c==')' || c=='}' || c==']' || c=='>';
	}
	
	/**
	 * Returns the partner of the provided bracket
	 *
	 * @param c           the original bracket
	 * 
	 * @return the matching bracket, or '0' if c is not a bracket
	 */
	private static char partner(char c) {
		return switch(c) {
			case '(' -> ')';
			case ')' -> '(';
			case '{' -> '}';
			case '}' -> '{';
			case '[' -> ']';
			case ']' -> '[';
			case '<' -> '>';
			case '>' -> '<';
			default -> '0';
		};
	}
	
	/**
	 * Creates a string containing the bracket and its line number
	 *
	 * @param lineNumber  the line's number in the file, starting at 1
	 * @param c           the current bracket
	 * 
	 * @return the encoded entry, e.g. "4: ("
	 */
	private static String encode(int lineNumber, char c) { return lineNumber + ": " + c; }

	/**
	 * Extracts the line number from the encoded error line
	 *
	 * @param errorLine   the string to extract from
	 * 
	 * @return the line number stored in the entry
	 */
	private static int decodeLineNumber(String errorLine) { return Integer.parseInt( errorLine.substring( 0, errorLine.indexOf( ':' ) ) ); }
	
	/**
	 * Extracts the bracket from the encoded error line
	 *
	 * @param errorLine   the string to extract from
	 * 
	 * @return the bracket stored in the entry
	 */
	private static char decodeBracket(String errorLine) { return errorLine.charAt(errorLine.length()-1); }
	
	/**
	 * Determines if the stack contains a specific bracket
	 *
	 * @param c           the bracket to search for
	 * 
	 * @return true if the bracket is anywhere in the stack, false otherwise
	 */
	private boolean stackHas(char c) {
		Iterator stackIterator = stack.iterator();
		
		while(stackIterator.hasNext()) {
			if(decodeBracket(stackIterator.next()) == c){
				return true;
			}
		}
		
		return false;
	}
	
	/*
	 *  ==========================
	 *  END HELPER METHODS
	 *  ==========================
	 */
	
	/**
	 * Checks every bracket in one line of the file. Everything that is not
	 * a bracket is ignored.
	 *
	 * @param code        the text of the line
	 * @param lineNumber  the line's number in the file, starting at 1
	 */
	public void processLine(String code, int lineNumber) {
		
		try {
			// For each character c in the line
			for(int i=0; i< code.length(); i++) {
				
				
				char c = code.charAt(i);
				
				// If c is an opening bracket
				if(isOpenBracket(c)) {
					
					// push "line number: c" into stack
					stack.push(encode(lineNumber, c));
					
				// If c is a closing bracket	
				} else if (isCloseBracket(c)) {
					
					// get the partner closing bracket of current bracket type
					char opener = partner(c);
					
					// If stack is not empty and current c and top of stack are partners
					if(!stack.isEmpty() && opener == decodeBracket(stack.peek())) {
						
						// pop stack and disregard pair (no errors)
						stack.pop();
						
					// If error queue is not empty and c and head of errorQ are partners
					} else if (!errorQ.isEmpty() && opener == decodeBracket(errorQ.peek())) {
						
						// get the head of errorQ and report it as an error
						// it wouldn't be in errorQ if it wasn't an error
						errors.add(errorQ.dequeue());
						
					// If stack is empty
					} else if (stack.isEmpty()) {
						
						// log the current character (a closing bracket) to the errorQ
						// meaning closing bracket doesn't have an opening
						errorQ.enqueue(encode(lineNumber, c));
						
					// If current closing bracket has an opening bracket in the stack
					} else if(stackHas(partner(c))) {
						
						// Pop each character in stack into errorQ until we find the partner
						// All openers we pop are errors because they aren't closed
						while(decodeBracket(stack.peek()) != opener) {
							errorQ.enqueue(stack.pop());
						}
						
						// Remove the matched opener
						stack.pop();
						
					// Else, add current closing character to queue of extra characters
					} else {
						extrasQ.enqueue(encode(lineNumber, c));
					}
				}
			}
		} catch (EmptyQueueException e) {
			throw new IllegalStateException( "Parser queue was unexpectedly empty.", e );
		}
		
		
	}
	
	/**
	 * Finishes parsing after the last line: any brackets still open are
	 * treated as errors, and all errors are sorted by line number.
	 *
	 * @return one formatted message per error, in line order;
	 *         an empty array if the file has no errors
	 */
	public String[] finalEvaluation() {
		ParseError[] parsedErrors = null;
		String[] results = null;
		
		// While stack is not empty, mark all remaining characters as errors
		while(!stack.isEmpty()) {
			errorQ.enqueue(stack.pop());
		}
		
		try {
			
			// While either errorQ or extrasQ is not empty
			while(!errorQ.isEmpty() || !extrasQ.isEmpty()) {
				
				// If errorQ is empty, mark remaining extra characters as errors
				if(errorQ.isEmpty()) {
					errors.add(extrasQ.dequeue());
					
				// If extrasQ is empty, mark remaining error queue characters as errors
				} else if(extrasQ.isEmpty()) {
					errors.add(errorQ.dequeue());
					
				// If current heads of errorQ and extrasQ don't match, dequeue head of  errorQ as an error
				} else if(partner(decodeBracket(errorQ.peek())) != decodeBracket(extrasQ.peek())) {
					errors.add(errorQ.dequeue());
					
				// Else, dequeue from both and log only one of the pair as an error
				} else {
					errors.add(errorQ.dequeue());
					extrasQ.dequeue();
				}
			}
		} catch (EmptyQueueException e) {
			throw new IllegalStateException( "Parser queue was unexpectedly empty.", e );
		}
		
		parsedErrors = new ParseError[errors.size()];
		results = new String[errors.size()];
		
		// transform all error strings
		for(int i=0; i<errors.size(); i++) {
			String error = errors.get(i);
			parsedErrors[i] = new ParseError(decodeLineNumber(error), decodeBracket(error));
		}
		
		// sort by line number
		for(int i=1; i<parsedErrors.length; i++) {
			ParseError current = parsedErrors[i];
			int j = i - 1;
			
			while (j >= 0 && parsedErrors[j].getLineNumber() > current.getLineNumber()) {
				parsedErrors[j+1] = parsedErrors[j];
				j--;
			}
			parsedErrors[j+1] = current;
			
		}
		
		
		for(int i=0; i<parsedErrors.length; i++) {
			results[i] = parsedErrors[i].toString();
		}
		
		
		return results;
	}

}
