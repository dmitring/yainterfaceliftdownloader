package com.dmitring.yainterfaceliftdownloader.domain;

/**
 * Represent InterfaceliftPictures class status.
 * It's only correct to change state using special transformation in PictureHandler class.
 * The statuses and transformations in PictureHandler class are FiniteStateMachine.
 */
public enum PictureStatus {
    JUST_FOUND,
    CONSIDERING,
    ACCEPTED,
    REJECTED,
    DOWNLOADED,
    ERRORFUL
}
