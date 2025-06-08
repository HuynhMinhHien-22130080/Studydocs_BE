package com.mobile.studydocs.model.messaging;

import com.mobile.studydocs.model.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentsMessage {
    private String documentId;
    private String userName;
    private String title;
    private Timestamp createAt;

    public String getDescription() {
        return "Tài liệu có mã số: " + documentId + " với tiêu đề \"" + title + "\" được tạo bởi " + userName + " vào lúc " + createAt.toString() + ".";
    }

    public DocumentsMessage(Document document){
        this.documentId = document.getId();
        this.userName = document.getUserId();
        this.title = document.getTitle();
        this.createAt = document.getCreatedAt();
    }
}
