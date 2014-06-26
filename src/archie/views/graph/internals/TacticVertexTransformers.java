/**

##########################
 DePaul SAREC
 Archie Project

 @author Ahmed Fakhry
##########################

 **/

package archie.views.graph.internals;

/*******************************************************
 * Defines a wrapper for all the vertex transformers
 *******************************************************/
public final class TacticVertexTransformers
{
	private final TacticVertexPainter mPainter;
	private final TacticVertexShaper mShaper;
	private final TacticVertexFilter mFilter;
	
	/*******************************************************
	 * Constructor.
	 * 
	 * @param painter The vertex painter
	 * @param shaper The vertex shaper
	 * @param filter The vertex filter
	 *******************************************************/
	public TacticVertexTransformers(TacticVertexPainter painter, TacticVertexShaper shaper, TacticVertexFilter filter)
	{
		mPainter = painter;
		mShaper = shaper;
		mFilter = filter;
	}
	
	/*******************************************************
	 * @return The vertex painter
	 *******************************************************/
	public TacticVertexPainter getVertexPainter()
	{
		return mPainter;
	}
	
	/*******************************************************
	 * @return The vertex shaper
	 *******************************************************/
	public TacticVertexShaper getVertexShaper()
	{
		return mShaper;
	}
	
	/*******************************************************
	 * @return The vertex filter
	 *******************************************************/
	public TacticVertexFilter getVertexFilter()
	{
		return mFilter;
	}
}
