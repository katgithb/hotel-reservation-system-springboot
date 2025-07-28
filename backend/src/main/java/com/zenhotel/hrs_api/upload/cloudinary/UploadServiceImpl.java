package com.zenhotel.hrs_api.upload.cloudinary;

import com.cloudinary.Cloudinary;
import com.zenhotel.hrs_api.enums.UploadType;
import com.zenhotel.hrs_api.exception.CloudinaryResourceException;
import com.zenhotel.hrs_api.exception.RequestValidationException;
import com.zenhotel.hrs_api.upload.cloudinary.payload.UploadSignatureRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.upload-preset}")
    private String cloudinaryUploadPreset;

    @Value("${cloudinary.upload-folder}")
    private String cloudinaryUploadFolder;

    @Override
    public Map<String, String> generateCloudinaryUploadSignature(UploadSignatureRequest signatureRequest) {
        Long recordId = signatureRequest.recordId();
        String uploadType = signatureRequest.uploadType();
        long timestamp = System.currentTimeMillis() / 1000L;
        System.out.println("Upload type: " + uploadType);

        String publicId = getPublicIdFromUploadType(recordId, uploadType);

        Map<String, Object> paramsToSign = Map.of("timestamp", String.valueOf(timestamp), "public_id", publicId);
        String apiSecret = cloudinary.config.apiSecret;
        System.out.println("Public id: " + publicId);

        String uploadSignature = cloudinary.apiSignRequest(
                paramsToSign, apiSecret
        );

        return Map.of("uploadSignature", uploadSignature, "timestamp", String.valueOf(timestamp), "public_id", publicId);
    }

    @Override
    public Map<String, String> uploadImageResourceToCloudinary(byte[] imageBytes, UploadSignatureRequest signatureRequest)
            throws CloudinaryResourceException {
        // Generate upload signature
        Map<String, String> uploadSignature = generateCloudinaryUploadSignature(signatureRequest);

        // Set upload options for the image
        Map<String, Object> uploadOptions = new HashMap<>();
        uploadOptions.put("public_id", uploadSignature.get("public_id"));
        uploadOptions.put("timestamp", uploadSignature.get("timestamp"));
        uploadOptions.put("signature", uploadSignature.get("uploadSignature"));
        uploadOptions.put("format", "jpg");
        uploadOptions.put("upload_preset", cloudinaryUploadPreset);

        try {
            // Upload the image to Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(imageBytes, uploadOptions);

            String publicId = (String) uploadResult.get("public_id");
            String secureUrl = (String) uploadResult.get("secure_url");
            System.out.println("Public ID: " + publicId);
            System.out.println("Secure URL: " + secureUrl);

            // Return public ID and secure URL of the uploaded image
            return Map.of("publicId", publicId, "secureUrl", secureUrl);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new CloudinaryResourceException("Failed to upload image resource");
        }
    }

    @Override
    public void deleteCloudinaryImageResourceByPublicId(String publicId, boolean invalidate) throws CloudinaryResourceException {
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("invalidate", invalidate);

            cloudinary.uploader().destroy(publicId, options);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new CloudinaryResourceException("Failed to delete image resource with public ID: " + publicId);
        }
    }

    private String getPublicIdFromUploadType(Long recordId, String uploadTypeStr) {
        UploadType uploadTypeValue = UploadType.from(uploadTypeStr);

        String rootFolder = cloudinaryUploadFolder;
        String randomPublicId = rootFolder + "/" + buildCloudinaryPublicId(uploadTypeValue, recordId);

        return Optional.ofNullable(recordId)
                .map(id -> randomPublicId)
                .orElseThrow(() -> new RequestValidationException("Invalid ID provided"));
    }

    private String buildCloudinaryPublicId(UploadType uploadType, Long recordId) {
        String uploadFolder = uploadType.getUploadFolder();
        String encodedId = encodeRecordIdForPublicId(recordId);
        String randomPublicId = cloudinary.randomPublicId();
        return uploadFolder + "/" + encodedId + randomPublicId;
    }

    private String encodeRecordIdForPublicId(Long id) {
        String idPrefix = "SN";
        String idSuffix = "__#";
        int startIndex = 1000;
        String encodedId = idPrefix + (startIndex + id) + idSuffix;
        return encodeToBase64(encodedId);
    }

    private String encodeToBase64(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes());
    }

}
