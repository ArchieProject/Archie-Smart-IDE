package archie.utils;

import java.io.File;

public class Utils {
	static public void createDir(String dir) {
		boolean success = new File(dir).mkdir();
		if (success) {
			System.out.println("Directory: " + dir + " created");
		}  else {
			System.out.println("Couln't create " +  dir);
		}
	}
	
	static public String getRelativeFilePath(String filepath, String curDir) {
		filepath = filepath.trim().replace("\\", "/");
		String tokens[] = filepath.split(curDir);
		filepath = tokens[tokens.length-1];
		return filepath;
	}

        // TODO: should make the filepath have the same format whenever they are used, so getRelativeFilePath should be removed and only getRelativeFilePath2 should be used in the future
	static public String getRelativeFilePath2(String filepath, String curDir) {
		filepath = filepath.trim().replace("\\", "/");
		String tokens[] = filepath.split(curDir);
		filepath = "/"+tokens[tokens.length-1];
                if(filepath.startsWith("//"))
                    filepath = filepath.substring(1);
		return filepath;
	}
}
