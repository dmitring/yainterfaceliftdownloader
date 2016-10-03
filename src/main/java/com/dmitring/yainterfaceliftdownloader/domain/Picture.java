package com.dmitring.yainterfaceliftdownloader.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
