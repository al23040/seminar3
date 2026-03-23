package seminar3;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileManager {
	public String baseDir;
	private Map<String, Integer> counters = new ConcurrentHashMap<>();
	
	public FileManager(String baseDir) {
		this.baseDir = baseDir;
		
		String[] dirs = {"images", "css", "js"};
		for (String dir : dirs) {
			new File(baseDir + dir).mkdirs();
			counters.put(dir,  1);
		}
		counters.put("html", 1);
 	}
	
	private String generatePath(String dir, String extension) {
		int count = counters.get(dir);
		counters.put(dir, count+1);
		
		if (dir.equals("html")) {
			return count + extension;
		}
		
		return dir + "/" + count + extension;
	}
	
	public String generateImagePath() {
		return generatePath("images", "");
	}
	
	public String generateCss() {
		return generatePath("css", "_style.css");
	}
	
	public String generateJs() {
		return generatePath("js", ".js");
	}
	
	public String generateHtml() {
		return generatePath("html", ".html");
	}
}
