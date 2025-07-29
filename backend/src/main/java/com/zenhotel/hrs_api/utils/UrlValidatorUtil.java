package com.zenhotel.hrs_api.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
@Slf4j
public class UrlValidatorUtil {

    public static boolean isValidHttpUrl(String url) {
        try {
            URI uri = new URI(url);

            // Basic checks
            if (uri.getScheme() == null || uri.getHost() == null) {
                return false;
            }

            String scheme = uri.getScheme().toLowerCase();

            return (scheme.equals("http") || scheme.equals("https"));
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
            return false;
        }
    }

}
