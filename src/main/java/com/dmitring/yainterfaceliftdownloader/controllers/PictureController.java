package com.dmitring.yainterfaceliftdownloader.controllers;

import com.dmitring.yainterfaceliftdownloader.domain.InterfaceliftPicture;
import com.dmitring.yainterfaceliftdownloader.domain.PictureStatus;
import com.dmitring.yainterfaceliftdownloader.repositories.PictureRepository;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@CrossOrigin
@RestController
public class PictureController {
    private final PictureRepository pictureRepository;

    @Autowired
    public PictureController(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    @Transactional
    @RequestMapping("/pictures/all")
    public Iterable<InterfaceliftPicture> getAllPictures() {
        return pictureRepository.findAll();
    }

    @Transactional
    @RequestMapping("/pictures")
    public Page<InterfaceliftPicture> getPaginatedPictures(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("status") @NotEmpty Collection<PictureStatus> statuses) {
        return pictureRepository.findByStatusIn(statuses, new PageRequest(page, size));
    }
}
