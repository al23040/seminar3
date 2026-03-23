package seminar3.extender;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import seminar3.FileManager;
import seminar3.PathRegistry;
import seminar3.Saver;

public class ImageExtender extends ResourceExtender {
	public ImageExtender(FileManager fileManager, Saver saver, PathRegistry pathRegistry) {
		super(fileManager, saver, pathRegistry);
	}
	
	@Override
	protected Elements getTargetElements(Document doc) {
		return doc.select("img");
	}
	
	@Override
	protected String getTargetUrl(Element element) {
		return element.absUrl("src");
	}
	
	@Override
	protected String downloadResource(String url) {
		String path = fileManager.generateImagePath();
		return saver.saveImage(url, path);
	}
	
	@Override
	protected void rewriteAttr(Element element, String localPath) {
		element.attr("src", localPath);
		element.removeAttr("srcset");
	}
}
