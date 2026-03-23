package seminar3;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PathRegistry {
	private Map<String, String> urlToPath = new ConcurrentHashMap<>();
	
	public PathRegistry() {
	}
	
	public boolean isRegistered(String url) {
		return urlToPath.containsKey(url);
	}
	
	public void register(String url, String path) {
		urlToPath.put(url, path);
	}
	
	public String getPath(String url) {
		return urlToPath.get(url);
	}
	
	public void unregister(String url) {
		urlToPath.remove(url);
	}
}
