package org.skypro.skyshop.controller;

import org.skypro.skyshop.model.article.Article;
import org.skypro.skyshop.model.product.Product;
import org.skypro.skyshop.model.product.SimpleProduct;
import org.skypro.skyshop.model.search.SearchResult;
import org.skypro.skyshop.service.SearchService;
import org.skypro.skyshop.service.StorageService;
import org.springframework.web.bind.annotation.*;
import org.skypro.skyshop.model.basket.UserBasket;
import org.skypro.skyshop.service.BasketService;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ShopController {
    private final StorageService storageService;
    private final SearchService searchService;
    private final BasketService basketService;

    public ShopController(StorageService storageService,
                          SearchService searchService,
                          BasketService basketService) {
        this.storageService = storageService;
        this.searchService = searchService;
        this.basketService = basketService;
    }

    @GetMapping("/products")
    public Collection<Product> getAllProducts() {
        return storageService.getAllProducts();
    }

    @GetMapping("/products/details")
    public List<ProductInfo> getAllProductsDetails() {
        return storageService.getAllProducts().stream()
                .map(p -> new ProductInfo(p.getId().toString(), p.getName(), p.getPrice()))
                .collect(Collectors.toList());
    }

    @PostMapping("/products")
    public Product createProduct(@RequestBody CreateProductRequest request) {
        // Валидация
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название продукта не может быть пустым");
        }
        if (request.getPrice() <= 0) {
            throw new IllegalArgumentException("Цена должна быть больше 0");
        }

        Product product = new SimpleProduct(
                UUID.randomUUID(),
                request.getName().trim(),
                request.getPrice()
        );
        return storageService.addProduct(product);
    }

    @GetMapping("/articles")
    public Collection<Article> getAllArticles() {
        return storageService.getAllArticles();
    }

    @GetMapping("/search")
    public Collection<SearchResult> search(@RequestParam String pattern) {
        return searchService.search(pattern);
    }

    @GetMapping("/basket/{id}")
    public String addProductToBasket(@PathVariable("id") String idString) {
        try {
            UUID id = UUID.fromString(idString);
            basketService.addProductToBasket(id);
            return "Продукт успешно добавлен";
        } catch (IllegalArgumentException e) {
            return "Ошибка: " + e.getMessage();
        }
    }

    @GetMapping("/basket")
    public UserBasket getUserBasket() {
        return basketService.getUserBasket();
    }

    @GetMapping("/basket/clear")
    public String clearBasket() {
        basketService.clearBasket();
        return "Корзина очищена";
    }

    public static class ProductInfo {
        private final String id;
        private final String name;
        private final int price;

        public ProductInfo(String id, String name, int price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public int getPrice() { return price; }
    }

    public static class CreateProductRequest {
        private String name;
        private int price;

        // геттеры и сеттеры
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getPrice() { return price; }
        public void setPrice(int price) { this.price = price; }
    }
}


