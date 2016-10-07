package com.dmitring.yainterfaceliftdownloader.services.crawler;

import com.dmitring.yainterfaceliftdownloader.domain.ParsedPage;

/**
 * Parse string content of interfacelift web page and return contained pictures and existence of next page
 */
public interface InterfaceliftPageParser {
    ParsedPage parsePage(String pageContent);
}
