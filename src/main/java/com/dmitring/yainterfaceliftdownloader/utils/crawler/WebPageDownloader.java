package com.dmitring.yainterfaceliftdownloader.utils.crawler;

import java.io.IOException;

public interface WebPageDownloader {
    String getWebPageContent(String webPageUrl) throws IOException;
}
