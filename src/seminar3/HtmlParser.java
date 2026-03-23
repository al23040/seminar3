package seminar3;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import seminar3.extender.CssExtender;
import seminar3.extender.ImageExtender;
import seminar3.extender.JsExtender;
import seminar3.extender.ResourceExtender;

public class HtmlParser {
	private FileManager fileManager;
	private Saver saver;
	private PathRegistry pathRegistry;
	
	private List<ResourceExtender> extenders;
	
	public HtmlParser(FileManager fileManager, Saver saver, PathRegistry pathRegistry) {
		this.fileManager = fileManager;
		this.saver = saver;
		this.pathRegistry = pathRegistry;
		
		this.extenders = new ArrayList<>();
		this.extenders.add(new ImageExtender(this.fileManager, this.saver, this.pathRegistry));
		this.extenders.add(new CssExtender(this.fileManager, this.saver, this.pathRegistry));
        this.extenders.add(new JsExtender(this.fileManager, this.saver, this.pathRegistry));
	}
	
	public void rewriteToLocalPaths(Document doc) {
		for (ResourceExtender extender : extenders) {
			extender.process(doc);
		}
	}
	
	public Elements extractNextLinks(Document doc) {
		return doc.select("a[href]");
	}
	
	public void rewriteNextLinks(Elements links) {
		for (Element link : links) {
			String originalUrl = link.absUrl("href");
			
            if (pathRegistry.isRegistered(originalUrl)) {
                String path = pathRegistry.getPath(originalUrl);
                link.attr("href", path);
            }
		}
	}
}
