# Spring AI RAG with Ollama and pgvector

## Overview

This project demonstrates a Retrieval-Augmented Generation (RAG) system built using Spring Boot. The application retrieves relevant document content from a vector database and provides accurate responses using a local Large Language Model (LLM) running through Ollama.

The system is designed as a simple enterprise-style AI backend that answers questions based only on internal documents.

---

## Architecture

User Question
↓
Spring Boot REST API
↓
ChatService
↓
Vector Similarity Search (pgvector)
↓
Retrieve Relevant Document Chunks
↓
Prompt Augmentation (Spring AI Advisor)
↓
Ollama LLM (Mistral)
↓
Generated Answer

---

## Technologies Used

Backend Framework

* Spring Boot
* Spring Web MVC

AI Integration

* Spring AI
* Ollama

LLM Model

* Mistral

Embeddings

* nomic-embed-text

Vector Database

* PostgreSQL
* pgvector

Document Processing

* Apache Tika

Containerization

* Docker

---

## Project Structure

src/main/java

controller

* ChatController
* DocumentIngestionController

service

* ChatService
* DocumentIngestionService

application

* SpringAiRagWithOllamaApplication

resources

* application.properties
* docs/

  * company_info.txt
  * company_policies.txt

---

## Core Concepts

### Retrieval Augmented Generation (RAG)

RAG combines document retrieval with LLM generation.

Instead of relying only on the LLM knowledge, the system retrieves relevant information from internal documents and injects that context into the LLM prompt.

RAG = Retrieve + Augment + Generate

This improves:

* Accuracy
* Domain knowledge
* Reduces hallucination

---

## Document Ingestion Pipeline

Documents are processed and stored in the vector database through the following steps:

1. Document Reading
   Apache Tika reads files such as TXT, PDF, or DOC.

2. Text Chunking
   Large documents are split into smaller chunks for better retrieval.

3. Embedding Generation
   Each chunk is converted into a vector embedding using the embedding model.

4. Vector Storage
   Embeddings are stored in PostgreSQL using pgvector.

Each chunk also stores metadata such as document type.

---

## Question Answering Flow

1. User sends a question

2. Question classification
   The system determines which document category should be searched.

Example categories

* company_policy
* company_info

3. Vector similarity search
   The query is converted into an embedding and compared with stored document embeddings.

4. Retrieve relevant chunks
   Top matching chunks are returned from the vector database.

5. Prompt augmentation
   Spring AI automatically inserts retrieved chunks into the prompt.

6. LLM response generation
   The prompt is sent to the Ollama model which generates the final answer.

---

## Example Query

User Question
How many leaves are allowed?

Retrieved Context
Employees are entitled to 18 paid leaves per calendar year.

LLM Response
Employees are entitled to 18 paid leaves per calendar year.

---

## ChatService Responsibilities

ChatService implements the main RAG pipeline.

Key steps:

* Receive user query
* Classify question
* Perform vector similarity search
* Retrieve relevant document chunks
* Inject context into the LLM prompt
* Generate the final response

---

## Configuration

Important properties configured in application.properties:

Ollama Configuration

* ollama base URL
* chat model (mistral)
* embedding model (nomic-embed-text)

Database Configuration

* PostgreSQL connection
* pgvector schema initialization

Model Settings

* temperature
* max tokens

---

## Running the Project

Prerequisites

* Docker
* Java 17+
* Maven

Steps

1. Start Docker services

Docker compose starts:

* PostgreSQL with pgvector
* Ollama

2. Pull required models

ollama pull mistral
ollama pull nomic-embed-text

3. Run the Spring Boot application

mvn spring-boot:run

4. Ingest documents

Use the ingestion API to load documents into the vector store.

5. Ask questions

POST /chat

---

## Example API Request

POST /chat

Request Body

{
"message": "How many leaves are allowed?"
}

Response

Employees are entitled to 18 paid leaves per calendar year.

---

## Key Features

* Retrieval-Augmented Generation (RAG)
* Multi-document support
* Metadata-based document filtering
* Vector similarity search
* Local LLM using Ollama
* Modular Spring Boot architecture

---

## Future Improvements

* Streaming responses (real-time token streaming)
* Hybrid search (vector + keyword)
* Conversational memory
* Dynamic document upload API
* Advanced query classification using LLM

---

## Learning Outcomes

This project demonstrates how to:

* Build a RAG pipeline using Spring AI
* Store embeddings in pgvector
* Integrate Ollama with a Java backend
* Retrieve contextual data for LLM responses
* Build enterprise-style AI APIs

---

## Summary

This project implements a complete RAG system where document knowledge is stored in a vector database and retrieved dynamically to generate accurate responses using a locally hosted LLM.
