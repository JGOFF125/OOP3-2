
package appDomain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import parser.Parser;

/**
 * This class reads text files and displays their contents.
 *
 * @author Adeeb Farzi
 */
public class AppDriver
{
    public static void main(String[] args)
    {
        // Check if a filename was provided
        if (args.length == 0)
        {
            System.out.println("Usage: java appDomain.AppDriver <filename1> [filename2 ...]");
            return;
        }

        // Go through each file provided
        for (String fileName : args)
        {
        	Parser parser = new Parser();
            int lineNumber = 0;

            // Open the file and read it one line at a time
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName)))
            {
                String line;

                while ((line = reader.readLine()) != null)
                {
                    lineNumber++;

                    // Send the line and its line number to the parser
                    parser.processLine(line, lineNumber);
                }

             // Check for any remaining bracket errors
                String[] errors = parser.finalEvaluation();

                // Display the results for this file
                System.out.println(fileName);
                System.out.println();

                if (errors.length == 0)
                {
                    System.out.println("File: " + fileName + " has no errors.");
                }
                else
                {
                    System.out.println("Errors in file " + fileName + ":");
                    System.out.println();

                    // Print each error found
                    for (String error : errors)
                    {
                        System.out.println(error);
                    }
                }

                System.out.println();
            }
            catch (IOException e)
            {
                // Display a message if the file cannot be opened or read
                System.out.println("Unable to read file: " + fileName);
            }
        }
    }
}
