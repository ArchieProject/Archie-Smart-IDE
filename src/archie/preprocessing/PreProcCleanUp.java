package archie.preprocessing;

/**
 * @author Carlos Castro
 * 
 * This Pre-Processor cleans up the text, eliminating all 'non characters' (punctuation marks, numbers, etc)
 * It is set up as a Chain of Command design pattern, where each preprocessor does its operation and calls on the next one
 * This allows for dynamic set up of the pre-processing steps, as well as adding or removing steps later on
 *
 */
public final class PreProcCleanUp implements PreProcessor{
	// Next in the chain of command
	PreProcessor _next;
	
	// Constructor - Package Private
	public PreProcCleanUp() {}
	
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
		String result = "";
		
		// Reduces the text to only characters - using Regular Expressions
		result = initialText.replaceAll("[^\\p{L}]", " ");
		// Eliminates any duplicate whitespace - using Regular Expressions
		result = result.replaceAll("\\s+", " ");
		// Lowers the case
		result = result.toLowerCase();
		
		if (_next != null) {
			result = _next.process(result);
		}
		
		return result;
	}
}
