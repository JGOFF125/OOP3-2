
package appDomain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
            int lineNumber = 0;

            // Open the file and read it one line at a time
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName)))
            {
                String line;

                while ((line = reader.readLine()) != null)
                {
                    lineNumber++;

                    // Display the line number and its contents
                    System.out.println(lineNumber + ": " + line);
                }

                // Display a message when the file is finished
                System.out.println();
                System.out.println("Finished reading: " + fileName);
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
