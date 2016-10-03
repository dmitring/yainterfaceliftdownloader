package com.dmitring.yainterfaceliftdownloader.utils.crawler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InterfaceliftPageUrlProvider {
    @Value("${com.dmitring.yainterfaceliftdownloader.interfaceliftPageUrlPattern}")
    private String pageUrlPattern;

    public String getPageUrlString(int pageNumber) {
        return String.format(pageUrlPattern, pageNumber);
    }
}
