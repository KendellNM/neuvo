package com.farm.dolores.farmacia.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PaginationUtils {
    private PaginationUtils() {}

    public static Pageable buildPageable(String sort, int page, int size) {
        String sortField = "nombre";
        String sortDir = "asc";
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            sortField = parts[0];
            if (parts.length == 2) sortDir = parts[1];
        }
        Sort sortObj = "desc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();
        return PageRequest.of(page, size, sortObj);
    }
}
