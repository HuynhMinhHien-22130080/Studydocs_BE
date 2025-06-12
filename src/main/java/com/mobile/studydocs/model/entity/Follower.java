package com.mobile.studydocs.model.entity;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.annotation.DocumentId;
import com.mobile.studydocs.model.enums.FollowType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follower {
    @DocumentId
    private String followerId ;
    private String targetId;
    private FollowType targetType;
    private List<DocumentReference> followerRefs;

}
