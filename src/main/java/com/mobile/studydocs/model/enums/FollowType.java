package com.mobile.studydocs.model.enums;

import lombok.Getter;

@Getter
public enum FollowType {
    USER("users");

    private final String value;

    FollowType(String value) {
        this.value = value;
    }


}
