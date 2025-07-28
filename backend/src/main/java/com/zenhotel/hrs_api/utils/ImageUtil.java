package com.zenhotel.hrs_api.utils;

import com.zenhotel.hrs_api.exception.RequestValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
public class ImageUtil {

    public byte[] getImageBytesFromMultipartFile(MultipartFile imageFile) throws RequestValidationException {
        byte[] imageBytes;

        // Check if the file is an image
        if (!Objects.requireNonNull(imageFile.getContentType()).startsWith("image")) {
            throw new RequestValidationException("File is not an image");
        }

        // Check if the image file can be read
        try {
            imageBytes = imageFile.getBytes();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RequestValidationException("File does not exist or could not be read");
        }

        return imageBytes;
    }

}
