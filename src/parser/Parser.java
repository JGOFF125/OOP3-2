package parser;

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
	public static String encode(int lineNumber, char c) { return lineNumber + ": " + c; }
	
	// get code from the error line
	public static char decode(String errorLine) { return errorLine.charAt(errorLine.length()-1); }
	
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
				
				if(!stack.isEmpty() && opener == decode(stack.peek())) {
					stack.pop();
				} else if (!errorQ.isEmpty() && opener == decode(errorQ.peek())) {
					errorQ.dequeue();
				} else if (stack.isEmpty()) {
					errorQ.enqueue(encode(lineNumber, c));
				} else {
					int codeLineLocation = stack.search(c + "");
					
					if(codeLineLocation != -1) {
						while(decode(stack.peek()) != opener) {
							errorQ.enqueue(stack.pop());
						}
						
					} else {
						extrasQ.enqueue(encode(lineNumber, c));
					}
					
				}
			}
		}
		
	}
	
	public String[] finalEvaluation() throws NullPointerException, EmptyQueueException {
		String[] errorString = null;
		
		while(!stack.isEmpty()) {
			errorQ.enqueue(stack.pop());
		}
		
		while(!errorQ.isEmpty() || !extrasQ.isEmpty()) {
			if(errorQ.isEmpty()) {
				errors.add(extrasQ.dequeue());
			} else if(extrasQ.isEmpty()) {
				errors.add(errorQ.dequeue());
			} else if(partner(decode(errorQ.peek())) != decode(extrasQ.peek())) {
				errors.add(errorQ.dequeue());
			} else {
				errorQ.dequeue();
				extrasQ.dequeue();
			}
		}
		
		
		return errorString;
	}

}
