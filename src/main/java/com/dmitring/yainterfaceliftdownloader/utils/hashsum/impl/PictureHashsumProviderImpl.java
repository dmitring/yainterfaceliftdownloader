package com.dmitring.yainterfaceliftdownloader.utils.hashsum.impl;

import com.dmitring.yainterfaceliftdownloader.domain.Picture;
import com.dmitring.yainterfaceliftdownloader.utils.hashsum.PictureHashsumProvider;
import com.dmitring.yainterfaceliftdownloader.utils.pictureStreams.PictureStreamProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

@Component
public class PictureHashsumProviderImpl implements PictureHashsumProvider {
    private static final Logger log = Logger.getLogger(PictureHashsumProviderImpl.class.getName());

    private final PictureStreamProvider pictureStreamProvider;

    @Autowired
    public PictureHashsumProviderImpl(PictureStreamProvider pictureStreamProvider) {
        this.pictureStreamProvider = pictureStreamProvider;
    }

    @Override
    public String getHashsum(Picture picture) {
        String result;

        try (InputStream pictureInputStream = pictureStreamProvider.getInputStream(picture)) {
            result = DigestUtils.md5DigestAsHex(pictureInputStream);
        } catch (IOException e) {
            log.warning(String.format("Could not get hash of the picture: %s, -- because of an IOError: %s",
                    picture.toString(), e.getMessage()));
            result = null;
        }

        return result;
    }
}
