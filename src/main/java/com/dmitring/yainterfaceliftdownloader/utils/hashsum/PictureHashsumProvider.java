package com.dmitring.yainterfaceliftdownloader.utils.hashsum;

import com.dmitring.yainterfaceliftdownloader.domain.Picture;

public interface PictureHashsumProvider {
    String getHashsum(Picture picture);
}
