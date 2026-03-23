package seminar3.extender;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import seminar3.FileManager;
import seminar3.PathRegistry;
import seminar3.Saver;

public abstract class ResourceExtender {
	protected FileManager fileManager;
	protected Saver saver;
	protected PathRegistry pathRegistry;
	
	public ResourceExtender(FileManager fileManager, Saver saver, PathRegistry pathRegistry) {
		this.fileManager = fileManager;
		this.saver = saver;
		this.pathRegistry = pathRegistry;
	}
	
	public void process(Document doc) {
		Elements elements = getTargetElements(doc);
		
		for (Element element : elements) {
			String url = getTargetUrl(element);
			if (url == null || url.isEmpty() || url.startsWith("data:")) continue;
			
			String localPath;
			if (pathRegistry.isRegistered(url)) {
				localPath = pathRegistry.getPath(url);
			} else {
				localPath = downloadResource(url);
				if (localPath == null) {
					continue;
				}
				pathRegistry.register(url, localPath);
			}
			rewriteAttr(element, localPath);
			
		}
	}
	
	protected abstract Elements getTargetElements(Document doc);
	protected abstract String getTargetUrl(Element element);
	protected abstract String downloadResource(String url);
	protected abstract void rewriteAttr(Element element, String localPath);
}
