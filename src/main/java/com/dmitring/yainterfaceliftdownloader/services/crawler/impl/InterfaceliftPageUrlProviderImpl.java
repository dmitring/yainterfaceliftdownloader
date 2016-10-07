package com.dmitring.yainterfaceliftdownloader.services.crawler.impl;

import com.dmitring.yainterfaceliftdownloader.services.crawler.InterfaceliftPageUrlProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InterfaceliftPageUrlProviderImpl implements InterfaceliftPageUrlProvider {
    @Value("${com.dmitring.yainterfaceliftdownloader.interfaceliftPageUrlPattern}")
    private String pageUrlPattern;

    @Override
    public String getPageUrlString(int pageNumber) {
        return String.format(pageUrlPattern, pageNumber);
    }
}
