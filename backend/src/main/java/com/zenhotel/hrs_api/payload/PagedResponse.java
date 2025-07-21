package com.zenhotel.hrs_api.payload;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;

import java.time.OffsetDateTime;
import java.util.List;

public record PagedResponse<T>(
        int status,
        String message,
        OffsetDateTime timestamp,
        List<T> content,
        int page,
        int size,
        long totalRecords,
        int totalPages,
        boolean isLast
) {

    public static <S, T> PagedResponse<T> fromPage(
            int status,
            String message,
            Page<S> sourcePage,
            ModelMapper modelMapper,
            TypeToken<List<T>> targetTypeToken
    ) {
        // Directly map the content from List<S> to List<T> using ModelMapper's TypeToken
        List<T> mappedContent = modelMapper.map(sourcePage.getContent(), targetTypeToken.getType());

        return new PagedResponse<>(
                status,
                message,
                OffsetDateTime.now(),
                mappedContent,
                sourcePage.getNumber(),
                sourcePage.getSize(),
                sourcePage.getTotalElements(),
                sourcePage.getTotalPages(),
                sourcePage.isLast()
        );
    }

}
