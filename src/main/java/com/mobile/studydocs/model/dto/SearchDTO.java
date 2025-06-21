package com.mobile.studydocs.model.dto;

import com.mobile.studydocs.model.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SearchDTO {
    private List<Document> documents;

    public SearchDTO(List<Document> documents) {
        this.documents = documents;
    }
}

