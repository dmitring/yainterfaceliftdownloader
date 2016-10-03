package com.dmitring.yainterfaceliftdownloader.repositories;

import com.dmitring.yainterfaceliftdownloader.domain.InterfaceliftPicture;
import com.dmitring.yainterfaceliftdownloader.domain.PictureStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface PictureRepository extends PagingAndSortingRepository<InterfaceliftPicture, String> {
    Collection<InterfaceliftPicture> findByStatusIn(Collection<PictureStatus> statuses);
    Page<InterfaceliftPicture> findByStatusIn(Collection<PictureStatus> statuses, Pageable pageable);
}