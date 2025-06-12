package com.mobile.studydocs.model.entity;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Following {
    @DocumentId
    private String followingId;
    private DocumentReference followerRef;
    private DocumentReference targetRef;
    private boolean notifyEnable;
}
