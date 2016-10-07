package com.dmitring.yainterfaceliftdownloader.services.crawler;

import com.dmitring.yainterfaceliftdownloader.domain.ParsedPage;

/**
 * Parse web page by page number and return pictures and info about next page
 */
public interface PageCrawler {
    ParsedPage getPagePictureInfo(int pageNumber);
}
