package parser;

import utilities.Iterator;
import utilities.ListADT;
import utilities.QueueADT;
import utilities.StackADT;

import exceptions.EmptyQueueException;
import implementations.MyArrayList;
import implementations.MyQueue;
import implementations.MyStack;

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
	
	// checks if character is an open bracket
	private static boolean isOpenBracket (char c) {
		return c=='(' || c=='{' || c=='[' || c=='<';
	}

	// checks if character is a close bracket
	private static boolean isCloseBracket (char c) {
		return c==')' || c=='}' || c==']' || c=='>';
	}
	
	// gets the partner of the provided bracket
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
	
	// adds a line number to the error code
	private static String encode(int lineNumber, char c) { return lineNumber + ": " + c; }

	// get line number from the error line
	private static int decodeLineNumber(String errorLine) { return Integer.parseInt( errorLine.substring( 0, errorLine.indexOf( ':' ) ) ); }
	
	// get bracket from the error line
	private static char decodeBracket(String errorLine) { return errorLine.charAt(errorLine.length()-1); }
	
	// check if stack contains an opening bracket
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
