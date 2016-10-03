package com.dmitring.yainterfaceliftdownloader.controllers;

import com.dmitring.yainterfaceliftdownloader.controllers.messages.ConsideringRequest;
import com.dmitring.yainterfaceliftdownloader.domain.InterfaceliftPicture;
import com.dmitring.yainterfaceliftdownloader.domain.PictureHandler;
import com.dmitring.yainterfaceliftdownloader.repositories.PictureRepository;
import com.dmitring.yainterfaceliftdownloader.services.PictureDownloadManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@CrossOrigin
@RestController
public class ConsiderController {
    private final PictureRepository pictureRepository;
    private final PictureDownloadManager pictureDownloader;
    private final PictureHandler pictureHandler;

    @Autowired
    public ConsiderController(PictureRepository pictureRepository, PictureDownloadManager pictureDownloader, PictureHandler pictureHandler) {
        this.pictureRepository = pictureRepository;
        this.pictureDownloader = pictureDownloader;
        this.pictureHandler = pictureHandler;
    }

    @Transactional
    @RequestMapping(value = "/pictures/user_consider", method = RequestMethod.POST)
    public void handleUserConsidering(@RequestBody ConsideringRequest consideringRequest) {
        Collection<String> acceptedIds = consideringRequest.getAcceptedIds();
        Collection<String> returnedToConsiderIds = consideringRequest.getReturnedToConsiderIds();
        Collection<String> rejectedIds = consideringRequest.getRejectedIds();

        if (!acceptedIds.isEmpty()) {
            pictureRepository.findAll(acceptedIds).forEach(this::handleAccept);
        }
        if (!returnedToConsiderIds.isEmpty()) {
            pictureRepository.findAll(returnedToConsiderIds).forEach(this::handleReturnToConsider);
        }
        if (!rejectedIds.isEmpty()) {
            pictureRepository.findAll(rejectedIds).forEach(this::handleReject);
        }
    }

    private void handleAccept(InterfaceliftPicture acceptingPicture) {
        pictureHandler.handleAccept(acceptingPicture);
        pictureDownloader.downloadFullPicture(acceptingPicture);
        pictureRepository.save(acceptingPicture);
    }

    private void handleReturnToConsider(InterfaceliftPicture returningPicture) {
        pictureDownloader.ensureCancelDownloadFullPicture(returningPicture);
        pictureHandler.handleReturnToConsider(returningPicture);
        pictureRepository.save(returningPicture);
    }

    private void handleReject(InterfaceliftPicture rejectingPicture) {
        pictureDownloader.ensureCancelDownloadFullPicture(rejectingPicture);
        pictureHandler.handleReject(rejectingPicture);
        pictureRepository.save(rejectingPicture);
    }
}
