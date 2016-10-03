package com.dmitring.yainterfaceliftdownloader.utils.pictureStreams;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Component
public class UrlStreamProvider {

    @Value("${com.dmitring.yainterfaceliftdownloader.urlReadTimeout}")
    private int readTimeoutMillis;

    @Value("${com.dmitring.yainterfaceliftdownloader.urlConnectionTimeout}")
    private int connectTimeoutMillis;

    public URLConnection getConnection(String urlString) throws IOException {
        checkUrlString(urlString);
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        connection.setReadTimeout(readTimeoutMillis);
        connection.setConnectTimeout(connectTimeoutMillis);
        connection.setAllowUserInteraction(false);
        return connection;
    }

    public InputStream getInputStream(URLConnection connection) throws IOException {
        return new BufferedInputStream(connection.getInputStream());
    }

    public InputStream getInputStream(String urlString) throws IOException {
        final URLConnection connection = getConnection(urlString);
        return getInputStream(connection);
    }

    private void checkUrlString(String urlString) {
        if (urlString == null || urlString.isEmpty())
            throw new IllegalArgumentException(
                    String.format("urlString must not be null or be empty, urlString=%s", urlString));
    }
}
