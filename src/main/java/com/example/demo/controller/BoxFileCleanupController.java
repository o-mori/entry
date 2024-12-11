package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/box")
public class BoxFileCleanupController {

    private final BoxFileCleanupService boxFileCleanupService;

    @Autowired
    public BoxFileCleanupController(BoxFileCleanupService boxFileCleanupService) {
        this.boxFileCleanupService = boxFileCleanupService;
    }

    @PostMapping("/cleanup")
    public List<String> deleteFilesByPattern(
        @RequestParam String boxFolderId, 
        @RequestParam String fileNameRegex
    ) {
        return boxFileCleanupService.deleteFilesByPattern(boxFolderId, fileNameRegex);
    }
}
