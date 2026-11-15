package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class AssignmentApiDocsController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping(value = "/api-docs.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAssignmentApiDocs() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/api-docs.json");
        ObjectNode specification = (ObjectNode) objectMapper.readTree(resource.getInputStream());

        ObjectNode multipart = (ObjectNode) specification
                .path("paths")
                .path("/api/users/{userId}")
                .path("patch")
                .path("requestBody")
                .path("content")
                .path("multipart/form-data");

        ObjectNode encoding = multipart.withObject("encoding");
        ObjectNode userUpdateEncoding = encoding.withObject("userUpdateRequest");
        userUpdateEncoding.put("contentType", MediaType.APPLICATION_JSON_VALUE);

        return ResponseEntity.ok(objectMapper.writeValueAsString(specification));
    }
}
