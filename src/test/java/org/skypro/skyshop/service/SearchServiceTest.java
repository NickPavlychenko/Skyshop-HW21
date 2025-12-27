package org.skypro.skyshop.service;

import org.skypro.skyshop.model.article.Article;
import org.skypro.skyshop.model.product.Product;
import org.skypro.skyshop.model.product.SimpleProduct;
import org.skypro.skyshop.model.search.SearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.skyshop.model.search.Searchable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private StorageService storageService;

    @InjectMocks
    private SearchService searchService;

    private Product product1;
    private Product product2;
    private Article article1;

    @BeforeEach
    void setUp() {
        product1 = new SimpleProduct(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "Игровой ноутбук",
                50000
        );

        product2 = new SimpleProduct(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "Офисный монитор",
                15000
        );

        article1 = new Article(
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                "Обзор игровых ноутбуков",
                "Современные игровые ноутбуки имеют мощные видеокарты"
        );
    }

    @Test
    void search_WhenStorageIsEmpty_ShouldReturnEmptyList() {
        when(storageService.getAllSearchables()).thenReturn(Collections.emptyList());

        Collection<SearchResult> results = searchService.search("игровой");

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(storageService, times(1)).getAllSearchables();
    }

    @Test
    void search_WhenNoMatches_ShouldReturnEmptyList() {
        List<Searchable> searchables = Arrays.asList(product2);
        when(storageService.getAllSearchables()).thenReturn(new ArrayList<>(searchables));

        Collection<SearchResult> results = searchService.search("игровой");

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(storageService, times(1)).getAllSearchables();
    }

    @Test
    void search_WhenMatchExists_ShouldReturnSearchResult() {
        List<Searchable> searchables = Arrays.asList(product1, product2, article1);
        when(storageService.getAllSearchables()).thenReturn(new ArrayList<>(searchables));

        Collection<SearchResult> results = searchService.search("игровой");

        assertNotNull(results);
        assertEquals(2, results.size());

        List<SearchResult> resultList = new ArrayList<>(results);
        assertTrue(resultList.stream().anyMatch(r -> r.getName().contains("ноутбук")));
        assertTrue(resultList.stream().anyMatch(r -> r.getName().contains("Обзор")));

        verify(storageService, times(1)).getAllSearchables();
    }

    @Test
    void search_ShouldBeCaseInsensitive() {
        List<Searchable> searchables = Arrays.asList(product1);
        when(storageService.getAllSearchables()).thenReturn(new ArrayList<>(searchables));

        Collection<SearchResult> results1 = searchService.search("ИГРОВОЙ");
        Collection<SearchResult> results2 = searchService.search("игровой");
        Collection<SearchResult> results3 = searchService.search("ИгРоВоЙ");

        assertEquals(1, results1.size());
        assertEquals(1, results2.size());
        assertEquals(1, results3.size());
    }

    @Test
    void search_WhenPartialMatch_ShouldReturnResults() {
        List<Searchable> searchables = Arrays.asList(product1);
        when(storageService.getAllSearchables()).thenReturn(new ArrayList<>(searchables));

        Collection<SearchResult> results = searchService.search("ноут");

        assertEquals(1, results.size());
    }

    @Test
    void search_WhenEmptyPattern_ShouldReturnAll() {
        List<Searchable> searchables = Arrays.asList(product1, product2, article1);
        when(storageService.getAllSearchables()).thenReturn(new ArrayList<>(searchables));

        Collection<SearchResult> results = searchService.search("");

        assertEquals(3, results.size());
    }

    @Test
    void search_WhenNullPattern_ShouldReturnAll() {
        List<Searchable> searchables = Arrays.asList(product1, product2, article1);
        when(storageService.getAllSearchables()).thenReturn(new ArrayList<>(searchables));

        Collection<SearchResult> results = searchService.search(null);

        assertEquals(3, results.size());
    }
}
