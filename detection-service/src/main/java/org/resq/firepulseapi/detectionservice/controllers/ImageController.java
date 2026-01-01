package org.resq.firepulseapi.detectionservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.resq.firepulseapi.detectionservice.dtos.ImageDto;
import org.resq.firepulseapi.detectionservice.dtos.ImagesBulkCreationDto;
import org.resq.firepulseapi.detectionservice.dtos.ImagesFilters;
import org.resq.firepulseapi.detectionservice.services.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/images")
@Tag(name = "Image Controller", description = "Endpoints for managing images")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ALERT_MONITOR')")
    @Operation(summary = "Get images list with optional filters")
    public ResponseEntity<List<ImageDto>> getImages(@Valid @ModelAttribute ImagesFilters filters) {
        List<ImageDto> images = imageService.getImages(filters);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/{imageId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ALERT_MONITOR')")
    @Operation(summary = "Get an image by ID")
    public ResponseEntity<ImageDto> getImageById(@PathVariable String imageId) {
        ImageDto image = imageService.getImageById(imageId);
        return ResponseEntity.ok(image);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create one or multiple images")
    public ResponseEntity<List<ImageDto>> createImages(@Valid @RequestBody ImagesBulkCreationDto imagesBulkCreationDto) {
        List<ImageDto> createdImages = imageService.createImages(imagesBulkCreationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdImages);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete one or multiple images")
    public ResponseEntity<Void> deleteImages(
            @Valid
            @RequestParam("imageIds")
            List<@NotBlank(message = "An image ID cannot be blank") String> imageIds
    ) {
        imageService.deleteImages(imageIds);
        return ResponseEntity.noContent().build();
    }
}
