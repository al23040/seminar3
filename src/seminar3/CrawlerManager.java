package seminar3;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
	private ExecutorService executor;
	private AtomicInteger activeTasks = new AtomicInteger(0);
	
	public CrawlerManager(String baseDir) {
		this.baseDir = baseDir;
		this.fileManager = new FileManager(baseDir);
		this.saver = new Saver(baseDir);
		this.pathRegistry = new PathRegistry();
		this.htmlParser = new HtmlParser(fileManager, saver, pathRegistry);
		this.executor = Executors.newFixedThreadPool(40);
	}
	
	public void start(String startUrl, String baseDir, int maxDepth) {
		crawl(startUrl, baseDir, 1, maxDepth);
		try {
			while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void crawl(String url, String baseDir,int depth, int maxDepth) {
		if (depth > maxDepth) return;
		synchronized (this) {
            if (pathRegistry.isRegistered(url)) return;
            
            String path = fileManager.generateHtml();
            pathRegistry.register(url, path);
        }
		activeTasks.incrementAndGet();
		executor.submit(() -> {
            try {
                Document doc = Jsoup.connect(url).get();
                
                htmlParser.rewriteToLocalPaths(doc);
                Elements links = htmlParser.extractNextLinks(doc);
                
                for (Element link : links) {
                    String nextUrl = link.absUrl("href");
                    crawl(nextUrl, baseDir, depth + 1, maxDepth);
                }
                
                htmlParser.rewriteNextLinks(links);
                
                String savedPath = pathRegistry.getPath(url);
                saver.saveHtml(baseDir + savedPath, doc);
                
            } catch(IOException e) {
                pathRegistry.unregister(url);
                System.err.println(url + " の処理中にエラーが発生しました");
            }finally {
            	if (activeTasks.decrementAndGet() == 0) {
            		executor.shutdown();
            	}
            }
        });
	}
}
