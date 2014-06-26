package archie.preprocessing;

import archie.utils.*;

/**
 * @author Carlos Castro
 * 
 * This Pre-Processor stemms the words to their roots.  It uses the Porter stemming algorithm.
 * It is set up as a Chain of Command design pattern, where each preprocessor does its operation and calls on the next one
 * This allows for dynamic set up of the pre-processing steps, as well as adding or removing steps later on
 *
 */
public class PreProcStemmer  implements PreProcessor{
	// Next in the chain of command
	PreProcessor _next;
	
	// Constructor - Package Private
	public PreProcStemmer() {}
	
	public PreProcessor setNextPreProcessor(PreProcessor next){
		// Integrity checks
		if (next==null)
			throw new IllegalArgumentException("The next preProcessor can't be null");

		// Sets the next chain link
		_next = next;
		
		return this;
	}
	
	public String process(String text) {
		String initialText = text;
		StringBuilder builder = new StringBuilder();
		String result = "";
		String stemmedWord;
		Stemmer porter = new Stemmer();
		
		// Splits the text into tokens and iterates over them
		String[] tokens = initialText.split(" ");
		for (String word : tokens) {
			
			// Calls the porter class to do the stemming
			porter.add(word.toCharArray(), word.length());
			porter.stem();
			stemmedWord = porter.toString();
			builder.append(stemmedWord + " ");
			
		}
		result = builder.toString();
		
		if (_next != null) {
			result = _next.process(result);
		}
		
		return result;
	}
}
