package com.dmitring.yainterfaceliftdownloader.services.crawler;

/**
 * Construct interfacelift page url string by page number
 */
public interface InterfaceliftPageUrlProvider {
    String getPageUrlString(int pageNumber);
}
