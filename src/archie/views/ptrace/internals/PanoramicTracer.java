/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.ptrace.internals;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;

import archie.globals.ArchieSettings;
import archie.preprocessing.PreProcCleanUp;
import archie.preprocessing.PreProcStemmer;
import archie.preprocessing.PreProcStopWords;
import archie.preprocessing.PreProcessor;
import archie.views.autodetect.internals.ProgressUpdater;

/*******************************************************
 * The panoramic trace engine.
 *******************************************************/
public final class PanoramicTracer
{
	private static Document mQueryDocument;
	private static List<Document> mTargetDocuments = new LinkedList<Document>();
	private static TfIdfVect mQueryDocVector;
	private static List<TfIdfVect> mTargetDocsVectors = new LinkedList<TfIdfVect>();
	private static ProgressUpdater mProgressCommand;
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
	 * Non-instantiable utility class.
	 *******************************************************/
	private PanoramicTracer()
	{
	}

	/*******************************************************
	 * Starts the panoramic tracer engine.
	 * 
	 * @param targetFolders
	 *            A list of target folders to scan the query file against.
	 * @param queryFile
	 *            The query document file.
	 * @param progressMonitor
	 *            [Optional - can be null] will be invoked whenever there's a
	 *            progress update.
	 * @return The results of the panoramic trace (By default, it will be sorted
	 *         by scores in a descending order).
	 * @throws FileNotFoundException
	 *             If a file wasn't found.
	 *******************************************************/
	public static TreeSet<PTraceResult> start(Collection<String> targetFolders, String queryFile,
			ProgressUpdater progressMonitor) throws FileNotFoundException
	{
		if (targetFolders == null || queryFile == null)
			throw new IllegalArgumentException();

		if (targetFolders.isEmpty() || queryFile.isEmpty())
			throw new IllegalArgumentException();

		// clear everything
		clear();

		mProgressCommand = progressMonitor;

		// ---- Start the operations -------

		if (mProgressCommand != null)
		{
			mProgressCommand.setProgressText("Processing the query file ...").setProgressValue(0).run();
		}

		// Build the query doc. ---> 10%
		mQueryDocument = buildDocumentFromFile(new File(queryFile));

		if (mProgressCommand != null)
		{
			mProgressCommand.setProgressValue(10).setProgressText("Processing target files ...").run();
		}

		if (mQueryDocument != null)
		{
			// ------------------------------------------

			// Build the target docs. --> 50%
			double inverseNumTargetFolders = (0.5 * 1.0) / (double) targetFolders.size();
			for (String folder : targetFolders)
			{
				scanFolder(new File(folder), inverseNumTargetFolders);
			}

			// Calculate all the extracted terms' Idfs.
			TermsManager.getInstance().calculateAllTermsIdfs(1 + mTargetDocuments.size());

			// ------------------------------------------
			// Building vectors --> 10%

			if (mProgressCommand != null)
			{
				mProgressCommand.setProgressValue(60).setProgressText("Building document vectors ...").run();
			}

			// Get the query doc terms.
			Iterable<Term> queryTerms = mQueryDocument.getTermsList();

			// Build the query vector.
			mQueryDocVector = mQueryDocument.buildTfIdfVect(queryTerms);

			// Build the target docs vectors.
			for (Document targetDoc : mTargetDocuments)
			{
				mTargetDocsVectors.add(targetDoc.buildTfIdfVect(queryTerms));
			}

			// ------------------------------------------
			// Building results --> 20%

			if (mProgressCommand != null)
			{
				mProgressCommand.setProgressValue(70).setProgressText("Building results ...").run();
			}

			// Now build and return results.
			TreeSet<PTraceResult> results = buildResults();

			if (mProgressCommand != null)
			{
				mProgressCommand.setProgressValue(90).setProgressText("Listing results ...").run();
			}

			return results;
		}
		else
		{
			// Return an empty list in the case that the query file is empty

			return new TreeSet<PTraceResult>();
		}
	}

	/*******************************************************
	 * Clears everything in preparation for starting a new scan.
	 *******************************************************/
	private static void clear()
	{
		mQueryDocument = null;
		mTargetDocuments.clear();
		mQueryDocVector = null;
		mTargetDocsVectors.clear();
		mProgressCommand = null;
		mTotalProgress = 0.0;

		TermsManager.getInstance().clearAllTerms();
	}

	/*******************************************************
	 * Recursively scans a folders and processes its java files (if any).
	 * 
	 * @param folder
	 *            The folder file that will be recursively scanned.
	 * @param parentStep
	 *            The progress value of the parent of the current folder to be
	 *            multiplied by the progress of the current folder.
	 * @throws FileNotFoundException
	 *             If error occurred while processing folder or one of its
	 *             children.
	 *******************************************************/
	private static void scanFolder(final File folder, double parentStep) throws FileNotFoundException
	{
		File[] childFiles = folder.listFiles();

		if (childFiles.length == 0)
		{
			// Empty folder
			mTotalProgress += parentStep;
		}
		else
		{
			double step = (1.0 / childFiles.length) * parentStep;

			for (File file : childFiles)
			{
				if (file.isDirectory())
				{
					// Recurse back into this function
					scanFolder(file, step);
				}
				else
				{
					// Update the total progress
					mTotalProgress += (step);
					if (mProgressCommand != null)
					{
						mProgressCommand.setProgressValue((int) (mTotalProgress * 100)).run();
					}

					// Is it a java file
					if (file.getAbsolutePath().endsWith(".java"))
					{
						// It is a java source file
						Document doc = buildDocumentFromFile(file);

						if (doc != null)
						{
							mTargetDocuments.add(doc);
						}
					}
				}
			}
		}
	}

	/*******************************************************
	 * Builds a {@link Document} representing the given file.
	 * 
	 * @param file
	 *            The file for which the document will be built.
	 * @return The newly-built document, or null if file is empty.
	 * @throws FileNotFoundException
	 *             If file doesn't exist.
	 *******************************************************/
	private static Document buildDocumentFromFile(final File file) throws FileNotFoundException
	{
		// Prepare the result
		Document result = null;

		// Create the scanner.
		Scanner scan = new Scanner(new BufferedInputStream(new FileInputStream(file)));

		// Read the whole file.
		scan.useDelimiter("\\Z");

		if (scan.hasNext())
		{
			String content = scan.useDelimiter("\\Z").next();

			// Get the first few lines as a sample content.
			int numLines = 7;
			int index = 0;
			int oldIndex = 0;
			do
			{
				oldIndex = index;
				index = content.indexOf("\n", oldIndex + 1);
				if (index == -1)
				{
					break;
				}

				--numLines;

			} while (numLines > 0);

			String sampleContent = content.substring(0, oldIndex);

			// Build the document.
			Document doc = new Document(file.getName(), file.getAbsolutePath(), sampleContent);

			// Use the engine's preprocessors
			content = mCleanupPreProcessor.process(content);

			String rawWords[] = content.split("\\s+");

			for (String word : rawWords)
			{
				doc.addTerm(word);
			}

			result = doc;
		}

		// Close the scanner.
		scan.close();

		return result;
	}

	/*******************************************************
	 * Builds and returns the results
	 * 
	 * @return The newly-built results.
	 *******************************************************/
	private static TreeSet<PTraceResult> buildResults()
	{
		TreeSet<PTraceResult> results = new TreeSet<PTraceResult>(PTraceResult.BY_DECREASING_SCORES);

		for (TfIdfVect targetVect : mTargetDocsVectors)
		{
			double cosineSimilarity = mQueryDocVector.cosineSimilarity(targetVect);

			// Cosine similarity is between 0 and +1,
			// We need to convert it to a score from 0 to +100.

			results.add(new PTraceResult(targetVect.getDocumentFileName(), targetVect.getDocumentPath(), targetVect
					.getSampleContent(), (cosineSimilarity * 100)));
		}

		return results;
	}
}
