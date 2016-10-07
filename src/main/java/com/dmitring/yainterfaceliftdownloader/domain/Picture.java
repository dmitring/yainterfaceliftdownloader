package com.dmitring.yainterfaceliftdownloader.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * It's a concrete picture. There can be several pictures in InterfaceliftPictures. They can be different by resolution.
 * Now there are only thumbnail and fullPicture in InterfaceliftPicture.
 */
@Embeddable
@Access(AccessType.FIELD)
@Data
@NoArgsConstructor
public class Picture {
    @Column(unique = true)
    private String downloadUrl;
    @Column(unique = true)
    private String filePath;
    @Column
    private String fileMd5HexHash;
    @Transient
    private boolean broken;

    public Picture(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
