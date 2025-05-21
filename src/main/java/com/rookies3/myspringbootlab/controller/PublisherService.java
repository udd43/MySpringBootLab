package com.rookies3.myspringbootlab.controller;

import com.rookies3.myspringbootlab.controller.dto.PublisherDTO;
import com.rookies3.myspringbootlab.exception.BusinessException;
import com.rookies3.myspringbootlab.exception.ErrorCode;
import com.rookies3.myspringbootlab.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
@Validated
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    public ResponseEntity<List<PublisherDTO.SimpleResponse>> getAllPublishers() {
        return ResponseEntity.ok(publisherService.getAllPublishers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublisherDTO.SimpleResponse> getPublisherById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(publisherService.getPublisherById(id));
        } catch (BusinessException ex) {
            if (ex.getErrorCode() == ErrorCode.RESOURCE_NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            throw ex;
        }
    }

    @GetMapping("/search")
    public ResponseEntity<PublisherDTO.SimpleResponse> getPublisherByName(@RequestParam String name) {
        try {
            return ResponseEntity.ok(publisherService.getPublisherByName(name));
        } catch (BusinessException ex) {
            if (ex.getErrorCode() == ErrorCode.RESOURCE_NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            throw ex;
        }
    }

    @PostMapping
    public ResponseEntity<PublisherDTO.SimpleResponse> createPublisher(
            @Valid @RequestBody PublisherDTO.Request request) {
        PublisherDTO.SimpleResponse created = publisherService.createPublisher(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublisherDTO.SimpleResponse> updatePublisher(
            @PathVariable Long id,
            @Valid @RequestBody PublisherDTO.Request request) {
        try {
            PublisherDTO.SimpleResponse updated = publisherService.updatePublisher(id, request);
            return ResponseEntity.ok(updated);
        } catch (BusinessException ex) {
            if (ex.getErrorCode() == ErrorCode.RESOURCE_NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            throw ex;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long id) {
        try {
            publisherService.deletePublisher(id);
            return ResponseEntity.noContent().build();
        } catch (BusinessException ex) {
            if (ex.getErrorCode() == ErrorCode.RESOURCE_NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            if (ex.getErrorCode() == ErrorCode.ILLEGAL_STATE) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            throw ex;
        }
    }
}
