package org.skypro.skyshop.service;

import org.skypro.skyshop.model.basket.BasketItem;
import org.skypro.skyshop.model.basket.ProductBasket;
import org.skypro.skyshop.model.basket.UserBasket;
import org.skypro.skyshop.model.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BasketService {
    private final ProductBasket productBasket;
    private final StorageService storageService;

    public BasketService(ProductBasket productBasket, StorageService storageService) {
        this.productBasket = productBasket;
        this.storageService = storageService;
    }

    public void addProductToBasket(UUID productId) {
        Product product = storageService.findProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Продукт с ID " + productId + "не найден"));

        productBasket.addProduct(productId);
    }

    public UserBasket getUserBasket() {
        Map<UUID, Integer> basketItems = productBasket.getAllProducts();

        List<BasketItem> items = basketItems.entrySet().stream()
                .map(entry -> {
                    Product product = storageService.getProductById(entry.getKey());
                    int quantity = entry.getValue();
                    return new BasketItem(product, quantity);
                })
                .collect(Collectors.toList());

        return new UserBasket(items);
    }

    public void clearBasket() {
        productBasket.clearBasket();
    }

    public int getTotalItemsCount() {
        return productBasket.getTotalItemCount();
    }
}
