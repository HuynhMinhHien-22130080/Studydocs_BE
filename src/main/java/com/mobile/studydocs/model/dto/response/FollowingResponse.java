package com.mobile.studydocs.model.dto.response;

import com.mobile.studydocs.model.enums.FollowType;
import lombok.Builder;

@Builder
public record FollowingResponse(String followingId, String targetId, FollowType targetType, String name, String avatarUrl,
                                boolean notifyEnables) {
}
