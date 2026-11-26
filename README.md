# OOP3 Assignment 2

Bracket checker built with custom array list, linked list, stack and queue implementations

## Requirements

- Java 21
- Eclipse
- JUnit 4

## Run in Eclipse

1. Import the project using File > Import > General > Existing Projects into Workspace
2. Select JavaSE-21 as the project JRE and keep JUnit 4 on the build path
3. Open Run Configurations > Java Application and select appDomain.AppDriver
4. Set the working directory to the project folder
5. Enter a file path under Program arguments, such as res/parserTest1.txt
6. Run and check the console

Put quotation marks around paths containing spaces

## Run the JAR

```text
java -jar Parser.jar res/parserTest1.txt
java -jar Parser.jar res/parserTest2.txt res/parserTest3.txt
```

Keep the res folder beside the JAR for these relative paths, or supply absolute file paths

## Tests

Run the unitTests package as a JUnit test in Eclipse

- 251 supplied data structure tests
- 12 parser tests
- 12 additional array list and stack tests

## Contributions

- Joaquin: MyArrayList and MyStack
- Sebastian: MyDLLNode, MyDLL and MyQueue
- Jose: Parser, ParseError and parser tests
- Adeeb: AppDriver and command-line file processing

## Parser behavior

Checks (), {}, [] and <> and ignores other characters
Angle brackets are checked even when used as comparison operators in the C samples
Errors include original line numbers and are sorted by line
Sample inputs and expected results are in res

