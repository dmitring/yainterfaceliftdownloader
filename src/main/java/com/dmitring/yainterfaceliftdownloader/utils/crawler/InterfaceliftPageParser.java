package com.dmitring.yainterfaceliftdownloader.utils.crawler;

import com.dmitring.yainterfaceliftdownloader.domain.ParsedPage;

public interface InterfaceliftPageParser {
    ParsedPage parsePage(String pageContent);
}
