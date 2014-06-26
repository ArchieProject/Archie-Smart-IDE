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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import archie.globals.ArchieSettings;
import archie.preprocessing.PreProcCleanUp;
import archie.preprocessing.PreProcStemmer;
import archie.preprocessing.PreProcStopWords;
import archie.preprocessing.PreProcessor;

/*******************************************************
 * A static utility class to scan a directory and find the results
 *******************************************************/
public final class FileQualityScanner
{
	/*******************************************************
	 * Defines an object that maps a file to its found indicator terms under a
	 * specific quality type.
	 *******************************************************/
	private static final class FileTermsMapper
	{
		private final File mFile;
		private final TermQualityType mFoundTerms;

		/*******************************************************
		 * Creates a {@link FileTermsMapper} to map a file to its found
		 * indicator terms under a specific quality type.
		 * 
		 * @param file
		 *            The source file entry
		 * @param foundTerms
		 *            The found indicator terms under a particular quality
		 *******************************************************/
		public FileTermsMapper(File file, TermQualityType foundTerms)
		{
			mFile = file;
			mFoundTerms = foundTerms;
		}
	}

	/*******************************************************
	 * Defines a wrapper for a list of files and their associated found terms
	 * under a particular quality type in order to store the results of the
	 * accepted files under a certain quality (or classification)
	 *******************************************************/
	private static final class FilesWrapper implements Iterable<FileTermsMapper>
	{
		/*******************************************************
		 * The internal list
		 *******************************************************/
		private final List<FileTermsMapper> mFiles;

		/*******************************************************
		 * Creates the wrapper for a list of files
		 *******************************************************/
		public FilesWrapper()
		{
			mFiles = new ArrayList<FileTermsMapper>();
		}

		/*******************************************************
		 * Adds a file to the list, along with its found indicator terms under a
		 * particular quality
		 * 
		 * @param file
		 *            File to be added to the list
		 * 
		 * @param foundTerms
		 *            The found indicator terms in this file
		 *******************************************************/
		public void addFile(File file, TermQualityType foundTerms)
		{
			mFiles.add(new FileTermsMapper(file, foundTerms));
		}

		/*******************************************************
		 * 
		 * @see java.lang.Iterable#iterator()
		 *******************************************************/
		@Override
		public Iterator<FileTermsMapper> iterator()
		{
			return mFiles.iterator();
		}
	}

	// ----------------------------------------------------------------------------------------
	// Fields
	// ----------------------------------------------------------------------------------------

	private static final Hashtable<String, FilesWrapper> mResults = new Hashtable<String, FilesWrapper>();
	private static IProgressCommand mProgressCommand;
	private static double mThreshold;
	private static double mTotalProgress;

	private static PreProcessor mCleanupPreProcessor = new PreProcCleanUp();

	/**
	 * This is a static initializer, to initialize the preprocessors.
	 */
	static
	{
		PreProcessor preprocStopWords = new PreProcStopWords(ArchieSettings.getInstance().getStopWordsFilePath());
		PreProcessor preprocStemmer = new PreProcStemmer();
		mCleanupPreProcessor.setNextPreProcessor(preprocStopWords.setNextPreProcessor(preprocStemmer));
	}

	/*******************************************************
	 * Static utility class == private constructor
	 *******************************************************/
	private FileQualityScanner()
	{
	}

	/*******************************************************
	 * Scan the specified path, with the specified threshold, given the
	 * specified stop words location.
	 * 
	 * @param path
	 *            Path to be scanned.
	 * @param threshold
	 *            Threshold to be used to determine the likelihood of file being
	 *            listed under a certain architectural classification
	 * @param onProgressCommand
	 *            [Optional] A runnable command to run on the progress of the scan. Used to
	 *            update a progress bar for example.
	 * 
	 * @return A linked list of tree items to be used as input to the results
	 *         tree view.
	 * 
	 * @throws IllegalArgumentException
	 *             If the passed path parameter is null or empty.
	 *******************************************************/
	public static LinkedList<ITreeItem> scanDirectory(final String path, final double threshold,
			IProgressCommand onProgressCommand)
	{
		// Validate the path argument
		if (path == null || path.isEmpty())
		{
			throw new IllegalArgumentException("The path parameter must never be null or empty");
		}

		// Set the threshold
		mThreshold = threshold;

		// Clear the old results if any
		mResults.clear();

		// Initialize the progress
		mTotalProgress = 0.0;

		// Set the progress command
		mProgressCommand = onProgressCommand;

		// List all the files in this directory and sub directories and
		// process only the java files
		scanDirectory(new File(path), 1.0);

		// Build the results tree and return it
		return buildResultsTree();
	}

	// ----------------------------------------------------------------------------------------
	// Private static methods
	// ----------------------------------------------------------------------------------------

	/*******************************************************
	 * Recursively scan the supplied file and its sub files if it is a
	 * directory.
	 * 
	 * @param rootfile
	 *            The file to scan
	 * @param parentStep
	 *            The progress value of the parent of the current folder to be
	 *            multiplied by the progress of the current folder.
	 *******************************************************/
	private static void scanDirectory(final File rootfile, final double parentStep)
	{
		File[] subFiles = rootfile.listFiles();

		if (subFiles == null)
		{
			System.err.println("An error occured while processing file: " + rootfile);
			return;
		}

		if (subFiles.length == 0)
		{
			// Empty directory
			mTotalProgress += parentStep;
		}
		else
		{
			double step = (1.0 / subFiles.length) * parentStep;

			for (File file : subFiles)
			{
				if (file.isDirectory())
				{
					// Recurse back into this function
					scanDirectory(file, step);
				}
				else
				{
					// Update the total progress
					mTotalProgress += (step);
					if (mProgressCommand != null)
					{
						mProgressCommand.run(mTotalProgress);
					}

					// Is it a java file
					if (file.getAbsolutePath().endsWith(".java"))
					{
						// It is a java source file
						processJavaFile(file);
					}
				}
			}
		}
	}

	/*******************************************************
	 * Process the given java source file to determine whether it matches one of
	 * our architectural decisions classifications (or qualities)
	 * 
	 * @param file
	 *            Java source file to scan
	 *******************************************************/
	private static void processJavaFile(final File file)
	{
		// Start scanning the file and extract the unique words
		try
		{
			Scanner fileScan = new Scanner(new BufferedInputStream(new FileInputStream(file)));

			// Read the whole file content.
			fileScan.useDelimiter("\\Z");
			String content = "";

			if (fileScan.hasNext())
			{
				content = mCleanupPreProcessor.process(fileScan.next());
			}

			// Done extracting all words split at the spaces and build a list of
			// processed words
			// process unique words
			String rawWords[] = content.split("\\s+");
			HashSet<String> uniqueWords = new HashSet<String>();

			// This will hold all the file found qualities and their found terms
			Hashtable<String, TermQualityType> foundFileQualities = new Hashtable<String, TermQualityType>();

			for (String word : rawWords)
			{
				// Is current word included on the list of unique words?
				if (uniqueWords.contains(word))
				{
					// Yes, then it has already been processed before
				}
				else
				{
					// process it and added to the list processed words
					processWord(word, foundFileQualities);
					uniqueWords.add(word);
				}
			}

			// Done processing all the words
			// Compare the probability of the found terms in this file to the
			// scanner threshold
			for (TermQualityType foundQuality : foundFileQualities.values())
			{
				TermQualityType matchingEngineQuality = IndicatorTermsReader.mClassifiedTerms.get(foundQuality
						.getName());
				if (matchingEngineQuality != null)
				{
					double probability = foundQuality.getTotalProbability()
							/ matchingEngineQuality.getTotalProbability();
					if (probability >= mThreshold)
					{
						// This is an accepted file, so add it to the results
						acceptFile(foundQuality.getName(), file, foundQuality);
					}
				}
			}

			// Close the scanner
			fileScan.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		finally
		{

		}
	}

	/*******************************************************
	 * Adds the accepted file to results list.
	 * 
	 * @param qualityName
	 *            The architectural classification of the file.
	 * @param file
	 *            The file to be added to the results.
	 * @param foundQuality
	 *            The terms in this file under a particular quality.
	 *******************************************************/
	private static void acceptFile(final String qualityName, final File file, final TermQualityType foundQuality)
	{
		// Determine first if this quality has already been added as one of the
		// previous results
		FilesWrapper files = mResults.get(qualityName);

		if (files == null)
		{
			// First time to add
			files = new FilesWrapper();
			mResults.put(qualityName, files);
		}

		// Add the newly accepted files
		files.addFile(file, foundQuality);
	}

	/*******************************************************
	 * Process the given word to determine whether it matches one of the
	 * engine's indicator terms present under all terms qualities.
	 * 
	 * @param word
	 *            Word to check
	 * @param foundQualities
	 *            This will be filled with the results the check. The KEY is the
	 *            string quality name, and the VALUE is the
	 *            {@link TermQualityType} found quality type with its sub terms
	 *            in the current file being processed.
	 *******************************************************/
	private static void processWord(final String word, Hashtable<String, TermQualityType> foundQualities)
	{
		// Is the word present on the engine's indicator terms?
		for (TermQualityType engineQuality : IndicatorTermsReader.mClassifiedTerms.values())
		{
			IndicatorTerm matchingEngineTerm = engineQuality.getTerm(word);
			if (matchingEngineTerm != null)
			{
				// This is one of the terms that the engine is interested in
				// put it on the found qualities

				TermQualityType quality = foundQualities.get(engineQuality.getName());
				if (quality == null)
				{
					// This is the first time we find a term under this quality
					quality = new TermQualityType(engineQuality.getName());
					foundQualities.put(quality.getName(), quality);
				}

				// Add a new term under this quality
				quality.addTerm(new IndicatorTerm(matchingEngineTerm));
			}
		}
	}

	/*******************************************************
	 * Builds the tree that will be used as input to the UI results tree view.
	 * 
	 * @return The built tree.
	 *******************************************************/
	private static LinkedList<ITreeItem> buildResultsTree()
	{
		// The list that will be used to build the tree
		LinkedList<ITreeItem> treeRoots = new LinkedList<ITreeItem>();

		// Iterate over the found quality names
		for (String qualityName : mResults.keySet())
		{
			// This is one of the tree roots that represents qualities
			// (classifications)
			TreeQualityItem qualityItem = new TreeQualityItem(qualityName);

			// Get the files under this quality name
			FilesWrapper files = mResults.get(qualityName);
			for (FileTermsMapper fileMapper : files)
			{
				TreeFileItem fileItem = new TreeFileItem(fileMapper.mFile, fileMapper.mFoundTerms);
				qualityItem.addFileItem(fileItem);
			}

			// Add that root to the list
			treeRoots.add(qualityItem);
		}

		return treeRoots;
	}
}
