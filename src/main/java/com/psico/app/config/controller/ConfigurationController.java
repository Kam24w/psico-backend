package com.psico.app.config.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psico.app.common.response.ApiResponse;
import com.psico.app.config.service.ConfigurationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/api/configuration", "/api/config"})
@RequiredArgsConstructor
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ConfigurationService.AppConfig>> getConfiguration(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Configuration loaded", configurationService.getConfiguration()));
    }
}
