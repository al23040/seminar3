package seminar3.extender;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import seminar3.FileManager;
import seminar3.PathRegistry;
import seminar3.Saver;

public class JsExtender extends ResourceExtender {
	public JsExtender(FileManager fileManager, Saver saver, PathRegistry pathRegistry) {
		super(fileManager, saver, pathRegistry);
	}
	
	@Override
	protected Elements getTargetElements(Document doc) {
		return doc.select("script[src]");
	}
	
	@Override
	protected String getTargetUrl(Element element) {
		return element.attr("abs:src");
	}
	
	@Override
	protected String downloadResource(String url) {
		String path = fileManager.generateJs();
		saver.saveFile(url, fileManager.baseDir + path);
		return path;
	}
	
	@Override
	protected void rewriteAttr(Element element, String localPath) {
		element.attr("src", localPath);
	}
}
