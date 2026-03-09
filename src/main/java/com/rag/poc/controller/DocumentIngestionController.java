package com.rag.poc.controller;

import com.rag.poc.service.DocumentIngestionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DocumentIngestionController {

    private final DocumentIngestionService documentIngestionService;

    public DocumentIngestionController(DocumentIngestionService documentIngestionService) {
        this.documentIngestionService = documentIngestionService;
    }

    @PostMapping("/document/inject")
    public String injectDocument() {
        documentIngestionService.ingestDocuments();
        return "Document injected successfully!";
    }

}
