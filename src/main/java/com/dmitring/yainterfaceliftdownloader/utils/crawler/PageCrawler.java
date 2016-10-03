package com.dmitring.yainterfaceliftdownloader.utils.crawler;

import com.dmitring.yainterfaceliftdownloader.domain.ParsedPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class PageCrawler {
    private static final Logger log = Logger.getLogger(PageCrawler.class.getName());

    private final InterfaceliftPageUrlProvider pageUrlProvider;
    private final InterfaceliftPageParser pageParser;
    private final WebPageDownloader webPageDownloader;

    @Autowired
    public PageCrawler(InterfaceliftPageUrlProvider pageUrlProvider,
                       InterfaceliftPageParser pageParser,
                       WebPageDownloader webPageDownloader) {
        this.pageUrlProvider = pageUrlProvider;
        this.pageParser = pageParser;
        this.webPageDownloader = webPageDownloader;
    }

    public ParsedPage getPagePictureInfo(int pageNumber) {
        ParsedPage parsedPage = null;

        try {
            String pageUrl = pageUrlProvider.getPageUrlString(pageNumber);
            String pageContent = webPageDownloader.getWebPageContent(pageUrl);
            parsedPage = pageParser.parsePage(pageContent);
            log.fine(String.format("Page number_%d has been successfully parsed", pageNumber));
        } catch (Exception e) {
            log.warning(String.format("Page number_%d has parsed unsuccessfully, reason: %s", pageNumber, e.getMessage()));
        }

        return parsedPage;
    }
}
