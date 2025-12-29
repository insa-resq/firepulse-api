package org.resq.firepulseapi.detectionservice.services;

import jakarta.persistence.criteria.Predicate;
import org.resq.firepulseapi.detectionservice.dtos.ImageDto;
import org.resq.firepulseapi.detectionservice.dtos.ImagesBulkCreationDto;
import org.resq.firepulseapi.detectionservice.dtos.ImagesBulkDeletionDto;
import org.resq.firepulseapi.detectionservice.dtos.ImagesFilters;
import org.resq.firepulseapi.detectionservice.entities.Image;
import org.resq.firepulseapi.detectionservice.exceptions.ApiException;
import org.resq.firepulseapi.detectionservice.repositories.ImageRepository;
import org.resq.firepulseapi.detectionservice.utils.Conversion;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ImageService {
    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public List<ImageDto> getImages(ImagesFilters filters) {
        Specification<Image> spec = buildSpecificationFromFilters(filters);
        return imageRepository.findAll(spec).stream()
                .map(ImageDto::fromEntity)
                .toList();
    }

    public ImageDto getImageById(String imageId) {
        return imageRepository.findById(imageId)
                .map(ImageDto::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Image not found"));
    }

    @Transactional
    public List<ImageDto> createImages(ImagesBulkCreationDto imagesBulkCreationDto) {
        Set<String> urlsSet = new HashSet<>();
        List<Integer> duplicateIndices = new ArrayList<>();

        for (int i = 0; i < imagesBulkCreationDto.getImages().size(); i++) {
            String url = imagesBulkCreationDto.getImages().get(i).getUrl();
            if (!urlsSet.add(url)) {
                duplicateIndices.add(i);
            }
        }

        if (!duplicateIndices.isEmpty()) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    String.format(
                            "Duplicate image URLs found at indices: %s",
                            duplicateIndices.stream().map(String::valueOf).collect(Collectors.joining(", "))
                    )
            );
        }

        List<Image> imagesToCreate = imagesBulkCreationDto.getImages().stream()
                .map(imgDto -> {
                    Image image = new Image();
                    image.setUrl(imgDto.getUrl());
                    image.setSplit(imgDto.getSplit());
                    image.setContainsFire(imgDto.getContainsFire());
                    image.setMetadata(Conversion.mapToJsonNode(imgDto.getMetadata()));
                    return image;
                })
                .collect(Collectors.toList());

        List<Image> createdImages = imageRepository.saveAll(imagesToCreate);

        return createdImages.stream()
                .map(ImageDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteImages(ImagesBulkDeletionDto imagesBulkDeletionDto) {
        Set<String> imageIdsSet = new HashSet<>(imagesBulkDeletionDto.getImageIds());
        List<Image> imagesToDelete = imageRepository.findAllById(imageIdsSet);

        if (imagesToDelete.size() != imagesBulkDeletionDto.getImageIds().size()) {
            Set<String> foundIds = imagesToDelete.stream()
                    .map(Image::getId)
                    .collect(Collectors.toSet());

            imageIdsSet.removeAll(foundIds);

            throw new ApiException(
                    HttpStatus.NOT_FOUND,
                    String.format(
                            "Images with the following IDs were not found: %s",
                            String.join(", ", imageIdsSet)
                    )
            );
        }

        imageRepository.deleteAllInBatch(imagesToDelete);
    }

    private Specification<Image> buildSpecificationFromFilters(ImagesFilters filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getSplits() != null && !filters.getSplits().isEmpty()) {
                predicates.add(root.get("split").in(filters.getSplits()));
            }

            if (filters.getContainsFire() != null) {
                predicates.add(criteriaBuilder.equal(root.get("containsFire"), filters.getContainsFire()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
