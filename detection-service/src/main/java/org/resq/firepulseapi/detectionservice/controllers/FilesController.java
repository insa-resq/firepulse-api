package org.resq.firepulseapi.detectionservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.resq.firepulseapi.detectionservice.entities.enums.FileGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/files")
@Tag(name = "Files Controller", description = "Endpoints for managing remote files")
public class FilesController {
    private final RestTemplate restTemplate;

    @Value("${http.detection-engine-api.base-url}")
    private String detectionEngineApiBaseUrl;

    public FilesController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("{fileGroup}/{fileName}")
    @Operation(summary = "Get a file")
    public ResponseEntity<Void> getFile(
            @PathVariable FileGroup fileGroup,
            @PathVariable String fileName,
            HttpServletResponse response
    ) {
        String remoteUrl = String.format(
                "%s/files/%s/%s",
                detectionEngineApiBaseUrl,
                fileGroup,
                fileName
        );

        restTemplate.execute(
                remoteUrl,
                HttpMethod.GET,
                null,
                (clientHttpResponse) -> {
                    MediaType contentType = clientHttpResponse.getHeaders().getContentType();
                    if (contentType != null) {
                        response.setContentType(contentType.toString());
                    }

                    long contentLength = clientHttpResponse.getHeaders().getContentLength();
                    if (contentLength >= 0) {
                        response.setContentLengthLong(contentLength);
                    }

                    String cacheControl = clientHttpResponse.getHeaders().getCacheControl();
                    if (cacheControl != null) {
                        response.setHeader("Cache-Control", cacheControl);
                    }

                    StreamUtils.copy(clientHttpResponse.getBody(), response.getOutputStream());

                    return null;
                });

        return ResponseEntity.ok().build();
    }
}
