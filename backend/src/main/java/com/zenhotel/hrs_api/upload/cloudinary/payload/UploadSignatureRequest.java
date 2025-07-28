package com.zenhotel.hrs_api.upload.cloudinary.payload;

public record UploadSignatureRequest(
        Long recordId,
        String uploadType
) {
}
