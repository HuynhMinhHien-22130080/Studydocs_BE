package com.mobile.studydocs.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Following {
    private String userId;
    private boolean notifyEnable;
}
