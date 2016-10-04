package com.dmitring.yainterfaceliftdownloader.controllers.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collection;

@Data
public class ConsideringRequest {
    private final Collection<String> acceptedIds;
    private final Collection<String> returnedToConsiderIds;
    private final Collection<String> rejectedIds;

    @JsonCreator
    public ConsideringRequest(@JsonProperty("accepted") Collection<String> acceptedIds,
                              @JsonProperty("returnedToConsider") Collection<String> returnedToConsiderIds,
                              @JsonProperty("rejected") Collection<String> rejectedIds) {
        this.acceptedIds = acceptedIds;
        this.returnedToConsiderIds = returnedToConsiderIds;
        this.rejectedIds = rejectedIds;
    }
}
