package com.dmitring.yainterfaceliftdownloader.services.crawler.impl;

import com.dmitring.yainterfaceliftdownloader.services.crawler.WebPageDownloader;
import com.dmitring.yainterfaceliftdownloader.utils.pictureStreams.UrlStreamProvider;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

@Component
public class WebPageDownloaderImpl implements WebPageDownloader {
    private final String ALLOWED_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";

    private final UrlStreamProvider urlStreamProvider;

    @Autowired
    public WebPageDownloaderImpl(UrlStreamProvider urlStreamProvider) {
        this.urlStreamProvider = urlStreamProvider;
    }

    @Override
    public String getWebPageContent(String webPageUrl) throws IOException {
        final URLConnection connection = urlStreamProvider.getConnection(webPageUrl);
        connection.setRequestProperty("User-Agent", ALLOWED_USER_AGENT);
        final String encoding = connection.getContentEncoding();
        try (InputStream inputStream = urlStreamProvider.getInputStream(connection)) {
            return IOUtils.toString(inputStream, encoding);
        }
    }
}
