package com.mobile.studydocs.controller;

import com.mobile.studydocs.model.dto.SearchDTO;
import com.mobile.studydocs.response.BaseResponse;
import com.mobile.studydocs.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/document")
public class DocumentController {
    private final DocumentService documentService;


    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;

    }

    @GetMapping( "/searchByTitle")
    public ResponseEntity<BaseResponse> searchByTitle(@RequestParam("keyword") String title ){
        SearchDTO searchDTO = documentService.searchByTitle(title);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách thành công", searchDTO));
    }
    @GetMapping( "/searchByUniversity")
    public ResponseEntity<BaseResponse> searchByUniversity(@RequestParam("keyword") String university ){
        SearchDTO searchDTO = documentService.searchByUniversity(university);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách thành công", searchDTO));
    }
    @GetMapping( "/searchBySubject")
    public ResponseEntity<BaseResponse> searchBySubject(@RequestParam("keyword") String subject ){
        SearchDTO searchDTO = documentService.searchBySubject(subject);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách thành công", searchDTO));
    }

    /**
     * Lấy tất cả tài liệu của một user cụ thể
     * @param userId ID của user cần lấy tài liệu
     * @return BaseResponse chứa danh sách tài liệu
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponse> getDocumentsByUserId(@PathVariable String userId) {
        SearchDTO searchDTO = documentService.getDocumentsByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách tài liệu của user thành công", searchDTO));
    }
    @GetMapping( "/getAllDocument")
    public ResponseEntity<BaseResponse> getAll(){
        System.out.println("da gui request lay tat ca doc");
        SearchDTO searchDTO = documentService.getAll();

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách thành công", searchDTO));
    }
    @GetMapping( "/getDocSaveInLibrary")
    public ResponseEntity<BaseResponse> getDocSaveInLibrary(){
//        @RequestAttribute("userId") String userid
        String userid="2Wy0mADO5lXPSRqxXpbCHjSmHV93";
        SearchDTO searchDTO = documentService.getDocSaveInLibrary(userid);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách thành công", searchDTO));
    }
    @GetMapping( "/saveToLibrary")
    public ResponseEntity<BaseResponse> saveDocument(@RequestParam("keyword") String idDocument,@RequestAttribute("userId") String userid ){
        boolean success = documentService.saveToLibrary(idDocument,userid);
        System.out.println("save doc: "+idDocument);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách thành công", success));
    }
    // ===== hao lam phần này (upload document + file) =====
    /**
     * Upload document metadata + file to Firebase Storage and Firestore
     */
    @PostMapping("/upload")
    public ResponseEntity<BaseResponse> uploadDocument(
            @RequestPart("document") Document document,
            @RequestPart("file") MultipartFile file,
            @RequestAttribute("userId")String userId) {
        try {
            Document savedDoc = documentService.uploadDocument(document, file,userId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse(HttpStatus.OK.value(), "Upload tài liệu thành công", savedDoc));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi upload tài liệu", null));
        }
    }
    // ===== end hao lam phần này =====
    // ===== phần này của Hảo =====
//    @GetMapping("/my-documents")
//    public BaseResponse getMyDocuments(@RequestAttribute("userId") String userId) {
//        SearchDTO searchDTO = documentService.getDocumentsByUserId(userId);
//        return new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách tài liệu thành công", searchDTO);
//    }
    // ===== end phần này của Hảo =====
    @GetMapping("/detail/{documentId}")
    public ResponseEntity<BaseResponse> getDocumentDetail(@PathVariable String documentId) {
        var optionalDoc = documentService.getDocumentById(documentId);
        System.out.println("Doc found? " + optionalDoc.isPresent());
        optionalDoc.ifPresent(doc -> System.out.println("Doc DTO: " + doc));

        return optionalDoc
                .map(dto -> ResponseEntity.status(HttpStatus.OK)
                        .body(new BaseResponse(HttpStatus.OK.value(), "Lấy chi tiết tài liệu thành công", dto)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(HttpStatus.NOT_FOUND.value(), "Tài liệu không tồn tại", null)));
    }
    @GetMapping("/my-documents")
    public BaseResponse getMyDocuments() {
//        @RequestParam("userId") String userId
        String userId="2Wy0mADO5lXPSRqxXpbCHjSmHV93";
        SearchDTO searchDTO = documentService.getDocumentsByUserId(userId);
        return new BaseResponse(HttpStatus.OK.value(), "Lấy danh sách tài liệu thành công", searchDTO);
    }
}
