package seminar3;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlerManager {
	String baseDir;
	private HtmlParser htmlParser;
	private Saver saver;
	private FileManager fileManager;
	private PathRegistry pathRegistry;
	
	public CrawlerManager(String baseDir) {
		this.baseDir = baseDir;
		this.fileManager = new FileManager(baseDir);
		this.saver = new Saver(baseDir);
		this.pathRegistry = new PathRegistry();
		this.htmlParser = new HtmlParser(fileManager, saver, pathRegistry);
	}
	
	public void start(String startUrl, String baseDir, int maxDepth) {
		crawl(startUrl, baseDir, 1, maxDepth);
	}
	
	private void crawl(String url, String baseDir,int depth, int maxDepth) {
		if (pathRegistry.isRegistered(url)) return;
		
		if (depth > maxDepth) return;
		
		try {
			String path = fileManager.generateHtml();
			pathRegistry.register(url, path);
			
			Document doc = Jsoup.connect(url).get();
			
			htmlParser.rewriteToLocalPaths(doc);
			Elements links = htmlParser.extractNextLinks(doc);
			for (Element link : links) {
				String nextUrl = link.absUrl("href");
				crawl(nextUrl, baseDir, depth+1, maxDepth);
			}
			
			htmlParser.rewriteNextLinks(links);
			saver.saveHtml(baseDir + path, doc);
		} catch(IOException e) {
			pathRegistry.unregister(url);
			e.printStackTrace();	
		}
	}
}
