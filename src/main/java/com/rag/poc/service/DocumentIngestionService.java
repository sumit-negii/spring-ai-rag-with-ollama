package com.rag.poc.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentIngestionService {

    private final VectorStore vectorStore;

    public DocumentIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingestDocuments() {

        Resource companyInfo = new ClassPathResource("docs/company_info.txt");
        Resource policyDoc = new ClassPathResource("docs/company_policies.txt");

        ingest(companyInfo, "company_info");
        ingest(policyDoc, "company_policy");
    }

    private void ingest(Resource resource, String docType) {

        // Read the pdf
        TikaDocumentReader reader = new TikaDocumentReader(resource);

        // Split the document content into chunks, NOTE: Because LLMs cannot process large documents at once.
        TextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(100)
                .build();

        List<Document> documents = splitter.split(reader.read());

        // useful for multi-document systems, this allows filtering later.
        documents.forEach(doc ->
                doc.getMetadata().put("doc_type", docType));

        // Store the chunks in vector db
        vectorStore.accept(documents);
    }
}
