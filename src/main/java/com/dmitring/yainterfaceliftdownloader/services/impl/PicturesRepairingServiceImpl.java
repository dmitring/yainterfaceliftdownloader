package com.dmitring.yainterfaceliftdownloader.services.impl;

import com.dmitring.yainterfaceliftdownloader.domain.InterfaceliftPicture;
import com.dmitring.yainterfaceliftdownloader.domain.Picture;
import com.dmitring.yainterfaceliftdownloader.domain.PictureStatus;
import com.dmitring.yainterfaceliftdownloader.repositories.PictureRepository;
import com.dmitring.yainterfaceliftdownloader.services.PictureDownloadManager;
import com.dmitring.yainterfaceliftdownloader.services.PicturesRepairingService;
import com.dmitring.yainterfaceliftdownloader.utils.hashsum.PictureHashsumProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Logger;

@Service
public class PicturesRepairingServiceImpl implements PicturesRepairingService {
    private final Logger log = Logger.getLogger(PicturesRepairingServiceImpl.class.getName());

    private final PictureRepository pictureRepository;
    private final PictureDownloadManager downloadManager;
    private final PictureHashsumProvider pictureHashsumProvider;

    @Autowired
    public PicturesRepairingServiceImpl(PictureRepository pictureRepository,
                                        PictureDownloadManager downloadManager,
                                        PictureHashsumProvider pictureHashsumProvider) {
        this.pictureRepository = pictureRepository;
        this.downloadManager = downloadManager;
        this.pictureHashsumProvider = pictureHashsumProvider;
    }

    @Scheduled(fixedDelayString = "${com.dmitring.yainterfaceliftdownloader.checkIntegrityDelay}")
    @Transactional
    @Override
    public void checkPictureHashsums() {
        log.fine("The picture repairing process has started");
        final CompletableFuture<?>[] results = pictureRepository.findByStatusIn(Arrays.asList(
                PictureStatus.CONSIDERING,
                PictureStatus.ACCEPTED,
                PictureStatus.REJECTED,
                PictureStatus.DOWNLOADED))
            .stream()
            .map(this::checkSinglePicture)
            .toArray(CompletableFuture[]::new);
        final CompletableFuture<Void> result = CompletableFuture.allOf(results);
        result.thenRun(() -> log.fine("The picture repairing process has stopped"));
        result.join();
    }

    private CompletableFuture<Void> checkSinglePicture(InterfaceliftPicture picture) {
        CompletableFuture<Void> result = handleCertainPicture(picture, picture.getThumbnail(), downloadManager::repairThumbnail);
        if (picture.getStatus() == PictureStatus.DOWNLOADED) {
            result = CompletableFuture.allOf(result,
                    handleCertainPicture(picture, picture.getFullPicture(), downloadManager::repairFullPicture));
        }
        return result;
    }

    private CompletableFuture<Void> handleCertainPicture(InterfaceliftPicture picture,
                                      Picture certainPictureToHandle,
                                      Function<InterfaceliftPicture, CompletableFuture<Boolean>> downloadRoutine) {
        if (isPictureCheckSumCorrect(certainPictureToHandle))
            return CompletableFuture.completedFuture(null);

        log.warning(String.format("The picture has incorrect hashsum %s, it will be redownloaded", picture.toString()));
        certainPictureToHandle.setBroken(true);
        final CompletableFuture<Void> resultFuture = downloadRoutine.apply(picture).thenAccept(result -> {
            if (result) {
                certainPictureToHandle.setBroken(false);
                pictureRepository.save(picture);
            }
        });
        return resultFuture;
    }

    private boolean isPictureCheckSumCorrect(Picture picture) {
        return picture.getFileMd5HexHash().equals(pictureHashsumProvider.getHashsum(picture));
    }
}
