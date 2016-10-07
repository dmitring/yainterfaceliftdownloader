package com.dmitring.yainterfaceliftdownloader.services.crawler.impl;

import com.dmitring.yainterfaceliftdownloader.domain.ParsedPage;
import com.dmitring.yainterfaceliftdownloader.services.crawler.InterfaceliftPageParser;
import com.dmitring.yainterfaceliftdownloader.services.crawler.InterfaceliftPageUrlProvider;
import com.dmitring.yainterfaceliftdownloader.services.crawler.PageCrawler;
import com.dmitring.yainterfaceliftdownloader.services.crawler.WebPageDownloader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class PageCrawlerImpl implements PageCrawler {
    private static final Logger log = Logger.getLogger(PageCrawlerImpl.class.getName());

    private final InterfaceliftPageUrlProvider pageUrlProvider;
    private final InterfaceliftPageParser pageParser;
    private final WebPageDownloader webPageDownloader;

    @Autowired
    public PageCrawlerImpl(InterfaceliftPageUrlProvider pageUrlProvider,
                           InterfaceliftPageParser pageParser,
                           WebPageDownloader webPageDownloader) {
        this.pageUrlProvider = pageUrlProvider;
        this.pageParser = pageParser;
        this.webPageDownloader = webPageDownloader;
    }

    @Override
    public ParsedPage getPagePictureInfo(int pageNumber) {
        ParsedPage parsedPage = null;

        try {
            String pageUrl = pageUrlProvider.getPageUrlString(pageNumber);
            String pageContent = webPageDownloader.getWebPageContent(pageUrl);
            parsedPage = pageParser.parsePage(pageContent);
            log.info(String.format("Page number_%d has been successfully parsed", pageNumber));
        } catch (Exception e) {
            log.warning(String.format("Page number_%d has parsed unsuccessfully, reason: %s", pageNumber, e.getMessage()));
        }

        return parsedPage;
    }
}
