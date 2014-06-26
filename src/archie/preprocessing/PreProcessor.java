package archie.preprocessing;

/**
 * @author Carlos Castro
 * 
 * This Interface exposes the functions related to the archie.preprocessing of text. 
 * It is set up as a Chain of Command design pattern, where each preprocessor does its operation and calls on the next one
 * This allows for dynamic set up of the pre-processing steps, as well as adding or removing steps later on
 *
 */
public interface PreProcessor {
	public PreProcessor setNextPreProcessor(PreProcessor next);
	public String process(String text);
}
