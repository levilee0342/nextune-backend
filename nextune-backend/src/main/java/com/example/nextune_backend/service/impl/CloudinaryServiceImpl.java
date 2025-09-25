package com.example.nextune_backend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import com.example.nextune_backend.dto.CloudAsset;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import com.example.nextune_backend.dto.response.CloudinaryResponse;
import com.example.nextune_backend.service.CloudinaryService;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File rỗng, không thể upload");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File không phải định dạng ảnh");
        }
    }

    private CloudAsset toAsset(Map<?, ?> res) {
        return CloudAsset.builder()
                .publicId((String) res.get("public_id"))
                .url((String) res.get("secure_url"))
                .format((String) res.get("format"))
                .resourceType((String) res.get("resource_type"))
                .width(res.get("width") != null ? ((Number) res.get("width")).intValue() : 0)
                .height(res.get("height") != null ? ((Number) res.get("height")).intValue() : 0)
                .bytes(res.get("bytes") != null ? ((Number) res.get("bytes")).longValue() : 0)
                .build();
    }

    private CloudAsset uploadFixedFile(MultipartFile file, String publicId, String folder) throws IOException {
        validateImage(file);
        Map<?, ?> res = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "public_id", publicId,
                        "resource_type", "image",
                        "overwrite", true,
                        "invalidate", true,
                        "folder", folder
                )
        );
        return toAsset(res);
    }

    private void destroyAllTypes(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video"));
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "raw"));
    }

    // ===================== USER =====================
    @Override
    public CloudAsset uploadUserAvatar(String userId, MultipartFile file) throws IOException {
        return uploadFixedFile(file, "users/" + userId + "/avatar", "users/" + userId);
    }

    @Override
    public void deleteUserAvatar(String userId) throws IOException {
        destroyAllTypes("users/" + userId + "/avatar");
    }

    @Override
    public CloudAsset uploadUserCover(String userId, MultipartFile file) throws IOException {
        return uploadFixedFile(file, "users/" + userId + "/cover", "users/" + userId);
    }

    @Override
    public void deleteUserCover(String userId) throws IOException {
        destroyAllTypes("users/" + userId + "/cover");
    }

    // ===================== SONG =====================
    @Override
    public CloudAsset uploadSongCover(String songId, MultipartFile file, boolean isGif) throws IOException {
        String publicId = isGif
                ? "songs/" + songId + "/cover_gif"
                : "songs/" + songId + "/cover_image";

        String folder = "songs/" + songId;

        return uploadFixedFile(file, publicId, folder);
    }

    @Override
    public void deleteSongCover(String songId, boolean isGif) throws IOException {
        String publicId = isGif
                ? "songs/" + songId + "/cover_gif"
                : "songs/" + songId + "/cover_image";

        destroyAllTypes(publicId);
    }

    // ===================== ALBUM =====================
    @Override
    public CloudAsset uploadAlbumCover(String albumId, MultipartFile file) throws IOException {
        return uploadFixedFile(file, "albums/" + albumId + "/cover", "albums/" + albumId);
    }

    @Override
    public void deleteAlbumCover(String albumId) throws IOException {
        destroyAllTypes("albums/" + albumId + "/cover");
    }
    // ===================== TRACK =====================
    @Override
    public CloudAsset uploadTrack(String userId, MultipartFile file) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "video", // Cloudinary lưu audio như video
                        "folder", "users/" + userId + "/tracks"
                )
        );

        CloudAsset asset = new CloudAsset();
        asset.setPublicId((String) uploadResult.get("public_id"));
        asset.setUrl((String) uploadResult.get("secure_url"));
        asset.setFormat((String) uploadResult.get("format"));
        asset.setResourceType((String) uploadResult.get("resource_type"));
        asset.setBytes(((Number) uploadResult.get("bytes")).longValue());
        asset.setDuration(uploadResult.get("duration") != null ? ((Number) uploadResult.get("duration")).doubleValue() : 0);

        return asset;
    }

    @Override
    public void deleteTrack(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video"));
    }

}
