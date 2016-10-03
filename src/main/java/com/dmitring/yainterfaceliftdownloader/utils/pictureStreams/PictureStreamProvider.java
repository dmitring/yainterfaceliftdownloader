package com.dmitring.yainterfaceliftdownloader.utils.pictureStreams;

import com.dmitring.yainterfaceliftdownloader.domain.Picture;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class PictureStreamProvider {
    public InputStream getInputStream(Picture picture) throws IOException {
        final String pictureFilePath = picture.getFilePath();
        return getInputStream(pictureFilePath);
    }

    public InputStream getInputStream(String pictureFilePath) throws IOException {
        validate(pictureFilePath);
        return new BufferedInputStream(new FileInputStream(pictureFilePath));
    }

    public OutputStream getOutputStream(Picture picture) throws IOException {
        final String pictureFilePath = picture.getFilePath();
        return getOutputStream(pictureFilePath);
    }

    public OutputStream getOutputStream(String pictureFilePath) throws IOException {
        validate(pictureFilePath);
        return new BufferedOutputStream(new FileOutputStream(pictureFilePath));
    }

    private void validate(String filePath) {
        if (filePath == null || filePath.isEmpty())
            throw new IllegalArgumentException(
                    String.format("Picture must has a filePath to get a stream, filePath: %s", filePath));
    }
}