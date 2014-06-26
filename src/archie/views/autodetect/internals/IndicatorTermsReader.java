/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.autodetect.internals;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;

/*******************************************************
 * Defines a utility class just for the purpose of reading the engine's
 * indicator terms, which will be used to process the source files by the
 * scanner.
 *******************************************************/
public final class IndicatorTermsReader
{
	/*******************************************************
	 * Package private to be accessible by the scanner.
	 *******************************************************/
	static final Hashtable<String, TermQualityType> mClassifiedTerms = new Hashtable<String, TermQualityType>();
	
	/*******************************************************
	 * To prevent rereading the file unless required.
	 *******************************************************/
	private static boolean mIsCashed = false;

	/*******************************************************
	 * Utility class: private constructor.
	 *******************************************************/
	private IndicatorTermsReader()
	{
	}

	/*******************************************************
	 * Reads the engine's internal indicator terms which will be used by the
	 * scanner to process the source files that the user wants to scan. It will
	 * read the file and cache the results. Calling this method again won't make
	 * it reread the file. If rereading the file is desirable then call
	 * forceRead()
	 * 
	 * @param filePath
	 *            The path to engine's internal IndicatorTerms file.
	 * @throws FileNotFoundException
	 *             If file was not found. Must be handled by the caller.
	 *******************************************************/
	public static void readIndicatorTerms(final String filePath) throws FileNotFoundException
	{
		// Only read the file if it is not already read and cached
		if (!mIsCashed)
		{
			// Clear the table
			mClassifiedTerms.clear();
			
			// Open a scanner
			Scanner fileScan = new Scanner(new BufferedInputStream(new FileInputStream(new File(filePath))));

			// Skip the header
			fileScan.nextLine();

			// Start scanning
			String[] lineFields;
			while (fileScan.hasNextLine())
			{
				lineFields = fileScan.nextLine().split(",");

				// Must be 3 fields, or otherwise the format is incorrect
				if (lineFields.length != 3)
				{
					// Close the scanner first
					fileScan.close();

					// Then throw an exception
					throw new RuntimeException("Indicator terms file format error!");
				}

				// First field is the quality type name
				String qualityName = lineFields[0];
				// Second field is the term
				String term = lineFields[1];
				// Third term is the term's probability
				double probability = Double.parseDouble(lineFields[2]);

				// Is there on in the table?
				TermQualityType quality = mClassifiedTerms.get(qualityName);
				if (quality == null)
				{
					// That quality doesn't exist in the table
					quality = new TermQualityType(qualityName);
					// Put it on the table
					mClassifiedTerms.put(qualityName, quality);
				}

				// Add the above term and its probability
				quality.addTerm(new IndicatorTerm(term, probability));
			}

			// Done with the scanner, close it
			fileScan.close();

			// Set the cached flag
			mIsCashed = true;
		}
	}

	/*******************************************************
	 * Forces rereading the file even it already had been read before.
	 * 
	 * @param filePath
	 *            The path to engine's internal IndicatorTerms file.
	 * @throws FileNotFoundException
	 *             If file was not found. Must be handled by the caller.
	 *******************************************************/
	public static void forceRead(final String filePath) throws FileNotFoundException
	{
		mIsCashed = false;
		readIndicatorTerms(filePath);
	}

}
