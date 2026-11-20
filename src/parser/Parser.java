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
	
	private StackADT stack = new MyStack();
	private QueueADT errorQ = new MyQueue();
	private QueueADT extrasQ = new MyQueue();
	private ListADT errors = new MyArrayList();
	
	/*
	 *  ==========================
	 *  HELPER METHODS
	 *  ==========================
	 */
	
	/**
	 * Checks if the character is one of the open brackets
	 *
	 * @param c   		  the character to check
	 */
	private static boolean isOpenBracket (char c) {
		return c=='(' || c=='{' || c=='[' || c=='<';
	}

	/**
	 * Checks if the character is one of the close brackets
	 *
	 * @param c   		  the character to check
	 */
	private static boolean isCloseBracket (char c) {
		return c==')' || c=='}' || c==']' || c=='>';
	}
	
	/**
	 * Returns the partner of the provided bracket
	 *
	 * @param c   		  the original bracket
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
	 * @param c        	  the current bracket
	 */
	private static String encode(int lineNumber, char c) { return lineNumber + ": " + c; }

	/**
	 * Extracts the line number from the encoded error line
	 *
	 * @param errorLine   the string to extract from
	 */
	private static int decodeLineNumber(String errorLine) { return Integer.parseInt( errorLine.substring( 0, errorLine.indexOf( ':' ) ) ); }
	
	/**
	 * Extracts the bracket from the encoded error line
	 *
	 * @param errorLine   the string to extract from
	 */
	private static char decodeBracket(String errorLine) { return errorLine.charAt(errorLine.length()-1); }
	
	/**
	 * Determines if the stack contains a specific bracket
	 *
	 * @param c   		  the bracket to search for
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
	public void processLine(String code, int lineNumber) throws EmptyQueueException {
		
		for(int i=0; i< code.length(); i++) {
			
			char c = code.charAt(i);
			
			if(isOpenBracket(c)) {
				stack.push(encode(lineNumber, c));
			} else if (isCloseBracket(c)) {
				
				char opener = partner(c);
				
				if(!stack.isEmpty() && opener == decodeBracket(stack.peek())) {
					stack.pop();
				} else if (!errorQ.isEmpty() && opener == decodeBracket(errorQ.peek())) {
					errors.add(errorQ.dequeue());
				} else if (stack.isEmpty()) {
					errorQ.enqueue(encode(lineNumber, c));
				} else if(stackHas(partner(c))) {
					while(decodeBracket(stack.peek()) != opener) {
						errorQ.enqueue(stack.pop());
					}
					stack.pop(); // removed the matched opener
				} else {
					extrasQ.enqueue(encode(lineNumber, c));
				}
			}
		}
		
		
	}
	
	/**
	 * Finishes parsing after the last line: any brackets still open are
	 * treated as errors, and all errors are sorted by line number.
	 *
	 * @return one formatted message per error, in line order;
	 *         an empty array if the file has no errors
	 */
	public String[] finalEvaluation() throws NullPointerException, EmptyQueueException {
		ParseError[] parsedErrors = null;
		String[] results = null;
		
		while(!stack.isEmpty()) {
			errorQ.enqueue(stack.pop());
		}
		
		while(!errorQ.isEmpty() || !extrasQ.isEmpty()) {
			if(errorQ.isEmpty()) {
				errors.add(extrasQ.dequeue());
			} else if(extrasQ.isEmpty()) {
				errors.add(errorQ.dequeue());
			} else if(partner(decodeBracket(errorQ.peek())) != decodeBracket(extrasQ.peek())) {
				errors.add(errorQ.dequeue());
			} else {
				errors.add(errorQ.dequeue());
				extrasQ.dequeue();
			}
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
