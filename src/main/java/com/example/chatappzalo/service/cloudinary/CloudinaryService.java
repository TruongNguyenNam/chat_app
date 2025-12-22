package com.example.chatappzalo.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private static final String DEFAULT_FOLDER = "chatapp_media";

    public Map<String, Object> uploadFile(MultipartFile file, String folderName) throws IOException {
        // Kiểm tra file trống
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        String resourceType = determineResourceType(file.getContentType());
        Map<String, Object> options = new HashMap<>();
        options.put("folder", folderName != null ? folderName : DEFAULT_FOLDER);
        options.put("resource_type", resourceType);
        options.put("overwrite", true); // Tránh trùng lặp file

        try {
            return cloudinary.uploader().upload(file.getBytes(), options);
        } catch (IOException e) {
            throw new IOException("Failed to upload file to Cloudinary: " + e.getMessage(), e);
        }
    }

    private String determineResourceType(String contentType) {
        if (contentType == null) return "auto";
        if (contentType.startsWith("image/")) return "image";
        if (contentType.startsWith("video/")) return "video";
        if (contentType.startsWith("audio/")) return "video"; // Cloudinary dùng "video" cho audio
        return "raw"; // Cho các file khác (PDF, DOC, v.v.)
    }

    public String getMediaUrl(Map<String, Object> uploadResult) {
        return (String) uploadResult.get("url");
    }
//    public Map uploadFileV1(MultipartFile file, String folderName) throws IOException {
//        return cloudinary.uploader().upload(file.getBytes(),
//                ObjectUtils.asMap(
//                        "folder", folderName
//                ));
//    }
//
//
//
//    public Map uploadVideo(MultipartFile file, String folderName) throws IOException {
//        return cloudinary.uploader().upload(file.getBytes(),
//                ObjectUtils.asMap(
//                        "resource_type", "video",
//                        "folder", folderName
//                ));
//    }
}
