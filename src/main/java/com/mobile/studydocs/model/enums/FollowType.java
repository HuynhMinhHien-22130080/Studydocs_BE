package com.mobile.studydocs.model.enums;

public enum FollowType {
    USER("users");

    private final String value;

    FollowType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
