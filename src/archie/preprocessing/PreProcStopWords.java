
package archie.preprocessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * @author Carlos Castro
 * 
 *         This Pre-Processor removes common stop words. It uses a file of
 *         common words as a reference. It is set up as a Chain of Command
 *         design pattern, where each preprocessor does its operation and calls
 *         on the next one This allows for dynamic set up of the pre-processing
 *         steps, as well as adding or removing steps later on
 * 
 */
public class PreProcStopWords implements PreProcessor
{
	// Next in the chain of command
	PreProcessor _next;
	// Array list that holds the stopwords
	ArrayList<String> stopWords;

	// Constructor - Package Private
	public PreProcStopWords(String stopWordsFile)
	{

		try
		{
			// The args should specify the location of the stop words file
			// String stopWordsFile = args[0];

			// Array with the stop words
			stopWords = new ArrayList<String>();
			String stopWord;

			// Creates a buffered reader
			BufferedReader in = new BufferedReader(new FileReader(stopWordsFile));

			// Reads the file
			while ((stopWord = in.readLine()) != null)
			{
				// Adds the stop word to the array
				stopWords.add(stopWord);
			}

			in.close();
		}
		catch (Exception e)
		{
			System.out.println("The following error ocurred:\n" + e.getMessage());
			System.out.println("Details:\n");
			e.printStackTrace();
		}
	}

	public PreProcessor setNextPreProcessor(PreProcessor next)
	{
		// Integrity checks
		if (next == null)
			throw new IllegalArgumentException("The next preProcessor can't be null");

		// Sets the next chain link
		_next = next;

		return this;
	}

	public String process(String text)
	{
		String initialText = text;
		StringBuilder builder = new StringBuilder();
		String result = "";

		// Splits the text into tokens and iterates over them
		String[] tokens = initialText.split(" ");
		for (String word : tokens)
		{
			// If the word is not in the stop list, it gets included
			if (!stopWords.contains(word))
			{
				builder.append(word + " ");
			}
		}
		result = builder.toString();

		if (_next != null)
		{
			result = _next.process(result);
		}

		return result;
	}

}
