package com.rookies3.myspringbootlab.controller;

import com.rookies3.myspringbootlab.dto.PublisherDTO;
import com.rookies3.myspringbootlab.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
public class PublisherController {
    private final PublisherService publisherService;

    @GetMapping
    public List<PublisherDTO.SimpleResponse> getAll() {
        return publisherService.getAllPublishers();
    }

    @GetMapping("/{id}")
    public PublisherDTO.SimpleResponse getById(@PathVariable Long id) {
        return publisherService.getPublisherById(id);
    }

    @GetMapping("/search")
    public PublisherDTO.SimpleResponse getByName(@RequestParam String name) {
        return publisherService.getPublisherByName(name);
    }

    @PostMapping
    public ResponseEntity<PublisherDTO.SimpleResponse> create(@RequestBody PublisherDTO.Request request) {
        var resp = publisherService.createPublisher(request);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    public PublisherDTO.SimpleResponse update(@PathVariable Long id, @RequestBody PublisherDTO.Request request) {
        return publisherService.updatePublisher(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }
}
