package com.rag.poc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
public class ChatService {

    private final OllamaChatModel ollamaChatModel;
    private final VectorStore vectorStore;

    String systemPrompt = """
                You are a precise question-answering assistant.
                
                Rules:
                - Answer ONLY what is asked.
                - Use ONLY the provided context.
                - Do NOT include extra details.
                - If the answer is not in the context, say:
                  "The answer is not available in the provided documents."
                - Keep the answer concise and specific.
                """;

    public ChatService(OllamaChatModel ollamaChatModel, VectorStore vectorStore) {
        this.ollamaChatModel = ollamaChatModel;
        this.vectorStore = vectorStore;
    }

    public String ask(String message) {
        log.info("Received user query: {}", message);

        String docType = classifyQuestion(message);
        log.info("Classified question into docType: {}", docType);

        SearchRequest searchRequest = SearchRequest.builder()
                .query(message)
                .topK(2)
                .filterExpression("doc_type == '" + docType + "'")
                .build();

//        This performs: Embedding(query) -> Vector similarity search -> Return closest chunks
        List<Document> docs = vectorStore.similaritySearch(searchRequest);
        log.info("Retrieved {} relevant document chunks from vector store", docs.size());

        docs.forEach(d -> log.debug("Retrieved chunk: {}", d.getText()));

//        It automatically injects retrieved context into the prompt.
        QuestionAnswerAdvisor advisor = QuestionAnswerAdvisor
                .builder(vectorStore)
                .searchRequest(searchRequest)
                .build();

        log.debug("QuestionAnswerAdvisor created with search request");

        return ChatClient.builder(ollamaChatModel)
                .build()
                .prompt()
                .system(systemPrompt)
                .advisors(advisor)
                .user(message)
                .call()
                .content();
    }

    private String classifyQuestion(String question) {
        log.debug("Classifying question: {}", question);

        question = question.toLowerCase();

        if (question.contains("leave")
                || question.contains("wfh")
                || question.contains("insurance")
                || question.contains("laptop")) {

            log.debug("Question classified as company_policy");
            return "company_policy";
        }

        log.debug("Question classified as company_info");
        return "company_info";
    }

    public Flux<String> askStream(String message) {

        SearchRequest searchRequest = SearchRequest.builder()
                .query(message)
                .topK(1)
                .build();

        QuestionAnswerAdvisor advisor =
                QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(searchRequest)
                        .build();

        return ChatClient.builder(ollamaChatModel)
                .build()
                .prompt()
                .system(systemPrompt)
                .advisors(advisor)
                .user(message)
                .stream()
                .content();
    }

}