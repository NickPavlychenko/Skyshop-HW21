package org.skypro.skyshop.service;

import org.skypro.skyshop.model.article.Article;
import org.skypro.skyshop.model.product.Product;
import org.skypro.skyshop.model.product.SimpleProduct;
import org.skypro.skyshop.model.product.DiscountedProduct;
import org.skypro.skyshop.model.product.FixPriceProduct;
import org.skypro.skyshop.model.search.Searchable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

@Service
public class StorageService {
    private final Map<UUID, Product> productStorage;
    private final Map<UUID, Article> articleStorage;

    public StorageService() {
        this.productStorage = new HashMap<UUID, Product>();
        this.articleStorage = new HashMap<UUID, Article>();
        initializeTestData();
    }

    private void initializeTestData() {

        Product laptop1 = new SimpleProduct(
                UUID.randomUUID(),
                "Игровой ноутбук",
                50000
        );

        Product laptop2 = new SimpleProduct(
                UUID.randomUUID(),
                "Игровой ноутбук",
                45000
        );

        Product mouse = new SimpleProduct(
                UUID.randomUUID(),
                "Игровая мышь",
                5000
        );

        Product keyboard = new DiscountedProduct(
                UUID.randomUUID(),
                "Механическая клавиатура",
                7000,
                30
        );

        Product fixedProduct = new FixPriceProduct(
                UUID.randomUUID(),
                "Фиксированный товар"
        );


        productStorage.put(laptop1.getId(), laptop1);
        productStorage.put(laptop2.getId(), laptop2);
        productStorage.put(mouse.getId(), mouse);
        productStorage.put(keyboard.getId(), keyboard);
        productStorage.put(fixedProduct.getId(), fixedProduct);


        Article article1 = new Article(
                UUID.randomUUID(),
                "Обзор игровых ноутбуков",
                "Лучшие игровые ноутбуки 2025 года"
        );

        Article article2 = new Article(
                UUID.randomUUID(),
                "Выбор игровой мыши",
                "Как выбрать игровую мышь для компьютерных игр"
        );


        articleStorage.put(article1.getId(), article1);
        articleStorage.put(article2.getId(), article2);
    }

    public Collection<Product> getAllProducts() {
        return new ArrayList<>(productStorage.values());
    }

    public Collection<Article> getAllArticles() {
        return new ArrayList<>(articleStorage.values());
    }

    public Collection<Searchable> getAllSearchables() {
        List<Searchable> allSearchables = new ArrayList<>();
        allSearchables.addAll(productStorage.values());
        allSearchables.addAll(articleStorage.values());
        return allSearchables;
    }

    public Product getProductById(UUID id) {
        return productStorage.get(id);
    }

    public Article getArticleById(UUID id) {
        return articleStorage.get(id);
    }

    public Product addProduct(Product product) {
        productStorage.put(product.getId(), product);
        return product;
    }

    public Article addArticle(Article article) {
        articleStorage.put(article.getId(), article);
        return article;
    }

    public int getProductCount() {
        return productStorage.size();
    }

    public int getArticleCount() {
        return articleStorage.size();
    }
}



