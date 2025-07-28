package com.zenhotel.hrs_api.upload.cloudinary;

import com.zenhotel.hrs_api.exception.CloudinaryResourceException;
import com.zenhotel.hrs_api.upload.cloudinary.payload.UploadSignatureRequest;

import java.util.Map;

public interface UploadService {

    Map<String, String> generateCloudinaryUploadSignature(UploadSignatureRequest signatureRequest);

    Map<String, String> uploadImageResourceToCloudinary(byte[] imageBytes, UploadSignatureRequest signatureRequest) throws CloudinaryResourceException;

    void deleteCloudinaryImageResourceByPublicId(String publicId, boolean invalidate) throws CloudinaryResourceException;
}
