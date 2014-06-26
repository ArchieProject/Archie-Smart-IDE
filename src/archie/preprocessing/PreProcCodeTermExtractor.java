
package archie.preprocessing;

import java.io.FileInputStream;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreProcCodeTermExtractor
{
	private static final String DELIM = " .,:;/?'\"[]{})(-_=+~!@#$%^&*<>\n\t\r1234567890";
	private static final String SPLIT_REGEX = "[A-Z][a-z]+|[a-z]+|[A-Z]+";

	static public String run(String filename)
	{
		StringBuilder result = null;
		try
		{
			FileInputStream fis = new FileInputStream(filename);
			int size = (int) fis.available();
			byte[] text = new byte[size];

			fis.read(text, 0, size);
			String content = new String(text);

			// System.out.println("=========== original code : " + filename);
			// System.out.println(content);

			// Remove copyright information in the beginning of the code

			StringTokenizer st = new StringTokenizer(content, DELIM);
			result = new StringBuilder();
			String space = " ";

			while (st.hasMoreTokens())
			{
				String tok = st.nextToken();
				Pattern p = Pattern.compile(SPLIT_REGEX);
				Matcher m = p.matcher(tok);
				boolean found = m.find();
				while (found)
				{
					String subStringFound = m.group();
					if (1 < subStringFound.length())
					{
						result.append(subStringFound + space);
					}
					found = m.find();
				}
			}
			
			fis.close();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result.toString();
	}

	public static void main(String args[])
	{
		PreProcCodeTermExtractor.run("src/requirements_tracer/Main.java");
	}
}
