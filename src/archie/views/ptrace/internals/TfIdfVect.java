/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.ptrace.internals;

import java.util.ArrayList;
import java.util.List;

/*******************************************************
 * Defines a Tf-Idf Vector.
 *******************************************************/
final class TfIdfVect
{
	/**
	 * The name of the file. i.e. The last component of the path, like
	 * "source.java".
	 */
	private final String mDocumentName;

	/**
	 * The absolute path of the document. We keep it just in case we need it for
	 * debugging.
	 */
	private final String mDocumentPath;

	/**
	 * Reference to some sample content from the document.
	 */
	private final String mSampleContent;

	/**
	 * Helps speed up magnitude calculation.
	 */
	private boolean mMagIsDirty = true;

	/**
	 * The vectors magnitude.
	 */
	private double mMagnitude = 0.0;

	/**
	 * The vector's entries.
	 */
	private final List<Double> mTfIfs = new ArrayList<Double>();

	/*******************************************************
	 * Constructs a Tf-Idf vector for a particular documents. The vector will
	 * need to be filled in with Tf-Idf values by calling the
	 * {@link TfIdfVect#addTfIdf(double)} method.
	 * 
	 * @param docFileName
	 *            The name of the file.
	 * @param docFilePath
	 *            The absolute path of the document.
	 * @param sampleContent
	 *            Reference to sample contents from the document.
	 *******************************************************/
	public TfIdfVect(final String docFileName, final String docFilePath, final String sampleContent)
	{
		if (docFileName == null || docFilePath == null || sampleContent == null)
		{
			throw new IllegalArgumentException();
		}

		mDocumentName = docFileName;
		mDocumentPath = docFilePath;
		mSampleContent = sampleContent;
	}

	/*******************************************************
	 * Adds a Tf-Idf value to the vector.
	 * 
	 * @param val
	 *            The Tf-Idf value.
	 *******************************************************/
	public void addTfIdf(double val)
	{
		mTfIfs.add(new Double(val));
		mMagIsDirty = true;
	}

	/*******************************************************
	 * Calculates and returns the magnitude of the vector.
	 * 
	 * @return The magnitude of the vector.
	 *******************************************************/
	public double magnitude()
	{
		if (mMagIsDirty)
		{
			// Recalculate only if needed.

			double squareSum = 0.0;

			for (double val : mTfIfs)
			{
				squareSum += val * val;
			}

			mMagnitude = Math.sqrt(squareSum);

			mMagIsDirty = false;
		}

		return mMagnitude;
	}

	/*******************************************************
	 * Calculates the dot product of two Tf-Idf vectors.
	 * 
	 * @note Tf-Idf values at elements of the same indices in the two vectors
	 *       must correspond to the same term, for this to work correctly.
	 * 
	 * @param rhs
	 *            The right-hand side {@link TfIdfVect} of the dot product
	 *            operation.
	 * @return The dot product value.
	 *******************************************************/
	public double dotProduct(final TfIdfVect rhs)
	{
		if (mTfIfs.size() != rhs.mTfIfs.size())
		{
			throw new IllegalArgumentException("Vectors are not of the same size");
		}

		double result = 0.0;
		int size = mTfIfs.size();

		for (int i = 0; i < size; ++i)
		{
			result += mTfIfs.get(i) * rhs.mTfIfs.get(i);
		}

		return result;
	}

	/*******************************************************
	 * Calculates the cosine similarity between two vectors. The value will be
	 * between 0 (completely dissimilar) and +1 (completely similar).
	 * 
	 * @param rhs
	 *            The right-hand side {@link TfIdfVect}.
	 * @return The cosine similarity between two vectors.
	 *******************************************************/
	public double cosineSimilarity(final TfIdfVect rhs)
	{
		double denominator = (this.magnitude() * rhs.magnitude());
		double result = 0.0;

		if (Double.compare(denominator, 0.0) != 0)
		{
			result = this.dotProduct(rhs) / denominator;
		}

		return result;
	}

	/*******************************************************
	 * Gets the absolute path of the document that this vector represents.
	 * 
	 * @return The document absolute path.
	 *******************************************************/
	public String getDocumentPath()
	{
		return mDocumentPath;
	}

	/*******************************************************
	 * Gets the sample content of the document.
	 * 
	 * @return The sample content from the document.
	 *******************************************************/
	public String getSampleContent()
	{
		return mSampleContent;
	}

	/*******************************************************
	 * Gets the name of the document that this vector represents.
	 * 
	 * @return The document's file name.
	 *******************************************************/
	public String getDocumentFileName()
	{
		return mDocumentName;
	}
}
