package com.dmitring.yainterfaceliftdownloader.services.crawler;

import java.io.IOException;

/**
 * The interface to get downloaded webPage content ny page url
 */
public interface WebPageDownloader {
    String getWebPageContent(String webPageUrl) throws IOException;
}
