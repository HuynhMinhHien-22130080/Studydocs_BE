package com.mobile.studydocs.model.entity;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Follower {
    @DocumentId
    private String followerId;
    private String targetId;
    private List<DocumentReference> followerRefs;

}
