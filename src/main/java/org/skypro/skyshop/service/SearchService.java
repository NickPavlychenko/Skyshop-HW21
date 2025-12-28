package org.skypro.skyshop.service;

import org.skypro.skyshop.model.search.SearchResult;
import org.skypro.skyshop.model.search.Searchable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class SearchService {
    private final StorageService storageService;

    public SearchService(StorageService storageService) {
        this.storageService = storageService;
    }

    public Collection<SearchResult> search(String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            return storageService.getAllSearchables().stream()
                    .map(SearchResult::fromSearchable)
                    .collect(Collectors.toList());
        }

        String patternLower = pattern.toLowerCase().trim();

        return storageService.getAllSearchables().stream()
                .filter(item -> item.getSearchTerm().toLowerCase().contains(patternLower))
                .map(SearchResult::fromSearchable)
                .collect(Collectors.toList());
    }
}
