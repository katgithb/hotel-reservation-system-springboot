package com.zenhotel.hrs_api.payload;

import com.zenhotel.hrs_api.utils.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    private Integer page = AppConstants.DEFAULT_PAGE_NUMBER;
    private Integer size = AppConstants.DEFAULT_PAGE_SIZE;
    private String sortBy = AppConstants.DEFAULT_SORT_BY;
    private String sortDir = AppConstants.DEFAULT_SORT_DIRECTION;

    public Pageable toPageable() {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDir)
                .orElse(Sort.Direction.fromString(AppConstants.DEFAULT_SORT_DIRECTION));

        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

}

