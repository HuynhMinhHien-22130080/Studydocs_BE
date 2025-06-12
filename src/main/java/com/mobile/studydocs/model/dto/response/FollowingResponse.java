package com.mobile.studydocs.model.dto.response;

import lombok.Builder;

@Builder
public record FollowingResponse(String followingId, String targetId, String name, String avatarUrl,
                                boolean notifyEnables) {
}
