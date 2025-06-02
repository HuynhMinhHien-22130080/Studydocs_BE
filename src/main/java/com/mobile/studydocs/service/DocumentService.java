package com.mobile.studydocs.service;

import com.mobile.studydocs.dao.DocumentDao;
import com.mobile.studydocs.dao.UserDao;
import com.mobile.studydocs.model.dto.SearchDTO;
import com.mobile.studydocs.model.entity.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class DocumentService {
    private final DocumentDao documentDao;
    public DocumentService(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    public SearchDTO searchByTitle(String keyword)  {
        List<Document>res= new ArrayList<>();
        try{
        res.addAll(documentDao.getDocumentsByTitle(keyword));}
        catch(ExecutionException | InterruptedException e){
        }
        return new SearchDTO(res);
    }

    public SearchDTO getAll(){
        List<Document>res= new ArrayList<>();
        try{
        res.addAll(documentDao.getAllDocuments());}
         catch(ExecutionException | InterruptedException e){
            }
        return new SearchDTO(res);
    }
}
