package com.dmitring.yainterfaceliftdownloader.utils.hashsum;

import com.dmitring.yainterfaceliftdownloader.domain.Picture;
import com.dmitring.yainterfaceliftdownloader.utils.pictureStreams.PictureStreamProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

@Component
public class PictureHashsumProvider {
    private static final Logger log = Logger.getLogger(PictureHashsumProvider.class.getName());

    private final PictureStreamProvider pictureStreamProvider;

    @Autowired
    public PictureHashsumProvider(PictureStreamProvider pictureStreamProvider) {
        this.pictureStreamProvider = pictureStreamProvider;
    }

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
