package com.mobile.studydocs.model.dto;

import com.mobile.studydocs.model.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDTO {
    private List<DocumentDTO> documents;
}
