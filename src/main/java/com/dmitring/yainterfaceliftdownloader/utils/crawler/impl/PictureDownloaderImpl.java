package com.dmitring.yainterfaceliftdownloader.utils.crawler.impl;

import com.dmitring.yainterfaceliftdownloader.utils.crawler.PictureDownloader;
import com.dmitring.yainterfaceliftdownloader.utils.pictureStreams.PictureStreamProvider;
import com.dmitring.yainterfaceliftdownloader.utils.pictureStreams.UrlStreamProvider;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

@Component
public class PictureDownloaderImpl implements PictureDownloader {
    private static final Logger log = Logger.getLogger(PictureDownloaderImpl.class.getName());

    private final UrlStreamProvider inputStreamProvider;
    private final PictureStreamProvider outputStreamProvider;

    @Autowired
    public PictureDownloaderImpl(UrlStreamProvider inputStreamProvider, PictureStreamProvider outputStreamProvider) {
        this.inputStreamProvider = inputStreamProvider;
        this.outputStreamProvider = outputStreamProvider;
    }

    public boolean download(String sourceUrl, String destinationFilePath) {
        try {
            doCopy(sourceUrl, destinationFilePath);
        } catch (Exception e) {
            tryDeleteFile(destinationFilePath);
            log.warning(String.format("Could not download and save a picture by url %s," +
                            "filename %s, because of the reason: %s",
                    sourceUrl, destinationFilePath, e.getMessage()));

            return false;
        }

        return true;
    }

    private void tryDeleteFile(String destinationFilePath) {
        final Path filePath = Paths.get(destinationFilePath);
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            log.warning(String.format("Could not delete file while cleaning because of an error, filePath: %s, error: %s",
                    destinationFilePath, e.getMessage()));
        }
    }

    private void doCopy(String sourceUrl, String destinationFilePath) throws IOException {
        try (
                InputStream inputStream = inputStreamProvider.getInputStream(sourceUrl);
                OutputStream outputStream = outputStreamProvider.getOutputStream(destinationFilePath)
        ) {
            IOUtils.copy(inputStream, outputStream);
        }
    }
}
