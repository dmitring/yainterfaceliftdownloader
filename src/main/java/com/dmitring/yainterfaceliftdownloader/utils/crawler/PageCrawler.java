package com.dmitring.yainterfaceliftdownloader.utils.crawler;

import com.dmitring.yainterfaceliftdownloader.domain.ParsedPage;

public interface PageCrawler {
    ParsedPage getPagePictureInfo(int pageNumber);
}
