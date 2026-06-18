package ma.groupe07.tourisme.modules.file.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.common.ApiResponse;
import ma.groupe07.tourisme.modules.file.model.UploadedImage;
import ma.groupe07.tourisme.modules.file.repository.UploadedImageRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final UploadedImageRepository imageRepo;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> upload(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType() != null ? file.getContentType() : "image/jpeg";
        UploadedImage image = imageRepo.save(UploadedImage.builder()
                .contentType(contentType)
                .data(file.getBytes())
                .build());

        String baseUrl = ServletUriComponentsBuilder.fromContextPath(request).build().toUriString();
        String url = baseUrl + "/api/v1/files/" + image.getId();

        return ResponseEntity.status(201).body(ApiResponse.success("File uploaded", Map.of("url", url)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> get(@PathVariable Long id) {
        UploadedImage image = imageRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
                .body(image.getData());
    }
}
