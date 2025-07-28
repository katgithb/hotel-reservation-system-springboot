package com.zenhotel.hrs_api.upload.cloudinary.payload;

public record UploadSignatureResponse(
        String uploadSignature,
        String timestamp,
        String publicId
) {
}
