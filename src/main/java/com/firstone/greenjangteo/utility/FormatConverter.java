package com.firstone.greenjangteo.utility;

import org.springframework.data.domain.Sort;

public class FormatConverter {
    public static Sort parseSortString(String sort) {
        String[] parts = sort.split(",");
        return Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
    }
}
