package com.dmitring.yainterfaceliftdownloader.utils.pictureStreams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public interface UrlStreamProvider {
    URLConnection getConnection(String urlString);
    InputStream getInputStream(URLConnection connection) throws IOException;
    InputStream getInputStream(String urlString) throws IOException;
}
