package com.mobile.studydocs.model.entity;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Following {
    @DocumentId
    private String followingId;
    private DocumentReference followerRef;
    private DocumentReference targetRef;
    private boolean notifyEnable;
}
