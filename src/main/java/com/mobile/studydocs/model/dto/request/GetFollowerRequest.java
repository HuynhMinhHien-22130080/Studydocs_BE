package com.mobile.studydocs.model.dto.request;

import com.mobile.studydocs.model.enums.FollowType;

public record GetFollowerRequest(String targetId, FollowType type) {
}
