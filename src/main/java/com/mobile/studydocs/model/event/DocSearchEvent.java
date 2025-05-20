package com.mobile.studydocs.model.event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocSearchEvent {
    private String query;
    private String userId;
}
