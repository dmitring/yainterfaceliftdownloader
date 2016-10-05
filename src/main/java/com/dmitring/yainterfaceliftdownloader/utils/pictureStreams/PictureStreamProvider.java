package com.dmitring.yainterfaceliftdownloader.utils.pictureStreams;

import com.dmitring.yainterfaceliftdownloader.domain.Picture;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface PictureStreamProvider {
    InputStream getInputStream(Picture picture) throws IOException;
    InputStream getInputStream(String pictureFilePath) throws IOException;
    OutputStream getOutputStream(Picture picture) throws IOException;
    OutputStream getOutputStream(String pictureFilePath) throws IOException;
}